package Common;

public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException() {
        super("The token is invalid!" +
                " Please Login again!");
    }
}
