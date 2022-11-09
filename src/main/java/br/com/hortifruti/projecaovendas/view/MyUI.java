package br.com.hortifruti.projecaovendas.view;

import javax.servlet.annotation.WebServlet;

//import br.com.hortifruti.projecaovendas.util.GenerateValuesUtil;
import com.backendless.Backendless;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.*;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 *
 */
@Theme("mytheme")
@Widgetset("br.com.hortifruti.MyAppWidgetset")
public class MyUI  extends UI {

    private static final String LOGGER_NAME = "LOGGER_ PROJECAO_VENDAS";
    private static final Logger logger = Logger.getLogger(LOGGER_NAME);
    private LoginView loginView = new LoginView();
    private MainView mainView = new MainView();

    private static DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
    private static DecimalFormat decimalFormat = new DecimalFormat("###,###,###,###,###");
    private static DecimalFormat decimalFormatPercet = new DecimalFormat("###.00%");
    private static DecimalFormat decimalFormatWithDecimal = new DecimalFormat("###,###,###,###,##0.00");
    {
        decimalFormatSymbols.setGroupingSeparator('.');
        decimalFormatSymbols.setDecimalSeparator(',');
        decimalFormat.setDecimalFormatSymbols(decimalFormatSymbols);
        decimalFormatPercet.setDecimalFormatSymbols(decimalFormatSymbols);
        decimalFormatWithDecimal.setDecimalFormatSymbols(decimalFormatSymbols);
    }


    @Override
    protected void init(VaadinRequest request) {
        UI.getCurrent().setLocale(new Locale("pt", "BR"));
        try {
            FileHandler fileHandler = new FileHandler("ProjecaoVendas.log");
            SimpleFormatter formatter = new SimpleFormatter();
            fileHandler.setFormatter(formatter);
            fileHandler.setLevel(Level.ALL);
            logger.addHandler(fileHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Responsive.makeResponsive(this);
        setWidth(100.f, Unit.PERCENTAGE);
        new Navigator(this, this);
        getNavigator().addView(LoginView.VIEW_NAME, loginView);
        getNavigator().addView(MainView.VIEW_NAME, mainView);
        getNavigator().addViewChangeListener(new ViewChangeListener() {
            @Override
            public boolean beforeViewChange(ViewChangeEvent event) {

                // Check if a user has logged in
                boolean isLoggedIn = getSession().getAttribute("user") != null;
                boolean isLoginView = event.getNewView() instanceof LoginView;

                if (!isLoggedIn && !isLoginView) {
                    // Redirect to login view always if a user has not yet
                    // logged in
                    getNavigator().navigateTo(LoginView.VIEW_NAME);
                    return false;

                } else if (isLoggedIn && isLoginView) {
                    getNavigator().navigateTo(MainView.VIEW_NAME);
                }

                return true;
            }

            @Override
            public void afterViewChange(ViewChangeEvent event) {

            }
        });

        Backendless.initApp( "9D3E8CF7-C54C-3304-FF24-D4FF89684900", "FE33EEDD-E3E6-3A63-FFC9-72332A2E6A00" );
    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = true)
    public static class MyUIServlet extends VaadinServlet {
    }

    public static Logger getLogger() {
        return logger;
    }

    public static DecimalFormat getDecimalFormat() {
        return decimalFormat;
    }

    public static DecimalFormat getDecimalFormatPercet() {
        return decimalFormatPercet;
    }

    public static DecimalFormat getDecimalFormatWithDecimal() {
        return decimalFormatWithDecimal;
    }
}
