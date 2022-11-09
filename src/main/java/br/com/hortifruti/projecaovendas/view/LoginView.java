package br.com.hortifruti.projecaovendas.view;

import br.com.hortifruti.projecaovendas.control.UserController;
import br.com.hortifruti.projecaovendas.model.User;
import com.vaadin.data.validator.*;
import com.vaadin.event.ShortcutAction;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.ValoTheme;

/**
 * Created by arimolo on 02/03/15.
 */
public class LoginView extends CssLayout implements View, Button.ClickListener {
    public static final String VIEW_NAME = "";
    private final TextField userField = new TextField("Login:");
    private final PasswordField passwordField = new PasswordField("Password:");;
    private final Button loginButton;

    public LoginView() {
        addStyleName("login-screen");

        FormLayout loginForm = new FormLayout();

        loginForm.addStyleName("login-form");
        loginForm.setSizeUndefined();

        // Create the userField input field

        userField.setWidth(15, Unit.EM);
        userField.setRequired(true);
        userField.setInputPrompt("Login de rede (ex. nome.sobrenome)");

        // Create the passwordField input field
        passwordField.setWidth(15, Unit.EM);
        passwordField.addValidator(new PasswordValidator());
        passwordField.setRequired(true);
        passwordField.setNullRepresentation("");

        // Create login button
        loginButton = new Button("Login", this);
        loginButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
        loginButton.setIcon(FontAwesome.LOCK);
        loginButton.setClickShortcut(ShortcutAction.KeyCode.ENTER);

        // Add both to a panel
        loginForm.addComponents(userField, passwordField, loginButton);
        // login form, centered in the available part of the screen


        // layout to center login form when there is sufficient screen space
        // - see the theme for how this is made responsive for various screen
        // sizes
        VerticalLayout centeringLayout = new VerticalLayout();
        centeringLayout.setStyleName("centering-layout");
        centeringLayout.addComponent(loginForm);
        centeringLayout.setComponentAlignment(loginForm, Alignment.MIDDLE_CENTER);

        // information text about logging in
        CssLayout loginInformation = buildLoginInformation();

        addComponent(centeringLayout);
        addComponent(loginInformation);

        userField.focus();

//        GenerateValuesUtil.createMetasDiarias(2018, 2025, 93);
//        GenerateValuesUtil.createMetasDiarias(2018, 2025, 94);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        // focus the username field when userField arrives to the login view
        userField.focus();
    }

    // Validator for validating the passwords
    private static final class PasswordValidator extends
            AbstractValidator<String> {

        public PasswordValidator() {
            super("O passwordField não é válido.");
        }

        @Override
        protected boolean isValidValue(String value) {
            //
            // Password must be at least 6 characters long and contain at least
            // one number
            //
            if (value != null
                    && (value.length() < 6 || !value.matches(".*\\d.*"))) {
                return false;
            }
            return true;
        }

        @Override
        public Class<String> getType() {
            return String.class;
        }
    }

    @Override
    public void buttonClick(ClickEvent event) {
        //
        // Validate the fields using the navigator. By using validors for the
        // fields we reduce the amount of queries we have to use to the database
        // for wrongly entered passwords
        //
        if (!userField.isValid() || !passwordField.isValid()) {
            return;
        }

        String username = userField.getValue();
        String password = this.passwordField.getValue();

        try {
            User user = UserController.doLogin(username, password);
            getSession().setAttribute("user", user);

            // Navigate to main view
            getUI().getNavigator().navigateTo(MainView.VIEW_NAME);
        } catch (Exception e) {
            e.printStackTrace();
            Notification.show(e.getMessage(), Notification.Type.ERROR_MESSAGE);
            this.passwordField.setValue(null);
            this.passwordField.focus();
        }
    }


    private CssLayout buildLoginInformation() {
        CssLayout loginInformation = new CssLayout();
        loginInformation.setStyleName("login-information");
        Resource res = new ThemeResource("img/logo.png");
        Image image = new Image(null, res);

        Label loginInfoText = new Label(
                "<h1>Projeção de Vendas</h1>",
                ContentMode.HTML);
        loginInformation.addComponents(image, loginInfoText);
        return loginInformation;
    }

}
