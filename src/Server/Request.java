package Server;

import java.io.Serializable;

public class Request implements Serializable {
    private final RequestType requestType;
    private final String token;

    private Object content;

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }

    public String getToken() {
        return token;
    }

    public enum RequestType {
        REQUEST_CURRENTLY_SHOWING_BILLBOARD,
        LOGIN,
        LOG_OUT,
        GET_CURRENT_OPERATOR,
        CREATE_EDIT_BILLBOARD,
        GET_USER_PERMISSIONS,
        GET_BILLBOARD_INFORMATION,
        LIST_BILLBOARDS,
        DELETE_BILLBOARD
    }

    public Request(RequestType type){
        this.requestType = type;
        token = null;
    }

    public Request(RequestType type, String token){
        this.requestType = type;
        this.token = token;
    }


    public RequestType getRequestType() {
        return requestType;
    }
}
