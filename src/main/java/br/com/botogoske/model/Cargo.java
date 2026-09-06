package br.com.botogoske.model;

public enum Cargo {

    VETERINARIO("Veterinário"),
    TOSADOR("Tosador"),
    BANHISTA("Banhista"),
    RECEPCIONISTA("Recepcionista"),
    GERENTE("Gerente"),
    AUXILIAR("Auxiliar");

    private final String descricao;

    Cargo(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
