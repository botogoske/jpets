package br.com.botogoske.service;

import br.com.botogoske.dao.FuncionarioDao;
import br.com.botogoske.model.Cargo;
import br.com.botogoske.model.Funcionario;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Servico de funcionarios. A persistencia fica no {@link FuncionarioDao};
 * esta classe cuida das regras de negocio e do cadastro inicial.
 */
@ApplicationScoped
public class FuncionarioService {

    @Inject
    private FuncionarioDao dao;

    @PostConstruct
    private void init() {
        popularDadosIniciais();
    }

    /** Cadastra a equipe de exemplo apenas quando a tabela esta vazia. */
    private void popularDadosIniciais() {
        if (dao.contar() > 0) {
            return;
        }

        Funcionario f1 = new Funcionario();
        f1.setNome("Ana Paula Silva");
        f1.setCpf("123.456.789-00");
        f1.setEmail("ana.silva@jpets.com.br");
        f1.setTelefone("(41) 99999-0001");
        f1.setDataNascimento(LocalDate.of(1990, 3, 15));
        f1.setDataAdmissao(LocalDate.of(2022, 1, 10));
        f1.setCargo(Cargo.VETERINARIO);
        f1.setSalario(5500.00);
        f1.setAtivo(true);
        dao.salvar(f1);

        Funcionario f2 = new Funcionario();
        f2.setNome("Carlos Eduardo Lima");
        f2.setCpf("987.654.321-00");
        f2.setEmail("carlos.lima@jpets.com.br");
        f2.setTelefone("(41) 99999-0002");
        f2.setDataNascimento(LocalDate.of(1995, 7, 22));
        f2.setDataAdmissao(LocalDate.of(2023, 4, 1));
        f2.setCargo(Cargo.TOSADOR);
        f2.setSalario(2800.00);
        f2.setAtivo(true);
        dao.salvar(f2);
    }

    public List<Funcionario> listarTodos() {
        return dao.listarTodos();
    }

    public List<Funcionario> listarAtivos() {
        return dao.listarAtivos();
    }

    public void salvar(Funcionario funcionario) {
        dao.salvar(funcionario);
    }

    public void excluir(Long id) {
        dao.excluir(id);
    }

    public Optional<Funcionario> buscarPorId(Long id) {
        return dao.buscarPorId(id);
    }
}
