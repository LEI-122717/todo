## Equipa
**Nome do grupo:** _Bolacha Commit_  

**Membros:**
- José Jarmela — 122663
- Nuno Neves — 122666
- Gonçalo Rua — 122678
- Beatriz Nunes — 122717

**9. Link do video:** https://youtu.be/Am4L4uOe0Wc

# Todo README

# Task Manager App

Esta aplicação é uma solução para gestão de tarefas com funcionalidades adicionais de produtividade e integração com email. Permite criar, visualizar, enviar e exportar tarefas, bem como gerar QR Codes e fazer conversão de moedas.

---

## Funcionalidades Principais

1. **Gestão de Tasks**
   - Criar tarefas com descrição e data de vencimento.
   - Marcar tarefas como concluídas ou reabrir tarefas já concluídas.
   - Visualizar todas as tarefas num grid interativo.

2. **Envio de Tasks por Email**
   - Enviar a lista completa de tarefas para um endereço de email.
   - Notificações visuais de sucesso ou falha no envio.

3. **Geração de PDF**
   - Exportar todas as tarefas para um PDF com layout organizado.
   - Download do PDF diretamente pelo browser.

4. **QR Codes das Tarefas**
   - Gerar QR Codes individuais para cada tarefa (para partilha rápida ou rastreio).

5. **Conversão de Moedas**
   - Funcionalidade integrada para converter valores entre diferentes moedas (ex: EUR ↔ USD).


## Project Structure

The sources of your Todo have the following structure:

```
src
├── main/frontend
│   └── themes
│       └── default
│           ├── styles.css
│           └── theme.json
├── main/java
│   └── [application package]
│       ├── base
│       │   └── ui
│       │       ├── component
│       │       │   └── ViewToolbar.java
│       │       ├── MainErrorHandler.java
│       │       └── MainLayout.java
│       ├── examplefeature
│       │   ├── ui
│       │   │   └── TaskListView.java
│       │   ├── Task.java
│       │   ├── TaskRepository.java
│       │   └── TaskService.java                
│       └── Application.java       
└── test/java
    └── [application package]
        └── examplefeature
           └── TaskServiceTest.java                 
```

The main entry point into the application is `Application.java`. This class contains the `main()` method that start up 
the Spring Boot application.

The skeleton follows a *feature-based package structure*, organizing code by *functional units* rather than traditional 
architectural layers. It includes two feature packages: `base` and `examplefeature`.

* The `base` package contains classes meant for reuse across different features, either through composition or 
  inheritance. You can use them as-is, tweak them to your needs, or remove them.
* The `examplefeature` package is an example feature package that demonstrates the structure. It represents a 
  *self-contained unit of functionality*, including UI components, business logic, data access, and an integration test.
  Once you create your own features, *you'll remove this package*.

The `src/main/frontend` directory contains an empty theme called `default`, based on the Lumo theme. It is activated in
the `Application` class, using the `@Theme` annotation.

## Starting in Development Mode

To start the application in development mode, import it into your IDE and run the `Application` class. 
You can also start the application from the command line by running: 

```bash
./mvnw
```

## Building for Production

To build the application in production mode, run:

```bash
./mvnw -Pproduction package
```

To build a Docker image, run:

```bash
docker build -t my-application:latest .
```

If you use commercial components, pass the license key as a build secret:

```bash
docker build --secret id=proKey,src=$HOME/.vaadin/proKey .
```

## Getting Started

The [Getting Started](https://vaadin.com/docs/latest/getting-started) guide will quickly familiarize you with your new
Todo implementation. You'll learn how to set up your development environment, understand the project 
structure, and find resources to help you add muscles to your skeleton — transforming it into a fully-featured 
application.
