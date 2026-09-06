package br.com.botogoske.service;

import br.com.botogoske.model.Cargo;
import br.com.botogoske.model.Funcionario;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@ApplicationScoped
public class FuncionarioService {

    private final List<Funcionario> funcionarios = new ArrayList<>();
    private final AtomicLong sequencia = new AtomicLong(1);

    @PostConstruct
    private void init() {
        Funcionario f1 = new Funcionario();
        f1.setId(sequencia.getAndIncrement());
        f1.setNome("Ana Paula Silva");
        f1.setCpf("123.456.789-00");
        f1.setEmail("ana.silva@jpets.com.br");
        f1.setTelefone("(41) 99999-0001");
        f1.setDataNascimento(LocalDate.of(1990, 3, 15));
        f1.setDataAdmissao(LocalDate.of(2022, 1, 10));
        f1.setCargo(Cargo.VETERINARIO);
        f1.setSalario(5500.00);
        f1.setAtivo(true);
        funcionarios.add(f1);

        Funcionario f2 = new Funcionario();
        f2.setId(sequencia.getAndIncrement());
        f2.setNome("Carlos Eduardo Lima");
        f2.setCpf("987.654.321-00");
        f2.setEmail("carlos.lima@jpets.com.br");
        f2.setTelefone("(41) 99999-0002");
        f2.setDataNascimento(LocalDate.of(1995, 7, 22));
        f2.setDataAdmissao(LocalDate.of(2023, 4, 1));
        f2.setCargo(Cargo.TOSADOR);
        f2.setSalario(2800.00);
        f2.setAtivo(true);
        funcionarios.add(f2);
    }

    public List<Funcionario> listarTodos() {
        return new ArrayList<>(funcionarios);
    }

    public void salvar(Funcionario funcionario) {
        if (funcionario.getId() == null) {
            funcionario.setId(sequencia.getAndIncrement());
            funcionarios.add(funcionario);
        } else {
            for (int i = 0; i < funcionarios.size(); i++) {
                if (funcionarios.get(i).getId().equals(funcionario.getId())) {
                    funcionarios.set(i, funcionario);
                    return;
                }
            }
        }
    }

    public void excluir(Long id) {
        funcionarios.removeIf(f -> f.getId().equals(id));
    }

    public Optional<Funcionario> buscarPorId(Long id) {
        return funcionarios.stream().filter(f -> f.getId().equals(id)).findFirst();
    }
}
