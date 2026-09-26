package br.com.botogoske.persistence;

import java.sql.SQLException;

/**
 * Erro de infraestrutura do banco de dados (arquivo, pool, SQL).
 *unchecked para nao obrigar os services a lidar com SQLException.
 */
public class BancoException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public BancoException(String message) {
        super(message);
    }

    public BancoException(String message, Throwable cause) {
        super(message, cause);
    }

    public static BancoException envolver(SQLException e, String contexto) {
        return new BancoException(contexto + ": " + e.getMessage(), e);
    }
}
