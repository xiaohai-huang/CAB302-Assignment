
DROP TABLE IF EXISTS scheduling;
DROP TABLE IF EXISTS billboard;
DROP TABLE IF EXISTS user;

CREATE TABLE IF NOT EXISTS user (
	userName VARCHAR(30) UNIQUE NOT NULL,
	password CHAR(128) NOT NULL,
    salt CHAR(128) NOT NULL,
	create_billboards BOOLEAN NOT NULL DEFAULT FALSE,
    edit_all_billboards BOOLEAN NOT NULL DEFAULT FALSE,
    schedule_billboards BOOLEAN NOT NULL DEFAULT FALSE,
    edit_users BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (userName)
    );
    
CREATE TABLE IF NOT EXISTS billboard (
	billboardName VARCHAR(30) UNIQUE NOT NULL,
    billboardCreator VARCHAR(30) NOT NULL,
	billboardContent TEXT NOT NULL,

    PRIMARY KEY (billboardName),
    FOREIGN KEY (billboardCreator) REFERENCES user(userName) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS scheduling (
	billboardName VARCHAR(30) NOT NULL,
    startTime DATETIME NOT NULL,
    endTime DATETIME  NOT NULL ,
    createdTime DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (billboardName,startTime),
    FOREIGN KEY (billboardName) REFERENCES billboard(billboardName) ON UPDATE CASCADE ON DELETE CASCADE
    );
-- Error Code: 1064. You have an error in your SQL syntax; check the manual that corresponds to your MariaDB server version for the right syntax to use near 'NOT NULL,     createdTime datetime AS (NOW()),     PRIMARY KEY (billboardName...' at line 5

    
-- add user (userName, password)
INSERT INTO user (userName, password,salt)
VALUES ("xiaohai","12","asdasd");

-- get user
SELECT userName,password,salt,create_billboards,edit_all_billboards,schedule_billboards,edit_users
FROM user 
WHERE userName = "xiaohai";

-- remove user
DELETE FROM user
WHERE userName = "ye";

-- change user password (userName, newPassword)
UPDATE user 
SET password = "new passowrd"
WHERE userName = "xiaohai";

-- grant permission (usrName, permission)
UPDATE user
SET edit_users_permission = TRUE
WHERE userName = "xiaohai";

-- revoke permission 
UPDATE user
SET edit_users_permission = FALSE
WHERE userName = "xiaohai";

-- create billboard
INSERT INTO billboard (billboardName,billboardCreator,billboardContent)
VALUES ("billboard 1","xiaohai",'<?xml version="1.0" encoding="UTF-8"?>
<billboard>
    <message>No billboard to be shown yet!</message>
    <picture url="https://www.azquotes.com/picture-quotes/quote-ideally-nothing-should-be-shown-but-that-s-impossible-robert-bresson-102-24-74.jpg" />
</billboard>');

-- update billbaord
UPDATE billboard
SET billboardName="billboard 3",billboardCreator="xiaohai",billboardContent='<?xml version="1.0" encoding="UTF-8"?>
<billboard>
    <message>I am billboard 3</message>
</billboard>'
WHERE billboardName = "billboard 1";

UPDATE billboard
SET billboardContent=?
WHERE billboardName =?;

-- get billboard onwer name
SELECT u.userName FROM user u
LEFT JOIN billboard b
ON u.userName = b.billboardCreator
WHERE b.billboardName=?;

SELECT billboardCreator FROM billboard
WHERE billboardName = ?;

-- has billbaord
SELECT count(billboardName) AS RowCount FROM billboard
WHERE billboardName = ?;

-- check where table exists
SELECT count(*) FROM information_schema.TABLES
WHERE (TABLE_SCHEMA = 'billboard_schema') AND (TABLE_NAME = 'billboard');

-- schedule billboard
INSERT INTO scheduling (billboardName,startTime,endTime)
VALUES ("billboard 1",
"2020-05-26 16:20:00",
"2020-05-26 17:00:00");

-- display which billboard should be shown or not


SELECT b.billboardName,b.billboardContent FROM billboard b
INNER JOIN 
(SELECT billboardName FROM scheduling
WHERE (startTime <= NOW() AND NOW() < endTime)
ORDER BY createdTime DESC
LIMIT 1) AS current_scheduled_billboard
ON b.billboardName = current_scheduled_billboard.billboardName;

SELECT now();

-- get billboard XML
SELECT billboardContent FROM billboard
WHERE billboardName = "billboard 1";

-- get all billboards
SELECT billboardName,billboardCreator FROM billboard;

-- delete a billboard
DELETE FROM billboard
WHERE billboardName = "billboard 1";

-- Error Code: 1451. Cannot delete or update a parent row: a foreign key constraint fails (`billboard_schema`.`scheduling`, CONSTRAINT `scheduling_ibfk_1` FOREIGN KEY (`billboardName`) REFERENCES `billboard` (`billboardName`) ON UPDATE CASCADE)


    