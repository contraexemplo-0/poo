# Sistema de Gerenciamento de Diabetes

Projeto desenvolvido para a disciplina de **Programação Orientada a Objetos**, utilizando **Java, JavaFX e SQLite**.

## Sobre o Projeto

O Sistema de Gerenciamento de Diabetes é uma aplicação desktop desenvolvida com o objetivo de auxiliar pacientes diabéticos no acompanhamento diário da glicemia, alimentação e outros eventos relacionados à rotina de tratamento.

O projeto aplica conceitos de **Programação Orientada a Objetos**, arquitetura **MVC**, padrão **DAO** e camada de serviços. A interface gráfica foi desenvolvida com **JavaFX**, enquanto a persistência dos dados é realizada localmente utilizando **SQLite**.

## Funcionalidades

### Autenticação

- Cadastro de pacientes
- Login utilizando e-mail e senha
- Gerenciamento de sessão do usuário

### Dashboard

A tela principal apresenta uma visão geral dos registros do paciente, incluindo:

- Gráfico com os valores de glicemia dos últimos dias
- Média glicêmica
- Estimativa de HbA1c
- Quantidade de episódios de hipoglicemia
- Quantidade de episódios de hiperglicemia
- Lista de eventos recentes

### Registro de Eventos

O sistema utiliza uma estrutura unificada de eventos por meio da classe `RoutineEvent`.

É possível registrar:

- Glicemia
  - Valor
  - Categoria
  - Data e hora

- Refeição
  - Descrição
  - Quantidade de carboidratos
  - Índice glicêmico
  - Categoria
  - Data e hora

- Registro combinado de refeição e glicemia

O cadastro dos eventos é realizado por meio de uma janela modal.

### Relatórios

O sistema permite visualizar relatórios referentes aos períodos de:

- 7 dias
- 30 dias
- 90 dias

Os relatórios apresentam:

- Média glicêmica
- Valor mínimo
- Valor máximo
- Número de episódios de hipoglicemia
- Número de episódios de hiperglicemia
- Lista dos eventos registrados no período selecionado

### Educação

A aplicação possui uma área destinada a informações relacionadas ao diabetes, contendo conteúdos sobre:

- Sintomas de hipoglicemia
- Sintomas de hiperglicemia
- Cuidados gerais
- Boas práticas para pacientes diabéticos

### Configurações

A seção de configurações permite visualizar:

- Informações do paciente autenticado
- Sensibilidade a carboidratos
- Opção de logout

## Arquitetura

O projeto está organizado em diferentes camadas, separando responsabilidades entre aplicação, controladores, modelos, persistência e serviços.

```text
src/main/java/com/project/
│
├── app/
│   ├── Main.java
│   ├── NavigationManager.java
│   └── SessionManager.java
│
├── controller/
│   ├── LoginController.java
│   ├── RegisterController.java
│   ├── MainController.java
│   ├── DashboardController.java
│   ├── ReportsController.java
│   ├── EducationController.java
│   └── NewEventDialogController.java
│
├── model/
│   ├── Patient.java
│   ├── RoutineEvent.java
│   ├── GlucoseCategory.java
│   └── MealCategory.java
│
├── persistence/
│   ├── DatabaseManager.java
│   ├── UserDAO.java
│   └── RoutineEventDAO.java
│
└── service/
    ├── UserService.java
    ├── RoutineEventService.java
    └── GlucoseStatsService.java
```

### Organização das camadas

- `app`: inicialização da aplicação, navegação e gerenciamento de sessão
- `controller`: controle das telas e interação com a interface gráfica
- `model`: entidades e estruturas de domínio
- `persistence`: acesso e persistência dos dados
- `service`: regras de negócio e processamento das informações

## Banco de Dados

O sistema utiliza **SQLite** como banco de dados local.

O banco é criado automaticamente durante a primeira execução da aplicação.

### Tabela `patient`

Armazena informações dos pacientes, como:

- ID
- Nome
- E-mail
- Senha
- Data de nascimento
- Tipo de diabetes
- Gênero
- Data de diagnóstico
- Sensibilidade padrão

### Tabela `routine_event`

Armazena os eventos registrados pelo paciente.

Um evento pode representar:

- Uma medição de glicemia
- Uma refeição
- Uma combinação entre refeição e glicemia

## Tecnologias Utilizadas

- Java 21
- JavaFX
- SQLite
- Xerial SQLite JDBC
- Maven
- MVC
- DAO
- Javadoc

## Como Executar

### Pré-requisitos

Certifique-se de possuir instalado:

- Java 21 ou superior
- Maven 3.8 ou superior

### Executando o Projeto

Compile o projeto utilizando:

```bash
mvn clean compile
```

Caso o projeto esteja configurado com o plugin JavaFX, execute:

```bash
mvn javafx:run
```

Dependendo da configuração do `pom.xml`, também pode ser utilizada a execução por meio do Maven:

```bash
mvn clean compile exec:java
```

## Banco de Dados

O arquivo:

```text
database.db
```

é criado automaticamente na pasta raiz do projeto durante a execução da aplicação.

## Documentação

O código possui documentação Javadoc para os principais componentes da aplicação, incluindo:

- Controllers
- Services
- Models
- DAOs
- Componentes principais

Para gerar a documentação, execute:

```bash
mvn javadoc:javadoc
```

A documentação será gerada em:

```text
target/site/javadoc/
```

## Possíveis Melhorias Futuras

Entre possíveis evoluções do projeto estão:

- Configuração da sensibilidade a carboidratos pelo próprio usuário
- Geração de relatórios em PDF
- Predição de glicemia pós-prandial
- Notificações relacionadas a padrões glicêmicos
- Suporte a temas claro e escuro
- Integração com dispositivos externos de monitoramento

## Diagrama de Classes

O diagrama de classes do projeto está disponível em:

https://imgur.com/a/kqFUMJn

## Licença

Projeto desenvolvido para fins acadêmicos e educacionais.
