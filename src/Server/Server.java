package Server;

import ControlPanel.BasicUser;
import ControlPanel.Permission;
import ControlPanel.ServerUser;
import Viewer.ServerConnection;

import javax.swing.text.StyledEditorKit;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.*;

import static Server.BillboardDB.bytesToHexString;

public class Server {

    /**
     * Token -> UserName
     */
    private static HashMap<String, String> validSessions = new HashMap<>();
    // 86400000
    private static final long DAY_IN_MILLISECONDS = 86400000;// to be changed

    public static void main(String[] args) throws IOException {


        int port = ServerConnection.getPort();
        ServerSocket serverSocket = new ServerSocket(port);

        while (true) {
            // connects to the database before starting to communicate with clients
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


            Request request;
            try {
                request = (Request) ois.readObject();
            } catch (ClassNotFoundException | ClassCastException | InvalidObjectException e) {
                sendError(oos, e.getMessage());
                continue;
            }

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
                            System.out.println("Created a token for user: " + validSessions.get(token));
                            Response response = new Response(Response.ResponseType.SUCCESS, token);
                            oos.writeObject(response);
                            // schedule to expire the token after 24 hours
                            Timer timer = new Timer();
                            timer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    if (validSessions.containsKey(token)) {
                                        System.out.println("Expired a token of user: " + userName);
                                        validSessions.remove(token);
                                    }
                                }
                            }, DAY_IN_MILLISECONDS);
                        } else {
                            Response response = new Response(Response.ResponseType.FAIL);
                            oos.writeObject(response);
                        }
                        oos.flush();
                        break;
                    }
                    case GET_BILLBOARD_INFORMATION: {
                        /*
                         the Control Panel will send the Server a billboard’s name and a valid session token.
                         The Server will then send the billboard’s contents.
                         (Permissions required: none.)
                         */
                        String token = request.getToken();
                        if (!validSessions.containsKey(token)) {
                            Response response = new Response(Response.ResponseType.INVALID_TOKEN);
                            sendResponse(oos, response);
                            break;
                        }
                        String billboardName = (String) request.getContent();
                        String xml = db.getBillboardXML(billboardName);
                        Response response = new Response(Response.ResponseType.SUCCESS, xml);
                        sendResponse(oos, response);
                        break;
                    }
                    case GET_CURRENT_OPERATOR: {
                        String token = request.getToken();
                        if (!validSessions.containsKey(token)) {
                            Response response = new Response(Response.ResponseType.INVALID_TOKEN);
                            sendResponse(oos, response);
                            break;
                        }
                        String userName = validSessions.get(token);
                        BasicUser user = db.getBasicUser(userName);
                        Response response = new Response(Response.ResponseType.SUCCESS, user);
                        sendResponse(oos, response);
                        break;
                    }
                    case GET_USER_PERMISSIONS: {
                        /*
                         the Control Panel will send the Server a username and valid session token.
                         The Server will respond with a list of that user’s permissions.
                         (Permissions required: if a user is requesting their own details, none.
                         To get details for other users, “Edit Users” permission is required.)
                         */
                        String token = request.getToken();
                        if (!validSessions.containsKey(token)) {
                            Response response = new Response(Response.ResponseType.INVALID_TOKEN);
                            sendResponse(oos, response);
                            break;
                        }
                        String requestedUserName = (String) request.getContent();
                        String operatorName = validSessions.get(token);
                        BasicUser operator = db.getBasicUser(operatorName);

                        Response response;
                        // check if the user is requesting its own details or it has Edit Users Permission
                        if (requestedUserName.equals(operatorName) || operator.hasPermission(Permission.EDIT_USERS)) {
                            BasicUser requestedUser = db.getBasicUser(requestedUserName);
                            var permissions = requestedUser.getPermissions();
                            // convert permissions from hashmap into arraylist
                            ArrayList<Permission> userPermissions = new ArrayList<>();
                            for (Permission p : permissions.keySet()) {
                                if (permissions.get(p)) {
                                    userPermissions.add(p);
                                }
                            }

                            response = new Response(Response.ResponseType.SUCCESS, userPermissions);
                        } else {
                            response = new Response(Response.ResponseType.FAIL, null);
                        }
                        sendResponse(oos, response);
                        break;
                    }
                    case CREATE_EDIT_BILLBOARD: {
                        String token = request.getToken();
                        if (!validSessions.containsKey(token)) {
                            Response response = new Response(Response.ResponseType.INVALID_TOKEN);
                            sendResponse(oos, response);
                            break;
                        }
                        // parse the content sent by the client
                        String[] name_contents = (String[]) request.getContent();

                        ServerUser user = db.getUser(validSessions.get(token));

                        // either create a new one or replace the old one
                        if (user.hasPermission(Permission.CREATE_BILLBOARDS)) {
                            if (db.hasBillboard(name_contents[0])) {// edit billboard

                                // if the user is the billboard owner and billboard is not currently scheduled
                                if (db.getBillboardCreatorName(name_contents[0]).equals(user.getUserName())
                                        && !db.getCurrentBillboardName().equals(name_contents[0])) {
                                    db.updateBillboard(name_contents[0], name_contents[1]);
                                    Response success = new Response(Response.ResponseType.SUCCESS);
                                    oos.writeObject(success);
                                    oos.flush();
                                } else if (user.hasPermission(Permission.EDIT_ALL_BILLBOARDS)) {
                                    //  To edit another user’s billboard or edit a billboard that is currently scheduled,
                                    //  must have “Edit All Billboards” permission
                                    db.updateBillboard(name_contents[0], name_contents[1]);
                                    Response success = new Response(Response.ResponseType.SUCCESS);
                                    oos.writeObject(success);
                                    oos.flush();
                                } else {
                                    Response fail = new Response(Response.ResponseType.FAIL);
                                    oos.writeObject(fail);
                                    oos.flush();
                                }
                            } else {// create a new one
                                db.createBillboard(name_contents[0], user.getUserName(), name_contents[1]);
                                Response success = new Response(Response.ResponseType.SUCCESS);
                                oos.writeObject(success);
                                oos.flush();
                            }
                        } else {
                            Response response = new Response(Response.ResponseType.FAIL, "You don't have " +
                                    "EDIT BILLBOARD permission!");
                            sendResponse(oos, response);
                        }

                        break;
                    }
                    case DELETE_BILLBOARD: {
                        /*
                        Delete billboard: the Control Panel will send the Server a billboard’s name
                        and a valid session token.
                        The Server will then delete the billboard
                        and any scheduled showings of it and send back an acknowledgement.
                        (Permissions required: if deleting own billboard and
                        that billboard is not currently scheduled, must have “Create Billboards” permission.
                        To delete any other billboards, including those currently scheduled,
                        must have “Edit All Billboards” permission.)
                        */
                        String token = request.getToken();
                        if (!validSessions.containsKey(token)) {
                            Response response = new Response(Response.ResponseType.INVALID_TOKEN);
                            sendResponse(oos, response);
                            break;
                        }
                        String boardName = (String) request.getContent();
                        BasicUser operator = db.getBasicUser(validSessions.get(token));
                        String[][] billboards = db.getAllBillboards();

                        boolean isOwner = false;
                        boolean isScheduled = false;
                        // check the ownership
                        for (String[] row : billboards) {
                            if (row[0].equals(boardName) && row[1].equals(operator.getUserName())) {
                                isOwner = true;
                                break;
                            }
                        }
                        // check whether the billboard is currently scheduled
                        String currentBillboardName = db.getCurrentBillboardName();
                        if (currentBillboardName.equals(boardName)) {
                            isScheduled = true;
                        }

                        if (operator.hasPermission(Permission.EDIT_ALL_BILLBOARDS)) {
                            db.deleteBillboard(boardName);
                            Response response = new Response(Response.ResponseType.SUCCESS,
                                    "Successfully deleted the billboard!");
                            sendResponse(oos, response);
                        } else if (isOwner && !isScheduled && operator.hasPermission(Permission.CREATE_BILLBOARDS)) {
                            db.deleteBillboard(boardName);
                            Response response = new Response(Response.ResponseType.SUCCESS,
                                    "Successfully deleted the billboard!");
                            sendResponse(oos, response);
                        } else {
                            Response response = new Response(Response.ResponseType.FAIL,
                                    "Fail to delete the billboard!");
                            sendResponse(oos, response);
                        }
                        break;
                    }
                    case LIST_BILLBOARDS: {
                        /*
                         the Control Panel will send the Server a valid session token
                         and the Server will respond with a list of billboards
                         (including the name and creator of each,
                         as well as any other information your team feels is appropriate.
                         (Permissions required: none.)
                         */
                        String token = request.getToken();
                        if (!validSessions.containsKey(token)) {
                            Response response = new Response(Response.ResponseType.INVALID_TOKEN);
                            sendResponse(oos, response);
                            break;
                        }
                        Response response = new Response(Response.ResponseType.SUCCESS, db.getAllBillboards());
                        sendResponse(oos, response);
                        break;
                    }
                    case LOG_OUT: {
                        String token = request.getToken();
                        if (!validSessions.containsKey(token)) {
                            Response response = new Response(Response.ResponseType.INVALID_TOKEN);
                            sendResponse(oos, response);
                            break;
                        }
                        // expire the token
                        if (validSessions.containsKey(token)) {
                            System.out.println("Expired a token of user: " + validSessions.get(token));
                            validSessions.remove(token);
                        }
                        Response response = new Response(Response.ResponseType.SUCCESS,
                                "Successfully log out!");
                        sendResponse(oos, response);
                        break;
                    }
                }


            } catch (Exception e) {
                System.out.println("Some error occurred switch!");
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
