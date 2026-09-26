package br.com.botogoske.dao;

import br.com.botogoske.persistence.BancoConfig;
import br.com.botogoske.persistence.BancoException;

import javax.inject.Inject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Base dos DAOs: resolve a conexao e engole {@link SQLException} em
 * {@link BancoException}. As classes concretas recebem o escopo
 * {@code @ApplicationScoped}, que nao e herdado.
 */
public abstract class DaoBase {

    @Inject
    protected BancoConfig banco;

    protected Connection conectar() {
        try {
            return banco.getDataSource().getConnection();
        } catch (SQLException e) {
            throw BancoException.envolver(e, "Nao foi possivel obter conexao com o banco");
        }
    }

    /** Executa um INSERT e devolve a chave gerada. */
    protected long inserir(String sql, Preparador preparador) {
        try (Connection conn = conectar();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparador.preparar(ps);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
            }
            throw new BancoException("O banco nao retornou o id gerado.");
        } catch (SQLException e) {
            throw BancoException.envolver(e, "Falha ao inserir registro");
        }
    }

    protected int atualizar(String sql, Preparador preparador) {
        try (Connection conn = conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            preparador.preparar(ps);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw BancoException.envolver(e, "Falha ao atualizar registro");
        }
    }

    protected <T> T consultarUnico(String sql, Preparador preparador, Mapeador<T> mapeador) {
        try (Connection conn = conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            preparador.preparar(ps);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapeador.mapear(rs) : null;
            }
        } catch (SQLException e) {
            throw BancoException.envolver(e, "Falha ao consultar registro");
        }
    }

    protected <T> java.util.List<T> consultarLista(String sql, Preparador preparador, Mapeador<T> mapeador) {
        java.util.List<T> resultados = new java.util.ArrayList<>();
        try (Connection conn = conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            preparador.preparar(ps);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    resultados.add(mapeador.mapear(rs));
                }
            }
            return resultados;
        } catch (SQLException e) {
            throw BancoException.envolver(e, "Falha ao listar registros");
        }
    }

    protected long contar(String sql) {
        try (Connection conn = conectar();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getLong(1) : 0L;
        } catch (SQLException e) {
            throw BancoException.envolver(e, "Falha ao contar registros");
        }
    }

    protected long contar(String sql, Preparador preparador) {
        try (Connection conn = conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            preparador.preparar(ps);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getLong(1) : 0L;
            }
        } catch (SQLException e) {
            throw BancoException.envolver(e, "Falha ao contar registros");
        }
    }

    /** Callback que define os parametros do PreparedStatement. */
    @FunctionalInterface
    public interface Preparador {
        void preparar(PreparedStatement ps) throws SQLException;
    }

    /** Callback que converte uma linha do ResultSet em objeto. */
    @FunctionalInterface
    public interface Mapeador<T> {
        T mapear(ResultSet rs) throws SQLException;
    }
}
