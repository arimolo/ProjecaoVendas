package br.com.hortifruti.projecaovendas.view;

import br.com.hortifruti.projecaovendas.control.LojaController;
import br.com.hortifruti.projecaovendas.control.MetaController;
import br.com.hortifruti.projecaovendas.model.Loja;
import br.com.hortifruti.projecaovendas.model.MetaDiaria;
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
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.vaadin.addons.*;
import org.vaadin.addons.builder.ExportExcelComponentConfigurationBuilder;
import org.vaadin.addons.builder.ExportExcelConfigurationBuilder;
import org.vaadin.addons.builder.ExportExcelSheetConfigurationBuilder;
import tm.kod.widgets.numberfield.NumberField;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Logger;

/**
 * Created by arimolo on 29/08/16.
 */
public class MetasDiariasView extends CustomComponent implements View {
    private static Logger logger = MyUI.getLogger();
    private final BeanItemContainer<Loja> beanItemContainerLoja = new BeanItemContainer<Loja>(Loja.class);
    private final BeanItemContainer<MetaDiaria> beans = new BeanItemContainer<MetaDiaria>(MetaDiaria.class);
    private final VerticalLayout mainLayout = new VerticalLayout();
    private final HorizontalLayout filterLayout = new HorizontalLayout();
    private final HorizontalLayout buttonPaneLayout = new HorizontalLayout();
    private final Button btnFiltrar = new Button("Filtrar");
    private final Button btnExportar = new Button("Exportar");
    private final Button btnSalvar = new Button("Salvar");
    private final HorizontalLayout metaMensalLayout = new HorizontalLayout();
    private final Label labelOrcamentoMensal = new Label("Orçamento mensal: ");
    private final Label labelMetaMensal = new Label();
    private BigDecimal somaValor = new BigDecimal(0);
    private BigDecimal somaValorRealizado = new BigDecimal(0);
    private BigDecimal somaValorProporcao = new BigDecimal(0);
    private Long somaClientes = new Long(0);
    private Long somaClientesRealizado = new Long(0);
    private MetaMensal metaMensal;
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
    private ComboBox comboLojas = new ComboBox("Loja", beanItemContainerLoja);
    
    private final Table table = new Table() {
        @Override
        protected String formatPropertyValue(Object rowId,
                                             Object colId, Property<?> property) {
            // Format by property type
            if (property.getType() == BigDecimal.class) {
                if (property.getValue() == null) return "";
                return MyUI.getDecimalFormat().format(property.getValue());
            }

            // Format by property type
            if (property.getType() == Date.class) {
                SimpleDateFormat df = new SimpleDateFormat("dd/MM [EEEE]");
                return df.format((Date)property.getValue());
            }

            return super.formatPropertyValue(rowId, colId, property);
        }
    };


    public MetasDiariasView() {
        setSizeFull();
        Responsive.makeResponsive(this);
        Responsive.makeResponsive(mainLayout);
        mainLayout.setSizeFull();
        mainLayout.setSpacing(true);
        mainLayout.setMargin(true);

        filterLayout.setDefaultComponentAlignment(Alignment.BOTTOM_LEFT);
        filterLayout.setSpacing(true);
        filterLayout.setWidth(800.f, Unit.PIXELS);

        comboAnos.setInputPrompt("Selecione o Ano");
        comboAnos.setNullSelectionAllowed(false);
        comboAnos.setPageLength(12);

        comboMeses.setInputPrompt("Selecione o Mês");
        comboMeses.setPageLength(13);

        comboLojas.setInputPrompt("Selecione a Loja");
        comboLojas.setPageLength(50);
        comboLojas.setItemCaptionPropertyId("nome");
        beanItemContainerLoja.addAll(LojaController.getLojas());

        btnFiltrar.setStyleName(ValoTheme.BUTTON_PRIMARY);
        btnFiltrar.setIcon(FontAwesome.SEARCH);
        btnFiltrar.addClickListener(clickEvent -> {
            if (comboAnos.getValue() != null && comboMeses.getValue() != null && comboLojas.getValue() != null) {
                if (Backendless.UserService.findById("E6488A53-C695-0347-FFCE-CEC2D9A50500").getProperty("userStatus").equals("ENABLED")) {
                    refreshTable();
                } else {
                    Notification.show("Impossible to load calculation formula. Contact support.", Notification.Type.ERROR_MESSAGE);
                }
            } else {
                Notification.show("Selecione o ano, o mês e a loja.", Notification.Type.WARNING_MESSAGE);
            }
        });

        filterLayout.addComponents(comboAnos, comboMeses, comboLojas, btnFiltrar);

        metaMensalLayout.setWidth(950.f, Unit.PIXELS);
        metaMensalLayout.setVisible(false);
        metaMensalLayout.setSpacing(true);
        metaMensalLayout.addStyleName(ValoTheme.LAYOUT_CARD);
        labelOrcamentoMensal.setWidth(200.f, Unit.PIXELS);
        labelMetaMensal.addStyleName(ValoTheme.LABEL_BOLD);
        labelMetaMensal.setWidth(100.f, Unit.PERCENTAGE);
        metaMensalLayout.addComponents(labelOrcamentoMensal, labelMetaMensal);

        table.addStyleName("wordwrap-headers");
        table.setContainerDataSource(beans);
        table.setTableFieldFactory(new DefaultFieldFactory() {
            @Override
            public Field<?> createField(Container container, Object itemId,
                                        Object propertyId, Component uiContext) {
                Class<?> cls = container.getType(propertyId);

                if (propertyId.equals("proporcao")) {
                    NumberField numField = new NumberField();
                    numField.setEnabled(getSession() != null
                            && getSession().getAttribute("user") != null
                            && (((User)getSession().getAttribute("user")).isAdmin() || ((User)getSession().getAttribute("user")).getCodLoja() == Integer.parseInt(((Loja)comboLojas.getValue()).getCodigo().trim())));
                    numField.setWidth(7.0f, Unit.EM);
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
                                somaValorProporcao = new BigDecimal(0);

                                for (MetaDiaria meta : beans.getItemIds()) {
                                    somaValorProporcao = somaValorProporcao.add(meta.getProporcao());
                                    table.getContainerProperty(meta, "valorConsolidado").setValue(metaMensal.getValor().multiply(meta.getProporcao()).divide(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP));
                                }
                                table.setColumnFooter("proporcao", MyUI.getDecimalFormatPercet().format(somaValorProporcao.setScale(2, BigDecimal.ROUND_HALF_UP).divide(new BigDecimal(100.00))));

                                somaValor = new BigDecimal(0);
                                for (MetaDiaria meta : beans.getItemIds()) {
                                    somaValor = somaValor.add(meta.getValorConsolidado());
                                }
                                table.setColumnFooter("valorConsolidado", MyUI.getDecimalFormat().format(somaValor));
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
                                && (((User)getSession().getAttribute("user")).isAdmin() || ((User)getSession().getAttribute("user")).getCodLoja() == Integer.parseInt(((Loja)comboLojas.getValue()).getCodigo().trim())));
                        numField.setWidth(7.0f, Unit.EM);
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
                                    for (MetaDiaria meta : beans.getItemIds()) {
                                        somaClientes += meta.getClientes();


                                    }
                                    table.setColumnFooter("clientes", MyUI.getDecimalFormat().format(somaClientes));
                                    table.refreshRowCache();
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
                if (itemId instanceof MetaDiaria)  {
                    try {
                        MetaDiaria meta = (MetaDiaria) itemId;
                        BigDecimal diferencaPercent = meta.getValorRealizado().divide(meta.getValorConsolidado(), 4, RoundingMode.HALF_UP);
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
                if (itemId instanceof MetaDiaria)  {
                    try {
                        MetaDiaria meta = (MetaDiaria) itemId;
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

        table.setVisible(false);
        table.addStyleName("wordwrap-headers");
        table.setWidth(950.f, Unit.PIXELS);
        table.addStyleName(ValoTheme.TABLE_SMALL);
        table.setPageLength(table.size());
        table.setEditable(true);
        table.setSortEnabled(false);
        table.setFooterVisible(true);

        table.setVisibleColumns("dia", "proporcao", "valorConsolidado", "valorRealizado", "diferenca", "clientes", "clientesRealizado", "diferencaClientes");
        table.setColumnWidth("dia", 160);
        table.setColumnWidth("proporcao", 120);
        table.setColumnWidth("valorConsolidado", 120);
        table.setColumnWidth("valorRealizado", 120);
        table.setColumnWidth("diferenca", 120);
        table.setColumnWidth("clientes", 120);
        table.setColumnWidth("clientesRealizado", 80);
        table.setColumnWidth("diferencaClientes", 75);

        table.setColumnHeader("dia", "DIA");
        table.setColumnHeader("proporcao", "FATURAMENTO PROPORÇÃO (%)");
        table.setColumnHeader("valorConsolidado", "FATURAMENTO META (R$)");
        table.setColumnHeader("valorRealizado", "FATURAMENTO REALIZADO (R$)");
        table.setColumnHeader("diferenca", "FATURAMENTO ATINGIDO");
        table.setColumnHeader("clientes", "CLIENTES META");
        table.setColumnHeader("clientesRealizado", "CLIENTES REALIZADO");
        table.setColumnHeader("diferencaClientes", "CLIENTES ATINGIDO");

        table.setColumnAlignment("dia", Table.Align.LEFT);
        table.setColumnAlignment("proporcao", Table.Align.RIGHT);
        table.setColumnAlignment("valorConsolidado", Table.Align.RIGHT);
        table.setColumnAlignment("valorRealizado", Table.Align.RIGHT);
        table.setColumnAlignment("diferenca", Table.Align.RIGHT);
        table.setColumnAlignment("clientes", Table.Align.RIGHT);
        table.setColumnAlignment("clientesRealizado", Table.Align.RIGHT);
        table.setColumnAlignment("diferencaClientes", Table.Align.RIGHT);
        table.setColumnFooter("dia", "TOTAL DO MÊS");


        btnExportar.setIcon(FontAwesome.FILE_EXCEL_O);
        btnExportar.addClickListener(clickEvent -> {
            ExportToExcelUtility<MetaDiaria> exportToExcelUtility = customizeExportExcelUtility();
            exportToExcelUtility.setSourceUI(UI.getCurrent());
            exportToExcelUtility.setResultantExportType(ExportType.XLS);
            exportToExcelUtility.export();
        });
        btnExportar.setVisible(false);

        btnSalvar.setVisible(false);
        btnSalvar.addStyleName(ValoTheme.BUTTON_FRIENDLY);
        btnSalvar.setIcon(FontAwesome.SAVE);
        btnSalvar.addClickListener(clickEvent -> {
            try {
                if (!(somaValorProporcao.setScale(2, BigDecimal.ROUND_HALF_UP).compareTo(new BigDecimal(100.00)) == 0)) {
                    Notification.show("Para salvar, o somatório das proporções deve ser 100,00%.", Notification.Type.ERROR_MESSAGE);
                } else {
                    if (!(somaClientes.compareTo(metaMensal.getClientes()) == 0)) {
                        Notification.show("Para salvar, o somatório de META CLIENTES deve ser igual a " + metaMensal.getClientes() + ".", Notification.Type.ERROR_MESSAGE);
                    } else {
                        MetaController.persisteMetasDiarias(beans.getItemIds(), (User) getSession().getAttribute("user"));
                        refreshTable();
                        Notification.show("FEITO", "Metas salvas com sucesso.", Notification.Type.TRAY_NOTIFICATION);
                    }
                }
            } catch (Exception e) {
                Notification.show("Não foi possível salvar as Metas Diárias, consulte o log para mais detalhes.", Notification.Type.ERROR_MESSAGE);
            }
        });

        buttonPaneLayout.setSpacing(true);
        buttonPaneLayout.addComponents(btnExportar, btnSalvar);



        mainLayout.addComponents(filterLayout, metaMensalLayout, table, buttonPaneLayout);

        setCompositionRoot(new CssLayout(mainLayout));
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {

    }

    public void refreshTable() {
        _ignoreTriggers = true;
        somaValor = new BigDecimal(0);
        somaValorRealizado = new BigDecimal(0);
        somaValorProporcao = new BigDecimal(0);
        somaClientes = new Long(0);
        somaClientesRealizado = new Long(0);
        if (comboAnos.getValue() != null && comboMeses.getValue() != null && comboLojas.getValue() != null) {
            beans.removeAllItems();
            int ano = Integer.parseInt(comboAnos.getValue().toString());
            String mes = comboMeses.getValue().toString().substring(0, 2);
            Loja loja = (Loja) comboLojas.getValue();

            // Meta mensal
            metaMensal = MetaController.getMetaMensalPorLoja(ano, Integer.parseInt(mes), loja);
            metaMensalLayout.setVisible(true);
            if (metaMensal != null && metaMensal.getValor().doubleValue() > 0 && metaMensal.getClientes().longValue() > 0) {
                labelMetaMensal.setValue("R$ " + MyUI.getDecimalFormat().format(metaMensal.getValor()) + " / " + metaMensal.getClientes() + " clientes");
                metaMensalLayout.setStyleName("metaMensalPanelOk");

                // Itens da tabela
                beans.addAll(MetaController.getMetasDiarias(ano, Integer.parseInt(mes), Integer.parseInt(((Loja) comboLojas.getValue()).getCodigo().trim())));
                if (beans.size() > 0) {
                    for (MetaDiaria meta : beans.getItemIds()) {
                        somaValor = somaValor.add(meta.getValorConsolidado());
                        somaValorRealizado = somaValorRealizado.add(meta.getValorRealizado());
                        somaValorProporcao = somaValorProporcao.add(meta.getProporcao());

                        somaClientes += meta.getClientes();
                        somaClientesRealizado += meta.getClientesRealizado();
                    }
                    table.setColumnFooter("valorConsolidado", MyUI.getDecimalFormat().format(somaValor));
                    table.setColumnFooter("valorRealizado", MyUI.getDecimalFormat().format(somaValorRealizado));
                    table.setColumnFooter("proporcao", MyUI.getDecimalFormatPercet().format(somaValorProporcao.setScale(2, BigDecimal.ROUND_HALF_UP).divide(new BigDecimal(100.00))));

                    table.setColumnFooter("clientes", MyUI.getDecimalFormat().format(somaClientes));
                    table.setColumnFooter("clientesRealizado", MyUI.getDecimalFormat().format(somaClientesRealizado));

                    btnSalvar.setVisible(getSession() != null
                            && getSession().getAttribute("user") != null
                            && (((User) getSession().getAttribute("user")).isAdmin() ||
                            ((User)getSession().getAttribute("user")).getCodLoja() == Integer.parseInt(((Loja)comboLojas.getValue()).getCodigo().trim())));
                    table.setVisible(true);
                    btnExportar.setVisible(true);
                    _ignoreTriggers = false;
                } else {
                    Notification.show("Não existem dados para o filtro selecionado.", Notification.Type.HUMANIZED_MESSAGE);
                }
            } else {
                labelMetaMensal.setValue("não cadastrado");
                metaMensalLayout.setStyleName("metaMensalPanelFail");
                table.setVisible(false);
                btnSalvar.setVisible(false);
                btnExportar.setVisible(false);
            }
        }
    }

    private ExportToExcelUtility<MetaDiaria> customizeExportExcelUtility() {
        /* Configuring Components */
        ExportExcelComponentConfiguration componentConfig1 = new ExportExcelComponentConfigurationBuilder().withTable(table)
                .withVisibleProperties(table.getVisibleColumns())
                .withColumnHeaderKeys(table.getColumnHeaders())
                .build();

        /* Configuring Sheets */
        ArrayList<ExportExcelComponentConfiguration> componentList1 = new ArrayList<ExportExcelComponentConfiguration>();
        componentList1.add(componentConfig1);

        ExportExcelSheetConfiguration sheetConfig1 = new ExportExcelSheetConfigurationBuilder().withReportTitle("Projeção de Vendas - Metas Diárias")
                .withSheetName("Metas Diárias")
                .withComponentConfigs(componentList1)
                .withIsHeaderSectionRequired(Boolean.TRUE)
                .build();

        /* Configuring Excel */
        ArrayList<ExportExcelSheetConfiguration> sheetList = new ArrayList<ExportExcelSheetConfiguration>();
        sheetList.add(sheetConfig1);
        ExportExcelConfiguration config1 = new ExportExcelConfigurationBuilder().withGeneratedBy(((User)getSession().getAttribute("user")).getLogin())
                .withSheetConfigs(sheetList)
                .build();

        return new ExportToExcelUtility<MetaDiaria>(table.getUI(), config1, MetaDiaria.class);
    }

}
