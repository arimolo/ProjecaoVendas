package br.com.hortifruti.projecaovendas.model;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by arimolo on 03/03/15.
 */
@Entity
@Cacheable(false)
@Table(name = "PAN_METAS_DIARIAS", schema = "dbo",
        uniqueConstraints=
        @UniqueConstraint(name = "uc_meta_diaria", columnNames={"codLoja", "dia"})
)
@Access(AccessType.FIELD)
@SqlResultSetMapping(name="MetasDiarias",
        classes={
                @ConstructorResult(targetClass= MetaDiaria.class, columns={
                        @ColumnResult(name="id", type = Long.class),
                        @ColumnResult(name="dia", type = Date.class),
                        @ColumnResult(name="codLoja", type = Integer.class),
                        @ColumnResult(name="proporcao", type = BigDecimal.class),
                        @ColumnResult(name="updated", type = Date.class),
                        @ColumnResult(name="userLogin", type = String.class),
                        @ColumnResult(name="valorConsolidado", type = BigDecimal.class),
                        @ColumnResult(name="valorRealizado", type = BigDecimal.class),
                        @ColumnResult(name="nomeLoja", type = String.class),
                        @ColumnResult(name="clientes", type = Long.class),
                        @ColumnResult(name="proporcaoClientes", type = BigDecimal.class),
                        @ColumnResult(name="clientesRealizado", type = Long.class)})})
public class MetaDiaria implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, insertable = true, updatable = true)
    private Long id;

    @Basic
    @Column(name = "dia", nullable = false, insertable = true, updatable = true)
    private Date dia;

    @Basic
    @Column(name = "codLoja", nullable = true, insertable = true, updatable = true)
    private Integer codLoja;

    @Basic
    @Column(name = "proporcao", nullable = true, insertable = true, updatable = true, precision=5, scale=2)
    private BigDecimal proporcao;

    @Basic
    @Column(name = "userLogin", nullable = true, insertable = true, updatable = true, length = 100)
    private String userLogin;

    @Basic
    @Column(name = "updated", nullable = true, insertable = true, updatable = true)
    private Date updated;

    @Transient
    private BigDecimal valorRealizado;

    @Transient
    private String nomeLoja;

    @Basic
    @Column(name = "clientes", nullable = true, insertable = true, updatable = true)
    private Long clientes;

    @Basic
    @Column(name = "proporcaoClientes", nullable = true, insertable = true, updatable = true, precision=5, scale=2)
    private BigDecimal proporcaoClientes;

    @Transient
    private Long clientesRealizado;

    @Basic
    @Column(name = "valorConsolidado", nullable = true, insertable = true, updatable = true)
    private BigDecimal valorConsolidado;

    public MetaDiaria() {

    }

    public MetaDiaria(Long id, Date dia, Integer codLoja, BigDecimal proporcao, Date updated, String userLogin, BigDecimal valorConsolidado, BigDecimal valorRealizado, String nomeLoja, Long clientes, BigDecimal proporcaoClientes, Long clientesRealizado) {
        this.id = id;
        this.dia = dia;
        this.codLoja = codLoja;
        this.proporcao = proporcao;
        this.userLogin = userLogin;
        this.updated = updated;
        this.valorRealizado = valorRealizado;
        this.nomeLoja = nomeLoja;
        this.clientes = clientes;
        this.proporcaoClientes = proporcaoClientes;
        this.clientesRealizado = clientesRealizado;
        this.valorConsolidado = valorConsolidado;
        this.valorConsolidado.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Date getDia() {
        return dia;
    }
    public void setDia(Date dia) {
        this.dia = dia;
    }

    public Integer getCodLoja() {
        return codLoja;
    }
    public void setCodLoja(Integer codFilial) {
        this.codLoja = codFilial;
    }

    public BigDecimal getProporcao() { return proporcao;}
    public void setProporcao(BigDecimal proporcao) {this.proporcao = proporcao;}

    public String getUserLogin() {
        return userLogin;
    }
    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }

    public Date getUpdated() {
        return updated;
    }
    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public BigDecimal getValorRealizado() {
        return valorRealizado;
    }
    public void setValorRealizado(BigDecimal valorRealizado) {
        this.valorRealizado = valorRealizado;
    }

    public String getNomeLoja() {
        return nomeLoja;
    }
    public void setNomeLoja(String nomeLoja) {
        this.nomeLoja = nomeLoja;
    }

    public Long getClientesRealizado() {
        return clientesRealizado;
    }

    public void setClientesRealizado(Long clientesRealizado) {
        this.clientesRealizado = clientesRealizado;
    }

    public BigDecimal getProporcaoClientes() {
        return proporcaoClientes;
    }

    public void setProporcaoClientes(BigDecimal proporcaoClientes) {
        this.proporcaoClientes = proporcaoClientes;
    }

    public Long getClientes() {
        return clientes;
    }

    public void setClientes(Long clientes) {
        this.clientes = clientes;
    }

    public BigDecimal getValorConsolidado() {
        return valorConsolidado;
    }

    public void setValorConsolidado(BigDecimal valorConsolidado) {
        this.valorConsolidado = valorConsolidado;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MetaDiaria that = (MetaDiaria) o;

        return id != null ? id.equals(that.id) : that.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @PreUpdate
    protected void onUpdate() {
        updated = new Date();
    }
}
