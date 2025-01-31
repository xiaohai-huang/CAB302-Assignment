package Common;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.util.Properties;

public class ServerConnection {
    private static String propPath  = "./src/network.props";
    public static Socket getSocket() throws IOException {
        Properties props = new Properties();
        FileInputStream in = null;
        String ip = null;
        Integer port = null;
        try {
            in = new FileInputStream(propPath);
            props.load(in);
            in.close();

            // specify the data source, username and password
            ip = props.getProperty("ip");
            port = Integer.parseInt(props.getProperty("port"));


        } catch (FileNotFoundException e) {
            System.err.println(e);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return new Socket(ip, port);
    }

    public static int getPort() {
        Properties props = new Properties();
        FileInputStream in = null;
        Integer port = null;
        try {
            in = new FileInputStream(propPath);
            props.load(in);
            in.close();

            port = Integer.parseInt(props.getProperty("port"));

        } catch (FileNotFoundException e) {
            System.err.println(e);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return port;
    }

    public static void main(String[] args) throws IOException {
        Socket socket = getSocket();

    }
}
