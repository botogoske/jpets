package br.com.botogoske.dao;

import br.com.botogoske.model.Cargo;
import br.com.botogoske.model.Funcionario;
import br.com.botogoske.persistence.Sql;

import javax.enterprise.context.ApplicationScoped;
import java.sql.ResultSet;
import java.util.List;
import java.util.Optional;

/** Persistencia da tabela {@code funcionario}. */
@ApplicationScoped
public class FuncionarioDao extends DaoBase {

    private static final String COLUNAS =
            "id, nome, cpf, email, telefone, data_nascimento, data_admissao, cargo, salario, ativo";

    private static final String SQL_LISTAR = "SELECT " + COLUNAS + " FROM funcionario ORDER BY nome";
    private static final String SQL_LISTAR_ATIVOS =
            "SELECT " + COLUNAS + " FROM funcionario WHERE ativo = 1 ORDER BY nome";
    private static final String SQL_BUSCAR = "SELECT " + COLUNAS + " FROM funcionario WHERE id = ?";

    private static final String SQL_INSERIR =
            "INSERT INTO funcionario (nome, cpf, email, telefone, data_nascimento, data_admissao, cargo, salario, ativo) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_ATUALIZAR =
            "UPDATE funcionario SET nome = ?, cpf = ?, email = ?, telefone = ?, data_nascimento = ?, "
                    + "data_admissao = ?, cargo = ?, salario = ?, ativo = ? WHERE id = ?";

    private static final String SQL_EXCLUIR = "DELETE FROM funcionario WHERE id = ?";
    private static final String SQL_CONTAR = "SELECT COUNT(*) FROM funcionario";

    public List<Funcionario> listarTodos() {
        return consultarLista(SQL_LISTAR, ps -> {
        }, FuncionarioDao::mapear);
    }

    public List<Funcionario> listarAtivos() {
        return consultarLista(SQL_LISTAR_ATIVOS, ps -> {
        }, FuncionarioDao::mapear);
    }

    public Optional<Funcionario> buscarPorId(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(consultarUnico(SQL_BUSCAR, ps -> ps.setLong(1, id), FuncionarioDao::mapear));
    }

    /** Insere (id == null) ou atualiza, e devolve o funcionario com o id persistido. */
    public Funcionario salvar(Funcionario funcionario) {
        if (funcionario.getId() == null) {
            long id = inserir(SQL_INSERIR, ps -> {
                ps.setString(1, funcionario.getNome());
                ps.setString(2, funcionario.getCpf());
                ps.setString(3, funcionario.getEmail());
                ps.setString(4, funcionario.getTelefone());
                Sql.setData(ps, 5, funcionario.getDataNascimento());
                Sql.setData(ps, 6, funcionario.getDataAdmissao());
                Sql.setEnum(ps, 7, funcionario.getCargo());
                Sql.setDouble(ps, 8, funcionario.getSalario());
                Sql.setBoolean(ps, 9, funcionario.isAtivo());
            });
            funcionario.setId(id);
        } else {
            atualizar(SQL_ATUALIZAR, ps -> {
                ps.setString(1, funcionario.getNome());
                ps.setString(2, funcionario.getCpf());
                ps.setString(3, funcionario.getEmail());
                ps.setString(4, funcionario.getTelefone());
                Sql.setData(ps, 5, funcionario.getDataNascimento());
                Sql.setData(ps, 6, funcionario.getDataAdmissao());
                Sql.setEnum(ps, 7, funcionario.getCargo());
                Sql.setDouble(ps, 8, funcionario.getSalario());
                Sql.setBoolean(ps, 9, funcionario.isAtivo());
                ps.setLong(10, funcionario.getId());
            });
        }
        return funcionario;
    }

    public void excluir(Long id) {
        if (id != null) {
            atualizar(SQL_EXCLUIR, ps -> ps.setLong(1, id));
        }
    }

    public long contar() {
        return contar(SQL_CONTAR);
    }

    private static Funcionario mapear(ResultSet rs) throws java.sql.SQLException {
        Funcionario funcionario = new Funcionario();
        funcionario.setId(rs.getLong("id"));
        funcionario.setNome(rs.getString("nome"));
        funcionario.setCpf(rs.getString("cpf"));
        funcionario.setEmail(rs.getString("email"));
        funcionario.setTelefone(rs.getString("telefone"));
        funcionario.setDataNascimento(Sql.getData(rs, "data_nascimento"));
        funcionario.setDataAdmissao(Sql.getData(rs, "data_admissao"));
        funcionario.setCargo(Sql.getEnum(rs, "cargo", Cargo.class));
        double salario = rs.getDouble("salario");
        funcionario.setSalario(rs.wasNull() ? null : salario);
        funcionario.setAtivo(Sql.getBoolean(rs, "ativo"));
        return funcionario;
    }
}
