package ControlPanel;

import Server.BillboardDB;
import Server.CannotCommunicateWithServerException;
import Server.Request;
import Server.Response;
import Viewer.ServerConnection;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class Model {

    private String token;


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

        if(success){
            token = (String) response.getResponseContent();
            return true;
        }
        else
        {
            return false;
        }
    }

    public String getBillboardContents(String billboardName){
        Request request = new Request(Request.RequestType.GET_BILLBOARD_INFORMATION,token);
        request.setContent(billboardName);
        Response response = sendRequestAndGetResponse(request);

        return (String) response.getResponseContent();
    }

    public void createEditBillboard(String boardName, String contents){
        Request request = new Request(Request.RequestType.CREATE_EDIT_BILLBOARD,token);
        request.setContent(new String[]{boardName,contents});
        Response response = sendRequestAndGetResponse(request);

    }


    public ArrayList<Permission> getPermissions(String userName){
        Request request = new Request(Request.RequestType.GET_USER_PERMISSIONS,token);
        request.setContent(userName);
        Response response = sendRequestAndGetResponse(request);
        if(response.getResponseType()== Response.ResponseType.SUCCESS){
            return (ArrayList<Permission>) response.getResponseContent();
        }
        else {
            return null;
        }
    }

    public String logout(){
        Request request = new Request(Request.RequestType.LOG_OUT,token);
        Response response = sendRequestAndGetResponse(request);
        if(response.getResponseType()== Response.ResponseType.SUCCESS){
            return (String) response.getResponseContent();
        }
        else
        {
            return "Fail to logout";
        }
    }

    public static String createBillboardXML(String billboardColour,
                                      String msg, String msgColour,
                                      String pic, String picType,
                                      String info, String infoColour) {
        String template =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<billboard bg>\n" +
                        "msg\n" +
                        "pic\n" +
                        "info\n" +
                        "</billboard>";
        String msgTag = "";
        String picTag = "";
        String infoTag = "";

        // replace bg colour
        if (!billboardColour.isBlank()) {
            template = template.replace("bg", "background=\"" + billboardColour + "\"");
        }


        if (!msg.isBlank()) {
            msgTag = createXMLTag("message", "colour", msgColour, msg);
        }
        if (!pic.isBlank()) {
            if (picType.toLowerCase().equals("url")) {// handle cases
                picTag = String.format("<picture url=\"%s\" />", pic);
            } else {
                picTag = String.format("<picture data=\"%s\" />", pic);
            }
        }
        if (!info.isBlank()) {
            infoTag = createXMLTag("information", "colour", infoColour, info);
        }

        template = template.replace("bg", "");
        template = template.replace("msg", msgTag);
        template = template.replace("pic", picTag);
        template = template.replace("info", infoTag);

        return template;
    }

    private static String createXMLTag(String tagName,
                                       String attributeName, String attributeValue,
                                       String text) {
        String xml = "";
        if (!attributeValue.isBlank()) {
            xml = String.format("<%s %s=\"%s\">%s</%s>", tagName, attributeName, attributeValue, text, tagName);
        } else {
            xml = String.format("<%s>%s</%s>", tagName, text, tagName);
        }
        return xml;
    }
}
