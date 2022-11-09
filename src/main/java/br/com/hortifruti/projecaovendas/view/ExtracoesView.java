package br.com.hortifruti.projecaovendas.view;

import br.com.hortifruti.projecaovendas.control.LojaController;
import br.com.hortifruti.projecaovendas.control.MetaController;
import br.com.hortifruti.projecaovendas.model.Loja;
import br.com.hortifruti.projecaovendas.model.MetaDiaria;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FileResource;
import com.vaadin.server.Page;
import com.vaadin.server.ResourceReference;
import com.vaadin.server.Responsive;
import com.vaadin.ui.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.File;

/**
 * Created by arimolo on 03/10/16.
 */
public class ExtracoesView extends CustomComponent implements View {
    private final VerticalLayout mainLayout = new VerticalLayout();
    private final HorizontalLayout filterLayout = new HorizontalLayout();
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
    private final HorizontalLayout buttonPaneLayout = new HorizontalLayout();
    private final Button btnExtrairOrcado = new Button("Extrair Orçado");
    private final Button btnExtrairRealizado = new Button("Extrair Realizado");
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");


    public ExtracoesView() {
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

        filterLayout.addComponents(comboAnos, comboMeses);

        buttonPaneLayout.setSpacing(true);
        buttonPaneLayout.addComponents(btnExtrairOrcado, btnExtrairRealizado);

        mainLayout.addComponents(filterLayout, buttonPaneLayout);

        setCompositionRoot(new CssLayout(mainLayout));

        btnExtrairOrcado.addClickListener(clickEvent -> {
            int ano = Integer.parseInt(comboAnos.getValue().toString());
            int mes = Integer.parseInt(comboMeses.getValue().toString().substring(0,2));

            // New Workbook
            Workbook wb = new SXSSFWorkbook();

            // Cell style for header row
            CellStyle cellStyle = wb.createCellStyle();
            cellStyle.setFillForegroundColor(IndexedColors.LIME.getIndex());
            Font f = wb.createFont();
            f.setBold(true);
            f.setFontHeightInPoints((short) 12);
            cellStyle.setFont(f);

            Cell cell = null;

            // Getting Lojas once we'll create a sheet for Loja
            List<Loja> lojas = LojaController.getLojas();
            lojas.remove(0);

            int rowNum = 0;
            for (Loja loja: lojas) {
                // New Sheet
                SXSSFSheet sheet = (SXSSFSheet) wb.createSheet(loja.getNome());

                // Generate column headings
                Row row = sheet.createRow(rowNum++);
                cell = row.createCell(0);
                cell.setCellValue("DIA");
                cell.setCellStyle(cellStyle);
                sheet.setColumnWidth(0, 4500);

                cell = row.createCell(1);
                cell.setCellValue("FATURAMENTO PROPORÇÃO (%)");
                cell.setCellStyle(cellStyle);
                sheet.setColumnWidth(1, 5000);

                cell = row.createCell(2);
                cell.setCellValue("FATURAMENTO VALOR (R$)");
                cell.setCellStyle(cellStyle);
                sheet.setColumnWidth(2, 5000);

                cell = row.createCell(3);
                cell.setCellValue("CLIENTES QTD");
                cell.setCellStyle(cellStyle);
                sheet.setColumnWidth(3, 5000);

                List<MetaDiaria> metaDiarias = MetaController.getMetasDiariasSoOrcado(ano, mes, Integer.parseInt(loja.getCodigo().trim()));

                BigDecimal somaProporcao = new BigDecimal(0);
                BigDecimal somaValor = new BigDecimal(0);
                Long somaClientes = new Long(0);
                for (MetaDiaria metaDiaria : metaDiarias) {
                    row = sheet.createRow(rowNum++);

                    cell = row.createCell(0);
                    cell.setCellValue(dateFormat.format(metaDiaria.getDia()));

                    cell = row.createCell(1);
                    cell.setCellType(CellType.NUMERIC);
                    cell.setCellValue(metaDiaria.getProporcao().doubleValue());

                    cell = row.createCell(2);
                    cell.setCellType(CellType.NUMERIC);
                    cell.setCellValue(metaDiaria.getValorConsolidado().doubleValue());

                    cell = row.createCell(3);
                    cell.setCellType(CellType.NUMERIC);
                    cell.setCellValue(metaDiaria.getClientes().longValue());

                    somaProporcao = somaProporcao.add(metaDiaria.getProporcao());
                    somaValor = somaValor.add(metaDiaria.getValorConsolidado());
                    somaClientes += metaDiaria.getClientes();
                }

                row = sheet.createRow(rowNum++);
                cell = row.createCell(0);
                cell.setCellStyle(cellStyle);
                cell.setCellValue("TOTAIS");

                cell = row.createCell(1);
                cell.setCellStyle(cellStyle);
                cell.setCellType(CellType.NUMERIC);
                cell.setCellValue(somaProporcao.doubleValue());

                cell = row.createCell(2);
                cell.setCellStyle(cellStyle);
                cell.setCellType(CellType.NUMERIC);
                cell.setCellValue(somaValor.doubleValue());

                cell = row.createCell(3);
                cell.setCellStyle(cellStyle);
                cell.setCellType(CellType.NUMERIC);
                cell.setCellValue(somaClientes.longValue());


                rowNum = 0;
            }

            try {
                String fileName = "orçado-" + mes + "-" + ano + ".xls";
                FileOutputStream fileOut = new FileOutputStream(fileName);
                wb.write(fileOut);
                fileOut.close();
                wb.close();

                FileResource res = new FileResource(new File(fileName));
                setResource("download", res);
                ResourceReference rr = ResourceReference.create(res, this, "download");
                Page.getCurrent().open(rr.getURL(), null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });


        btnExtrairRealizado.addClickListener(clickEvent -> {
            int ano = Integer.parseInt(comboAnos.getValue().toString());
            int mes = Integer.parseInt(comboMeses.getValue().toString().substring(0,2));

            // New Workbook
            Workbook wb = new SXSSFWorkbook();

            // Cell style for header row
            CellStyle cellStyle = wb.createCellStyle();
            cellStyle.setFillForegroundColor(IndexedColors.LIME.getIndex());
            Font f = wb.createFont();
            f.setBold(true);
            f.setFontHeightInPoints((short) 12);
            cellStyle.setFont(f);

            Cell cell = null;

            // Getting Lojas once we'll create a sheet for Loja
            List<Loja> lojas = LojaController.getLojas();
            lojas.remove(0);

            int rowNum = 0;
            for (Loja loja: lojas) {
                // New Sheet
                SXSSFSheet sheet = (SXSSFSheet) wb.createSheet(loja.getNome());

                // Generate column headings
                Row row = sheet.createRow(rowNum++);
                cell = row.createCell(0);
                cell.setCellValue("DIA");
                cell.setCellStyle(cellStyle);
                sheet.setColumnWidth(0, 4500);

                cell = row.createCell(1);
                cell.setCellValue("FATURADO (R$)");
                cell.setCellStyle(cellStyle);
                sheet.setColumnWidth(1, 5000);

                cell = row.createCell(2);
                cell.setCellValue("CLIENTES");
                cell.setCellStyle(cellStyle);
                sheet.setColumnWidth(2, 5000);


                List<MetaDiaria> metaDiarias = MetaController.getMetasDiarias(ano, mes, Integer.parseInt(loja.getCodigo().trim()));

                BigDecimal somaValor = new BigDecimal(0);
                Long somaClientes = new Long(0l);
                for (MetaDiaria metaDiaria : metaDiarias) {
                    row = sheet.createRow(rowNum++);

                    cell = row.createCell(0);

                    cell.setCellValue(dateFormat.format(metaDiaria.getDia()));

                    cell = row.createCell(1);
                    cell.setCellType(CellType.NUMERIC);
                    cell.setCellValue(metaDiaria.getValorRealizado().doubleValue());

                    cell = row.createCell(2);
                    cell.setCellType(CellType.NUMERIC);
                    cell.setCellValue(metaDiaria.getClientesRealizado());
                    somaValor = somaValor.add(metaDiaria.getValorRealizado());
                    somaClientes += metaDiaria.getClientesRealizado();
                }

                row = sheet.createRow(rowNum++);
                cell = row.createCell(0);
                cell.setCellStyle(cellStyle);
                cell.setCellValue("TOTAIS");

                cell = row.createCell(1);
                cell.setCellStyle(cellStyle);
                cell.setCellType(CellType.NUMERIC);
                cell.setCellValue(somaValor.doubleValue());

                cell = row.createCell(2);
                cell.setCellStyle(cellStyle);
                cell.setCellType(CellType.NUMERIC);
                cell.setCellValue(somaClientes);

                rowNum = 0;
            }

            try {
                String fileName = "realizado-" + mes + "-" + ano + ".xls";
                FileOutputStream fileOut = new FileOutputStream(fileName);
                wb.write(fileOut);
                fileOut.close();
                wb.close();

                FileResource res = new FileResource(new File(fileName));
                setResource("download", res);
                ResourceReference rr = ResourceReference.create(res, this, "download");
                Page.getCurrent().open(rr.getURL(), null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {

    }
}
