package ControlPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginView extends JFrame{

    private int WIDTH = 320;

    private int HEIGHT = 220;

    private JButton loginBtn;
    private JButton exitBtn;
    private JTextField username;
    private JTextField password;

    public LoginView(){
        super("Billboard Control Panel");
        this.setLayout(new BorderLayout());
        this.setSize(new Dimension(WIDTH,HEIGHT));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        loginBtn = new JButton("Login");
        exitBtn = new JButton("Exit");

        JLabel welcome = new JLabel("Welcome to control panel login page!",SwingConstants.CENTER);
        this.getContentPane().add(welcome,BorderLayout.NORTH);
        this.getContentPane().add(textPanel(),BorderLayout.CENTER);
        this.getContentPane().add(btnPanel(),BorderLayout.SOUTH);

        this.setLocationRelativeTo(null);
        this.getRootPane().setDefaultButton(loginBtn);// bind enter with login button
    }

    private JPanel btnPanel(){
        JPanel panel = new JPanel();
        loginBtn = new JButton("Login");
        panel.add(loginBtn);
        panel.add(exitBtn);
        return panel;
    }

    private JPanel textPanel(){
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(-1,1));
        username = new JTextField(10);
        password = new JPasswordField(10);

        panel.add(new JLabel("Username"));
        panel.add(username);
        panel.add(new JLabel("Password"));
        panel.add(password);
        panel.setBorder(new EmptyBorder(10,10,10,10));

        return panel;
    }

    /**
     * Display authentication success / failed dialog box based on the boolean input
     * @param flag
     */
    public void displayDialog(boolean flag){
        if (!flag) {
            JOptionPane.showMessageDialog(this, "Authentication failed!",
                    "Fail to login", JOptionPane.ERROR_MESSAGE);

        } else {
            JOptionPane.showMessageDialog(this, "Authentication success!");
        }
    }

    public String getUserName(){
        return username.getText();
    }

    public String getPassword(){
        return password.getText();
    }
    public JButton getLoginBtn(){
        return loginBtn;
    }
    public JButton getExitBtn() {
        return exitBtn;
    }

    public static void main(String[] args) {
        new LoginView();
    }
}
