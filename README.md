# task-manager-api
API REST de gerenciamento de tarefas desenvolvida com Spring Boot, JPA, PostgreSQL e MapStruct. CRUD completo, paginação, filtro por status e tratamento de erros centralizado.

# task-manager-api

API REST de gerenciamento de tarefas desenvolvida com Spring Boot, JPA, PostgreSQL e MapStruct.

## Tecnologias

- Java 17
- Spring Boot 3.4.4
- Spring Data JPA / Hibernate
- PostgreSQL
- MapStruct
- Lombok
- Bean Validation

## Funcionalidades

- [x] CRUD completo de tarefas
- [x] Paginação e ordenação
- [x] Filtro por status
- [x] Tratamento de erros centralizado com `GlobalExceptionHandler`
- [x] Validação de dados de entrada com `@Valid`
- [ ] Tarefas recorrentes (única, mensal)
- [ ] Autenticação e autorização com Spring Security

## Estrutura do projeto

```
src/main/java/com/project/taskhub/
├── controller/       # Endpoints REST
├── service/          # Regras de negócio
├── repository/       # Acesso ao banco de dados
├── entity/           # Entidades JPA
│   └── enums/        # Enums (StatusTask, TipoRecorrencia)
├── dto/              # DTOs de entrada e saída + MapStruct
└── exceptions/       # Exceções customizadas e GlobalExceptionHandler
```

## Endpoints

| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/api/tasks` | Criar tarefa |
| `GET` | `/api/tasks` | Listar tarefas (paginado) |
| `GET` | `/api/tasks?status=PENDENTE` | Filtrar por status |
| `GET` | `/api/tasks/{id}` | Buscar por ID |
| `PUT` | `/api/tasks/{id}` | Atualizar tarefa |
| `DELETE` | `/api/tasks/{id}` | Deletar tarefa |

## Status de uma tarefa

| Status | Descrição |
|---|---|
| `PENDENTE` | Estado inicial — toda tarefa nasce com esse status |
| `EM_ANDAMENTO` | Tarefa em execução |
| `CONCLUIDO` | Tarefa finalizada |
| `CANCELADO` | Tarefa cancelada |

## Como rodar localmente

### Pré-requisitos
- Java 17+
- PostgreSQL rodando localmente
- Maven

### Configuração do banco

Crie um banco de dados chamado `tasks` no PostgreSQL e configure as credenciais no `application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/tasks
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
```

### Rodando a aplicação

```bash
./mvnw spring-boot:run
```

A aplicação sobe em `http://localhost:8080`.

## Exemplo de requisição

### Criar tarefa
```http
POST /api/tasks
Content-Type: application/json

{
  "titulo": "Estudar Spring Boot",
  "descricao": "Revisar anotações de JPA e MapStruct"
}
```

### Resposta
```json
{
  "id": 1,
  "titulo": "Estudar Spring Boot",
  "descricao": "Revisar anotações de JPA e MapStruct",
  "status": "PENDENTE",
  "criadoEm": "2026-04-19T15:00:00"
}
```
