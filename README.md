# JPets - Aplicação para Gestão de Pet Shop

Sistema web para gestão de pet shop desenvolvido com Java EE 8, JSF e PrimeFaces.

---

## 🛠️ Tecnologias e Versões Utilizadas

* **Java:** Java SE Development Kit (JDK) 8
* **Servidor de Aplicação:** Apache Tomcat 9.0.x
* **Gerenciador de Build:** Apache Maven 3.9+
* **Java Servlet API:** 4.0.1 (`javax.servlet:javax.servlet-api:4.0.1` – fornecido pelo Tomcat 9)
* **JavaServer Faces (JSF):** 2.3.9 (`org.glassfish:javax.faces:2.3.9` – Mojarra)
* **Expression Language (EL):** 3.0 (`org.glassfish:javax.el:3.0.1-b12`)
* **CDI (Contexts and Dependency Injection):** CDI 2.0 (`weld-servlet-shaded:3.1.9.Final` e `cdi-api:2.0`)
* **Componentes de UI:** PrimeFaces 12.0.0
* **Tema PrimeFaces:** `saga` (PrimeOne Design)
* **Ícones:** PrimeIcons (incluso no PrimeFaces)
* **Banco de Dados:** SQLite 3 (`org.xerial:sqlite-jdbc:3.45.3.0`)
* **Pool de Conexões:** HikariCP 4.0.3
* **Logs:** SLF4J 1.7.36 com binding para `java.util.logging` (vai para o log do Tomcat)

---

## 💾 Persistência em banco de dados (SQLite)

Todos os cadastros (**Funcionários**, **Tutores** e **Pets**) são gravados em um banco
SQLite. Não há mais dados apenas em memória: eles sobrevivem ao reinício do Tomcat.

### Como funciona

O acesso ao banco segue três camadas:

```
bean (interface)  →  service (regras de negócio)  →  dao (SQL)  →  SQLite
```

* **`br.com.botogoske.persistence.BancoConfig`** — cria o pool de conexões, cria o
  diretório do arquivo e executa o DDL em `src/main/resources/db/schema.sql`.
  Usa `PRAGMA foreign_keys`, `journal_mode=WAL` e `busy_timeout`.
* **`br.com.botogoske.dao.*Dao`** — todo o SQL (INSERT, UPDATE, DELETE, SELECT).
* **`br.com.botogoske.service.*Service`** — regras de negócio (ex.: não excluir tutor
  que tenha pets vinculados).

As telas (`*.xhtml`) e os beans não mudaram: a API pública dos services foi mantida.

### Local do arquivo

O arquivo **não** fica dentro da aplicação, para não ser apagado a cada novo deploy.
A resolução acontece nesta ordem:

1. propriedade de sistema `jpets.db.path`;
2. `${catalina.base}/jpets-data/jpets.db` (padrão no Tomcat);
3. `${user.dir}/jpets-data/jpets.db`.

Para escolher outro caminho, defina a propriedade no `setenv.bat`/`setenv.sh` do Tomcat:

```bash
# Linux / macOS
export CATALINA_OPTS="$CATALINA_OPTS -Djpets.db.path=/var/lib/jpets/jpets.db"
```

```powershell
# Windows (setenv.bat)
set CATALINA_OPTS=%CATALINA_OPTS% -Djpets.db.path=C:\dados\jpets.db
```

O tamanho do pool pode ser ajustado com `-Djpets.db.poolSize=N` (padrão: 5).

### Estrutura das tabelas

| Tabela | Colunas principais |
| --- | --- |
| `tutor` | id, nome, cpf, email, telefone, data_nascimento, endereco, bairro, cidade, data_cadastro, ativo |
| `funcionario` | id, nome, cpf, email, telefone, data_nascimento, data_admissao, cargo, salario, ativo |
| `pet` | id, nome, especie, raca, sexo, porte, peso, data_nascimento, data_cadastro, observacoes, **tutor_id**, ativo |

Detalhes importantes:

* Os `id` são gerados pelo próprio SQLite (`INTEGER PRIMARY KEY AUTOINCREMENT`).
* `LocalDate` é gravado como texto no formato ISO-8601 (`AAAA-MM-DD`).
* Enums (`Cargo`, `Especie`, `SexoPet`, `Porte`) são gravados pelo `name()`.
* `boolean` é gravado como `INTEGER` (0/1).
* `pet.tutor_id` é uma chave estrangeira para `tutor(id)` com `ON DELETE RESTRICT`, o que
  impede no banco a exclusão de um tutor que tenha pets vinculados.

### Dados iniciais

Na primeira execução (tabela vazia) o sistema cadastra automaticamente 2 funcionários,
3 tutores e 4 pets de exemplo. Em execuções seguintes os dados **não** são duplicados:
o banco passa a ser a fonte da verdade e o que você salvar pela tela permanece.

Para começar do zero, basta apagar o arquivo `jpets.db` e reiniciar o Tomcat.

---

## 📦 Build e Empacotamento

Para compilar e gerar o arquivo WAR:

```bash
mvn clean package
```

O arquivo empacotado será gerado em:
`target/jpets.war`

---

## 🚀 Deploy no Apache Tomcat

1. Copie o arquivo gerado `target/jpets.war` para o diretório `webapps/` do Apache Tomcat:

   ```bash
   cp target/jpets.war $CATALINA_HOME/webapps/
   ```

2. Inicie o servidor Tomcat:

   ```powershell
   # Windows (PowerShell)
   $env:CATALINA_HOME\bin\startup.bat
   ```

   ```bash
   # Linux / macOS
   $CATALINA_HOME/bin/startup.sh
   ```

---

## 🌐 Acesso à Aplicação

Após a inicialização do Tomcat, acesse pelo navegador:

* **URL Principal / Login:** [http://localhost:8080/jpets/](http://localhost:8080/jpets/)
