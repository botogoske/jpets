package br.com.botogoske.dao;

import br.com.botogoske.model.Especie;
import br.com.botogoske.model.Pet;
import br.com.botogoske.model.Porte;
import br.com.botogoske.model.SexoPet;
import br.com.botogoske.model.Tutor;
import br.com.botogoske.persistence.Sql;

import javax.enterprise.context.ApplicationScoped;
import java.sql.ResultSet;
import java.util.List;
import java.util.Optional;

/** Persistencia da tabela {@code pet}. */
@ApplicationScoped
public class PetDao extends DaoBase {

    private static final String COLUNAS =
            "p.id, p.nome, p.especie, p.raca, p.sexo, p.porte, p.peso, "
                    + "p.data_nascimento, p.data_cadastro, p.observacoes, p.tutor_id, p.ativo";

    /** Colunas do tutor com alias, usadas no LEFT JOIN. */
    private static final String COLUNAS_TUTOR =
            "t.id AS t_id, t.nome AS t_nome, t.cpf AS t_cpf, t.email AS t_email, t.telefone AS t_telefone, "
                    + "t.data_nascimento AS t_data_nascimento, t.endereco AS t_endereco, t.bairro AS t_bairro, "
                    + "t.cidade AS t_cidade, t.data_cadastro AS t_data_cadastro, t.ativo AS t_ativo";

    private static final String SELECT_COM_TUTOR =
            "SELECT " + COLUNAS + ", " + COLUNAS_TUTOR
                    + " FROM pet p LEFT JOIN tutor t ON t.id = p.tutor_id";

    private static final String SQL_LISTAR = SELECT_COM_TUTOR + " ORDER BY p.nome";
    private static final String SQL_BUSCAR = SELECT_COM_TUTOR + " WHERE p.id = ?";
    private static final String SQL_BUSCAR_POR_TUTOR = SELECT_COM_TUTOR + " WHERE p.tutor_id = ?";

    private static final String SQL_INSERIR =
            "INSERT INTO pet (nome, especie, raca, sexo, porte, peso, data_nascimento, data_cadastro, observacoes, tutor_id, ativo) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_ATUALIZAR =
            "UPDATE pet SET nome = ?, especie = ?, raca = ?, sexo = ?, porte = ?, peso = ?, "
                    + "data_nascimento = ?, data_cadastro = ?, observacoes = ?, tutor_id = ?, ativo = ? WHERE id = ?";

    private static final String SQL_EXCLUIR = "DELETE FROM pet WHERE id = ?";
    private static final String SQL_CONTAR = "SELECT COUNT(*) FROM pet";
    private static final String SQL_CONTAR_POR_TUTOR = "SELECT COUNT(*) FROM pet WHERE tutor_id = ?";

    public List<Pet> listarTodos() {
        return consultarLista(SQL_LISTAR, ps -> {
        }, PetDao::mapear);
    }

    public List<Pet> listarPorTutor(Long tutorId) {
        if (tutorId == null) {
            return listarTodos();
        }
        return consultarLista(SQL_BUSCAR_POR_TUTOR, ps -> ps.setLong(1, tutorId), PetDao::mapear);
    }

    public Optional<Pet> buscarPorId(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(consultarUnico(SQL_BUSCAR, ps -> ps.setLong(1, id), PetDao::mapear));
    }

    /** Insere (id == null) ou atualiza, e devolve o pet com o id persistido. */
    public Pet salvar(Pet pet) {
        Long tutorId = pet.getTutor() != null ? pet.getTutor().getId() : null;
        if (pet.getId() == null) {
            long id = inserir(SQL_INSERIR, ps -> {
                ps.setString(1, pet.getNome());
                Sql.setEnum(ps, 2, pet.getEspecie());
                ps.setString(3, pet.getRaca());
                Sql.setEnum(ps, 4, pet.getSexo());
                Sql.setEnum(ps, 5, pet.getPorte());
                Sql.setDouble(ps, 6, pet.getPeso());
                Sql.setData(ps, 7, pet.getDataNascimento());
                Sql.setData(ps, 8, pet.getDataCadastro());
                ps.setString(9, pet.getObservacoes());
                setTutorId(ps, 10, tutorId);
                Sql.setBoolean(ps, 11, pet.isAtivo());
            });
            pet.setId(id);
        } else {
            atualizar(SQL_ATUALIZAR, ps -> {
                ps.setString(1, pet.getNome());
                Sql.setEnum(ps, 2, pet.getEspecie());
                ps.setString(3, pet.getRaca());
                Sql.setEnum(ps, 4, pet.getSexo());
                Sql.setEnum(ps, 5, pet.getPorte());
                Sql.setDouble(ps, 6, pet.getPeso());
                Sql.setData(ps, 7, pet.getDataNascimento());
                Sql.setData(ps, 8, pet.getDataCadastro());
                ps.setString(9, pet.getObservacoes());
                setTutorId(ps, 10, tutorId);
                Sql.setBoolean(ps, 11, pet.isAtivo());
                ps.setLong(12, pet.getId());
            });
        }
        return pet;
    }

    public void excluir(Long id) {
        if (id != null) {
            atualizar(SQL_EXCLUIR, ps -> ps.setLong(1, id));
        }
    }

    public long contar() {
        return contar(SQL_CONTAR);
    }

    public long contarPorTutor(Long tutorId) {
        if (tutorId == null) {
            return 0L;
        }
        return contar(SQL_CONTAR_POR_TUTOR, ps -> ps.setLong(1, tutorId));
    }

    private static void setTutorId(java.sql.PreparedStatement ps, int indice, Long tutorId)
            throws java.sql.SQLException {
        if (tutorId == null) {
            ps.setNull(indice, java.sql.Types.INTEGER);
        } else {
            ps.setLong(indice, tutorId);
        }
    }

    private static Pet mapear(ResultSet rs) throws java.sql.SQLException {
        Pet pet = new Pet();
        pet.setId(rs.getLong("id"));
        pet.setNome(rs.getString("nome"));
        pet.setEspecie(Sql.getEnum(rs, "especie", Especie.class));
        pet.setRaca(rs.getString("raca"));
        pet.setSexo(Sql.getEnum(rs, "sexo", SexoPet.class));
        pet.setPorte(Sql.getEnum(rs, "porte", Porte.class));
        double peso = rs.getDouble("peso");
        pet.setPeso(rs.wasNull() ? null : peso);
        pet.setDataNascimento(Sql.getData(rs, "data_nascimento"));
        pet.setDataCadastro(Sql.getData(rs, "data_cadastro"));
        pet.setObservacoes(rs.getString("observacoes"));
        pet.setAtivo(Sql.getBoolean(rs, "ativo"));
        pet.setTutor(mapearTutor(rs));
        return pet;
    }

    private static Tutor mapearTutor(ResultSet rs) throws java.sql.SQLException {
        long id = rs.getLong("t_id");
        if (rs.wasNull()) {
            return null;
        }
        Tutor tutor = new Tutor();
        tutor.setId(id);
        tutor.setNome(rs.getString("t_nome"));
        tutor.setCpf(rs.getString("t_cpf"));
        tutor.setEmail(rs.getString("t_email"));
        tutor.setTelefone(rs.getString("t_telefone"));
        tutor.setDataNascimento(Sql.getData(rs, "t_data_nascimento"));
        tutor.setEndereco(rs.getString("t_endereco"));
        tutor.setBairro(rs.getString("t_bairro"));
        tutor.setCidade(rs.getString("t_cidade"));
        tutor.setDataCadastro(Sql.getData(rs, "t_data_cadastro"));
        tutor.setAtivo(Sql.getBoolean(rs, "t_ativo"));
        return tutor;
    }
}
