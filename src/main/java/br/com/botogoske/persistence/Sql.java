package br.com.botogoske.persistence;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Conversores entre tipos Java e tipos do SQLite.
 *
 * <p>O SQLite nao possui tipos nativos de data nem de enumeracao, entao:
 * <ul>
 *   <li>{@link LocalDate} e gravada como TEXT no formato ISO-8601 ({@code yyyy-MM-dd});</li>
 *   <li>enumeracoes sao gravadas pelo {@link Enum#name()} (coluna TEXT);</li>
 *   <li>{@code boolean} e gravado como INTEGER (0/1).</li>
 * </ul>
 */
public final class Sql {

    private Sql() {
    }

    public static void setData(PreparedStatement ps, int indice, LocalDate data) throws SQLException {
        if (data == null) {
            ps.setNull(indice, java.sql.Types.VARCHAR);
        } else {
            ps.setString(indice, data.toString());
        }
    }

    public static LocalDate getData(ResultSet rs, String coluna) throws SQLException {
        String valor = rs.getString(coluna);
        if (valor == null || valor.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(valor);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    public static void setEnum(PreparedStatement ps, int indice, Enum<?> valor) throws SQLException {
        if (valor == null) {
            ps.setNull(indice, java.sql.Types.VARCHAR);
        } else {
            ps.setString(indice, valor.name());
        }
    }

    public static <E extends Enum<E>> E getEnum(ResultSet rs, String coluna, Class<E> tipo) throws SQLException {
        String valor = rs.getString(coluna);
        if (valor == null || valor.trim().isEmpty()) {
            return null;
        }
        try {
            return Enum.valueOf(tipo, valor);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static void setBoolean(PreparedStatement ps, int indice, boolean valor) throws SQLException {
        ps.setInt(indice, valor ? 1 : 0);
    }

    public static boolean getBoolean(ResultSet rs, String coluna) throws SQLException {
        return rs.getInt(coluna) == 1;
    }

    public static void setDouble(PreparedStatement ps, int indice, Double valor) throws SQLException {
        if (valor == null) {
            ps.setNull(indice, java.sql.Types.REAL);
        } else {
            ps.setDouble(indice, valor);
        }
    }
}
