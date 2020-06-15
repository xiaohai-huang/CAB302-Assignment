package Server;

public class TokenExpireException extends RuntimeException {
    public TokenExpireException() {
        super("The token has expired!" +
                " Please Login again!");
    }
}
