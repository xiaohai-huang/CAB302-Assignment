package ControlPanel;

public class ControlPanel {
    public static void main(String[] args) {
        View view = new View();
        Model model = new Model();

        Controller controller = new Controller(view, model);

        view.getLoginView().setVisible(true);
    }
}
