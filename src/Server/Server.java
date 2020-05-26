package Server;

import Viewer.ServerConnection;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class Server {



    public static void main(String[] args) throws SQLException, IOException {




        int port = ServerConnection.getPort();
        ServerSocket serverSocket = new ServerSocket(port);

        while (true){
            Socket socket = serverSocket.accept();

            InputStream inputStream = socket.getInputStream();

            ObjectInputStream ois = new ObjectInputStream(inputStream);

            OutputStream outputStream = socket.getOutputStream();

            ObjectOutputStream oos = new ObjectOutputStream(outputStream);

            BillboardDB db = new BillboardDB();

            Request request = null;
            try {
               request = (Request)ois.readObject();
            } catch (ClassNotFoundException | ClassCastException e) {
                sendError(oos,e.getMessage());
            }

            // handle viewer request
            assert request != null;
            if(request.getRequestType()== Request.RequestType.REQUEST_CURRENTLY_SHOWING_BILLBOARD)
            {
                String xml = db.getCurrentBillboardXML();
                Response billboardXML = new Response(Response.ResponseType.SUCCESS,xml);
                sendResponse(oos,billboardXML);
            }


            // close all connections
            ois.close();
            oos.close();
            socket.close();

        }



    }

    private static void sendError(ObjectOutputStream oos, String reason){
            Response errorResponse = new Response(Response.ResponseType.ERROR,reason);
        try {
            oos.writeObject(errorResponse);
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void sendResponse(ObjectOutputStream oos, Object content){
        try {
            oos.writeObject(content);
        } catch (IOException e) {
            sendError(oos,e.getMessage());
        }
    }


}
