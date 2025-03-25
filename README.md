<p align="center">
    <img src="https://www.svgrepo.com/show/184143/java.svg" width="130" />
    <img src="https://www.edureka.co/blog/wp-content/uploads/2019/08/recyclebin-data-1.png" width="220" />
</p>

# Project Management System - v1.0.0

## Objetivo

Este projeto implementa uma API RESTFULL para gestão de projetos e desenvolvimento de software, visando substituir um sistema legado desktop. Desenvolvido para permitir o acesso remoto, especialmente em um contexto de trabalho à distância, o sistema gerencia clientes, projetos e atividades, com foco no planejamento e acompanhamento dos times e suas tarefas.

## Funcionalidades Implementadas

- [x] **Cadastro e gerenciamento de clientes**
- [x] **Cadastro e gerenciamento de projetos**
- [x] **Cadastro e gerenciamento de atividades**
- [x] **Relacionamento entre clientes, projetos e atividades**
- [x] **Visualização de projetos por status**

## Características Principais

- Adição de atividades a um projeto específico de um cliente
- Listagem de projetos em aberto (com diferentes status)
- Visualização detalhada dos projetos com suas atividades relacionadas
- Interface de fácil utilização para gerenciamento de todas as entidades

## Regras de Negócio

- Projetos pertencem a um cliente específico
- Atividades devem ser relacionadas a um projeto
- Projetos possuem status que determinam seu ciclo de vida (aberto, em progresso, concluído, etc.)
- Controle de integridade relacional entre clientes, projetos e atividades

## Pré-requisitos

Para a execução do serviço, é necessário ter instalado no ambiente os softwares abaixo:

- **Java SDK 21+**
- **Maven v3.9.0+**
- **PostgreSQL 17+**
- **Docker** (Opcional)

## Principais dependências

### Backend
- **Spring Boot**
- **Spring Data JPA**
- **Spring Validation**
- **PostgreSQL Driver**
- **Hibernate ORM**
- **FlywayDb** (migrações de banco de dados)
- **Lombok**
- **JUnit/Mockito** (Testes)

## Environment Variables

O projeto utiliza variáveis de ambiente que podem ser definidas nos arquivos **application.yaml** para o backend

## Testes

O sistema possui cobertura de testes, conforme requisito do projeto.

```bash
# Backend
mvn test
```

## Execução local

### Backend
Para executar o projeto de forma local, faça a configuração do arquivo application.yml e execute os comandos abaixo.

```bash
mvn spring-boot:run
```

## Instalação e execução com Docker

Para facilitar instalação e execução da aplicação, foram implementados containers Docker que realizam o processo de build, testes e execução.
O profile configurado para utilização no Docker é o **application-docker.yml**

Estrutura dos arquivos Docker:
- **Dockerfile:** responsável por realizar o build da imagem da aplicação
- **docker-compose.yml:** responsável por realizar o build e execução dos serviços

Os passos abaixo devem ser executados na raiz do projeto.

### Build

```bash
docker-compose -p projectmanagement -f .docker/docker-compose.yml build
```

### Up

```bash
docker-compose -p projectmanagement -f .docker/docker-compose.yml up -d
```

### Down

```bash
docker-compose -p projectmanagement -f .docker/docker-compose.yml down
```

## Swagger

Para facilitar a documentação e interação com a API deste projeto, utilizamos o Swagger, uma ferramenta que
permite visualizar, testar e entender melhor os endpoints disponíveis.

### Acessando o Swagger

Após iniciar o serviço backend localmente ou através do Docker, você pode acessar a interface do Swagger no seguinte endereço: **http://localhost:8080/swagger-ui/index.html**

## Arquitetura

O projeto segue uma arquitetura limpa (Clean Architecture) com Domain-Driven Design (DDD), estruturado nas seguintes camadas:

### Backend
- **Domain**: Entidades e regras de negócio
- **Application**: Use cases e interfaces de serviço
- **Infrastructure**: Implementações concretas (adaptadores, repositórios, controladores)

## Diagramas

O projeto inclui um diagrama de classes das entidades principais:
- **Client** (Cliente)
- **Project** (Projeto)
- **Activity** (Atividade)

## Postman

O projeto inclui um arquivo de collections do postman em /.postman, que pode ser importado para testar as interações com a APIS

## Desenvolvedor

- Bruno F Godoi - brunofgodoi@outlook.com.br
