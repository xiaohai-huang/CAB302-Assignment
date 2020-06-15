package ControlPanel;

public class View {

    private LoginView loginView;
    private ControlPanelView controlPanelView;

    public View(){
        loginView = new LoginView();
        controlPanelView = new ControlPanelView();
    }

    public LoginView getLoginView() {
        return loginView;
    }

    public ControlPanelView getControlPanelView() {
        return controlPanelView;
    }
}
