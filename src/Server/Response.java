package Server;

import java.io.Serializable;

public class Response implements Serializable {
    private final ResponseType responseType;
    private Object responseContent;


    public enum ResponseType {
        SUCCESS,
        FAIL,//include authentication failure
        ERROR,
        INVALID_TOKEN
    }
    public Response(ResponseType type, Object responseContent){
        this.responseType = type;
        this.responseContent = responseContent;
    }
    public Response(ResponseType type){
        this.responseType = type;
    }

    public ResponseType getResponseType() {
        return responseType;
    }

    public Object getResponseContent(){
        return responseContent;
    }
}
