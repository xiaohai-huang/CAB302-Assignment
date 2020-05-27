package ControlPanel;

import java.util.HashMap;

// User Management
public class UM {
    private HashMap<String, ServerUser> users;
    private ServerUser currentOperator;

    public UM(HashMap<String, ServerUser> users){
        this.users = users;
    }

    public void setCurrentOperator(ServerUser operator){
        currentOperator = operator;
    }

    public void setCurrentOperator(String userName){
        currentOperator = users.get(userName);
    }

    public ServerUser getUser(String userName){
        return users.get(userName);
    }


    public HashMap<String, ServerUser> getUsers(){
        return users;
    }

    public boolean deleteUser(String userName){
        // todo: adds more constraints
        users.remove(userName);
        return true;
    }

    public Boolean grantPermission(String userName,Permission p){
        // todo: more constraints
        ServerUser u = getUser(userName);
        var permissions = u.getPermissions();
        permissions.put(p,true);

        return true;
    }

    public Boolean revokePermission(String userName,Permission p){
        // todo: more constraints
        ServerUser u = getUser(userName);
        var permissions = u.getPermissions();
        permissions.put(p,false);

        return true;
    }


}
