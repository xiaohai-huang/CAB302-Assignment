package ControlPanel;

import java.util.HashMap;

public class User {

    private final String userName;
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

}
