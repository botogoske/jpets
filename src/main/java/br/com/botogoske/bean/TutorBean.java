package br.com.botogoske.bean;

import br.com.botogoske.model.Tutor;
import br.com.botogoske.service.PetService;
import br.com.botogoske.service.TutorService;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Named("tutorBean")
@ViewScoped
public class TutorBean implements Serializable {

    @Inject
    private TutorService tutorService;

    @Inject
    private PetService petService;

    private List<Tutor> tutores;
    private Tutor tutor;
    private Tutor selecionado;

    @PostConstruct
    public void init() {
        novoTutor();
        carregarLista();
    }

    public void novoTutor() {
        tutor = new Tutor();
    }

    public void carregarLista() {
        tutores = tutorService.listarTodos();
    }

    public void salvar() {
        FacesContext ctx = FacesContext.getCurrentInstance();
        try {
            tutorService.salvar(tutor);
            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Sucesso", "Tutor salvo com sucesso."));
            novoTutor();
            carregarLista();
        } catch (Exception e) {
            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Erro", "Não foi possível salvar o tutor."));
        }
    }

    public void editar(Tutor t) {
        tutor = new Tutor();
        tutor.setId(t.getId());
        tutor.setNome(t.getNome());
        tutor.setCpf(t.getCpf());
        tutor.setEmail(t.getEmail());
        tutor.setTelefone(t.getTelefone());
        tutor.setDataNascimento(t.getDataNascimento());
        tutor.setEndereco(t.getEndereco());
        tutor.setBairro(t.getBairro());
        tutor.setCidade(t.getCidade());
        tutor.setDataCadastro(t.getDataCadastro());
        tutor.setAtivo(t.isAtivo());
    }

    public void excluir() {
        FacesContext ctx = FacesContext.getCurrentInstance();
        if (selecionado != null) {
            if (petService.temPetsVinculados(selecionado.getId())) {
                ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
                        "Não permitido", "Não é possível excluir o tutor pois existem pets associados a ele."));
                selecionado = null;
                return;
            }
            tutorService.excluir(selecionado.getId());
            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Sucesso", "Tutor excluído com sucesso."));
            carregarLista();
            selecionado = null;
        }
    }

    public void cancelar() {
        novoTutor();
    }

    public boolean isEdicao() {
        return tutor != null && tutor.getId() != null;
    }

    public LocalDate getHoje() {
        return LocalDate.now();
    }

    // Getters e Setters

    public List<Tutor> getTutores() {
        return tutores;
    }

    public Tutor getTutor() {
        return tutor;
    }

    public void setTutor(Tutor tutor) {
        this.tutor = tutor;
    }

    public Tutor getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(Tutor selecionado) {
        this.selecionado = selecionado;
    }
}
