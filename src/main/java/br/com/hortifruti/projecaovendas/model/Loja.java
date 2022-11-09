package br.com.hortifruti.projecaovendas.model;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by arimolo on 19/08/16.
 */
@Entity
@Table(name = "FILIAL", schema = "dbo")
@Access(AccessType.FIELD)
public class Loja implements Serializable {
    @Id
    @Column(name = "CODIGO_FIL", nullable = false, insertable = true, updatable = true)
    private String codigo;

    @Basic
    @Column(name = "NOME_FIL", nullable = false, insertable = true, updatable = true)
    private String nome;

    @Basic
    @Column(name = "SIGLA_FIL", nullable = false, insertable = true, updatable = true)
    private String sigla;

    @Basic
    @Column(name = "BAND_FIL", nullable = false, insertable = true, updatable = true)
    private String bandeira;

    public Loja() {
    }

    public Loja(String codigo, String nome, String sigla, String bandeira) {
        this.codigo = codigo;
        this.nome = nome;
        this.sigla = sigla;
        this.bandeira = bandeira;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

    public String getBandeira() {
        return bandeira;
    }

    public void setBandeira(String bandeira) {
        this.bandeira = bandeira;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Loja loja = (Loja) o;

        return codigo.equals(loja.codigo);

    }

    @Override
    public int hashCode() {
        return codigo.hashCode();
    }
}
