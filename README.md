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
