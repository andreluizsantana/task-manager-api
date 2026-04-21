# task-manager-api

API REST de gerenciamento de tarefas desenvolvida com Spring Boot, JPA, PostgreSQL e MapStruct. CRUD completo, paginação, filtro por status, tarefas recorrentes e tratamento de erros centralizado.

## Tecnologias

- Java 17
- Spring Boot 3.4.4
- Spring Data JPA / Hibernate
- PostgreSQL
- MapStruct 1.6.3
- Lombok
- Bean Validation

## Funcionalidades

- [x] CRUD completo de tarefas
- [x] Paginação e ordenação
- [x] Filtro por status
- [x] Tratamento de erros centralizado com `GlobalExceptionHandler`
- [x] Validação de dados de entrada com `@Valid`
- [x] Tarefas recorrentes (MENSAL com múltiplas ocorrências)
- [x] Relacionamento entre Task e TaskGroup
- [x] Enumerações para Status e Tipo de Recorrência
- [ ] Autenticação e autorização com Spring Security
- [ ] Tarefas únicas com suporte a UNICA recorrência

## Estrutura do projeto

```
src/main/java/com/project/taskhub/
├── controller/          # Endpoints REST
├── service/             # Regras de negócio
├── repository/          # Acesso ao banco de dados
├── entity/              # Entidades JPA
│   ├── Task.java
│   ├── TaskBase.java    # Superclasse com timestamps
│   ├── TaskGroup.java
│   └── enums/
│       ├── StatusTask.java
│       └── TipoRecorrencia.java
├── dto/                 # DTOs de entrada e saída
│   ├── TaskRequestDTO.java
│   ├── TaskResponseDTO.java
│   ├── TaskUpdateDTO.java
│   ├── TaskGroupResponseDTO.java
│   ├── TaskMapper.java
│   └── TaskGroupMapper.java
└── exceptions/          # Exceções customizadas
    ├── TaskNotFoundException.java
    ├── TaskRecurrenceException.java
    └── GlobalExceptionHandler.java
```

## Endpoints

| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/api/tasks` | Criar tarefa única |
| `GET` | `/api/tasks` | Listar tarefas (paginado) |
| `GET` | `/api/tasks/{id}` | Buscar tarefa por ID |
| `PUT` | `/api/tasks/{id}` | Atualizar tarefa |
| `DELETE` | `/api/tasks/{id}` | Deletar tarefa |
| `POST` | `/api/tasks/recurrent` | Criar tarefas recorrentes (MENSAL) |

## Status de uma tarefa

| Status | Descrição |
|---|---|
| `PENDENTE` | Estado inicial — toda tarefa nasce com esse status |
| `EM_ANDAMENTO` | Tarefa em execução |
| `CONCLUIDO` | Tarefa finalizada |
| `CANCELADO` | Tarefa cancelada |

## Tipos de Recorrência

| Tipo | Descrição |
|---|---|
| `UNICA` | Tarefa única (sem recorrência) — uso em POST `/api/tasks` |
| `MENSAL` | Tarefa recorrente mensal — uso em POST `/api/tasks/recurrent` |

## Como rodar localmente

### Pré-requisitos
- Java 17+
- PostgreSQL rodando localmente
- Maven

### Configuração do banco

Crie um banco de dados chamado `tasks` no PostgreSQL e configure no `application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/tasks?reWriteBatchedInserts=true
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.open-in-view=false
```

### Rodando a aplicação

```bash
./mvnw spring-boot:run
```

A aplicação sobe em `http://localhost:8080`.

## Exemplos de requisição

### Criar tarefa única
```http
POST /api/tasks
Content-Type: application/json

{
  "titulo": "Estudar Spring Boot",
  "descricao": "Revisar anotações de JPA e MapStruct"
}
```

**Resposta (201 Created):**
```json
{
  "id": 1,
  "titulo": "Estudar Spring Boot",
  "descricao": "Revisar anotações de JPA e MapStruct",
  "status": "PENDENTE",
  "criadoEm": "2026-04-21T15:30:00",
  "atualizadoEm": "2026-04-21T15:30:00",
  "ocorrencia": null,
  "taskGroup": null
}
```

### Criar tarefas recorrentes (MENSAL)
```http
POST /api/tasks/recurrent
Content-Type: application/json

{
  "titulo": "Pagar conta de água",
  "descricao": "Fazer o pagamento da conta de água todo mês",
  "tipoRecorrencia": "MENSAL",
  "totalRecorrencia": 12
}
```

**Resposta (201 Created):**
```json
[
  {
    "id": 2,
    "titulo": "Pagar conta de água",
    "descricao": "Fazer o pagamento da conta de água todo mês",
    "status": "PENDENTE",
    "criadoEm": "2026-04-21T15:35:00",
    "atualizadoEm": "2026-04-21T15:35:00",
    "ocorrencia": 1,
    "taskGroup": {
      "id": 1,
      "frequencia": "MENSAL",
      "totalRecorrencia": 12,
      "criadoEm": "2026-04-21T15:35:00",
      "atualizadoEm": "2026-04-21T15:35:00"
    }
  },
  {
    "id": 3,
    "titulo": "Pagar conta de água",
    "descricao": "Fazer o pagamento da conta de água todo mês",
    "status": "PENDENTE",
    "criadoEm": "2026-04-21T15:35:00",
    "atualizadoEm": "2026-04-21T15:35:00",
    "ocorrencia": 2,
    "taskGroup": {
      "id": 1,
      "frequencia": "MENSAL",
      "totalRecorrencia": 12,
      "criadoEm": "2026-04-21T15:35:00",
      "atualizadoEm": "2026-04-21T15:35:00"
    }
  }
  // ... mais 10 ocorrências
]
```

### Listar tarefas com paginação
```http
GET /api/tasks?page=0&size=10
```

### Buscar tarefa por ID
```http
GET /api/tasks/1
```

### Atualizar tarefa
```http
PUT /api/tasks/1
Content-Type: application/json

{
  "titulo": "Estudar Spring Boot e JPA",
  "descricao": "Revisar MapStruct e Lombok"
}
```

### Deletar tarefa
```http
DELETE /api/tasks/1
```

## Tratamento de Erros

Todas as exceções retornam um mapa estruturado:

```json
{
  "status": "404",
  "message": "Task com ID 999 não encontrada."
}
```

Códigos HTTP utilizados:
- `200 OK` — Requisição bem-sucedida
- `201 Created` — Recurso criado
- `204 No Content` — Deletado com sucesso
- `400 Bad Request` — Dados inválidos ou regra de negócio violada
- `404 Not Found` — Recurso não encontrado

## Roadmap

- [ ] Suporte a recorrência UNICA com validações
- [ ] Endpoint para atualizar TaskGroup
- [ ] Endpoint para deletar TaskGroup e suas Tasks
- [ ] Autenticação com Spring Security
- [ ] Testes unitários e integração
- [ ] Documentação com Swagger/OpenAPI
