package ControlPanel;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

public class ControlPanel extends JFrame {

    private static final int WIDTH = 800;

    private static final int HEIGHT = 600;


    public ControlPanel() {
        super("Billboard Control Panel");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        showLoginScreen();
        showMainScreen();
    }


    private void showLoginScreen() {
        JTextField username = new JTextField();
        JTextField password = new JPasswordField();
        Object[] message = {
                "Username:", username,
                "Password:", password
        };

        int option = JOptionPane.showConfirmDialog(this, message,
                "Billboard Control Panel Login", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String name = username.getText();
            String word = password.getText();
            // verify the userName and password
            if (!verify(name, word)) {
                JOptionPane.showMessageDialog(this, "Authentication failed!",
                        "Fail to login", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            } else {
                JOptionPane.showMessageDialog(this, "Authentication success!");
            }
        } else {// press cancel or close the window
            System.exit(1);
        }
    }

    private void showMainScreen() {

        this.setSize(new Dimension(WIDTH, HEIGHT));
        this.setLocationRelativeTo(null);

        this.setVisible(true);
    }

    private UM getUsers() {
        // todo: connect to server to get users
        HashMap<String, User> users = new HashMap<String, User>();
        User u = new User("xiaohai");
        users.put(u.getUserName(), u);

        return new UM(users);
    }

    private Boolean verify(String userName, String password) {
        // todo: connect to server
        return true;
    }


    public static void main(String[] args) {
        String password = "xiaohai520";
        System.out.println(password.hashCode());
        //new ControlPanel();
    }
}
