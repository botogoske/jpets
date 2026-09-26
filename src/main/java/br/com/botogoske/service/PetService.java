package br.com.botogoske.service;

import br.com.botogoske.dao.PetDao;
import br.com.botogoske.dao.TutorDao;
import br.com.botogoske.model.Especie;
import br.com.botogoske.model.Pet;
import br.com.botogoske.model.Porte;
import br.com.botogoske.model.SexoPet;
import br.com.botogoske.model.Tutor;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Servico de pets. A persistencia fica no {@link PetDao}; esta classe cuida
 * das regras de negocio e do vinculo com o tutor.
 */
@ApplicationScoped
public class PetService implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private transient PetDao dao;

    @Inject
    private transient TutorService tutorService;

    @PostConstruct
    private void init() {
        popularDadosIniciais();
    }

    /** Cadastra os pets de exemplo apenas quando a tabela esta vazia. */
    private void popularDadosIniciais() {
        if (dao.contar() > 0) {
            return;
        }

        List<Tutor> tutores = tutorService.listarTodos();
        if (tutores.isEmpty()) {
            return;
        }
        Tutor mariana = tutores.size() > 0 ? tutores.get(0) : null;
        Tutor rodrigo = tutores.size() > 1 ? tutores.get(1) : null;
        Tutor juliana = tutores.size() > 2 ? tutores.get(2) : null;

        Pet p1 = new Pet();
        p1.setNome("Thor");
        p1.setEspecie(Especie.CACHORRO);
        p1.setRaca("Golden Retriever");
        p1.setSexo(SexoPet.MACHO);
        p1.setPorte(Porte.GRANDE);
        p1.setPeso(32.5);
        p1.setDataNascimento(LocalDate.of(2021, 4, 10));
        p1.setObservacoes("Dócil e brincalhão. Adora banho de piscina.");
        p1.setTutor(mariana);
        p1.setAtivo(true);
        dao.salvar(p1);

        Pet p2 = new Pet();
        p2.setNome("Mia");
        p2.setEspecie(Especie.GATO);
        p2.setRaca("Siamês");
        p2.setSexo(SexoPet.FEMEA);
        p2.setPorte(Porte.PEQUENO);
        p2.setPeso(4.2);
        p2.setDataNascimento(LocalDate.of(2022, 8, 15));
        p2.setObservacoes("Tímida com estranhos. Requer cuidado no corte de unhas.");
        p2.setTutor(mariana);
        p2.setAtivo(true);
        dao.salvar(p2);

        Pet p3 = new Pet();
        p3.setNome("Bob");
        p3.setEspecie(Especie.CACHORRO);
        p3.setRaca("Poodle");
        p3.setSexo(SexoPet.MACHO);
        p3.setPorte(Porte.MEDIO);
        p3.setPeso(9.8);
        p3.setDataNascimento(LocalDate.of(2020, 1, 25));
        p3.setObservacoes("Pele sensível, usar shampoo hipoalergênico.");
        p3.setTutor(rodrigo);
        p3.setAtivo(true);
        dao.salvar(p3);

        Pet p4 = new Pet();
        p4.setNome("Pipoca");
        p4.setEspecie(Especie.AVE);
        p4.setRaca("Calopsita");
        p4.setSexo(SexoPet.MACHO);
        p4.setPorte(Porte.PEQUENO);
        p4.setPeso(0.12);
        p4.setDataNascimento(LocalDate.of(2023, 3, 12));
        p4.setObservacoes("Manso e carinhoso.");
        p4.setTutor(juliana);
        p4.setAtivo(true);
        dao.salvar(p4);
    }

    public List<Pet> listarTodos() {
        return dao.listarTodos();
    }

    public List<Pet> listarPorTutor(Long tutorId) {
        return dao.listarPorTutor(tutorId);
    }

    public boolean temPetsVinculados(Long tutorId) {
        return dao.contarPorTutor(tutorId) > 0;
    }

    public void salvar(Pet pet) {
        if (pet.getTutor() != null && pet.getTutor().getId() != null) {
            tutorService.buscarPorId(pet.getTutor().getId()).ifPresent(pet::setTutor);
        }

        dao.salvar(pet);
    }

    public void excluir(Long id) {
        dao.excluir(id);
    }

    public Optional<Pet> buscarPorId(Long id) {
        return dao.buscarPorId(id);
    }
}
