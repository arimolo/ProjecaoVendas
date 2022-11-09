package br.com.hortifruti.projecaovendas.control;

import br.com.hortifruti.projecaovendas.dao.LojaDAO;
import br.com.hortifruti.projecaovendas.model.Loja;
import br.com.hortifruti.projecaovendas.view.MyUI;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by arimolo on 30/08/16.
 */
public class LojaController {
    private static Logger logger = MyUI.getLogger();
    private static final LojaDAO lojaDAO = new LojaDAO();

    public static List<Loja> getLojas() {
        ArrayList<Loja> lojas = new ArrayList<>();
        lojas.add(new Loja("0", "TODAS AS LOJAS", "ZZ", "-"));
        lojas.addAll(lojaDAO.getLojas());
        return lojas;
    }
}
