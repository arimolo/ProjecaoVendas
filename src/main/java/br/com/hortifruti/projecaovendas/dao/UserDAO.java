package br.com.hortifruti.projecaovendas.dao;

import br.com.hortifruti.projecaovendas.control.UserController;
import br.com.hortifruti.projecaovendas.model.User;
import br.com.hortifruti.projecaovendas.view.MyUI;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;

import javax.naming.Context;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by arimolo on 25/08/16.
 */
public class UserDAO {
    private static Logger logger = MyUI.getLogger();
    EntityManager em = JPAContainerFactory.createEntityManagerForPersistenceUnit("DADOSHFPU");

    public User getUserByLogin(String login) {
        try {
            TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.login = :login", User.class);
            query.setParameter("login", login);

            User user = query.getSingleResult();
            em.refresh(user);
            return user;
        } catch (NoResultException noResultException) {
            logger.log(Level.INFO, "Login falhou para o username " + login + ". (Not in DB)");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Falha na consulta por usuários por login para o username " + login + "." , e);
        }
        return null;
    }

    public List<User> getUsers() {
        try {
            Query nativeQuery = em.createNativeQuery(
                    "SELECT U.*, F.NOME_FIL as loja " +
                            " FROM PAN_USERS U left OUTER join FILIAL F " +
                            " ON  U.codLoja = F.CODIGO_FIL " +
                            " ORDER BY U.perfil, U.login", "Users");
            List<User> result = nativeQuery.getResultList();
            return result;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Falha ao recuperar os usuários.", e);
        }
        return null;
    }

}
