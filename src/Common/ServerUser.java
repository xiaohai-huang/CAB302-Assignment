package Common;

import java.util.HashMap;

public class ServerUser extends BasicUser {

    private String saltedPassword;
    private String salt;

    public ServerUser(String userName)
    {
        super(userName);
    }

    public ServerUser(String userName, String saltedPassword, String salt,
                      boolean createBillboard,
                      boolean editUser,
                      boolean scheduleBillboard,
                      boolean editAllBillboard ){
        super(userName);

        this.saltedPassword = saltedPassword;
        this.salt = salt;
        setPermission(Permission.CREATE_BILLBOARDS,createBillboard);
        setPermission(Permission.EDIT_USERS,editUser);
        setPermission(Permission.SCHEDULE_BILLBOARDS,scheduleBillboard);
        setPermission(Permission.EDIT_ALL_BILLBOARDS,editAllBillboard);
    }

    public void changePassword(String newPassword){
        // todo: ask server for changing password
    }



    public void setPermissions(HashMap<Permission,Boolean> permissions){
        this.permissions = permissions;
    }

    public void setPermission(Permission p, boolean flag){
        permissions.put(p,flag);
    }

    public void grantPermission(Permission p){
        permissions.put(p,true);
    }

    public void revokePermission(Permission p){
        permissions.put(p,false);
    }


    public String getSaltedPassword() {
        return saltedPassword;
    }

    public String getSalt() {
        return salt;
    }
}
