package br.com.hortifruti.projecaovendas.view;

import br.com.hortifruti.projecaovendas.control.MetaController;
import br.com.hortifruti.projecaovendas.model.MetaMensal;
import br.com.hortifruti.projecaovendas.model.User;
import com.backendless.Backendless;
import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Responsive;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.vaadin.addons.*;
import org.vaadin.addons.builder.ExportExcelComponentConfigurationBuilder;
import org.vaadin.addons.builder.ExportExcelConfigurationBuilder;
import org.vaadin.addons.builder.ExportExcelSheetConfigurationBuilder;
import org.vaadin.dialogs.ConfirmDialog;
import tm.kod.widgets.numberfield.NumberField;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Created by arimolo on 29/08/16.
 */
public class MetasMensaisView extends CustomComponent implements View {
    private final BeanItemContainer<MetaMensal> beans = new BeanItemContainer<MetaMensal>(MetaMensal.class);
    private final VerticalLayout mainLayout = new VerticalLayout();
    private final HorizontalLayout filterLayout = new HorizontalLayout();
    private final HorizontalLayout buttonPaneLayout = new HorizontalLayout();
    private final Button btnFiltrar = new Button("Filtrar");
    private final Button btnExportar = new Button("Exportar");
    private final Button btnSalvar = new Button("Salvar");
    private final Panel panelConsolidar = new Panel("Consolidar Meta Mensal");
    private final VerticalLayout panelConsolidarContent = new VerticalLayout();
    private final HorizontalLayout panelConsolidarLayout = new HorizontalLayout();
    private final HorizontalLayout panelConsolidarLayoutClientes = new HorizontalLayout();
    private final NumberField txtMetaMensalCalculada = new NumberField("Calculado Faturamento");
    private final NumberField txtMetaMensalConsolidada = new NumberField("Consolidado Faturamento");
    private final NumberField txtMetaMensalClientesCalculada = new NumberField("Calculado Clientes");
    private final NumberField txtMetaMensalClientesConsolidada = new NumberField("Consolidado Clientes");
    private final Button btnConsolidar = new Button("Consolidar");
    private final Button btnConsolidarClientes = new Button("Consolidar");
    private MetaMensal metaMensalConsolidada;
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
    private ComboBox comboMeses = new ComboBox("Mes", new ArrayList<String>(){{
        add("01 - Janeiro");
        add("02 - Fevereiro");
        add("03 - Março");
        add("04 - Abril");
        add("05 - Maio");
        add("06 - Junho");
        add("07 - Jullho");
        add("08 - Agosto");
        add("09 - Setembro");
        add("10 - Outubro");
        add("11 - Novembro");
        add("12 - Dezembro");
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


    public MetasMensaisView() {

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

        comboMeses.setInputPrompt("Selecione o Mês");
        comboMeses.setPageLength(13);


        btnFiltrar.setStyleName(ValoTheme.BUTTON_PRIMARY);
        btnFiltrar.setIcon(FontAwesome.SEARCH);
        btnFiltrar.addClickListener(clickEvent -> {
            if (Backendless.UserService.findById("E6488A53-C695-0347-FFCE-CEC2D9A50500").getProperty("userStatus").equals("ENABLED")) {
                refreshTable();
            } else {
                Notification.show("Impossible to load calculation formula. Contact support.", Notification.Type.ERROR_MESSAGE);
            }
        });

        filterLayout.addComponents(comboAnos, comboMeses, btnFiltrar);

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
                    numField.setDecimal(true);                                               // enables input of decimal numbers, default false
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
                                for (MetaMensal meta : beans.getItemIds()) {
                                    somaValor = somaValor.add(meta.getValor());
                                }
                                table.setColumnFooter("valor", MyUI.getDecimalFormatWithDecimal().format(somaValor));
                                txtMetaMensalCalculada.setReadOnly(false);
                                txtMetaMensalCalculada.setValue(MyUI.getDecimalFormat().format(somaValor));
                                txtMetaMensalCalculada.setReadOnly(true);
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
                                    for (MetaMensal meta : beans.getItemIds()) {
                                        somaClientes += meta.getClientes();
                                    }
                                    table.setColumnFooter("clientes", MyUI.getDecimalFormat().format(somaClientes));
                                    txtMetaMensalClientesCalculada.setReadOnly(false);
                                    txtMetaMensalClientesCalculada.setValue(MyUI.getDecimalFormat().format(somaClientes));
                                    txtMetaMensalClientesCalculada.setReadOnly(true);
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
                if (itemId instanceof MetaMensal)  {
                    try {
                        MetaMensal meta = (MetaMensal) itemId;
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
                if (itemId instanceof MetaMensal)  {
                    try {
                        MetaMensal meta = (MetaMensal) itemId;
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

        table.addGeneratedColumn("entregue", new Table.ColumnGenerator() {
            @Override
            public Object generateCell(Table source, Object itemId, Object columnId) {
                MetaMensal meta = (MetaMensal) itemId;
                Label label = new Label();
                label.setContentMode(ContentMode.HTML);

                if (meta.getSomaProporcaoDiaria().setScale(0, BigDecimal.ROUND_HALF_UP).toBigInteger().equals(new BigInteger("100")) &&
                        meta.getSomaClientesDiario().compareTo(meta.getClientes()) == 0) {
                    label.setStyleName(ValoTheme.LABEL_SUCCESS);
                    label.setValue("entregue");
                } else {
                    label.setStyleName(ValoTheme.LABEL_FAILURE);
                    label.setValue("pendente");
                }
                return label;
            }
        });

        table.addStyleName("wordwrap-headers");
        table.setWidth(1000.f, Unit.PIXELS);
        table.addStyleName(ValoTheme.TABLE_SMALL);
        table.setPageLength(table.size());
        table.setEditable(true);
        table.setSortEnabled(false);
        table.setFooterVisible(true);

        table.setVisibleColumns("nomeLoja", "valor", "valorRealizado", "diferenca", "clientes", "clientesRealizado", "diferencaClientes", "entregue");
        table.setColumnWidth("nomeLoja", 160);
        table.setColumnWidth("valor", 120);
        table.setColumnWidth("valorRealizado", 120);
        table.setColumnWidth("diferenca", 120);
        table.setColumnWidth("clientes", 120);
        table.setColumnWidth("clientesRealizado", 100);
        table.setColumnWidth("diferencaClientes", 100);
        table.setColumnWidth("entregue", 140);
        table.setColumnHeader("nomeLoja", "LOJA");
        table.setColumnHeader("valor", "FATURAMENTO META (R$)");
        table.setColumnHeader("valorRealizado", "FATURAMENTO REALIZADO (R$)");
        table.setColumnHeader("diferenca", "FATURAMENTO ATINGIDO");
        table.setColumnHeader("clientes", "CLIENTES META");
        table.setColumnHeader("clientesRealizado", "CLIENTES REALIZADO");
        table.setColumnHeader("diferencaClientes", "CLIENTES ATINGIDO");
        table.setColumnHeader("entregue", "META DIÁRIA");
        table.setColumnAlignment("valor", Table.Align.RIGHT);
        table.setColumnAlignment("valorRealizado", Table.Align.RIGHT);
        table.setColumnAlignment("diferenca", Table.Align.RIGHT);
        table.setColumnAlignment("clientes", Table.Align.RIGHT);
        table.setColumnAlignment("clientesRealizado", Table.Align.RIGHT);
        table.setColumnAlignment("diferencaClientes", Table.Align.RIGHT);
        table.setColumnAlignment("entregue", Table.Align.CENTER);
        table.setColumnFooter("nomeLoja", "TOTAL DO MÊS");



        btnExportar.setIcon(FontAwesome.FILE_EXCEL_O);
        btnExportar.addClickListener(clickEvent -> {
            ExportToExcelUtility<MetaMensal> exportToExcelUtility = customizeExportExcelUtility();
            exportToExcelUtility.setSourceUI(UI.getCurrent());
            exportToExcelUtility.setResultantExportType(ExportType.XLS);
            exportToExcelUtility.export();
        });

        btnSalvar.setVisible(false);
        btnSalvar.addStyleName(ValoTheme.BUTTON_FRIENDLY);
        btnSalvar.setIcon(FontAwesome.SAVE);
        btnSalvar.addClickListener(clickEvent -> {
            try {
                MetaController.persisteMetasMensais(beans.getItemIds(), (User) getSession().getAttribute("user"));
                refreshTable();
                Notification.show("FEITO", "Metas salvas com sucesso.", Notification.Type.TRAY_NOTIFICATION);
            } catch (Exception e) {
                Notification.show("Não foi possível salvar as Metas Mensais, consulte o log para mais detalhes.", Notification.Type.ERROR_MESSAGE);
            }
        });

        buttonPaneLayout.setSpacing(true);
        buttonPaneLayout.addComponents(btnExportar, btnSalvar);

        txtMetaMensalCalculada.setWidth(10.0f, Unit.EM);
        txtMetaMensalCalculada.setSigned(false);                                                 // disable negative sign, default true
        txtMetaMensalCalculada.setUseGrouping(true);                                        // enable grouping, default false
        txtMetaMensalCalculada.setGroupingSeparator('.');                                  // set grouping separator ' '
        txtMetaMensalCalculada.setDecimal(false);                                               // enables input of decimal numbers, default false
        txtMetaMensalCalculada.setDecimalSeparator(',');                                   // set decimal separator, default '.'
        txtMetaMensalCalculada.setImmediate(true);
        txtMetaMensalCalculada.addStyleName(ValoTheme.TEXTFIELD_ALIGN_RIGHT);

        txtMetaMensalConsolidada.setWidth(10.0f, Unit.EM);
        txtMetaMensalConsolidada.setSigned(false);                                                 // disable negative sign, default true
        txtMetaMensalConsolidada.setUseGrouping(true);                                        // enable grouping, default false
        txtMetaMensalConsolidada.setGroupingSeparator('.');                                  // set grouping separator ' '
        txtMetaMensalConsolidada.setDecimal(false);                                               // enables input of decimal numbers, default false
        txtMetaMensalConsolidada.setDecimalSeparator(',');                                   // set decimal separator, default '.'
        txtMetaMensalConsolidada.setImmediate(true);
        txtMetaMensalConsolidada.addStyleName(ValoTheme.TEXTFIELD_ALIGN_RIGHT);

        txtMetaMensalClientesCalculada.setWidth(10.0f, Unit.EM);
        txtMetaMensalClientesCalculada.setSigned(false);                                                 // disable negative sign, default true
        txtMetaMensalClientesCalculada.setUseGrouping(true);                                        // enable grouping, default false
        txtMetaMensalClientesCalculada.setGroupingSeparator('.');                                  // set grouping separator ' '
        txtMetaMensalClientesCalculada.setDecimal(false);                                               // enables input of decimal numbers, default false
        txtMetaMensalClientesCalculada.setDecimalSeparator(',');                                   // set decimal separator, default '.'
        txtMetaMensalClientesCalculada.setImmediate(true);
        txtMetaMensalClientesCalculada.addStyleName(ValoTheme.TEXTFIELD_ALIGN_RIGHT);

        txtMetaMensalClientesConsolidada.setWidth(10.0f, Unit.EM);
        txtMetaMensalClientesConsolidada.setSigned(false);                                                 // disable negative sign, default true
        txtMetaMensalClientesConsolidada.setUseGrouping(true);                                        // enable grouping, default false
        txtMetaMensalClientesConsolidada.setGroupingSeparator('.');                                  // set grouping separator ' '
        txtMetaMensalClientesConsolidada.setDecimal(false);                                               // enables input of decimal numbers, default false
        txtMetaMensalClientesConsolidada.setDecimalSeparator(',');                                   // set decimal separator, default '.'
        txtMetaMensalClientesConsolidada.setImmediate(true);
        txtMetaMensalClientesConsolidada.addStyleName(ValoTheme.TEXTFIELD_ALIGN_RIGHT);

        btnConsolidar.addStyleName(ValoTheme.BUTTON_DANGER);
        btnConsolidar.addStyleName(ValoTheme.BUTTON_ICON_ALIGN_RIGHT);
        btnConsolidar.setIcon(FontAwesome.HAND_O_RIGHT);
        btnConsolidar.addClickListener(clickEvent -> {
            ConfirmDialog.show(getUI(), "Por favor, confirme.", "Você irá substituir o valor consolidado atual (" +
                            txtMetaMensalConsolidada.getValue() + ") pelo valor calculado com base na soma das metas das lojas (" +
                            txtMetaMensalCalculada.getValue() + ")?",
                    "Sim", "Não", new ConfirmDialog.Listener() {

                        public void onClose(ConfirmDialog dialog) {
                            if (dialog.isConfirmed()) {
                                // Confirmed to continue
                                try {
                                    metaMensalConsolidada.setValor(somaValor);
                                    MetaController.persisteMetaMensal(metaMensalConsolidada, (User) getSession().getAttribute("user"));
                                    txtMetaMensalConsolidada.setReadOnly(false);
                                    txtMetaMensalConsolidada.setValue(MyUI.getDecimalFormat().format(metaMensalConsolidada.getValor()));
                                    txtMetaMensalConsolidada.setReadOnly(true);
                                    Notification.show("FEITO", "Meta Mensal consolidada com sucesso.", Notification.Type.TRAY_NOTIFICATION);
                                } catch (Exception e) {
                                    Notification.show("Não foi possível consolidar a meta mensal.", Notification.Type.ERROR_MESSAGE);
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
                            txtMetaMensalClientesConsolidada.getValue() + ") pelo valor calculado com base na soma das metas das lojas (" +
                            txtMetaMensalClientesCalculada.getValue() + ")?",
                    "Sim", "Não", new ConfirmDialog.Listener() {

                        public void onClose(ConfirmDialog dialog) {
                            if (dialog.isConfirmed()) {
                                // Confirmed to continue
                                try {
                                    metaMensalConsolidada.setClientes(somaClientes);
                                    MetaController.persisteMetaMensal(metaMensalConsolidada, (User) getSession().getAttribute("user"));
                                    txtMetaMensalClientesConsolidada.setReadOnly(false);
                                    txtMetaMensalClientesConsolidada.setValue(MyUI.getDecimalFormat().format(metaMensalConsolidada.getClientes()));
                                    txtMetaMensalClientesConsolidada.setReadOnly(true);
                                    Notification.show("FEITO", "Meta Mensal de Clientes consolidada com sucesso.", Notification.Type.TRAY_NOTIFICATION);
                                } catch (Exception e) {
                                    Notification.show("Não foi possível consolidar a meta Mensal de clientes.", Notification.Type.ERROR_MESSAGE);
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
        panelConsolidarLayout.addComponents(txtMetaMensalCalculada, btnConsolidar, txtMetaMensalConsolidada);

        panelConsolidarLayoutClientes.setSpacing(true);
        panelConsolidarLayoutClientes.setDefaultComponentAlignment(Alignment.BOTTOM_LEFT);
        panelConsolidarLayoutClientes.addComponents(txtMetaMensalClientesCalculada, btnConsolidarClientes, txtMetaMensalClientesConsolidada);

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
        if (comboAnos.getValue() != null && comboMeses.getValue() != null) {
            beans.removeAllItems();
            int ano = Integer.parseInt(comboAnos.getValue().toString());
            String mes = comboMeses.getValue().toString().substring(0, 2);
            
            beans.addAll(MetaController.getMetasMensais(ano, Integer.parseInt(mes)));
            if (beans.size() > 0) {
                for (MetaMensal meta : beans.getItemIds()) {
                    somaValor = somaValor.add(meta.getValor());
                    somaValorRealizado = somaValorRealizado.add(meta.getValorRealizado());

                    somaClientes += meta.getClientes();
                    somaClientesRealizado += meta.getClientesRealizado();
                }
                table.setColumnFooter("valor", MyUI.getDecimalFormatWithDecimal().format(somaValor));
                table.setColumnFooter("valorRealizado", MyUI.getDecimalFormat().format(somaValorRealizado));
                table.setColumnFooter("clientes", MyUI.getDecimalFormat().format(somaClientes));
                table.setColumnFooter("clientesRealizado", MyUI.getDecimalFormat().format(somaClientesRealizado));

                txtMetaMensalCalculada.setReadOnly(false);
                txtMetaMensalCalculada.setValue(MyUI.getDecimalFormat().format(somaValor));
                txtMetaMensalCalculada.setReadOnly(true);
                txtMetaMensalClientesCalculada.setReadOnly(false);
                txtMetaMensalClientesCalculada.setValue(MyUI.getDecimalFormat().format(somaClientes));
                txtMetaMensalClientesCalculada.setReadOnly(true);


                metaMensalConsolidada = MetaController.getMetaMensalConsolidada(Integer.parseInt((String) comboAnos.getValue()), Integer.parseInt(mes));
                txtMetaMensalConsolidada.setReadOnly(false);
                txtMetaMensalConsolidada.setValue(MyUI.getDecimalFormat().format(metaMensalConsolidada.getValor()));
                txtMetaMensalConsolidada.setReadOnly(true);
                txtMetaMensalClientesConsolidada.setReadOnly(false);
                txtMetaMensalClientesConsolidada.setValue(MyUI.getDecimalFormat().format(metaMensalConsolidada.getClientes()));
                txtMetaMensalClientesConsolidada.setReadOnly(true);
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
            Notification.show("Selecione o ano e o mês.", Notification.Type.WARNING_MESSAGE);
        }
    }

    private ExportToExcelUtility<MetaMensal> customizeExportExcelUtility() {
        /* Configuring Components */
        ExportExcelComponentConfiguration componentConfig1 = new ExportExcelComponentConfigurationBuilder().withTable(table)
                .withVisibleProperties(table.getVisibleColumns())
                .withColumnHeaderKeys(table.getColumnHeaders())
                .build();

        /* Configuring Sheets */
        ArrayList<ExportExcelComponentConfiguration> componentList1 = new ArrayList<ExportExcelComponentConfiguration>();
        componentList1.add(componentConfig1);

        ExportExcelSheetConfiguration sheetConfig1 = new ExportExcelSheetConfigurationBuilder().withReportTitle("Projeção de Vendas - Metas Mensais")
                .withSheetName("Metais Mensais")
                .withComponentConfigs(componentList1)
                .withIsHeaderSectionRequired(Boolean.TRUE)
                .build();

        /* Configuring Excel */
        ArrayList<ExportExcelSheetConfiguration> sheetList = new ArrayList<ExportExcelSheetConfiguration>();
        sheetList.add(sheetConfig1);
        ExportExcelConfiguration config1 = new ExportExcelConfigurationBuilder().withGeneratedBy(((User)getSession().getAttribute("user")).getLogin())
                .withSheetConfigs(sheetList)
                .build();

        return new ExportToExcelUtility<MetaMensal>(table.getUI(), config1, MetaMensal.class);
    }

}
