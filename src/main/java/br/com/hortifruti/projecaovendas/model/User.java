package br.com.hortifruti.projecaovendas.model;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by arimolo on 04/03/15.
 */
@Entity
@Access(AccessType.FIELD)
@Table(name = "PAN_USERS", schema = "dbo")
@SqlResultSetMapping(name="Users",
        classes={
                @ConstructorResult(targetClass= User.class, columns={
                        @ColumnResult(name="id", type = Long.class),
                        @ColumnResult(name="email", type = String.class),
                        @ColumnResult(name="login", type = String.class),
                        @ColumnResult(name="perfil", type = String.class),
                        @ColumnResult(name="codLoja", type = Integer.class),
                        @ColumnResult(name="nome", type = String.class),
                        @ColumnResult(name="loja", type = String.class)})})
public class User implements Serializable {
    public static final String PERFIL_ADMIN = "admin";
    public static final String PERFIL_NORMAL = "normal";

    @Id
    @Column(name = "id", nullable = false, insertable = true, updatable = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Basic
    @Column(name = "email", nullable = false, insertable = true, updatable = true, length = 120)
    private String email;

    @Basic
    @Column(name = "login", nullable = true, insertable = true, updatable = true, length = 30, unique = true)
    private String login;

    @Basic
    @Column(name = "perfil", nullable = true, insertable = true, updatable = true, length = 30)
    private String perfil;

    @Basic
    @Column(name = "codLoja", nullable = true, insertable = true, updatable = true)
    private Integer codLoja;

    @Basic
    @Column(name = "nome", nullable = true, insertable = true, updatable = true, length = 60)
    private String nome;

    @Transient
    private String loja;

    public User() {

    }

    public User(Long id, String email, String login, String perfil, Integer codLoja, String nome, String loja) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.perfil = perfil;
        this.codLoja = codLoja;
        this.nome = nome;
        this.loja = loja;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getLogin() {
        return login;
    }
    public void setLogin(String login) {
        this.login = login;
    }

    public String getPerfil() {
        return perfil;
    }
    public void setPerfil(String perfil) {
        this.perfil = perfil;
    }

    public Integer getCodLoja() {
        return codLoja;
    }

    public void setCodLoja(Integer codLoja) {
        this.codLoja = codLoja;
    }

    public String getLoja() {
        return loja;
    }

    public void setLoja(String loja) {
        this.loja = loja;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return id == user.id;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    @PrePersist
    protected void onCreate() {
        perfil = PERFIL_NORMAL;
    }

    public boolean isAdmin() {
        return this.perfil.equals(PERFIL_ADMIN);
    }

    @Override
    public String toString() {
        return nome + " (" + login + ") [" + loja + "]";
    }
}
