package ControlPanel;

import java.io.Serializable;
import java.util.HashMap;

public class BasicUser implements Serializable {

    private final String userName;

    protected HashMap<Permission, Boolean> permissions = new HashMap<Permission, Boolean>();

    public BasicUser(String userName) {
        this.userName = userName;
        permissions.put(Permission.CREATE_BILLBOARDS, false);
        permissions.put(Permission.EDIT_USERS, false);
        permissions.put(Permission.SCHEDULE_BILLBOARDS, false);
        permissions.put(Permission.EDIT_ALL_BILLBOARDS, false);
    }

    public BasicUser(String userName,
                     boolean createBillboard,
                     boolean editUser,
                     boolean scheduleBillboard,
                     boolean editAllBillboard) {
        this.userName = userName;
        permissions.put(Permission.CREATE_BILLBOARDS, createBillboard);
        permissions.put(Permission.EDIT_USERS, editUser);
        permissions.put(Permission.SCHEDULE_BILLBOARDS, scheduleBillboard);
        permissions.put(Permission.EDIT_ALL_BILLBOARDS, editAllBillboard);
    }

    public Boolean hasPermission(Permission p) {
        return permissions.get(p);
    }

    public HashMap<Permission, Boolean> getPermissions() {
        return permissions;
    }

    public String getUserName() {
        return userName;
    }

}
