package br.com.hortifruti.projecaovendas.model;

import org.eclipse.persistence.annotations.*;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by arimolo on 05/03/15.
 */
@Entity
@Cacheable(false)
@Table(name = "PAN_METAS_MENSAIS", schema = "dbo",
        uniqueConstraints=
        @UniqueConstraint(columnNames={"ano", "mes", "codLoja"})
)
@Access(AccessType.FIELD)
@SqlResultSetMapping(name="MetasMensais",
        classes={
                @ConstructorResult(targetClass= MetaMensal.class, columns={
                        @ColumnResult(name="id", type = Long.class),
                        @ColumnResult(name="ano", type = Integer.class),
                        @ColumnResult(name="mes", type = Integer.class),
                        @ColumnResult(name="codLoja", type = Integer.class),
                        @ColumnResult(name="valor", type = BigDecimal.class),
                        @ColumnResult(name="updated", type = Date.class),
                        @ColumnResult(name="userLogin", type = String.class),
                        @ColumnResult(name="valorRealizado", type = BigDecimal.class),
                        @ColumnResult(name="nomeLoja", type = String.class),
                        @ColumnResult(name="somaProporcaoDiaria", type = BigDecimal.class),
                        @ColumnResult(name="clientes", type = Long.class),
                        @ColumnResult(name="clientesRealizado", type = Long.class),
                        @ColumnResult(name="somaClientesDiario", type = Long.class)})})
public class MetaMensal implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, insertable = true, updatable = true)
    private Long id;

    @Basic
    @Column(name = "ano", nullable = false, insertable = true, updatable = true)
    private Integer ano;

    @Basic
    @Column(name = "mes", nullable = false, insertable = true, updatable = true)
    private Integer mes;

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
    private BigDecimal somaProporcaoDiaria;

    @Transient
    private Long somaClientesDiario;

    @Transient
    private Long clientesRealizado;

    public MetaMensal() {

    }

    public MetaMensal(Long id, Integer ano, Integer mes, Integer codLoja, BigDecimal valor, Date updated, String userLogin, BigDecimal valorRealizado, String nomeLoja, BigDecimal somaProporcaoDiaria, Long clientes, Long clientesRealizado, Long somaClientesDiario) {
        this.id = id;
        this.ano = ano;
        this.mes = mes;
        this.codLoja = codLoja;
        this.valor = valor;
        this.userLogin = userLogin;
        this.updated = updated;
        this.valorRealizado = valorRealizado;
        this.nomeLoja = nomeLoja;
        this.somaProporcaoDiaria = somaProporcaoDiaria;
        this.clientes = clientes;
        this.clientesRealizado = clientesRealizado;
        this.somaClientesDiario = somaClientesDiario;
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

    public Integer getMes() {
        return mes;
    }
    public void setMes(Integer mes) {
        this.mes = mes;
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

    public BigDecimal getSomaProporcaoDiaria() { return somaProporcaoDiaria; }
    public void setSomaProporcaoDiaria(BigDecimal somaProporcaoDiaria) { this.somaProporcaoDiaria = somaProporcaoDiaria; }

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

    public Long getSomaClientesDiario() {
        return somaClientesDiario;
    }

    public void setSomaClientesDiario(Long somaClientesDiario) {
        this.somaClientesDiario = somaClientesDiario;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MetaMensal that = (MetaMensal) o;

        if (!id.equals(that.id)) return false;
        if (ano != null ? !ano.equals(that.ano) : that.ano != null) return false;
        if (mes != null ? !mes.equals(that.mes) : that.mes != null) return false;
        if (codLoja != null ? !codLoja.equals(that.codLoja) : that.codLoja != null) return false;
        return valor != null ? valor.equals(that.valor) : that.valor == null;

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }


    @PreUpdate
    protected void onUpdate() {
        updated = new Date();
    }
}