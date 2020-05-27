package Server;

import ControlPanel.BasicUser;
import ControlPanel.Permission;
import ControlPanel.ServerUser;
import Viewer.Viewer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.Random;

public class BillboardDB {
    private static final String CREATE_USER_TABLE =
            "CREATE TABLE IF NOT EXISTS user (\n" +
                    "\tuserName VARCHAR(30) UNIQUE NOT NULL,\n" +
                    "\tpassword CHAR(128) NOT NULL,\n" +
                    "    salt CHAR(128) NOT NULL,\n" +
                    "\tcreate_billboards BOOLEAN NOT NULL DEFAULT FALSE,\n" +
                    "    edit_all_billboards BOOLEAN NOT NULL DEFAULT FALSE,\n" +
                    "    schedule_billboards BOOLEAN NOT NULL DEFAULT FALSE,\n" +
                    "    edit_users BOOLEAN NOT NULL DEFAULT FALSE,\n" +
                    "    PRIMARY KEY (userName)\n" +
                    "    );";
    private static final String CREATE_BILLBOARD_TABLE =
            "CREATE TABLE IF NOT EXISTS billboard (\n" +
                    "\tbillboardName VARCHAR(30) UNIQUE NOT NULL,\n" +
                    "    billboardCreator VARCHAR(30) NOT NULL,\n" +
                    "\tbillboardContent TEXT NOT NULL,\n" +
                    "\n" +
                    "    PRIMARY KEY (billboardName),\n" +
                    "    FOREIGN KEY (billboardCreator) REFERENCES user(userName)\n" +
                    ");";
    private static final String CREATE_SCHEDULING_TABLE =
            "CREATE TABLE IF NOT EXISTS scheduling (\n" +
                    "\tbillboardName VARCHAR(30) NOT NULL,\n" +
                    "    startTime DATETIME NOT NULL,\n" +
                    "    endTime DATETIME  NOT NULL ,\n" +
                    "    createdTime DATETIME DEFAULT CURRENT_TIMESTAMP,\n" +
                    "    PRIMARY KEY (billboardName,startTime),\n" +
                    "    FOREIGN KEY (billboardName) REFERENCES billboard(billboardName)\n" +
                    "    );";
    // 1 means exists
    private static final String CHECK_TABLE_EXISTENCE =
            "SELECT count(*) FROM information_schema.TABLES\n" +
                    "WHERE (TABLE_SCHEMA = 'billboard_schema') AND (TABLE_NAME = ?);";

    private static final String ADD_USER =
            "INSERT INTO user (userName, password,salt)\n" +
                    "VALUES (?,?,?);";

    private static final String DELETE_USER =
            "DELETE FROM user\n" +
                    "WHERE userName = ?;";

    private static final String GET_USER =
            "SELECT userName,password,salt,create_billboards,edit_all_billboards,schedule_billboards,edit_users\n" +
                    "FROM user \n" +
                    "WHERE userName = ?;";

    private static final String CHANGE_USER_PASSWORD =
            "UPDATE user \n" +
                    "SET password = ?\n" +
                    "WHERE userName = ?;";

    private static final  String GRANT_PERMISSION =
            "UPDATE user\n" +
                    "SET permission = TRUE\n" +
                    "WHERE userName = ?;";

    private static final String REVOKE_PERMISSION =
            "UPDATE user\n" +
                    "SET permission = FALSE\n" +
                    "WHERE userName = ?;";

    private static final String CREATE_BILLBOARD =
            "INSERT INTO billboard (billboardName,billboardCreator,billboardContent)\n" +
                    "VALUES (?,?,?)";
    private static final String GET_BILLBOARD =
            "SELECT billboardContent FROM billboard\n" +
                    "WHERE billboardName = ?;";

    private static final String SCHEDULE_BILLBOARD =
            "INSERT INTO scheduling (billboardName,startTime,endTime)\n" +
                    "VALUES (?,?,?)";

    private static final String GET_CURRENT_BILLBOARD_XML =
            "SELECT b.billboardName,b.billboardContent FROM billboard b\n" +
                    "INNER JOIN \n" +
                    "(SELECT billboardName FROM scheduling\n" +
                    "WHERE (startTime <= NOW() AND NOW() < endTime)\n" +
                    "ORDER BY createdTime DESC\n" +
                    "LIMIT 1) AS current_scheduled_billboard\n" +
                    "ON b.billboardName = current_scheduled_billboard.billboardName;";


    private Connection connection;

    private PreparedStatement checkTableExist;

    private PreparedStatement addUser;

    private PreparedStatement deleteUser;

    private PreparedStatement getUser;

    private PreparedStatement changeUserPassword;

    private PreparedStatement createBillboard;

    private PreparedStatement getBillboard;

    private PreparedStatement scheduleBillboard;

    private PreparedStatement currentBillboardXML;

    private Statement statement;

    private Random rng = new Random();

    private static MessageDigest md;

    static {
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public BillboardDB() throws SQLException, IOException {

        connection = DBConnection.getInstance();

        statement = connection.createStatement();
        checkTableExist = connection.prepareStatement(CHECK_TABLE_EXISTENCE);
        addUser = connection.prepareStatement(ADD_USER);
        deleteUser = connection.prepareStatement(DELETE_USER);
        getUser = connection.prepareStatement(GET_USER);
        changeUserPassword = connection.prepareStatement(CHANGE_USER_PASSWORD);
        createBillboard = connection.prepareStatement(CREATE_BILLBOARD);
        getBillboard = connection.prepareStatement(GET_BILLBOARD);
        scheduleBillboard = connection.prepareStatement(SCHEDULE_BILLBOARD);
        currentBillboardXML = connection.prepareStatement(GET_CURRENT_BILLBOARD_XML);


        // will have effects when fresh start up
        initTablesAndUserIfNotExist();

    }

    /**
     * Creates first user with full permissions, if this is a fresh start up
     */
    private void initTablesAndUserIfNotExist() {
        try {
            checkTableExist.setString(1, "user");
            ResultSet rs = checkTableExist.executeQuery();
            rs.next();
            int userRows = rs.getInt(1);

            checkTableExist.setString(1, "billboard");
            rs = checkTableExist.executeQuery();
            rs.next();
            int billboardRows = rs.getInt(1);

            checkTableExist.setString(1, "billboard");
            rs = checkTableExist.executeQuery();
            rs.next();
            int schedulingRows = rs.getInt(1);

            rs.close();

            int total = userRows + schedulingRows + schedulingRows;
            if (total == 0) {
                // creates 3 tables
                createTablesIfNotExists();

                // create an admin user
                String userName = "admin";
                String password = "123456";

                String hashedPassword = bytesToHexString(md.digest(password.getBytes()));

                byte[] saltBytes = new byte[32];
                rng.nextBytes(saltBytes);
                String salt = bytesToHexString(saltBytes);
                addUser(userName, hashedPassword, salt);
                // grant all permissions to the admin
                grantAllPermission("admin");
            }

        } catch (SQLException e) {
            System.out.println("Create first user failed!");
        }

    }

    private void createTablesIfNotExists() {
        try {
            statement.execute(CREATE_USER_TABLE);
            statement.execute(CREATE_BILLBOARD_TABLE);
            statement.execute(CREATE_SCHEDULING_TABLE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds an user, using the provided salt and hashedPassword
     *
     * @param userName
     * @param hashedPassword
     * @param salt
     */
    public void addUser(String userName, String hashedPassword, String salt) {
        try {
            addUser.setString(1, userName);
            addUser.setString(2, saltPassword(hashedPassword, salt));
            addUser.setString(3, salt);
            addUser.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Adds an user to the database, a random salt will be generated
     *
     * @param userName
     * @param hashedPassword
     */
    public void addUser(String userName, String hashedPassword) {
        String salt = getSalt();
        addUser(userName, hashedPassword, salt);
    }

    public void removeUser(String userName){
        try {
            deleteUser.setString(1,userName);
            deleteUser.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Changes the user's password without changing its original salt
     * @param userName
     * @param newHashedPassword
     */
    public void changeUserPassword(String userName,String newHashedPassword){

        try {
            // get the original salt
            ServerUser serverUser = getUser(userName);
            String salt = serverUser.getSalt();
            changeUserPassword.setString(1,saltPassword(newHashedPassword,salt));
            changeUserPassword.setString(2,userName);
            changeUserPassword.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Falied to change the user's password");
        }
    }

    public boolean verifyUserPassword(String userName, String hashedPassword) {
        ServerUser serverUser = getUser(userName);
        String salt = serverUser.getSalt();
        String saltedPassword = saltPassword(hashedPassword, salt);
        String realPassword = serverUser.getSaltedPassword();

        return saltedPassword.equals(realPassword);
    }

    public void grantPermission(String userName, Permission p){
        try {

            PreparedStatement grant = connection.prepareStatement(getGrantPermissionSQL(p));
            grant.setString(1,userName);
            grant.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void grantAllPermission(String userName){
        grantPermission(userName,Permission.CREATE_BILLBOARDS);
        grantPermission(userName,Permission.EDIT_ALL_BILLBOARDS);
        grantPermission(userName,Permission.EDIT_USERS);
        grantPermission(userName,Permission.SCHEDULE_BILLBOARDS);
    }

    public void revokePermission(String userName, Permission p){
        try {
            PreparedStatement revoke = connection.prepareStatement(getRevokePermissionSQL(p));
            revoke.setString(1,userName);
            revoke.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public String getGrantPermissionSQL(Permission p){
        return GRANT_PERMISSION.replace("permission",p.toString());
    }
    public String getRevokePermissionSQL(Permission p){
        return REVOKE_PERMISSION.replace("permission",p.toString());
    }

    /**
     * Salt a hashed password
     *
     * @param hashedPassword
     * @param salt
     * @return
     */
    private static String saltPassword(String hashedPassword, String salt) {
        return bytesToHexString(md.digest((hashedPassword + salt).getBytes()));
    }

    public ServerUser getUser(String userName) {
        try {
            getUser.setString(1, userName);
            ResultSet rs = getUser.executeQuery();
            rs.next();
            return new ServerUser(userName,
                    rs.getString("password"),
                    rs.getString("salt"),
                    rs.getBoolean("create_billboards"),
                    rs.getBoolean("edit_all_billboards"),
                    rs.getBoolean("schedule_billboards"),
                    rs.getBoolean("edit_users"));
        } catch (SQLException ignored) { }
        return null;
    }

    public BasicUser getBasicUser(String userName){
        try {
            getUser.setString(1, userName);
            ResultSet rs = getUser.executeQuery();
            rs.next();
            return new BasicUser(userName,
                    rs.getBoolean("create_billboards"),
                    rs.getBoolean("edit_all_billboards"),
                    rs.getBoolean("schedule_billboards"),
                    rs.getBoolean("edit_users"));
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public static String bytesToHexString(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b & 0xFF));
        }
        return sb.toString();
    }

    /**
     * Hash a string using SHA-256 algorithm
     * @param str
     * @return hex representation of the string
     */
    public static String hashString(String str) {
        return bytesToHexString(md.digest(str.getBytes()));
    }

    private static String getSalt() {
        Random rng = new Random();
        byte[] saltBytes = new byte[32];
        rng.nextBytes(saltBytes);
        String salt = bytesToHexString(saltBytes);
        return salt;
    }

    //---------------billboard table

    public void createBillboard(String billboardName, String billboardCreator, String xml){
        try {
            createBillboard.setString(1,billboardName);
            createBillboard.setString(2,billboardCreator);
            createBillboard.setString(3,xml);
            createBillboard.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to add a new billboard");
            System.out.println(e.getMessage());
        }
    }

    /**
     * Schedule a billboard
     * @param billboardName
     * @param startTime e.g "2020-05-26 16:20:00"
     * @param endTime
     */
    public void scheduleBillboard(String billboardName,String startTime, String endTime){
        try {
            scheduleBillboard.setString(1,billboardName);
            scheduleBillboard.setString(2,startTime);
            scheduleBillboard.setString(3,endTime);
            scheduleBillboard.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private final String NOTHING_TO_BE_DISPLAYED =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<billboard>\n" +
            "    <message>No billboard to be shown yet!</message>\n" +
            "</billboard>";


    /**
     * Gets the current displaying billboard's XML
     * @return
     */
    public String getCurrentBillboardXML(){
        try {
            ResultSet rs = currentBillboardXML.executeQuery();
            if( rs.next())
            {
                return rs.getString("billboardContent");
            }
            else
            {
                return NOTHING_TO_BE_DISPLAYED;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getBillboardXML(String billboardName){
        try {
            getBillboard.setString(1,billboardName);
            ResultSet rs = getBillboard.executeQuery();
            rs.next();
            return rs.getString("billboardContent");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) throws SQLException, IOException {
        BillboardDB db = new BillboardDB();
      db.addUser("xiaohai",hashString("123"));


    }
}
