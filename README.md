Sistema de Gerenciamento de Diabetes
Projeto de Programação Orientada a Objetos – Java + JavaFX + SQLite

Autores: Equipe do projeto
Ano: 2025

📌 Sobre o Projeto

Este sistema foi desenvolvido como trabalho da disciplina Programação Orientada a Objetos, com o objetivo de construir uma aplicação desktop funcional para auxiliar pacientes diabéticos no acompanhamento diário da glicemia e de sua alimentação.

A aplicação segue os princípios de POO, MVC, DAO, e utiliza JavaFX como interface gráfica, além de integração com SQLite para persistência dos dados.

🎯 Funcionalidades Implementadas
🔐 Autenticação

Cadastro de novo paciente

Login utilizando e-mail e senha

Gerenciamento de sessão

🏠 Dashboard (Visão Geral)

Gráfico de glicemia dos últimos dias

Estatísticas automáticas:

Média glicêmica

Estimativa de HbA1c

Contagem de hipoglicemias e hiperglicemias

Lista de eventos recentes

📝 Registro de Eventos

Sistema unificado de registro (RoutineEvent):

Glicemia: valor, categoria e data/hora

Refeição: descrição, carboidratos, índice glicêmico, categoria, data/hora

Registro combinado refeição + glicemia

Interface em modal para facilitar o cadastro

📊 Relatórios

Relatórios de 7, 30 e 90 dias contendo:

Média, mínimo, máximo

Número de hipos/hipers

Lista completa dos eventos filtrados

🎓 Educação

Uma aba dedicada a informações úteis sobre:

Sintomas de hipo e hiper

Cuidados gerais

Boas práticas para pacientes diabéticos

⚙️ Configurações ("Mais")

Informações do paciente logado

Sensibilidade a carboidratos (futuramente ajustável)

Logout

🧱 Arquitetura

O sistema segue uma estrutura modular e organizada:

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

🗄️ Banco de Dados

O banco utiliza SQLite e é criado automaticamente no primeiro uso.
Principais tabelas:

patient

id, nome, email, senha

data de nascimento

tipo de diabetes

gênero

data de diagnóstico

sensibilidade padrão

routine_event

Registro unificado de:

glicemia

refeição

ou ambos

💻 Tecnologias Utilizadas

Java 21

JavaFX

SQLite (Xerial JDBC)

Maven

Arquitetura MVC + DAO

Javadoc para documentação de código

🚀 Como Executar
1. Instalar Dependências

Certifique-se de ter:

Java 21+

Maven 3.8+

JavaFX configurado no seu ambiente

2. Rodar o Projeto

Via Maven plugin:

mvn clean compile exec:java


ou configurando o Main:

mvn javafx:run

3. Banco de Dados

O arquivo database.db será criado automaticamente na pasta raiz ao iniciar o sistema.

📚 Documentação

O projeto possui Javadoc completo para:

Controllers

Services

Models

DAO

Módulos principais da aplicação

Para gerar a documentação:

mvn javadoc:javadoc


Os arquivos serão gerados em:

target/site/javadoc/

🧪 Futuras Melhorias

Ajuste de sensibilidade a carboidratos pelo usuário

Geração de relatórios em PDF

Predição de glicemia pós-prandial (modelo de IA no backend)

Notificações inteligentes sobre padrões glicêmicos

Temas claro/escuro para a interface

Sincronização com dispositivos externos

👥 Autores

Trabalho desenvolvido para fins acadêmicos por:

Nome da equipe

Turma / Universidade

📄 Licença

Projeto acadêmico — uso livre para fins educacionais.

Diagrama de Classes:
https://imgur.com/a/kqFUMJn
