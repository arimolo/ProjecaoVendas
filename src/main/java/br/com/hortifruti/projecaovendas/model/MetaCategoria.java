package br.com.hortifruti.projecaovendas.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by arimolo on 09/04/17.
 */
@Entity
@Cacheable(false)
@Table(name = "PAN_METAS_CATEGORIAS", schema = "dbo",
        uniqueConstraints=
        @UniqueConstraint(name = "uc_meta_categoria", columnNames={"codLoja", "codCategoria", "dia"})
)
@Access(AccessType.FIELD)
public class MetaCategoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, insertable = true, updatable = true)
    private Long id;

    @Basic
    @Column(name = "codLoja", nullable = false, insertable = true, updatable = true)
    private Integer codLoja;

    @Basic
    @Column(name = "codCategoria", nullable = false, insertable = true, updatable = true, length = 18)
    private String codCategoria;

    @Basic
    @Column(name = "dia", nullable = false, insertable = true, updatable = true)
    private Date dia;

    @Basic
    @Column(name = "valor", nullable = false, insertable = true, updatable = true)
    private BigDecimal valor;

    @Basic
    @Column(name = "userLogin", nullable = true, insertable = true, updatable = true, length = 100)
    private String userLogin;

    @Basic
    @Column(name = "updated", nullable = true, insertable = true, updatable = true)
    private Date updated;

    @Transient
    private BigDecimal valorRealizado;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCodLoja() {
        return codLoja;
    }

    public void setCodLoja(Integer codLoja) {
        this.codLoja = codLoja;
    }

    public String getCodCategoria() {
        return codCategoria;
    }

    public void setCodCategoria(String codCategoria) {
        this.codCategoria = codCategoria;
    }

    public Date getDia() {
        return dia;
    }

    public void setDia(Date dia) {
        this.dia = dia;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MetaCategoria that = (MetaCategoria) o;

        return id.equals(that.id);
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
