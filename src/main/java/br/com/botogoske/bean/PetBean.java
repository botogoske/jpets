package br.com.botogoske.bean;

import br.com.botogoske.model.Especie;
import br.com.botogoske.model.Pet;
import br.com.botogoske.model.Porte;
import br.com.botogoske.model.SexoPet;
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
import java.util.Arrays;
import java.util.List;

@Named("petBean")
@ViewScoped
public class PetBean implements Serializable {

    @Inject
    private PetService petService;

    @Inject
    private TutorService tutorService;

    private List<Pet> pets;
    private List<Tutor> tutores;
    private Pet pet;
    private Pet selecionado;

    @PostConstruct
    public void init() {
        novoPet();
        carregarLista();
        carregarTutores();
    }

    public void novoPet() {
        pet = new Pet();
    }

    public void carregarLista() {
        pets = petService.listarTodos();
    }

    public void carregarTutores() {
        tutores = tutorService.listarAtivos();
    }

    public void salvar() {
        FacesContext ctx = FacesContext.getCurrentInstance();
        try {
            petService.salvar(pet);
            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Sucesso", "Pet salvo com sucesso."));
            novoPet();
            carregarLista();
            carregarTutores();
        } catch (Exception e) {
            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Erro", "Não foi possível salvar o pet."));
        }
    }

    public void editar(Pet p) {
        pet = new Pet();
        pet.setId(p.getId());
        pet.setNome(p.getNome());
        pet.setEspecie(p.getEspecie());
        pet.setRaca(p.getRaca());
        pet.setSexo(p.getSexo());
        pet.setPorte(p.getPorte());
        pet.setPeso(p.getPeso());
        pet.setDataNascimento(p.getDataNascimento());
        pet.setDataCadastro(p.getDataCadastro());
        pet.setObservacoes(p.getObservacoes());
        pet.setTutor(p.getTutor());
        pet.setAtivo(p.isAtivo());
    }

    public void excluir() {
        FacesContext ctx = FacesContext.getCurrentInstance();
        if (selecionado != null) {
            petService.excluir(selecionado.getId());
            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Sucesso", "Pet excluído com sucesso."));
            carregarLista();
            selecionado = null;
        }
    }

    public void cancelar() {
        novoPet();
    }

    public boolean isEdicao() {
        return pet != null && pet.getId() != null;
    }

    public List<Especie> getEspecies() {
        return Arrays.asList(Especie.values());
    }

    public List<SexoPet> getSexos() {
        return Arrays.asList(SexoPet.values());
    }

    public List<Porte> getPortes() {
        return Arrays.asList(Porte.values());
    }

    public LocalDate getHoje() {
        return LocalDate.now();
    }

    // Getters e Setters

    public List<Pet> getPets() {
        return pets;
    }

    public List<Tutor> getTutores() {
        return tutores;
    }

    public Pet getPet() {
        return pet;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }

    public Pet getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(Pet selecionado) {
        this.selecionado = selecionado;
    }
}
