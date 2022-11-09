package br.com.hortifruti.projecaovendas.control;

import br.com.hortifruti.projecaovendas.dao.MetaDAO;
import br.com.hortifruti.projecaovendas.model.*;
import br.com.hortifruti.projecaovendas.view.MyUI;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import javax.persistence.EntityManager;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by arimolo on 30/08/16.
 */
public class MetaController {
    private static Logger logger = MyUI.getLogger();
    public static EntityManager em = JPAContainerFactory.createEntityManagerForPersistenceUnit("DADOSHFPU");

    private static final MetaDAO metaDAO = new MetaDAO();

    public static List<MetaAnual> getMetasAnuais(Integer ano) {
        return metaDAO.getMetasAnuais(ano);
    }

    public static MetaAnual getMetaAnualConsolidada(Integer ano) { return metaDAO.getMetaAnualConsolidada(ano); }

    public static void persisteMetaAnual(MetaAnual metaAnual, User user) throws Exception {
        em.getTransaction().begin();
        try {
            metaAnual = em.merge(metaAnual);
            metaAnual.setUserLogin(user.getLogin());
            em.persist(metaAnual);
            em.flush();
        } catch (Exception e) {
            e.printStackTrace();
            logger.log(Level.SEVERE, "Não foi possível salvar a Meta Anual.", e);
            em.getTransaction().rollback();
            throw e;
        }
        em.getTransaction().commit();
    }

    public static void persisteMetasAnuais(List<MetaAnual> metas, User user) throws Exception {
        em.getTransaction().begin();
        try {
            for (MetaAnual metaAnual : metas) {
                metaAnual = em.merge(metaAnual);
                metaAnual.setUserLogin(user.getLogin());
                em.persist(metaAnual);
                em.flush();
                em.clear();
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Não foi possível salvar as Metas Anuais.", e);
            em.getTransaction().rollback();
            throw e;
        }
        em.getTransaction().commit();
    }


    /* METAS MENSAIS */

    public static List<MetaMensal> getMetasMensais(Integer ano, Integer mes) {
        return metaDAO.getMetasMensais(ano, mes);
    }

    public static MetaMensal getMetaMensalConsolidada(Integer ano, Integer mes) { return metaDAO.getMetaMensalConsolidada(ano, mes); }
    public static MetaMensal getMetaMensalPorLoja(Integer ano, Integer mes, Loja loja) { return metaDAO.getMetaMensalPorLoja(ano, mes, loja); }

    public static void persisteMetaMensal(MetaMensal metaMensal, User user) throws Exception {
        em.getTransaction().begin();
        try {
            metaMensal = em.merge(metaMensal);
            metaMensal.setUserLogin(user.getLogin());
            em.persist(metaMensal);
            em.flush();
            em.clear();
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            logger.log(Level.SEVERE, "Não foi possível salvar a Meta Mensal.", e);
            em.getTransaction().rollback();
            throw e;
        }
    }

    public static void persisteMetasMensais(List<MetaMensal> metas, User user) throws Exception {
        em.getTransaction().begin();
        try {
            for (MetaMensal metaMensal : metas) {
                metaMensal = em.merge(metaMensal);
                metaMensal.setUserLogin(user.getLogin());
                em.persist(metaMensal);
                em.flush();
                em.clear();
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Não foi possível salvar as Metas Mensais.", e);
            em.getTransaction().rollback();
            throw e;
        }
        em.getTransaction().commit();
    }

     /* METAS DIARIAS */

    public static List<MetaDiaria> getMetasDiarias(Integer ano, Integer mes, Integer codLoja) {
        return metaDAO.getMetasDiarias(ano, mes, codLoja);
    }

    public static List<MetaDiaria> getMetasDiariasSoOrcado(Integer ano, Integer mes, Integer codLoja) {
        return metaDAO.getMetasDiariasSoOrcado(ano, mes, codLoja);
    }


    public static void persisteMetaDiaria(MetaDiaria metaDiaria, User user) throws Exception {
        em.getTransaction().begin();
        try {
            metaDiaria = em.merge(metaDiaria);
            metaDiaria.setUserLogin(user.getLogin());
            em.persist(metaDiaria);
            em.flush();
            em.clear();
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            logger.log(Level.SEVERE, "Não foi possível salvar a Meta Diaria.", e);
            em.getTransaction().rollback();
            throw e;
        }
    }

    public static void persisteMetasDiarias(List<MetaDiaria> metas, User user) throws Exception {
        em.getTransaction().begin();
        try {
            for (MetaDiaria metaDiaria : metas) {
                metaDiaria = em.merge(metaDiaria);
                metaDiaria.setUserLogin(user.getLogin());
                em.persist(metaDiaria);
                em.flush();
                em.clear();
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Não foi possível salvar as Metas Diarias.", e);
            em.getTransaction().rollback();
            throw e;
        }
        em.getTransaction().commit();
    }
}
