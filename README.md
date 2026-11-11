# Database Replicator

Um aplicativo desktop e serviço simples e prático desenvolvido para replicação de dados entre bancos de dados relacionais.

O objetivo principal deste projeto é demonstrar como construir uma aplicação funcional e robusta utilizando exclusivamente **Java Puro** e as bibliotecas nativas do JDK, mantendo o projeto leve, sem dependências externas ou frameworks pesados.

## 💻 Tecnologias Utilizadas

* **Java (JDK Nativo):** Toda a lógica de negócios, controle de dados e manipulação de fluxos.
* **Java Swing:** Interface gráfica desktop (GUI) nativa para interação com o usuário (quando não executado como serviço).
* **JDBC Nativo:** Conexão e execução de consultas SQL diretamente via drivers relacionais, sem o uso de ORM ou bibliotecas de abstração.
* **Sem Frameworks:** Desenvolvido **sem** Maven, Gradle, Spring Boot, Hibernate/JPA ou qualquer outra dependência de terceiros.

## ⚙️ Variáveis de Ambiente

Antes de executar o projeto, é **obrigatório** configurar as seguintes variáveis de ambiente no sistema. Elas contêm as credenciais de acesso ao banco de dados de controle da aplicação:

| Variável | Descrição | Exemplo |
| ----- | ----- | ----- |
| `db_connection` | URL de conexão JDBC com o banco de controle | `jdbc:postgresql://localhost:5432/db_controle` |
| `db_user` | Usuário do banco de controle | `postgres` |
| `db_pass` | Senha do banco de controle | `123456` |

### Exemplo de Configuração no Terminal

**Linux / macOS:**

```bash
export db_connection="jdbc:postgresql://localhost:5432/db_controle"
export db_user="meu_usuario"
export db_pass="minha_senha"
```

**Windows (PowerShell):**

```powershell
$env:db_connection="jdbc:postgresql://localhost:5432/db_controle"
$env:db_user="meu_usuario"
$env:db_pass="minha_senha"
```

## 🗄️ Inicialização do Banco de Controle

Antes de rodar a aplicação pela primeira vez, é necessário preparar a estrutura do banco de dados de controle. 

Execute o arquivo SQL localizado em:
```text
db/scripts.sql
```

Este script criará a estrutura de tabelas necessária para que o sistema funcione corretamente e permita a configuração dos bancos de dados e das tabelas que serão replicadas.

## 🚀 Como Executar

### 1. Pré-requisitos

* **JDK 8** (ou superior) instalado e configurado nas variáveis de ambiente.
* Driver JDBC (`.jar`) correspondente ao seu banco de dados.

### 2. Clonar o Repositório

```bash
git clone https://github.com/lucasesmeradino/database-replicator.git
cd database-replicator
```

### 3. Compilar o Projeto

Crie a pasta de saída `bin` e compile o código-fonte:

```bash
mkdir bin
javac -d bin -sourcepath src $(find src -name "*.java")
```

### 4. Execução da Aplicação

A classe principal de entrada da aplicação é a **`ReplicadorStart`**.

Ela exige o argumento `run_as_service` para definir a forma de execução:

* `run_as_service=false`: Inicializa a aplicação com a interface gráfica **Java Swing**.
* `run_as_service=true`: Executa a aplicação em modo **serviço (background/headless)**, sem carregar a interface gráfica.

#### Executando com Interface Gráfica (Desktop)

```bash
java -cp "bin:lib/*" ReplicadorStart run_as_service=false
```

#### Executando em Modo Serviço (Background)

```bash
java -cp "bin:lib/*" ReplicadorStart run_as_service=true
```

> **Nota:** Certifique-se de substituir `lib/*` pelo caminho onde o arquivo `.jar` do driver JDBC do seu banco de dados está localizado no parâmetro `-cp`. No Windows, utilize ponto e vírgula como separador de classpath (ex: `"bin;lib/*"`).
