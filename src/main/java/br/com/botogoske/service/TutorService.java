package br.com.botogoske.service;

import br.com.botogoske.dao.TutorDao;
import br.com.botogoske.model.Tutor;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Servico de tutores. A persistencia fica no {@link TutorDao}; esta classe
 * cuida das regras de negocio e do cadastro inicial.
 */
@ApplicationScoped
public class TutorService implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private transient TutorDao dao;

    @PostConstruct
    private void init() {
        popularDadosIniciais();
    }

    /** Cadastra os tutores de exemplo apenas quando a tabela esta vazia. */
    private void popularDadosIniciais() {
        if (dao.contar() > 0) {
            return;
        }

        Tutor t1 = new Tutor();
        t1.setNome("Mariana Costa");
        t1.setCpf("111.222.333-44");
        t1.setEmail("mariana.costa@email.com");
        t1.setTelefone("(41) 98888-1111");
        t1.setDataNascimento(LocalDate.of(1988, 6, 12));
        t1.setEndereco("Rua das Flores, 123");
        t1.setBairro("Centro");
        t1.setCidade("Curitiba");
        t1.setDataCadastro(LocalDate.of(2023, 1, 15));
        t1.setAtivo(true);
        dao.salvar(t1);

        Tutor t2 = new Tutor();
        t2.setNome("Rodrigo Ferreira");
        t2.setCpf("222.333.444-55");
        t2.setEmail("rodrigo.ferreira@email.com");
        t2.setTelefone("(41) 98888-2222");
        t2.setDataNascimento(LocalDate.of(1992, 11, 20));
        t2.setEndereco("Av. Brasil, 456");
        t2.setBairro("Batel");
        t2.setCidade("Curitiba");
        t2.setDataCadastro(LocalDate.of(2023, 3, 22));
        t2.setAtivo(true);
        dao.salvar(t2);

        Tutor t3 = new Tutor();
        t3.setNome("Juliana Mendes");
        t3.setCpf("333.444.555-66");
        t3.setEmail("juliana.mendes@email.com");
        t3.setTelefone("(41) 98888-3333");
        t3.setDataNascimento(LocalDate.of(1985, 2, 5));
        t3.setEndereco("Rua XV de Novembro, 789");
        t3.setBairro("Alto da XV");
        t3.setCidade("Curitiba");
        t3.setDataCadastro(LocalDate.of(2023, 5, 10));
        t3.setAtivo(true);
        dao.salvar(t3);
    }

    public List<Tutor> listarTodos() {
        return dao.listarTodos();
    }

    public List<Tutor> listarAtivos() {
        return dao.listarAtivos();
    }

    public void salvar(Tutor tutor) {
        dao.salvar(tutor);
    }

    public void excluir(Long id) {
        dao.excluir(id);
    }

    public Optional<Tutor> buscarPorId(Long id) {
        return dao.buscarPorId(id);
    }
}
