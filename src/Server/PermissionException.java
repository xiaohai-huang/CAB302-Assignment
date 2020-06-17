package Server;


public class PermissionException extends RuntimeException {
    public PermissionException() {
        super("You don't have enough permission!");
    }
}
