package br.com.botogoske.dao;

import br.com.botogoske.model.Tutor;
import br.com.botogoske.persistence.Sql;

import javax.enterprise.context.ApplicationScoped;
import java.sql.ResultSet;
import java.util.List;
import java.util.Optional;

/** Persistencia da tabela {@code tutor}. */
@ApplicationScoped
public class TutorDao extends DaoBase {

    private static final String COLUNAS =
            "id, nome, cpf, email, telefone, data_nascimento, endereco, bairro, cidade, data_cadastro, ativo";

    private static final String SQL_LISTAR = "SELECT " + COLUNAS + " FROM tutor ORDER BY nome";
    private static final String SQL_LISTAR_ATIVOS =
            "SELECT " + COLUNAS + " FROM tutor WHERE ativo = 1 ORDER BY nome";
    private static final String SQL_BUSCAR = "SELECT " + COLUNAS + " FROM tutor WHERE id = ?";

    private static final String SQL_INSERIR =
            "INSERT INTO tutor (nome, cpf, email, telefone, data_nascimento, endereco, bairro, cidade, data_cadastro, ativo) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_ATUALIZAR =
            "UPDATE tutor SET nome = ?, cpf = ?, email = ?, telefone = ?, data_nascimento = ?, "
                    + "endereco = ?, bairro = ?, cidade = ?, data_cadastro = ?, ativo = ? WHERE id = ?";

    private static final String SQL_EXCLUIR = "DELETE FROM tutor WHERE id = ?";
    private static final String SQL_CONTAR = "SELECT COUNT(*) FROM tutor";

    public List<Tutor> listarTodos() {
        return consultarLista(SQL_LISTAR, ps -> {
        }, TutorDao::mapear);
    }

    public List<Tutor> listarAtivos() {
        return consultarLista(SQL_LISTAR_ATIVOS, ps -> {
        }, TutorDao::mapear);
    }

    public Optional<Tutor> buscarPorId(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(consultarUnico(SQL_BUSCAR, ps -> ps.setLong(1, id), TutorDao::mapear));
    }

    /** Insere (id == null) ou atualiza, e devolve o tutor com o id persistido. */
    public Tutor salvar(Tutor tutor) {
        if (tutor.getId() == null) {
            long id = inserir(SQL_INSERIR, ps -> {
                ps.setString(1, tutor.getNome());
                ps.setString(2, tutor.getCpf());
                ps.setString(3, tutor.getEmail());
                ps.setString(4, tutor.getTelefone());
                Sql.setData(ps, 5, tutor.getDataNascimento());
                ps.setString(6, tutor.getEndereco());
                ps.setString(7, tutor.getBairro());
                ps.setString(8, tutor.getCidade());
                Sql.setData(ps, 9, tutor.getDataCadastro());
                Sql.setBoolean(ps, 10, tutor.isAtivo());
            });
            tutor.setId(id);
        } else {
            atualizar(SQL_ATUALIZAR, ps -> {
                ps.setString(1, tutor.getNome());
                ps.setString(2, tutor.getCpf());
                ps.setString(3, tutor.getEmail());
                ps.setString(4, tutor.getTelefone());
                Sql.setData(ps, 5, tutor.getDataNascimento());
                ps.setString(6, tutor.getEndereco());
                ps.setString(7, tutor.getBairro());
                ps.setString(8, tutor.getCidade());
                Sql.setData(ps, 9, tutor.getDataCadastro());
                Sql.setBoolean(ps, 10, tutor.isAtivo());
                ps.setLong(11, tutor.getId());
            });
        }
        return tutor;
    }

    public void excluir(Long id) {
        if (id != null) {
            atualizar(SQL_EXCLUIR, ps -> ps.setLong(1, id));
        }
    }

    public long contar() {
        return contar(SQL_CONTAR);
    }

    private static Tutor mapear(ResultSet rs) throws java.sql.SQLException {
        Tutor tutor = new Tutor();
        tutor.setId(rs.getLong("id"));
        tutor.setNome(rs.getString("nome"));
        tutor.setCpf(rs.getString("cpf"));
        tutor.setEmail(rs.getString("email"));
        tutor.setTelefone(rs.getString("telefone"));
        tutor.setDataNascimento(Sql.getData(rs, "data_nascimento"));
        tutor.setEndereco(rs.getString("endereco"));
        tutor.setBairro(rs.getString("bairro"));
        tutor.setCidade(rs.getString("cidade"));
        tutor.setDataCadastro(Sql.getData(rs, "data_cadastro"));
        tutor.setAtivo(Sql.getBoolean(rs, "ativo"));
        return tutor;
    }
}
