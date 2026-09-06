package br.com.botogoske.bean;

import br.com.botogoske.model.Cargo;
import br.com.botogoske.model.Funcionario;
import br.com.botogoske.service.FuncionarioService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Named("funcionarioBean")
@ViewScoped
public class FuncionarioBean implements Serializable {

    @Inject
    private FuncionarioService funcionarioService;

    private List<Funcionario> funcionarios;
    private Funcionario funcionario;
    private Funcionario selecionado;

    @PostConstruct
    public void init() {
        novoFuncionario();
        carregarLista();
    }

    public void novoFuncionario() {
        funcionario = new Funcionario();
    }

    public void carregarLista() {
        funcionarios = funcionarioService.listarTodos();
    }

    public void salvar() {
        FacesContext ctx = FacesContext.getCurrentInstance();
        try {
            funcionarioService.salvar(funcionario);
            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Sucesso", "Funcionário salvo com sucesso."));
            novoFuncionario();
            carregarLista();
        } catch (Exception e) {
            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Erro", "Não foi possível salvar o funcionário."));
        }
    }

    public void editar(Funcionario f) {
        // copia rasa para não alterar a lista enquanto edita
        funcionario = new Funcionario();
        funcionario.setId(f.getId());
        funcionario.setNome(f.getNome());
        funcionario.setCpf(f.getCpf());
        funcionario.setEmail(f.getEmail());
        funcionario.setTelefone(f.getTelefone());
        funcionario.setDataNascimento(f.getDataNascimento());
        funcionario.setDataAdmissao(f.getDataAdmissao());
        funcionario.setCargo(f.getCargo());
        funcionario.setSalario(f.getSalario());
        funcionario.setAtivo(f.isAtivo());
    }

    public void excluir() {
        FacesContext ctx = FacesContext.getCurrentInstance();
        if (selecionado != null) {
            funcionarioService.excluir(selecionado.getId());
            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Sucesso", "Funcionário excluído."));
            carregarLista();
            selecionado = null;
        }
    }

    public void cancelar() {
        novoFuncionario();
    }

    public boolean isEdicao() {
        return funcionario != null && funcionario.getId() != null;
    }

    public List<Cargo> getCargos() {
        return Arrays.asList(Cargo.values());
    }

    public LocalDate getHoje() {
        return LocalDate.now();
    }

    // getters e setters

    public List<Funcionario> getFuncionarios() { return funcionarios; }

    public Funcionario getFuncionario() { return funcionario; }
    public void setFuncionario(Funcionario funcionario) { this.funcionario = funcionario; }

    public Funcionario getSelecionado() { return selecionado; }
    public void setSelecionado(Funcionario selecionado) { this.selecionado = selecionado; }
}
