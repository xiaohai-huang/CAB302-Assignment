package Server;

public class Response {
    private final ResponseType responseType;
    private Object responseContent;


    public enum ResponseType{
        SUCCESS,
        FAIL,//include authentication failure
        ERROR
    }
    public Response(ResponseType type, Object responseContent){
        this.responseType = type;
        this.responseContent = responseContent;
    }

    public ResponseType getResponseType() {
        return responseType;
    }

    public Object getResponseContent(){
        return responseContent;
    }
}
