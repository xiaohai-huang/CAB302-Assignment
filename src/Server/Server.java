package Server;

import ControlPanel.BasicUser;
import Viewer.ServerConnection;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Random;

import static Server.BillboardDB.bytesToHexString;

public class Server {

    /**
     * Token -> UserName
     */
    private static HashMap<String, String> validSessions = new HashMap<>();


    public static void main(String[] args) throws SQLException, IOException {


        int port = ServerConnection.getPort();
        ServerSocket serverSocket = new ServerSocket(port);

        while (true) {
            // connects to the database before start communicate with clients
            BillboardDB db = null;
            try {
                db = new BillboardDB();
            } catch (SQLException | IOException e) {
                System.out.println("Cannot connect to Billboard database!");
                System.exit(-1);
            }

            Socket socket = serverSocket.accept();

            InputStream inputStream = socket.getInputStream();

            ObjectInputStream ois = new ObjectInputStream(inputStream);

            OutputStream outputStream = socket.getOutputStream();

            ObjectOutputStream oos = new ObjectOutputStream(outputStream);


            Request request = null;
            try {
                request = (Request) ois.readObject();
            } catch (ClassNotFoundException | ClassCastException e) {
                sendError(oos, e.getMessage());
            }

            // handle viewer request
            assert request != null;
            try {


                switch (request.getRequestType()) {
                    case REQUEST_CURRENTLY_SHOWING_BILLBOARD: {
                        String xml = db.getCurrentBillboardXML();
                        Response billboardXML = new Response(Response.ResponseType.SUCCESS, xml);
                        sendResponse(oos, billboardXML);
                        break;
                    }

                    case LOGIN: {
                        HashMap<String, String> userInfo = (HashMap<String, String>) request.getContent();
                        String userName = userInfo.get("userName");
                        String hashedPassword = userInfo.get("hashedPassword");
                        boolean valid = false;
                        try {
                            valid = db.verifyUserPassword(userName, hashedPassword);
                        } catch (NullPointerException e) {
                            Response response = new Response(Response.ResponseType.ERROR, "No such user");
                            oos.writeObject(response);
                            oos.flush();
                        }
                        if (valid) {
                            String token = createToken();
                            validSessions.put(token, userName);
                            Response response = new Response(Response.ResponseType.SUCCESS, token);
                            oos.writeObject(response);
                        } else {
                            Response response = new Response(Response.ResponseType.FAIL);
                            oos.writeObject(response);
                        }
                        oos.flush();
                        break;
                    }
                    case GET_CURRENT_OPERATOR: {
                        String token = (String) request.getToken();
                        String userName = validSessions.get(token);
                        BasicUser user = db.getBasicUser(userName);
                        Response response = new Response(Response.ResponseType.SUCCESS,user);
                        oos.writeObject(response);
                        oos.flush();
                        break;
                    }
                }


            } catch (Exception e) {
                System.out.println("Some error occur");
                System.out.println(e.getMessage());
            }

            // close all connections
            ois.close();
            oos.close();
            socket.close();

        }


    }

    private static String createToken() {
        Random rng = new Random();
        byte[] saltBytes = new byte[32];
        rng.nextBytes(saltBytes);
        String token = bytesToHexString(saltBytes);
        return token;
    }

    private static void sendError(ObjectOutputStream oos, String reason) {
        Response errorResponse = new Response(Response.ResponseType.ERROR, reason);
        try {
            oos.writeObject(errorResponse);
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void sendResponse(ObjectOutputStream oos, Response response) {
        try {
            oos.writeObject(response);
            oos.flush();
        } catch (IOException e) {
            sendError(oos, e.getMessage());
        }
    }


}
