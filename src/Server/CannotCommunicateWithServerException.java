package Server;

public class CannotCommunicateWithServerException extends RuntimeException {

    public CannotCommunicateWithServerException(){
        super("Cannot communicate with the billboard server!");
    }
}
