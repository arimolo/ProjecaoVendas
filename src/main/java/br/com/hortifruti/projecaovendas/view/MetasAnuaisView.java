package br.com.hortifruti.projecaovendas.view;

import br.com.hortifruti.projecaovendas.control.MetaController;
import br.com.hortifruti.projecaovendas.model.MetaAnual;
import br.com.hortifruti.projecaovendas.model.User;
import org.vaadin.addons.*;
import org.vaadin.addons.builder.ExportExcelConfigurationBuilder;
import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Responsive;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.vaadin.addons.builder.ExportExcelComponentConfigurationBuilder;
import org.vaadin.addons.builder.ExportExcelSheetConfigurationBuilder;
import org.vaadin.dialogs.ConfirmDialog;
import tm.kod.widgets.numberfield.NumberField;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

/**
 * Created by arimolo on 29/08/16.
 */
public class MetasAnuaisView extends CustomComponent implements View {
    private final BeanItemContainer<MetaAnual> beans = new BeanItemContainer<MetaAnual>(MetaAnual.class);
    private final VerticalLayout mainLayout = new VerticalLayout();
    private final HorizontalLayout filterLayout = new HorizontalLayout();
    private final HorizontalLayout buttonPaneLayout = new HorizontalLayout();
    private final Button btnFiltrar = new Button("Filtrar");
    private final Button btnExportar = new Button("Exportar");
    private final Button btnSalvar = new Button("Salvar");
    private final Panel panelConsolidar = new Panel("Consolidar Meta Anual");
    private final VerticalLayout panelConsolidarContent = new VerticalLayout();
    private final HorizontalLayout panelConsolidarLayout = new HorizontalLayout();
    private final HorizontalLayout panelConsolidarLayoutClientes = new HorizontalLayout();
    private final NumberField txtMetaAnualCalculada = new NumberField("Calculado Faturamento");
    private final NumberField txtMetaAnualConsolidada = new NumberField("Consolidado Faturamento");
    private final NumberField txtMetaAnualClientesCalculada = new NumberField("Calculado Clientes");
    private final NumberField txtMetaAnualClientesConsolidada = new NumberField("Consolidado Clientes");
    private final Button btnConsolidar = new Button("Consolidar");
    private final Button btnConsolidarClientes = new Button("Consolidar");
    private MetaAnual metaAnualConsolidada;
    private BigDecimal somaValor = new BigDecimal(0);
    private BigDecimal somaValorRealizado = new BigDecimal(0);
    private Long somaClientes = new Long(0);
    private Long somaClientesRealizado = new Long(0);
    private boolean _ignoreTriggers= true;

    private ComboBox comboAnos = new ComboBox("Ano", new ArrayList(){{
        add("2015");
        add("2016");
        add("2017");
        add("2018");
        add("2019");
        add("2020");
        add("2021");
        add("2022");
        add("2023");
        add("2024");
        add("2025");
    }});
    private final Table table = new Table() {
        @Override
        protected String formatPropertyValue(Object rowId,
                                             Object colId, Property<?> property) {
            // Format by property type
            if (property.getType() == BigDecimal.class) {
                if (property.getValue() == null) return "";
                return MyUI.getDecimalFormat().format(property.getValue());
            }

            return super.formatPropertyValue(rowId, colId, property);
        }
    };

    public MetasAnuaisView() {
        setSizeFull();
        Responsive.makeResponsive(this);
        Responsive.makeResponsive(mainLayout);
        mainLayout.setSizeFull();
        mainLayout.setSpacing(true);
        mainLayout.setMargin(true);


        filterLayout.setDefaultComponentAlignment(Alignment.BOTTOM_LEFT);
        filterLayout.setSpacing(true);

        comboAnos.setInputPrompt("Selecione o Ano");
        comboAnos.setNullSelectionAllowed(false);
        comboAnos.setPageLength(12);


        btnFiltrar.setStyleName(ValoTheme.BUTTON_PRIMARY);
        btnFiltrar.setIcon(FontAwesome.SEARCH);
        btnFiltrar.addClickListener(clickEvent -> {
            refreshTable();
        });

        filterLayout.addComponents(comboAnos, btnFiltrar);

        table.setContainerDataSource(beans);
        table.setTableFieldFactory(new DefaultFieldFactory() {
            @Override
            public Field<?> createField(Container container, Object itemId,
                                        Object propertyId, Component uiContext) {
                Class<?> cls = container.getType(propertyId);

                if (propertyId.equals("valor")) {
                    NumberField numField = new NumberField();
                    numField.setEnabled(getSession() != null
                            && getSession().getAttribute("user") != null
                            && ((User)getSession().getAttribute("user")).isAdmin());
                    numField.setWidth(110.0f, Unit.PIXELS);
                    numField.setSigned(false);                                                 // disable negative sign, default true
                    numField.setUseGrouping(true);                                        // enable grouping, default false
                    numField.setGroupingSeparator('.');                                  // set grouping separator ' '
                    numField.setDecimal(false);                                               // enables input of decimal numbers, default false
                    numField.setDecimalSeparator(',');                                   // set decimal separator, default '.'
                    numField.setImmediate(true);
                    numField.addStyleName(ValoTheme.TEXTFIELD_ALIGN_RIGHT);
                    numField.addStyleName(ValoTheme.TEXTFIELD_SMALL);
                    numField.setRequired(true);
                    numField.setNullSettingAllowed(false);
                    numField.setNullRepresentation("0");
                    numField.addValueChangeListener(new Property.ValueChangeListener() {
                        @Override
                        public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                            if (!_ignoreTriggers) {
                                _ignoreTriggers= true;
                                somaValor = new BigDecimal(0);
                                for (MetaAnual meta : beans.getItemIds()) {
                                    somaValor = somaValor.add(meta.getValor());
                                }
                                table.setColumnFooter("valor", MyUI.getDecimalFormat().format(somaValor));
                                txtMetaAnualCalculada.setReadOnly(false);
                                txtMetaAnualCalculada.setValue(MyUI.getDecimalFormat().format(somaValor));
                                txtMetaAnualCalculada.setReadOnly(true);
                                _ignoreTriggers= false;
                            }
                        }
                    });
                    return numField;
                } else {
                    if (propertyId.equals("clientes")) {
                        NumberField numField = new NumberField();
                        numField.setEnabled(getSession() != null
                                && getSession().getAttribute("user") != null
                                && ((User)getSession().getAttribute("user")).isAdmin());
                        numField.setWidth(110.0f, Unit.PIXELS);
                        numField.setSigned(false);                                                 // disable negative sign, default true
                        numField.setUseGrouping(true);                                        // enable grouping, default false
                        numField.setGroupingSeparator('.');                                  // set grouping separator ' '
                        numField.setDecimal(false);                                               // enables input of decimal numbers, default false
                        numField.setDecimalSeparator(',');                                   // set decimal separator, default '.'
                        numField.setImmediate(true);
                        numField.addStyleName(ValoTheme.TEXTFIELD_ALIGN_RIGHT);
                        numField.addStyleName(ValoTheme.TEXTFIELD_SMALL);
                        numField.setRequired(true);
                        numField.setNullSettingAllowed(false);
                        numField.setNullRepresentation("0");
                        numField.addValueChangeListener(new Property.ValueChangeListener() {
                            @Override
                            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                                if (!_ignoreTriggers) {
                                    _ignoreTriggers= true;
                                    somaClientes = new Long(0);
                                    for (MetaAnual meta : beans.getItemIds()) {
                                        somaClientes += meta.getClientes();
                                    }
                                    table.setColumnFooter("clientes", MyUI.getDecimalFormat().format(somaClientes));
                                    txtMetaAnualClientesCalculada.setReadOnly(false);
                                    txtMetaAnualClientesCalculada.setValue(MyUI.getDecimalFormat().format(somaClientes));
                                    txtMetaAnualClientesCalculada.setReadOnly(true);
                                    _ignoreTriggers= false;
                                }
                            }
                        });
                        return numField;
                    } else {
                        return null;
                    }
                }


            }
        });

        table.addGeneratedColumn("diferenca", new Table.ColumnGenerator() {
            @Override
            public Component generateCell(Table source, Object itemId, Object columnId) {
                Label comp = new Label("?");
                comp.addStyleName(ValoTheme.LABEL_SMALL);
                comp.addStyleName(ValoTheme.TEXTFIELD_ALIGN_RIGHT);
                if (itemId instanceof MetaAnual)  {
                    try {
                        MetaAnual meta = (MetaAnual) itemId;
                        BigDecimal diferencaPercent = meta.getValorRealizado().divide(meta.getValor(), 4, RoundingMode.HALF_UP);
                        comp.setValue(MyUI.getDecimalFormatPercet().format(diferencaPercent));
                    } catch (ArithmeticException aex) {
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                return comp;
            }
        });

        table.addGeneratedColumn("diferencaClientes", new Table.ColumnGenerator() {
            @Override
            public Component generateCell(Table source, Object itemId, Object columnId) {
                Label comp = new Label("?");
                comp.addStyleName(ValoTheme.LABEL_SMALL);
                comp.addStyleName(ValoTheme.TEXTFIELD_ALIGN_RIGHT);
                if (itemId instanceof MetaAnual)  {
                    try {
                        MetaAnual meta = (MetaAnual) itemId;
                        if (meta.getClientes() > 0) {
                            BigDecimal diferencaPercent = new BigDecimal((float) meta.getClientesRealizado() / (float) meta.getClientes());
                            comp.setValue(MyUI.getDecimalFormatPercet().format(diferencaPercent));
                        }
                    } catch (ArithmeticException aex) {
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                return comp;
            }
        });

        table.addStyleName("wordwrap-headers");
        table.setWidth(860.f, Unit.PIXELS);
        table.addStyleName(ValoTheme.TABLE_SMALL);
        table.setPageLength(table.size());
        table.setEditable(true);
        table.setSortEnabled(false);
        table.setFooterVisible(true);
        table.setVisibleColumns("nomeLoja", "valor", "valorRealizado", "diferenca", "clientes", "clientesRealizado", "diferencaClientes");
        table.setColumnWidth("nomeLoja", 160);
        table.setColumnWidth("valor", 120);
        table.setColumnWidth("valorRealizado", 120);
        table.setColumnWidth("diferenca", 120);
        table.setColumnWidth("clientes", 120);
        table.setColumnWidth("clientesRealizado", 100);
        table.setColumnWidth("diferencaClientes", 100);
        table.setColumnHeader("nomeLoja", "LOJA");
        table.setColumnHeader("valor", "FATURAMENTO META (R$)");
        table.setColumnHeader("valorRealizado", "FATURAMENTO REALIZADO (R$)");
        table.setColumnHeader("diferenca", "FATURAMENTO ATINGIDO");
        table.setColumnHeader("clientes", "CLIENTES META");
        table.setColumnHeader("clientesRealizado", "CLIENTES REALIZADO");
        table.setColumnHeader("diferencaClientes", "CLIENTES ATINGIDO");
        table.setColumnAlignment("valor", Table.Align.RIGHT);
        table.setColumnAlignment("valorRealizado", Table.Align.RIGHT);
        table.setColumnAlignment("diferenca", Table.Align.RIGHT);
        table.setColumnAlignment("clientes", Table.Align.RIGHT);
        table.setColumnAlignment("clientesRealizado", Table.Align.RIGHT);
        table.setColumnAlignment("diferencaClientes", Table.Align.RIGHT);
        table.setColumnFooter("nomeLoja", "TOTAL DO ANO");



        btnExportar.setIcon(FontAwesome.FILE_EXCEL_O);
        btnExportar.addClickListener(clickEvent -> {
            ExportToExcelUtility<MetaAnual> exportToExcelUtility = customizeExportExcelUtility();
            exportToExcelUtility.setSourceUI(UI.getCurrent());
            exportToExcelUtility.setResultantExportType(ExportType.XLS);
            exportToExcelUtility.export();
        });

        btnSalvar.setVisible(false);
        btnSalvar.addStyleName(ValoTheme.BUTTON_FRIENDLY);
        btnSalvar.setIcon(FontAwesome.SAVE);
        btnSalvar.addClickListener(clickEvent -> {
            try {
                MetaController.persisteMetasAnuais(beans.getItemIds(), (User) getSession().getAttribute("user"));
                refreshTable();
                Notification.show("FEITO", "Metas salvas com sucesso.", Notification.Type.TRAY_NOTIFICATION);
            } catch (Exception e) {
                Notification.show("Não foi possível salvar as Metas Anuais, consulte o log para mais detalhes.", Notification.Type.ERROR_MESSAGE);
            }
        });

        buttonPaneLayout.setSpacing(true);
        buttonPaneLayout.addComponents(btnExportar, btnSalvar);

        txtMetaAnualCalculada.setWidth(10.0f, Unit.EM);
        txtMetaAnualCalculada.setSigned(false);                                                 // disable negative sign, default true
        txtMetaAnualCalculada.setUseGrouping(true);                                        // enable grouping, default false
        txtMetaAnualCalculada.setGroupingSeparator('.');                                  // set grouping separator ' '
        txtMetaAnualCalculada.setDecimal(false);                                               // enables input of decimal numbers, default false
        txtMetaAnualCalculada.setDecimalSeparator(',');                                   // set decimal separator, default '.'
        txtMetaAnualCalculada.setImmediate(true);
        txtMetaAnualCalculada.addStyleName(ValoTheme.TEXTFIELD_ALIGN_RIGHT);

        txtMetaAnualConsolidada.setWidth(10.0f, Unit.EM);
        txtMetaAnualConsolidada.setSigned(false);                                                 // disable negative sign, default true
        txtMetaAnualConsolidada.setUseGrouping(true);                                        // enable grouping, default false
        txtMetaAnualConsolidada.setGroupingSeparator('.');                                  // set grouping separator ' '
        txtMetaAnualConsolidada.setDecimal(false);                                               // enables input of decimal numbers, default false
        txtMetaAnualConsolidada.setDecimalSeparator(',');                                   // set decimal separator, default '.'
        txtMetaAnualConsolidada.setImmediate(true);
        txtMetaAnualConsolidada.addStyleName(ValoTheme.TEXTFIELD_ALIGN_RIGHT);

        txtMetaAnualClientesCalculada.setWidth(10.0f, Unit.EM);
        txtMetaAnualClientesCalculada.setSigned(false);                                                 // disable negative sign, default true
        txtMetaAnualClientesCalculada.setUseGrouping(true);                                        // enable grouping, default false
        txtMetaAnualClientesCalculada.setGroupingSeparator('.');                                  // set grouping separator ' '
        txtMetaAnualClientesCalculada.setDecimal(false);                                               // enables input of decimal numbers, default false
        txtMetaAnualClientesCalculada.setDecimalSeparator(',');                                   // set decimal separator, default '.'
        txtMetaAnualClientesCalculada.setImmediate(true);
        txtMetaAnualClientesCalculada.addStyleName(ValoTheme.TEXTFIELD_ALIGN_RIGHT);

        txtMetaAnualClientesConsolidada.setWidth(10.0f, Unit.EM);
        txtMetaAnualClientesConsolidada.setSigned(false);                                                 // disable negative sign, default true
        txtMetaAnualClientesConsolidada.setUseGrouping(true);                                        // enable grouping, default false
        txtMetaAnualClientesConsolidada.setGroupingSeparator('.');                                  // set grouping separator ' '
        txtMetaAnualClientesConsolidada.setDecimal(false);                                               // enables input of decimal numbers, default false
        txtMetaAnualClientesConsolidada.setDecimalSeparator(',');                                   // set decimal separator, default '.'
        txtMetaAnualClientesConsolidada.setImmediate(true);
        txtMetaAnualClientesConsolidada.addStyleName(ValoTheme.TEXTFIELD_ALIGN_RIGHT);

        btnConsolidar.addStyleName(ValoTheme.BUTTON_DANGER);
        btnConsolidar.addStyleName(ValoTheme.BUTTON_ICON_ALIGN_RIGHT);
        btnConsolidar.setIcon(FontAwesome.HAND_O_RIGHT);
        btnConsolidar.addClickListener(clickEvent -> {
            ConfirmDialog.show(getUI(), "Por favor, confirme.", "Você irá substituir o valor consolidado atual (" +
                            txtMetaAnualConsolidada.getValue() + ") pelo valor calculado com base na soma das metas das lojas (" +
                            txtMetaAnualCalculada.getValue() + ")?",
                    "Sim", "Não", new ConfirmDialog.Listener() {

                        public void onClose(ConfirmDialog dialog) {
                            if (dialog.isConfirmed()) {
                                // Confirmed to continue
                                try {
                                    metaAnualConsolidada.setValor(somaValor);
                                    MetaController.persisteMetaAnual(metaAnualConsolidada, (User) getSession().getAttribute("user"));
                                    txtMetaAnualConsolidada.setReadOnly(false);
                                    txtMetaAnualConsolidada.setValue(MyUI.getDecimalFormat().format(metaAnualConsolidada.getValor()));
                                    txtMetaAnualConsolidada.setReadOnly(true);
                                    Notification.show("FEITO", "Meta Anual de Faturamento consolidada com sucesso.", Notification.Type.TRAY_NOTIFICATION);
                                } catch (Exception e) {
                                    Notification.show("Não foi possível consolidar a meta anual de faturamento.", Notification.Type.ERROR_MESSAGE);
                                    e.printStackTrace();
                                }
                            } else {
                                // User did not confirm

                            }
                        }
                    });
        });

        btnConsolidarClientes.addStyleName(ValoTheme.BUTTON_DANGER);
        btnConsolidarClientes.addStyleName(ValoTheme.BUTTON_ICON_ALIGN_RIGHT);
        btnConsolidarClientes.setIcon(FontAwesome.HAND_O_RIGHT);
        btnConsolidarClientes.addClickListener(clickEvent -> {
            ConfirmDialog.show(getUI(), "Por favor, confirme.", "Você irá substituir o valor consolidado atual (" +
                            txtMetaAnualClientesConsolidada.getValue() + ") pelo valor calculado com base na soma das metas das lojas (" +
                            txtMetaAnualClientesCalculada.getValue() + ")?",
                    "Sim", "Não", new ConfirmDialog.Listener() {

                        public void onClose(ConfirmDialog dialog) {
                            if (dialog.isConfirmed()) {
                                // Confirmed to continue
                                try {
                                    metaAnualConsolidada.setClientes(somaClientes);
                                    MetaController.persisteMetaAnual(metaAnualConsolidada, (User) getSession().getAttribute("user"));
                                    txtMetaAnualClientesConsolidada.setReadOnly(false);
                                    txtMetaAnualClientesConsolidada.setValue(MyUI.getDecimalFormat().format(metaAnualConsolidada.getClientes()));
                                    txtMetaAnualClientesConsolidada.setReadOnly(true);
                                    Notification.show("FEITO", "Meta Anual de Clientes consolidada com sucesso.", Notification.Type.TRAY_NOTIFICATION);
                                } catch (Exception e) {
                                    Notification.show("Não foi possível consolidar a meta anual de clientes.", Notification.Type.ERROR_MESSAGE);
                                    e.printStackTrace();
                                }
                            } else {
                                // User did not confirm

                            }
                        }
                    });
        });

        panelConsolidarLayout.setSpacing(true);
        panelConsolidarLayout.setDefaultComponentAlignment(Alignment.BOTTOM_LEFT);
        panelConsolidarLayout.addComponents(txtMetaAnualCalculada, btnConsolidar, txtMetaAnualConsolidada);

        panelConsolidarLayoutClientes.setSpacing(true);
        panelConsolidarLayoutClientes.setDefaultComponentAlignment(Alignment.BOTTOM_LEFT);
        panelConsolidarLayoutClientes.addComponents(txtMetaAnualClientesCalculada, btnConsolidarClientes, txtMetaAnualClientesConsolidada);

        panelConsolidarContent.setSpacing(true);
        panelConsolidarContent.setMargin(true);
        panelConsolidarContent.addComponents(panelConsolidarLayout, panelConsolidarLayoutClientes);
        panelConsolidar.setVisible(false);
        panelConsolidar.setIcon(FontAwesome.CHECK_SQUARE_O);
        panelConsolidar.setContent(panelConsolidarContent);

        mainLayout.addComponents(filterLayout, table, buttonPaneLayout, panelConsolidar);

        setCompositionRoot(new CssLayout(mainLayout));
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {

    }

    private void refreshTable() {
        _ignoreTriggers = true;
        somaValor = new BigDecimal(0);
        somaValorRealizado = new BigDecimal(0);
        somaClientes = new Long(0);
        somaClientesRealizado = new Long(0);
        if (comboAnos.getValue() != null) {
            beans.removeAllItems();
            beans.addAll(MetaController.getMetasAnuais(Integer.parseInt((String) comboAnos.getValue())));
            if (beans.size() > 0) {
                for (MetaAnual meta : beans.getItemIds()) {
                    somaValor = somaValor.add(meta.getValor());
                    somaValorRealizado = somaValorRealizado.add(meta.getValorRealizado());

                    somaClientes += meta.getClientes();
                    somaClientesRealizado += meta.getClientesRealizado();
                }
                table.setColumnFooter("valor", MyUI.getDecimalFormat().format(somaValor));
                table.setColumnFooter("valorRealizado", MyUI.getDecimalFormat().format(somaValorRealizado));
                table.setColumnFooter("clientes", MyUI.getDecimalFormat().format(somaClientes));
                table.setColumnFooter("clientesRealizado", MyUI.getDecimalFormat().format(somaClientesRealizado));
                txtMetaAnualCalculada.setReadOnly(false);
                txtMetaAnualCalculada.setValue(MyUI.getDecimalFormat().format(somaValor));
                txtMetaAnualCalculada.setReadOnly(true);

                txtMetaAnualClientesCalculada.setReadOnly(false);
                txtMetaAnualClientesCalculada.setValue(MyUI.getDecimalFormat().format(somaClientes));
                txtMetaAnualClientesCalculada.setReadOnly(true);

                metaAnualConsolidada = MetaController.getMetaAnualConsolidada(Integer.parseInt((String) comboAnos.getValue()));
                txtMetaAnualConsolidada.setReadOnly(false);
                txtMetaAnualConsolidada.setValue(MyUI.getDecimalFormat().format(metaAnualConsolidada.getValor()));
                txtMetaAnualConsolidada.setReadOnly(true);
                txtMetaAnualClientesConsolidada.setReadOnly(false);
                txtMetaAnualClientesConsolidada.setValue(MyUI.getDecimalFormat().format(metaAnualConsolidada.getClientes()));
                txtMetaAnualClientesConsolidada.setReadOnly(true);
                btnSalvar.setVisible(getSession() != null
                        && getSession().getAttribute("user") != null
                        && ((User)getSession().getAttribute("user")).isAdmin());
                panelConsolidar.setVisible(getSession() != null
                        && getSession().getAttribute("user") != null
                        && ((User)getSession().getAttribute("user")).isAdmin());
                _ignoreTriggers = false;
            } else {
                Notification.show("Não existem dados para o filtro selecionado.", Notification.Type.HUMANIZED_MESSAGE);
            }
        } else {
            Notification.show("Selecione o ano.", Notification.Type.WARNING_MESSAGE);
        }
    }

    private ExportToExcelUtility<MetaAnual> customizeExportExcelUtility() {
        /* Configuring Components */
        ExportExcelComponentConfiguration componentConfig1 = new ExportExcelComponentConfigurationBuilder().withTable(table)
                .withVisibleProperties(table.getVisibleColumns())
                .withColumnHeaderKeys(table.getColumnHeaders())
                .build();

        /* Configuring Sheets */
        ArrayList<ExportExcelComponentConfiguration> componentList1 = new ArrayList<ExportExcelComponentConfiguration>();
        componentList1.add(componentConfig1);

        ExportExcelSheetConfiguration sheetConfig1 = new ExportExcelSheetConfigurationBuilder().withReportTitle("Projeção de Vendas - Metas Anuais")
                .withSheetName("Metais Anuais")
                .withComponentConfigs(componentList1)
                .withIsHeaderSectionRequired(Boolean.TRUE)
                .build();

        /* Configuring Excel */
        ArrayList<ExportExcelSheetConfiguration> sheetList = new ArrayList<ExportExcelSheetConfiguration>();
        sheetList.add(sheetConfig1);
        ExportExcelConfiguration config1 = new ExportExcelConfigurationBuilder().withGeneratedBy(((User)getSession().getAttribute("user")).getLogin())
                .withSheetConfigs(sheetList)
                .build();

        return new ExportToExcelUtility<MetaAnual>(table.getUI(), config1, MetaAnual.class);
    }

}
