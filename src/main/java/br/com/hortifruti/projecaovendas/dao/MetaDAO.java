package br.com.hortifruti.projecaovendas.dao;

import br.com.hortifruti.projecaovendas.model.Loja;
import br.com.hortifruti.projecaovendas.model.MetaAnual;
import br.com.hortifruti.projecaovendas.model.MetaDiaria;
import br.com.hortifruti.projecaovendas.model.MetaMensal;
import br.com.hortifruti.projecaovendas.view.MyUI;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import javax.persistence.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by arimolo on 25/08/16.
 */
public class MetaDAO {
    private static Logger logger = MyUI.getLogger();
    public static EntityManager em = JPAContainerFactory.createEntityManagerForPersistenceUnit("DADOSHFPU");

    public List<MetaAnual> getMetasAnuais(Integer ano) {
        try {
            Query nativeQuery = em.createNativeQuery(
                    "SELECT M.*, ISNULL((SELECT SUM(FATURAMENTO) FROM VIEW_VENDA_TOTAL WITH (NOLOCK)" +
                            "WHERE YEAR(CAST(DATA_VEN as date)) = M.ano AND M.codLoja = FILIAL_VEN), 0) as valorRealizado, " +
                            "            F.NOME_FIL as nomeLoja, " +
                            "            ISNULL((SELECT SUM(CUPONS) FROM VIEW_VENDA_TOTAL WITH (NOLOCK)" +
                            "            WHERE YEAR(CAST(DATA_VEN as date)) = M.ano AND M.codLoja = FILIAL_VEN), 0) as clientesRealizado " +
                            "FROM PAN_METAS_ANUAIS M, FILIAL F " +
                            "WHERE M.codLoja = F.CODIGO_FIL " +
                            "      AND   M.ano = ?1 " +
                            "      AND M.codLoja NOT IN (3, 68) " +
                            "                                                  ORDER BY ano, codLoja", "MetasAnuais");
            nativeQuery.setParameter(1, ano);
            List<MetaAnual> result = nativeQuery.getResultList();
            return result;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Falha ao recuperar Metas Anuais.", e);
        }
        return null;
    }

    public MetaAnual getMetaAnualConsolidada(Integer ano) {
        try {
            Query nativeQuery = em.createQuery("select m from MetaAnual m where m.codLoja = 0 and m.ano = :ano");
            nativeQuery.setParameter("ano", ano);
            MetaAnual result = (MetaAnual) nativeQuery.getSingleResult();
            return result;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Falha ao recuperar Metas Anual consolidada.", e);
        }
        return null;
    }

    /* Metas Mensais */
    public List<MetaMensal> getMetasMensais(Integer ano, Integer mes) {
        try {
            Query nativeQuery = em.createNativeQuery(
                    " DECLARE @ANO INT = ?1;   " +
                            "DECLARE @MES INT = ?2;   " +
                            "   " +
                            "SELECT   " +
                            "  M.*,   " +
                            "  F.NOME_FIL                        [nomeLoja],   " +
                            "  ISNULL(valorRealizado, 0)         [valorRealizado],   " +
                            "  ISNULL(REAL.clientesRealizado, 0) [clientesRealizado],   " +
                            "  ISNULL(MD.somaProporcaoDiaria, 0) [somaProporcaoDiaria],   " +
                            "  ISNULL(MD.somaClientesDiario, 0)  [somaClientesDiario]   " +
                            "   " +
                            "FROM PAN_METAS_MENSAIS M WITH ( NOLOCK )   " +
                            "  JOIN FILIAL F WITH ( NOLOCK )   " +
                            "    ON M.codLoja = F.CODIGO_FIL   " +
                            "  LEFT OUTER JOIN   " +
                            "  (SELECT   " +
                            "     FILIAL_VEN,   " +
                            "     SUM(FATURAMENTO) [valorRealizado],   " +
                            "     SUM(CUPONS)      [clientesRealizado]   " +
                            "   FROM VIEW_VENDA_TOTAL WITH ( NOLOCK )   " +
                            "   WHERE YEAR(DATA_VEN) = @ANO   " +
                            "         AND MONTH(DATA_VEN) = @MES   " +
                            "   GROUP BY FILIAL_VEN) [REAL]   " +
                            "    ON M.codLoja = REAL.FILIAL_VEN   " +
                            "  LEFT OUTER JOIN   " +
                            "  (SELECT   " +
                            "     codLoja,   " +
                            "     SUM(MD.proporcao) [somaProporcaoDiaria],   " +
                            "     SUM(MD.clientes)  [somaClientesDiario]   " +
                            "   FROM PAN_METAS_DIARIAS MD WITH ( NOLOCK )   " +
                            "   WHERE YEAR(MD.dia) = @ANO   " +
                            "         AND MONTH(MD.dia) = @MES   " +
                            "   GROUP BY codLoja) [MD]   " +
                            "    ON M.codLoja = MD.codLoja   " +
                            "WHERE M.ano = @ANO   " +
                            "      AND M.mes = @MES   " +
                            "      AND M.codLoja NOT IN (3, 68)   " +
                            "ORDER BY ano, codLoja", "MetasMensais");
            nativeQuery.setParameter(1, ano);
            nativeQuery.setParameter(2, mes);
            List<MetaMensal> result = nativeQuery.getResultList();
            return result;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Falha ao recuperar Metas Mensais.", e);
        }
        return null;
    }

    public MetaMensal getMetaMensalConsolidada(Integer ano, Integer mes) {
        try {
            Query query = em.createQuery("select m from MetaMensal m where m.codLoja = 0 and m.ano = :ano and m.mes = :mes");
            query.setParameter("ano", ano);
            query.setParameter("mes", mes);
            MetaMensal result = (MetaMensal) query.getSingleResult();
            return result;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Falha ao recuperar Metas Mensal consolidada.", e);
        }
        return null;
    }

    public MetaMensal getMetaMensalPorLoja(Integer ano, Integer mes, Loja loja) {
        try {
            Query query = em.createQuery("select m from MetaMensal m where m.codLoja = :codLoja and m.ano = :ano and m.mes = :mes");
            query.setParameter("codLoja", Integer.parseInt(loja.getCodigo().trim()));
            query.setParameter("ano", ano);
            query.setParameter("mes", mes);
            MetaMensal result = (MetaMensal) query.getSingleResult();
            em.refresh(result);
            return result;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Falha ao recuperar Metas Mensal por Loja.", e);
        }
        return null;
    }

    /* Metas Diarias */
    public List<MetaDiaria> getMetasDiarias(Integer ano, Integer mes, Integer codLoja) {
        try {
            Query nativeQuery = em.createNativeQuery(
                    " DECLARE @ANO INT = ?1; " +
                       " DECLARE @MES INT = ?2; " +
                       " DECLARE @LOJA INT = ?3; " +
                        " " +
                        " SELECT M.*,    " +
                            "    F.NOME_FIL as nomeLoja, ISNULL(valorRealizado, 0) [valorRealizado], ISNULL(clientesRealizado, 0) [clientesRealizado]  " +
                            "    FROM PAN_METAS_DIARIAS M WITH (NOLOCK) JOIN FILIAL F WITH (NOLOCK)    " +
                            "      ON M.codLoja = F.CODIGO_FIL    " +
                            "      LEFT OUTER JOIN    " +
                            "      (SELECT FILIAL_VEN, DATA_VEN, SUM(FATURAMENTO) [valorRealizado], SUM(CUPONS) [clientesRealizado]  " +
                            "       FROM VIEW_VENDA_TOTAL WITH (NOLOCK)    " +
                            "      WHERE YEAR(DATA_VEN) = @ANO    " +
                            "         AND MONTH(DATA_VEN) = @MES    " +
                            "         AND FILIAL_VEN = @LOJA    " +
                            "      GROUP BY FILIAL_VEN, DATA_VEN) [REAL]    " +
                            "      ON M.codLoja = REAL.FILIAL_VEN    " +
                            "      AND M.dia = REAL.DATA_VEN    " +
                            "     WHERE YEAR(M.dia)= @ANO    " +
                            "     AND MONTH(M.dia) = @MES    " +
                            "     AND M.codLoja = @LOJA    " +
                            "   ORDER BY M.dia, codLoja", "MetasDiarias");
            nativeQuery.setParameter(1, ano);
            nativeQuery.setParameter(2, mes);
            nativeQuery.setParameter(3, codLoja);
            List<MetaDiaria> result = nativeQuery.getResultList();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            logger.log(Level.SEVERE, "Falha ao recuperar Metas Diarias.", e);
        }
        return null;
    }

    public List<MetaDiaria> getMetasDiariasSoOrcado(Integer ano, Integer mes, Integer codLoja) {
        try {
            Query nativeQuery = em.createNativeQuery(
                    " SELECT M.*, " +
                            " ISNULL(((SELECT MM.valor FROM PAN_METAS_MENSAIS MM WHERE MM.ano = ?1 AND MM.mes = ?2 AND MM.codLoja = ?3) * M.proporcao)/100, 0) as valor, " +
                            " 0 as valorRealizado, " +
                            " F.NOME_FIL as nomeLoja, " +
                            " 0 as clientes " +
                            " FROM PAN_METAS_DIARIAS M WITH (NOLOCK), FILIAL F WITH (NOLOCK) " +
                            "  WHERE M.codLoja = F.CODIGO_FIL " +
                            "  AND YEAR(M.dia)= ?1 " +
                            "  AND MONTH(M.dia) = ?2 " +
                            "  AND M.codLoja = ?3 " +
                            "ORDER BY M.dia, codLoja", "MetasDiarias");
            nativeQuery.setParameter(1, ano);
            nativeQuery.setParameter(2, mes);
            nativeQuery.setParameter(3, codLoja);
            List<MetaDiaria> result = nativeQuery.getResultList();
            return result;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Falha ao recuperar Metas Diarias só Orçado.", e);
        }
        return null;
    }
}
