package ControlPanel;

import java.util.HashMap;

public class User {

    private final String userName;
    private String saltedPassword;
    private String salt;
    private HashMap<Permission,Boolean> permissions;

    public User(String userName)
    {
        this.userName = userName;
        permissions = new HashMap<Permission, Boolean>();
        permissions.put(Permission.CREATE_BILLBOARDS,false);
        permissions.put(Permission.EDIT_USERS,false);
        permissions.put(Permission.SCHEDULE_BILLBOARDS,false);
        permissions.put(Permission.EDIT_ALL_BILLBOARDS,false);
    }

    public User(String userName, String saltedPassword, String salt,
                boolean createBillboard,
                boolean editUser,
                boolean scheduleBillboard,
                boolean editAllBillboard ){

        this.userName = userName;
        this.saltedPassword = saltedPassword;
        this.salt = salt;
        permissions = new HashMap<Permission, Boolean>();
        permissions.put(Permission.CREATE_BILLBOARDS,createBillboard);
        permissions.put(Permission.EDIT_USERS,editUser);
        permissions.put(Permission.SCHEDULE_BILLBOARDS,scheduleBillboard);
        permissions.put(Permission.EDIT_ALL_BILLBOARDS,editAllBillboard);
    }

    public void changePassword(String newPassword){
        // todo: ask server for changing password
    }

    public Boolean hasPermission(Permission p){
        return permissions.get(p);
    }

    public HashMap<Permission,Boolean> getPermissions(){
        return permissions;
    }

    public void setPermissions(HashMap<Permission,Boolean> permissions){
        this.permissions = permissions;
    }

    public String getUserName(){
        return userName;
    }

    public String getSaltedPassword() {
        return saltedPassword;
    }

    public String getSalt() {
        return salt;
    }
}
