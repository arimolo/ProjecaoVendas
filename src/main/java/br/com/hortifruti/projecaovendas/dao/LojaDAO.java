package br.com.hortifruti.projecaovendas.dao;

import br.com.hortifruti.projecaovendas.model.Loja;
import br.com.hortifruti.projecaovendas.view.MyUI;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by arimolo on 25/08/16.
 */

public class LojaDAO {
    private static Logger logger = MyUI.getLogger();
    public static EntityManager em = JPAContainerFactory.createEntityManagerForPersistenceUnit("DADOSHFPU");

    public List<Loja> getLojas() {
        try {
            TypedQuery<Loja> query = em.createQuery("SELECT l FROM Loja l WHERE l.codigo not in (' 03', ' 68')", Loja.class);
            List<Loja> result = query.getResultList();
            return result;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Falha obtenção das Lojas", e);
        }
        return null;
    }
}
