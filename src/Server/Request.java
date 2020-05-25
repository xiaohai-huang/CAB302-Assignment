package Server;

import java.io.Serializable;

public class Request implements Serializable {
    private final RequestType requestType;


    public enum RequestType{
        REQUEST_CURRENTLY_SHOWING_BILLBOARD,

    }
    public Request(RequestType type){
        this.requestType = type;
    }


    public RequestType getRequestType() {
        return requestType;
    }
}
