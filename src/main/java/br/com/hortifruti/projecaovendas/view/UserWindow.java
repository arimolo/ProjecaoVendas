package br.com.hortifruti.projecaovendas.view;

import br.com.hortifruti.projecaovendas.control.UserController;
import br.com.hortifruti.projecaovendas.model.User;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Responsive;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.*;
import com.vaadin.ui.Button;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Created by arimolo on 01/09/16.
 */
public class UserWindow extends Window implements View {
    private static Logger logger = MyUI.getLogger();
    private final BeanItemContainer<User> beans = new BeanItemContainer<>(User.class);
    private final VerticalLayout mainLayout = new VerticalLayout();
    private final HorizontalLayout topLayout = new HorizontalLayout();
    private final BeanItemContainer<User> usersLDAP = new BeanItemContainer<>(User.class);
    private final ComboBox comboUsers = new ComboBox("Novo Usuário");
    private final Button btnAdd = new Button("Adicionar");
    private final Table table = new Table();

    public UserWindow() {
        super("Manutenção dos Usuários"); // Set window caption
        setWidth("800px");
        setHeight("620px");
        center();
        setClosable(true);
        Responsive.makeResponsive(this);
        Responsive.makeResponsive(mainLayout);
        setModal(true);
        mainLayout.setMargin(true);
        mainLayout.setSpacing(true);

        comboUsers.setContainerDataSource(usersLDAP);
        comboUsers.setPageLength(50);
        comboUsers.setWidth(36.0f, Unit.EM);
        comboUsers.setFilteringMode(FilteringMode.CONTAINS);

        btnAdd.addStyleName(ValoTheme.BUTTON_FRIENDLY);
        btnAdd.setIcon(FontAwesome.PLUS_CIRCLE);
        btnAdd.addClickListener(clickEvent -> {
            try {
                if (comboUsers.getValue() != null) {
                    List<User> result = beans.getItemIds().stream()
                            .filter(item -> item.getLogin().equals(((User) comboUsers.getValue()).getLogin()))
                            .collect(Collectors.toList());
                    if (result != null && !result.isEmpty()) {
                        Notification.show("Usuário já existe.", Notification.Type.WARNING_MESSAGE);
                    } else {
                        UserController.persisteUser((User) comboUsers.getValue());
                        refresh();
                    }
                } else {
                    Notification.show("Selecione um usuário na lista.", Notification.Type.WARNING_MESSAGE);
                }
            } catch (Exception e) {
                Notification.show("Não foi possível adicionar o usuário.", Notification.Type.ERROR_MESSAGE);
                logger.log(Level.SEVERE, "Não foi possível salvar o Usuário " + ((User) comboUsers.getValue()).getLogin(), e);
            }
        });
        topLayout.setSpacing(true);
        topLayout.setDefaultComponentAlignment(Alignment.BOTTOM_LEFT);
        topLayout.addComponents(comboUsers, btnAdd);

        table.addGeneratedColumn("del", (Table.ColumnGenerator) (source, itemId, columnId) -> {
            User user = (User) itemId;
            if (!user.isAdmin()) {
                Button btnDel = new Button();
                btnDel.addStyleName(ValoTheme.BUTTON_SMALL);
                btnDel.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
                btnDel.addStyleName(ValoTheme.BUTTON_DANGER);
                btnDel.setIcon(FontAwesome.TRASH_O);
                btnDel.addClickListener(clickEvent -> {
                    try {
                        UserController.deleteUser(user);
                        refresh();
                    } catch (Exception e) {
                        Notification.show("Não foi possível remover o usuário " + user.getLogin(), Notification.Type.ERROR_MESSAGE);
                        logger.log(Level.SEVERE, "Não foi possível salvar o Usuário " + ((User) comboUsers.getValue()).getLogin(), e);
                    }
                });
                return btnDel;
            } else {
                return null;
            }
        });
        table.setContainerDataSource(beans);
        table.setHeight(480.0f, Unit.PIXELS);
        table.setWidth(100.f, Unit.PERCENTAGE);
        table.addStyleName(ValoTheme.TABLE_SMALL);
        table.setSortEnabled(true);
        table.setVisibleColumns("nome", "login", "perfil", "loja", "del");
        table.setColumnHeader("nome", "NOME");
        table.setColumnHeader("login", "LOGIN");
        table.setColumnHeader("perfil", "PERFIL");
        table.setColumnHeader("loja", "LOJA");
        table.setColumnHeader("del", "X");
        table.setColumnAlignment("del", Table.Align.CENTER);

        mainLayout.addComponents(topLayout, table);
        setContent(mainLayout);
    }

    public void refreshLDAPUsers() {
        usersLDAP.removeAllItems();
        usersLDAP.addAll(UserController.getLdapUsers());
    }

    public void refresh() {
        beans.removeAllItems();
        beans.addAll(UserController.getUsers());
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {

    }
}
