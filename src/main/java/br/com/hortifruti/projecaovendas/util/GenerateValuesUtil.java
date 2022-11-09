package br.com.hortifruti.projecaovendas.util;

import br.com.hortifruti.projecaovendas.dao.LojaDAO;
import br.com.hortifruti.projecaovendas.model.Loja;
import br.com.hortifruti.projecaovendas.model.MetaAnual;
import br.com.hortifruti.projecaovendas.model.MetaDiaria;
import br.com.hortifruti.projecaovendas.model.MetaMensal;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by arimolo on 30/08/16.
 */
public class GenerateValuesUtil {
    private static EntityManager em = JPAContainerFactory.createEntityManagerForPersistenceUnit("DADOSHFPU");
    private static final LojaDAO lojaDAO = new LojaDAO();

//    public static void createMetasAnuais(int anoInicial, int anoFinal) {
//        List<Loja> lojas = lojaDAO.getLojas();
//        em.getTransaction().begin();
//        try {
//            for (Loja loja : lojas) {
//                for (int i = anoInicial; i <= anoFinal; i++) {
//                    MetaAnual metaAnual = new MetaAnual();
//                    metaAnual.setAno(i);
//                    metaAnual.setCodLoja(Integer.parseInt(loja.getCodigo().trim()));
//                    metaAnual.setValor(new BigDecimal(0));
//                    metaAnual.setUserLogin("alexandre.camargo");
//                    em.persist(metaAnual);
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            em.getTransaction().rollback();
//        }
//        em.getTransaction().commit();
//    }

        public static void createMetasAnuais(int anoInicial, int anoFinal, int codLoja) {
        em.getTransaction().begin();
        try {

            for (int i = anoInicial; i <= anoFinal; i++) {
                MetaAnual metaAnual = new MetaAnual();
                metaAnual.setAno(i);
                metaAnual.setCodLoja(codLoja);
                metaAnual.setValor(new BigDecimal(0));
                metaAnual.setUserLogin("alexandre.camargo");
                metaAnual.setClientes(0l);
                em.persist(metaAnual);
            }

        } catch (Exception e) {
            e.printStackTrace();
            em.getTransaction().rollback();
        }
        em.getTransaction().commit();
    }

    public static void createMetasMensais(int anoInicial, int anoFinal, int codLoja) {
        em.getTransaction().begin();
        try {
            for (int i = anoInicial; i <= anoFinal; i++) {
                for (int j = 1; j <= 12; j++) {
                    MetaMensal metaMensal = new MetaMensal();
                    metaMensal.setAno(i);
                    metaMensal.setMes(j);
                    metaMensal.setCodLoja(codLoja);
                    metaMensal.setValor(new BigDecimal(0));
                    metaMensal.setUserLogin("alexandre.camargo");
                    metaMensal.setClientes(0l);
                    em.persist(metaMensal);
                    em.flush();
                    em.clear();
                }
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            em.getTransaction().rollback();
        }
    }

    public static void createMetasDiarias(int anoInicial, int anoFinal, int codLoja) {
        em.getTransaction().begin();
        try {
            for (int ano = anoInicial; ano <= anoFinal; ano++) {
                for (int mes = 1; mes <= 12; mes++) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    Calendar calendar = Calendar.getInstance();
                    try {
                        calendar.setTime(simpleDateFormat.parse("01/" + mes + "/" + ano));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    int maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

                    for (int co = 0; co < maxDay; co++) {
                        MetaDiaria metaDiaria = new MetaDiaria();
                        metaDiaria.setDia(calendar.getTime());
                        metaDiaria.setProporcao(new BigDecimal(0));
                        metaDiaria.setValorConsolidado(new BigDecimal(0));
                        metaDiaria.setCodLoja(codLoja);
                        metaDiaria.setClientes(0l);
                        em.persist(metaDiaria);
                        em.flush();
                        em.clear();

                        calendar.add(Calendar.DATE, 1);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            em.getTransaction().rollback();
        }
    }
}
