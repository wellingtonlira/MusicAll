package br.com.musicall.api.controllers.form;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class AlterarSenhaForm {

    @NotEmpty @NotNull @Length(min = 8, max = 40)
    private String novaSenha;

    @NotEmpty @NotNull @Length(min = 8, max = 40)
    private String senhaAntiga;

    public void setNovaSenha(String novaSenha) {
        this.novaSenha = novaSenha;
    }

    public String getNovaSenha() {
        return novaSenha;
    }

    public String getSenhaAntiga() {
        return senhaAntiga;
    }

    public void setSenhaAntiga(String senhaAntiga) {
        this.senhaAntiga = senhaAntiga;
    }
}
