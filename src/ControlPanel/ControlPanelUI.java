package ControlPanel;

import javax.swing.*;
import java.awt.*;

public class ControlPanelUI extends JFrame {
    private JTabbedPane mainTabPanel;
    private JButton importXMLButton;
    private JButton exportXMLButton;
    private JButton previewButton;
    private JButton addImageButton;
    private JButton addTextButton;
    private JButton setColorButton;
    private JButton previewButton1;
    private JButton editButton;
    private JButton deleteButton;
    private JList billboardList;
    private JComboBox comboBox1;
    private JButton scheduleButton;
    private JButton viewUsersButton;
    private JButton createUserButton;
    private JButton modifyUsersButton;
    private JButton deleteUserButton;
    private JButton changePasswordButton;

    private static final int WIDTH = 800;

    private static final int HEIGHT = 600;

    public ControlPanelUI(){
        super("Billboard Control Panel");
        showLoginScreen();
        showMainScreen();
    }


    public void showMainScreen(){
        // setup main screen
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);// temporary, add close socket connection later
        var schedulePanel = (JPanel)mainTabPanel.getComponentAt(2);
        schedulePanel.add(createWeekCalendarPanel(),BorderLayout.NORTH);




        this.getContentPane().add(mainTabPanel);
        this.setSize(new Dimension(WIDTH, HEIGHT));
        this.setLocationRelativeTo(null);

        this.setVisible(true);
    }

    private JPanel createWeekCalendarPanel(){
        String[] DAY_NAMES = {"Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"};
        // todo: get correct data source

        String[][] data = {
                {"Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"},
                {"xiaohai","ye","nice","Good","wonder woman","Avengers","Thor"},
                {"xiaohai","ye","nice","Good","wonder woman","Avengers","Thor"},
                {"xiaohai","ye","nice","Good","wonder woman","Avengers","Thor"},
                {"xiaohai","ye","nice","Good","wonder woman","Avengers","Thor"},
                {"xiaohai","ye","nice","Good","wonder woman","Avengers","Thor"},
                {"xiaohai","ye","nice","Good","wonder woman","Avengers","Thor"},
                {"xiaohai","ye","nice","Good","wonder woman","Avengers","Thor"},
        };
        JTable weekCalendar = new JTable(data,DAY_NAMES);
        JPanel weekCalendarPanel = new JPanel();
        weekCalendarPanel.setLayout(new BorderLayout());
        weekCalendarPanel.add(weekCalendar,BorderLayout.NORTH);

        return weekCalendarPanel;
    }



    public static void main(String[] args){
       var ui = new ControlPanelUI();
    }


    private void showLoginScreen() {
        JTextField username = new JTextField();
        JTextField password = new JPasswordField();
        Object[] message = {
                "Username:", username,
                "Password:", password
        };

        int option = JOptionPane.showConfirmDialog(null, message,
                "Billboard Control Panel Login", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String name = username.getText();
            String word = password.getText();
            // verify the userName and password
            if (!verify(name, word)) {
                JOptionPane.showMessageDialog(null, "Authentication failed!",
                        "Fail to login", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            } else {
                JOptionPane.showMessageDialog(null, "Authentication success!");
            }
        } else {// press cancel or close the window
            System.exit(1);
        }
    }

    private static boolean verify(String name, String word) {
        return true;
    }


}
