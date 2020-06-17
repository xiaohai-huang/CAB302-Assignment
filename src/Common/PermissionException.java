package Common;


public class PermissionException extends RuntimeException {
    public PermissionException() {
        super("You don't have enough permission!");
    }
}
