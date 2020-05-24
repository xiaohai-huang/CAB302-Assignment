package Server;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class Server {
    private static final String CREATE_USER_TABLE =
            "CREATE TABLE IF NOT EXISTS user ("
                + "userName VARCHAR(30) UNIQUE NOT NULL,"
                + "password CHAR(128) NOT NULL"
                + "PRIMARY KEY (userName) );";


    public static void main(String[] args) throws SQLException {
        Connection c = DBConnection.getInstance();

        Statement st = c.createStatement();

        c.close();
    }
}
