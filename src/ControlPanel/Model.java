package ControlPanel;

import Common.*;
import Server.*;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class Model {

    private String token;


    /**
     * Sends the request and retrieve the response, if the token is expired it would thorw InvalidTokenException
     * else if the communication is time out it would throw CannotCommunicateWithServerException
     *
     * @param request
     * @return a response
     */
    private Response sendRequestAndGetResponse(Request request) {
        try {
            Socket socket = ServerConnection.getSocket();
            OutputStream outputStream = socket.getOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(request);
            objectOutputStream.flush();

            // get response
            InputStream inputStream = socket.getInputStream();
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
            Response response = (Response) objectInputStream.readObject();

            // throw invalid token exception
            if (response.getResponseType() == Response.ResponseType.INVALID_TOKEN) {
                throw new InvalidTokenException();
            }

            objectOutputStream.close();
            objectInputStream.close();
            socket.close();
            return response;
        } catch (ClassNotFoundException | IOException e) {
            throw new CannotCommunicateWithServerException();
        }
    }

    public boolean verifyPassword(String userName, String password){
        if(userName==null){
            userName = "";
        }
        if(password==null){
            password="";
        }
        HashMap<String,String> content = new HashMap<>();
        content.put("userName", userName);
        content.put("hashedPassword", BillboardDB.hashString(password));

        // communicate with server
        Request loginRequest = new Request(Request.RequestType.LOGIN);
        loginRequest.setContent(content);

        Response response = sendRequestAndGetResponse(loginRequest);
        boolean success = response.getResponseType()== Response.ResponseType.SUCCESS;

        if (success) {
            token = (String) response.getResponseContent();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Get a single billboard xml
     *
     * @param billboardName
     * @return billboard contents (xml)
     */
    public String getBillboardContents(String billboardName) {
        Request request = new Request(Request.RequestType.GET_BILLBOARD_INFORMATION, token);
        request.setContent(billboardName);
        Response response = sendRequestAndGetResponse(request);


        return (String) response.getResponseContent();
    }

    public void createEditBillboard(String boardName, String contents) {
        Request request = new Request(Request.RequestType.CREATE_EDIT_BILLBOARD, token);
        request.setContent(new String[]{boardName, contents});
        Response response = sendRequestAndGetResponse(request);
        if (response.getResponseType() == Response.ResponseType.FAIL) {
            throw new PermissionException();
        }
    }

    public boolean deleteBillboard(String billboardName) {
        Request request = new Request(Request.RequestType.DELETE_BILLBOARD, token);
        request.setContent(billboardName);
        Response response = sendRequestAndGetResponse(request);
        return response.getResponseType() == Response.ResponseType.SUCCESS;

    }

    /**
     * All users will be able to access a list of all billboards on the system and preview their contents
     *
     * @return a 2d array of billboard information
     */
    public String[][] getBillboards() {
        Request request = new Request(Request.RequestType.LIST_BILLBOARDS, token);
        Response response = sendRequestAndGetResponse(request);
        if (response.getResponseType() == Response.ResponseType.SUCCESS) {
            String data[][] = (String[][]) response.getResponseContent();
            return data;
        } else if (response.getResponseType() == Response.ResponseType.INVALID_TOKEN) {
            throw new InvalidTokenException();
        }
        // todo handle exception may generated by string[0][2] in db
        return null;

    }

    public ArrayList<Permission> getPermissions(String userName) {
        Request request = new Request(Request.RequestType.GET_USER_PERMISSIONS, token);
        request.setContent(userName);
        Response response = sendRequestAndGetResponse(request);
        if (response.getResponseType() == Response.ResponseType.SUCCESS) {
            return (ArrayList<Permission>) response.getResponseContent();
        }
        else {
            return null;
        }
    }

    public String logout(){
        Request request = new Request(Request.RequestType.LOG_OUT,token);
        Response response = sendRequestAndGetResponse(request);
        if (response.getResponseType() == Response.ResponseType.SUCCESS) {
            return (String) response.getResponseContent();
        } else if (response.getResponseType() == Response.ResponseType.INVALID_TOKEN) {

            throw new InvalidTokenException();
        }
        return null;
    }


}
