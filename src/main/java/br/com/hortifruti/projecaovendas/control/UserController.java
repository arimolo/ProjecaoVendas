package br.com.hortifruti.projecaovendas.control;

import br.com.hortifruti.projecaovendas.dao.LojaDAO;
import br.com.hortifruti.projecaovendas.dao.UserDAO;
import br.com.hortifruti.projecaovendas.model.Loja;
import br.com.hortifruti.projecaovendas.model.User;
import br.com.hortifruti.projecaovendas.util.UserAuthException;
import br.com.hortifruti.projecaovendas.view.MyUI;
import com.backendless.Backendless;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.ldap.sdk.*;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.controls.SimplePagedResultsControl;
import com.unboundid.util.LDAPTestUtils;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.directory.*;
import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Created by arimolo on 26/08/16.
 */
public class UserController {
    private static Logger logger = MyUI.getLogger();
    public static final EntityManager em = JPAContainerFactory.createEntityManagerForPersistenceUnit("DADOSHFPU");
    private static final String LDAP_HOST = "ldap01.hortifrutisa.local";
    private static final Integer LDAP_PORT = 389;
    private static final String LDAP_PROVIDER_URL = "ldap://" + LDAP_HOST + ":" + LDAP_PORT;
    private static final String LDAP_DOMAIN = "HORTIFRUTISA";
    private static final String LDAP_SECURITY_PRINCIPAL = "liferay";
    private static final String LDAP_SECURITY_CREDENTIALS = "6z#Wmikp2";
    private static final UserDAO userDAO = new UserDAO();
    private static final LojaDAO lojaDAO = new LojaDAO();
    private static final List<Loja> listLojas = new ArrayList();

    public static boolean ldapAuth(String login, String password) {
        try {
            Hashtable<String, String> ldapEnv = new Hashtable<>(11);
            ldapEnv.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            ldapEnv.put(Context.PROVIDER_URL, LDAP_PROVIDER_URL);
            ldapEnv.put(Context.SECURITY_AUTHENTICATION, "simple");
            ldapEnv.put(Context.SECURITY_PRINCIPAL, login + "@" + LDAP_DOMAIN);
            ldapEnv.put(Context.SECURITY_CREDENTIALS, password);
            DirContext ldapContext = new InitialDirContext(ldapEnv);
            return ldapContext != null;
        } catch (AuthenticationException aex) {
            logger.log(Level.INFO, "Login falhou para o username " + login + ". (AD Fail)");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Falha na autenticação pelo AD", e);
        }
        return false;
    }

    public static User doLogin(String login, String password) throws Exception {
        if (ldapAuth(login, password)) {
            User user = userDAO.getUserByLogin(login);
            if (user != null) {
                logger.log(Level.INFO, String.format("Login bem sucedido para o username %s.", login));
                return user;
            } else {
                throw new UserAuthException("Usuário não cadastrado no sistema Projeção de Vendas.");
            }
        } else {
            throw new UserAuthException("Login / senha não autenticado pelo AD.");
        }
    }

    public static List<User> getLdapUsers() {

        try (LDAPConnection conn = new LDAPConnection()){

            conn.connect(LDAP_HOST, LDAP_PORT);
            conn.bind(LDAP_SECURITY_PRINCIPAL, LDAP_SECURITY_CREDENTIALS);

            List<User> users = new ArrayList<>();
            SearchRequest searchRequest = new SearchRequest("OU=UNIT,OU=HORTIFRUTISA,DC=hortifrutisa,DC=local",
                    SearchScope.SUB, Filter.createANDFilter(Filter.createEqualityFilter("objectCategory", "person"), Filter.createEqualityFilter("objectClass", "user")));

            ASN1OctetString resumeCookie = null;
            Long generatedId = 1l;
            while (true) {

                searchRequest.setControls(new SimplePagedResultsControl(10, resumeCookie));
                SearchResult searchResult = conn.search(searchRequest);

                for (SearchResultEntry e : searchResult.getSearchEntries()) {
                    User user = new User();
                    user.setLogin(e.getAttributeValue("samAccountName"));
                    user.setEmail(e.getAttributeValue("samAccountName") + "@hortifruti.com.br");
                    user.setNome(e.getAttributeValue("CN"));
                    user.setPerfil(User.PERFIL_NORMAL);

                    String[] parts = e.getDN().split(",");
                    parts = parts[2].split("=");
                    String sigla = parts[1];

                    List<Loja> result = getListLojas().stream()
                            .filter(item -> item.getSigla().equals(sigla))
                            .collect(Collectors.toList());
                    if (result != null && !result.isEmpty()) {
                        user.setCodLoja(Integer.parseInt(result.get(0).getCodigo().trim()));
                        user.setLoja(result.get(0).getNome());
                        user.setId(generatedId++);
                        users.add(user);
                    }
                }

                LDAPTestUtils.assertHasControl(searchResult, SimplePagedResultsControl.PAGED_RESULTS_OID);
                SimplePagedResultsControl responseControl = SimplePagedResultsControl.get(searchResult);

                if (responseControl.moreResultsToReturn()) {
                    // The resume cookie can be included in the simple paged results
                    // control included in the next search to get the next page of results.
                    resumeCookie = responseControl.getCookie();
                }
                else {
                    break;
                }
            }

            return users;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Não foi possível recuperar os usuários via LDAP.", e);
        }
        return null;
    }

    public static List<User> getUsers() {
        return userDAO.getUsers();
    }

    private static List<Loja>  getListLojas() {
        if (listLojas.isEmpty()) {
            listLojas.addAll(lojaDAO.getLojas());
        }
        return listLojas;
    }

    public static void persisteUser(User user) throws Exception {
        em.getTransaction().begin();
        try {
            user.setId(null);
            em.persist(user);
            em.flush();
            em.clear();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        }
        em.getTransaction().commit();
    }

    public static void deleteUser(User user) throws Exception {
        em.getTransaction().begin();
        try {
            user = em.merge(user);
            em.remove(user);
            em.flush();
            em.clear();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        }
        em.getTransaction().commit();
    }
}