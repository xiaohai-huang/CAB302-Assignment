package ControlPanel;

public class ControlPanelMain {
    public static void main(String[] args) {
        View view = new View();
        Model model = new Model();

        Controller controller = new Controller(view,model);

        view.getLoginView().setVisible(true);
    }
}
