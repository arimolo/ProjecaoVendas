package br.com.hortifruti.projecaovendas.model;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by arimolo on 05/03/15.
 */
@Entity
@Cacheable(false)
@Table(name = "PAN_METAS_ANUAIS", schema = "dbo",
        uniqueConstraints=
        @UniqueConstraint(columnNames={"ano", "codLoja"})
)
@Access(AccessType.FIELD)
@SqlResultSetMapping(name="MetasAnuais",
        classes={
                @ConstructorResult(targetClass= MetaAnual.class, columns={
                        @ColumnResult(name="id", type = Long.class),
                        @ColumnResult(name="ano", type = Integer.class),
                        @ColumnResult(name="codLoja", type = Integer.class),
                        @ColumnResult(name="valor", type = BigDecimal.class),
                        @ColumnResult(name="updated", type = Date.class),
                        @ColumnResult(name="userLogin", type = String.class),
                        @ColumnResult(name="valorRealizado", type = BigDecimal.class),
                        @ColumnResult(name="nomeLoja", type = String.class),
                        @ColumnResult(name="clientes", type = Long.class),
                        @ColumnResult(name="clientesRealizado", type = Long.class)})})
public class MetaAnual implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, insertable = true, updatable = true)
    private Long id;

    @Basic
    @Column(name = "ano", nullable = false, insertable = true, updatable = true)
    private Integer ano;

    @Basic
    @Column(name = "codLoja", nullable = false, insertable = true, updatable = true)
    private Integer codLoja;

    @Basic
    @Column(name = "valor", nullable = false, insertable = true, updatable = true, precision=18, scale=2)
    private BigDecimal valor;

    @Basic
    @Column(name = "userLogin", nullable = true, insertable = true, updatable = true, length = 100)
    private String userLogin;

    @Basic
    @Column(name = "updated", nullable = true, insertable = true, updatable = true)
    private Date updated;

    @Basic
    @Column(name = "clientes", nullable = true, insertable = true, updatable = true)
    private Long clientes;

    @Transient
    private BigDecimal valorRealizado;

    @Transient
    private String nomeLoja;



    @Transient
    private Long clientesRealizado;

    public MetaAnual() {

    }

    public MetaAnual(Long id, Integer ano, Integer codLoja, BigDecimal valor, Date updated, String userLogin, BigDecimal valorRealizado, String nomeLoja, Long clientes, Long clientesRealizado) {
        this.id = id;
        this.ano = ano;
        this.codLoja = codLoja;
        this.valor = valor;
        this.userLogin = userLogin;
        this.updated = updated;
        this.valorRealizado = valorRealizado;
        this.nomeLoja = nomeLoja;
        this.clientes = clientes;
        this.clientesRealizado = clientesRealizado;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Integer getAno() {
        return ano;
    }
    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public Integer getCodLoja() {
        return codLoja;
    }
    public void setCodLoja(Integer codLoja) {
        this.codLoja = codLoja;
    }

    public BigDecimal getValor() {
        return valor;
    }
    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

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

    public BigDecimal getValorRealizado() { return valorRealizado; }
    public void setValorRealizado(BigDecimal valorRealizado) { this.valorRealizado = valorRealizado; }

    public String getNomeLoja() {
        return nomeLoja;
    }
    public void setNomeLoja(String nomeLoja) {
        this.nomeLoja = nomeLoja;
    }

    public Long getClientes() {
        return clientes;
    }

    public void setClientes(Long clientes) {
        this.clientes = clientes;
    }

    public Long getClientesRealizado() {
        return clientesRealizado;
    }

    public void setClientesRealizado(Long clientesRealizado) {
        this.clientesRealizado = clientesRealizado;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MetaAnual metaAnual = (MetaAnual) o;

        if (id != null ? !id.equals(metaAnual.id) : metaAnual.id != null) return false;
        if (ano != null ? !ano.equals(metaAnual.ano) : metaAnual.ano != null) return false;
        if (codLoja != null ? !codLoja.equals(metaAnual.codLoja) : metaAnual.codLoja != null) return false;
        if (valor != null ? !valor.equals(metaAnual.valor) : metaAnual.valor != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (ano != null ? ano.hashCode() : 0);
        result = 31 * result + (codLoja != null ? codLoja.hashCode() : 0);
        result = 31 * result + (valor != null ? valor.hashCode() : 0);
        return result;
    }

    @PreUpdate
    protected void onUpdate() {
        updated = new Date();
    }
}
