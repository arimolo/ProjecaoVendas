package br.com.hortifruti.projecaovendas.view;

import br.com.hortifruti.projecaovendas.model.User;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.*;
import com.vaadin.ui.*;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;

import java.time.*;
import java.time.temporal.IsoFields;
import java.util.logging.Logger;
import java.util.Date;



/**
 * Created by arimolo on 02/03/15.
 */
public class MainView extends CustomComponent implements View {
    public static final String VIEW_NAME = "MainView";
    private Logger logger = MyUI.getLogger();
    private VerticalLayout mainLayout = new VerticalLayout();
    private MenuBar barmenu = new MenuBar();
    private Label lblTitulo = new Label("Projeção de Vendas");
    private TabSheet tabSheet = new TabSheet();
    private MetasAnuaisView metasAnuaisView = new MetasAnuaisView();
    private MetasMensaisView metasMensaisView = new MetasMensaisView();
    private MetasDiariasView metasDiariasView = new MetasDiariasView();
    private ExtracoesView extracoesView = new ExtracoesView();
    private UserWindow userWindow = new UserWindow();
    private MenuBar.MenuItem menuUsuarios;
    private TabSheet.Tab tabExtracoes;

    public MainView() {
        Responsive.makeResponsive(this);
        mainLayout.setSizeFull();
        mainLayout.setSpacing(true);
        mainLayout.setMargin(true);
        Responsive.makeResponsive(mainLayout);

        Resource res = new ThemeResource("img/logo_menor.png");
        Image imageLogo = new Image(null, res);

        LocalDate date = new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        date.get ( IsoFields.WEEK_OF_WEEK_BASED_YEAR );



        HorizontalLayout titleBarLayout = new HorizontalLayout(imageLogo, lblTitulo, barmenu);
        titleBarLayout.setSpacing(true);
        titleBarLayout.setSizeFull();
        Responsive.makeResponsive(titleBarLayout);
        titleBarLayout.setComponentAlignment(lblTitulo, Alignment.MIDDLE_LEFT);
        titleBarLayout.setComponentAlignment(barmenu, Alignment.MIDDLE_RIGHT);
        mainLayout.addComponent(titleBarLayout);


        lblTitulo.setStyleName("h2");

        MenuBar.Command mycommand = new MenuBar.Command() {
            MenuBar.MenuItem previous = null;

            @Override
            public void menuSelected(MenuBar.MenuItem selectedItem) {
                if (previous != null)
                    previous.setStyleName(null);
                selectedItem.setStyleName("highlight");
                previous = selectedItem;
            }
        };

        barmenu.addStyleName("mybarmenu");
        menuUsuarios = barmenu.addItem("Usuários", FontAwesome.USER, new MenuBar.Command() {
            @Override
            public void menuSelected(MenuBar.MenuItem menuItem) {
                userWindow.refreshLDAPUsers();
                userWindow.refresh();
                getUI().addWindow(userWindow);
            }
        });
        menuUsuarios.setVisible(false);
        barmenu.addItem("Ajuda", FontAwesome.QUESTION_CIRCLE, new MenuBar.Command() {
            @Override
            public void menuSelected(MenuBar.MenuItem menuItem) {
                getUI().getPage().open("http://srv-pera.hortifrutisa.local/chamados/", "_blank");
            }
        });
        barmenu.addItem("Logout", FontAwesome.SIGN_OUT, new MenuBar.Command() {
            @Override
            public void menuSelected(MenuBar.MenuItem menuItem) {
                // "Logout" the user
                getSession().setAttribute("user", null);

                // Refresh this view, should redirect to login view
                getUI().getNavigator().navigateTo(LoginView.VIEW_NAME);
            }
        });


        tabSheet.addStyleName(ValoTheme.TABSHEET_FRAMED);
        tabSheet.addStyleName(ValoTheme.TABSHEET_PADDED_TABBAR);
        tabSheet.addTab(metasAnuaisView, "Metas Anuais");
        tabSheet.addTab(metasMensaisView, "Metas Mensais");
        tabSheet.addTab(metasDiariasView, "Metas Diárias");
        tabExtracoes = tabSheet.addTab(extracoesView, "Extrações");
        tabExtracoes.setVisible(false);

        tabSheet.setSizeFull();
        Responsive.makeResponsive(tabSheet);

        tabSheet.addSelectedTabChangeListener(
                new TabSheet.SelectedTabChangeListener() {
                    public void selectedTabChange(TabSheet.SelectedTabChangeEvent event) {
                        // Find the tabsheet
                        TabSheet tabsheet = event.getTabSheet();

                        // Find the tab (here we know it's a layout)
                        Component tab = (Component) tabsheet.getSelectedTab();

                        if (tab instanceof MetasDiariasView) {
                            metasDiariasView.refreshTable();
                        }
                    }
                });

        mainLayout.addComponent(tabSheet);

        setCompositionRoot(mainLayout);
    }

    @Override
    public void enter(ViewChangeEvent viewChangeEvent) {
        if (getSession() != null && getSession().getAttribute("user") != null
                && ((User)getSession().getAttribute("user")).isAdmin()) {
            tabExtracoes.setVisible(true);
            menuUsuarios.setVisible(true);
        } else {
            tabExtracoes.setVisible(false);
            menuUsuarios.setVisible(false);
        }
    }
}
