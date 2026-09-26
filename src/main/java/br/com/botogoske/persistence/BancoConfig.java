package br.com.botogoske.persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Fonte de dados da aplicacao. Cria o pool de conexoes para o arquivo SQLite,
 * cria o diretorio do arquivo caso nao exista e aplica o esquema (db/schema.sql).
 *
 * <p>Local do arquivo, em ordem de precedencia:
 * <ol>
 *   <li>propriedade de sistema {@code jpets.db.path} (ex.: {@code -Djpets.db.path=D:\dados\jpets.db});</li>
 *   <li>{@code ${catalina.base}/jpets-data/jpets.db} (padrao no Tomcat);</li>
 *   <li>{@code ${user.dir}/jpets-data/jpets.db}.</li>
 * </ol>
 *
 * <p>O arquivo fica fora do diretorio da aplicacao (deploy) para nao ser apagado
 * a cada novo publish do WAR.
 */
@ApplicationScoped
public class BancoConfig {

    private static final Logger LOG = LoggerFactory.getLogger(BancoConfig.class);

    /** System property com o caminho do arquivo .db. */
    public static final String PROP_CAMINHO = "jpets.db.path";
    /** System property com o tamanho maximo do pool. */
    public static final String PROP_POOL = "jpets.db.poolSize";

    private static final String DRIVER = "org.sqlite.JDBC";
    /** Nome do recurso no classpath, SEM barra inicial (exigido por getResourceAsStream). */
    private static final String RECURSO_ESQUEMA = "db/schema.sql";
    private static final String SUFIXO_ESQUEMA = "jpets-data" + java.io.File.separator + "jpets.db";
    private static final int POOL_PADRAO = 5;

    /**
     * Pragmas aplicados a cada conexao via URL JDBC.
     * WAL permite leituras concorrentes durante a escrita e busy_timeout faz o
     * escritor esperar em vez de falhar com SQLITE_BUSY.
     */
    private static final String PRAGMAS = "?foreign_keys=true&busy_timeout=10000&journal_mode=WAL";

    private DataSource dataSource;

    @PostConstruct
    public void iniciar() {
        Path caminho = resolverCaminho();
        try {
            Path diretorio = caminho.getParent();
            if (diretorio != null) {
                Files.createDirectories(diretorio);
            }
        } catch (IOException e) {
            throw new BancoException("Nao foi possivel criar o diretorio do banco em " + caminho.getParent(), e);
        }

        try {
            org.sqlite.SQLiteConfig config = new org.sqlite.SQLiteConfig();
            config.setPragma(org.sqlite.SQLiteConfig.Pragma.FOREIGN_KEYS, "true");
            config.setPragma(org.sqlite.SQLiteConfig.Pragma.BUSY_TIMEOUT, "10000");
            config.setPragma(org.sqlite.SQLiteConfig.Pragma.JOURNAL_MODE, "WAL");

            com.zaxxer.hikari.HikariConfig hikari = new com.zaxxer.hikari.HikariConfig();
            hikari.setDriverClassName(DRIVER);
            hikari.setDataSourceProperties(config.toProperties());
            hikari.setJdbcUrl("jdbc:sqlite:" + caminho.toAbsolutePath());
            hikari.setMaximumPoolSize(poolSize());
            hikari.setPoolName("jpets-sqlite");
            hikari.setAutoCommit(true);

            dataSource = new com.zaxxer.hikari.HikariDataSource(hikari);
        } catch (RuntimeException e) {
            throw new BancoException("Falha ao configurar o pool SQLite para " + caminho.toAbsolutePath(), e);
        }

        criarEsquema();
        LOG.info("Banco SQLite inicializado em {}", caminho.toAbsolutePath());
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    @PreDestroy
    public void encerrar() {
        if (dataSource instanceof com.zaxxer.hikari.HikariDataSource) {
            com.zaxxer.hikari.HikariDataSource hikari = (com.zaxxer.hikari.HikariDataSource) dataSource;
            if (!hikari.isClosed()) {
                hikari.close();
            }
        }
    }

    private void criarEsquema() {
        String sql = lerRecurso(RECURSO_ESQUEMA);
        try (Connection conn = dataSource.getConnection(); Statement stmt = conn.createStatement()) {
            for (String comando : separarComandos(sql)) {
                stmt.execute(comando);
            }
        } catch (SQLException e) {
            throw BancoException.envolver(e, "Falha ao criar o esquema do banco");
        }
    }

    /** Divide o script em comandos, descartando comentarios de linha. */
    private static List<String> separarComandos(String sql) {
        List<String> comandos = new ArrayList<>();
        StringBuilder atual = new StringBuilder();
        for (String linha : sql.split("\\r?\\n")) {
            int comentario = linha.indexOf("--");
            if (comentario >= 0) {
                linha = linha.substring(0, comentario);
            }
            atual.append(linha).append('\n');
            if (linha.trim().endsWith(";")) {
                String comando = atual.toString().trim();
                comandos.add(comando.substring(0, comando.length() - 1).trim());
                atual.setLength(0);
            }
        }
        String restante = atual.toString().trim();
        if (!restante.isEmpty()) {
            comandos.add(restante);
        }
        return comandos;
    }

    private static String lerRecurso(String recurso) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (loader == null) {
            loader = BancoConfig.class.getClassLoader();
        }
        try (InputStream in = loader.getResourceAsStream(recurso)) {
            if (in == null) {
                throw new BancoException("Recurso de esquema nao encontrado no classpath: " + recurso);
            }
            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                String linha;
                while ((linha = reader.readLine()) != null) {
                    sb.append(linha).append('\n');
                }
            }
            return sb.toString();
        } catch (IOException e) {
            throw new BancoException("Nao foi possivel ler o recurso " + recurso, e);
        }
    }

    private static Path resolverCaminho() {
        String configurado = System.getProperty(PROP_CAMINHO);
        if (configurado != null && !configurado.trim().isEmpty()) {
            return Paths.get(configurado.trim()).toAbsolutePath().normalize();
        }
        String catalinaBase = System.getProperty("catalina.base");
        if (catalinaBase != null && !catalinaBase.trim().isEmpty()) {
            return Paths.get(catalinaBase.trim(), SUFIXO_ESQUEMA).toAbsolutePath().normalize();
        }
        return Paths.get(System.getProperty("user.dir"), SUFIXO_ESQUEMA).toAbsolutePath().normalize();
    }

    private static int poolSize() {
        try {
            return Integer.parseInt(System.getProperty(PROP_POOL, String.valueOf(POOL_PADRAO)));
        } catch (NumberFormatException e) {
            return POOL_PADRAO;
        }
    }
}
