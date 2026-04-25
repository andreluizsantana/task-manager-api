# task-manager-api

API REST de gerenciamento de tarefas desenvolvida com Spring Boot, JPA, PostgreSQL e MapStruct. CRUD completo, paginação, filtro por status, tarefas recorrentes com agendamento automático e tratamento de erros centralizado.

## Tecnologias

- Java 17
- Spring Boot 3.4.4
- Spring Data JPA / Hibernate
- PostgreSQL
- MapStruct 1.6.3
- Lombok
- Bean Validation
- Spring Scheduling

## Funcionalidades

- [x] CRUD completo de tarefas
- [x] Paginação e ordenação
- [x] Filtro por status
- [x] Tratamento de erros centralizado com `GlobalExceptionHandler`
- [x] Validação de dados de entrada com `@Valid`
- [x] Tarefas recorrentes (MENSAL com múltiplas ocorrências)
- [x] Tarefas únicas (UNICA)
- [x] Data de execução com cálculo automático para recorrências
- [x] Agendamento automático para marcar tarefas vencidas como NAO_EXECUTADA
- [x] Relacionamento entre Task e TaskGroup
- [x] Enumerações para Status e Tipo de Recorrência
- [ ] Autenticação e autorização com Spring Security
- [ ] Testes unitários e integração completos
- [ ] Documentação com Swagger/OpenAPI

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
| `NAO_EXECUTADA` | Tarefa vencida (data de execução passou) |

## Tipos de Recorrência

| Tipo | Descrição | Uso |
|---|---|---|
| `UNICA` | Tarefa única (sem recorrência) | `POST /api/tasks` |
| `MENSAL` | Tarefa recorrente mensal | `POST /api/tasks/recurrent` |

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

# Batch configuration
spring.jpa.properties.hibernate.order_inserts=true
spring.jpa.properties.hibernate.jdbc.batch_size=50

# Paginação padrão
spring.data.web.pageable.default-page-size=100
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
  "descricao": "Revisar anotações de JPA e MapStruct",
  "dataExecucao": "2026-04-25"
}
```

**Resposta (201 Created):**
```json
{
  "id": 1,
  "titulo": "Estudar Spring Boot",
  "descricao": "Revisar anotações de JPA e MapStruct",
  "status": "PENDENTE",
  "tipoRecorrencia": null,
  "criadoEm": "2026-04-21T15:30:00",
  "atualizadoEm": "2026-04-21T15:30:00",
  "ocorrencia": 1,
  "dataExecucao": "2026-04-25",
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
Retorna uma lista com 12 tasks, cada uma com:
- `ocorrencia`: 1, 2, 3, ..., 12
- `dataExecucao`: calculada automaticamente (+1 mês, +2 meses, etc)
- `taskGroup.id`: referência ao grupo de recorrência
- `status`: PENDENTE (todas nascem assim)

```json
[
  {
    "id": 2,
    "titulo": "Pagar conta de água",
    "descricao": "Fazer o pagamento da conta de água todo mês",
    "status": "PENDENTE",
    "tipoRecorrencia": "MENSAL",
    "criadoEm": "2026-04-21T15:35:00",
    "atualizadoEm": "2026-04-21T15:35:00",
    "ocorrencia": 1,
    "dataExecucao": "2026-04-21",
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
    "tipoRecorrencia": "MENSAL",
    "criadoEm": "2026-04-21T15:35:00",
    "atualizadoEm": "2026-04-21T15:35:00",
    "ocorrencia": 2,
    "dataExecucao": "2026-05-21",
    "taskGroup": {
      "id": 1,
      "frequencia": "MENSAL",
      "totalRecorrencia": 12
    }
  }
  // ... mais 10 ocorrências
]
```

### Listar tarefas com paginação
```http
GET /api/tasks?page=0&size=10&sort=criadoEm,desc
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

## Agendamento automático

A aplicação roda um job todos os dias **à 00:01** (horário de Brasília) que:

1. Busca todas as tasks com `status = PENDENTE` e `dataExecucao < hoje`
2. Muda o status para `NAO_EXECUTADA`
3. Registra no log o número de tarefas atualizadas

**Configuração do cron:**
```
cron = "1 0 0 * * *"  → Minuto 1, Hora 0, Qualquer dia/mês/dia-da-semana
timezone = "America/Sao_Paulo"
```

Logs aparecem assim:
```
2026-04-25 00:01:00 INFO - Inicio da tarefa... 2026-04-25T00:01:00
2026-04-25 00:01:02 INFO - Atualizadas 5 tarefas vencidas
2026-04-25 00:01:02 INFO - Fim da tarefa... 2026-04-25T00:01:02
```

## Validações

### Tarefas únicas
- `titulo`: obrigatório, máx 120 caracteres
- `descricao`: obrigatório
- `tipoRecorrencia`: se informado, rejeita com erro 400

### Tarefas recorrentes
- `tipoRecorrencia`: obrigatório, deve ser `MENSAL`
- `totalRecorrencia`: obrigatório, deve ser > 0 e ≤ 60 (máximo 5 anos)
- Se violado: `400 Bad Request` com mensagem de erro

## Tratamento de Erros

Todas as exceções retornam um mapa estruturado:

```json
{
  "status": 404,
  "message": "Task com ID 999 não encontrada."
}
```

Códigos HTTP utilizados:
- `200 OK` — Requisição bem-sucedida
- `201 Created` — Recurso criado
- `204 No Content` — Deletado com sucesso
- `400 Bad Request` — Dados inválidos ou regra de negócio violada
- `404 Not Found` — Recurso não encontrado
- `500 Internal Server Error` — Erro no servidor

## Branches de desenvolvimento

O projeto utiliza Git Flow com branches de feature:

- `main` — branch principal (production)
- `feature/recorrencia-base` — entidades e mapeamentos
- `feature/recorrencia-dto-mapper` — DTOs e mappers
- `feature/recorrencia-service` — lógica de serviço e testes
- `feature/scheduling-vencidas` — agendamento automático

Cada feature é desenvolvida em branch separada, testada e mergeada via Pull Request.

## Performance

Otimizações implementadas:

- **Batch Insert:** `batch_size=50` para operações em massa
- **Rewrite Batched Inserts:** PostgreSQL otimiza múltiplos INSERTs
- **Order Inserts:** Hibernate ordena comandos para melhor performance
- **Paginação:** Limite padrão de 100 registros por página

## Roadmap

- [ ] Suporte a recorrência SEMANAL e ANUAL
- [ ] Endpoint para atualizar TaskGroup
- [ ] Endpoint para deletar TaskGroup em cascata
- [ ] Autenticação com Spring Security + JWT
- [ ] Testes unitários (JUnit 5) e integração (TestContainers)
- [ ] Documentação com Swagger/OpenAPI
- [ ] Cache com Redis
- [ ] Rate limiting
- [ ] Métricas com Micrometer/Prometheus

## Autor

André Luiz Santana  
Backend Java Developer  
GitHub: [@andreluizsantana](https://github.com/andreluizsantana)  
LinkedIn: [andrelssr](https://www.linkedin.com/in/andrelssr/)
