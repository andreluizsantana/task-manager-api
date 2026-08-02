# TaskHub API

Uma REST API para gerenciamento de tarefas com suporte a tarefas recorrentes, agendamento automático e autenticação JWT.

**Stack:** Java 17 • Spring Boot 3.4.4 • PostgreSQL • JPA/Hibernate • MapStruct • Spring Security • JWT (Auth0) • Log4j2

---

## Funcionalidades

- CRUD completo de tarefas
- Tarefas simples (`UNICA`) e recorrentes (`MENSAL`, até 36 ocorrências)
- Atualização automática de status de tarefas vencidas (job diário)
- Agrupamento de tarefas recorrentes via `TaskGroup`
- Paginação configurável
- Autenticação JWT + registro com BCrypt
- Tratamento global de erros (validação, autenticação, conflito)
- Testes unitários e de integração (JUnit 5 + Mockito)
- Formatação automática com Spotless

---

## Pré-requisitos

| Requisito   | Versão   |
|-------------|----------|
| Java        | 17+      |
| PostgreSQL  | 12+      |
| Maven       | 3.8+     |

---

## Configuração

Crie o banco de dados:

```sql
CREATE DATABASE tasks;
```

A aplicação é configurada por variáveis de ambiente (com defaults para desenvolvimento local):

| Variável          | Default                                                     |
|-------------------|-------------------------------------------------------------|
| `DB_URL`          | `jdbc:postgresql://localhost:5432/tasks?reWriteBatchedInserts=true` |
| `DB_USERNAME`     | `postgres`                                                  |
| `DB_PASSWORD`     | `root`                                                      |
| `JWT_SECRET`      | valor de desenvolvimento (troque em produção)               |
| `JWT_EXPIRATION`  | `86400` (segundos = 24h)                                    |

> **Segurança:** nunca commite segredos. Em produção, defina `DB_PASSWORD` e `JWT_SECRET` via variáveis de ambiente ou seu gerenciador de segredos.

Instale as dependências e execute:

```bash
mvn clean install
mvn spring-boot:run
```

API disponível em: **http://localhost:8080**

---

## Autenticação

Todos os endpoints de tarefas exigem o header:

```
Authorization: Bearer <token>
```

### Registrar usuário

**POST** `/api/auth/register`

```json
{
  "name": "André Luiz",
  "email": "andre@example.com",
  "password": "senha123"
}
```

**Resposta (201):**

```json
{
  "id": 1,
  "name": "André Luiz",
  "email": "andre@example.com"
}
```

### Login

**POST** `/api/auth/login`

```json
{
  "email": "andre@example.com",
  "password": "senha123"
}
```

**Resposta (200):**

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

---

## Endpoints de Tarefas

### GET `/api/tasks` — Listar tarefas (paginado)

Query params: `page`, `size` (máx 50), `sort`.

**Resposta (200):**

```json
{
  "content": [
    {
      "id": 1,
      "title": "Comprar pão",
      "description": "Pão integral no mercado",
      "status": "PENDENTE",
      "recurrenceType": "UNICA",
      "createdAt": "2026-04-25T14:30:00",
      "updatedAt": "2026-04-25T14:30:00",
      "occurrence": null,
      "executionDate": "2026-05-15",
      "taskGroup": null
    }
  ],
  "pageable": { "pageNumber": 0, "pageSize": 50, "totalElements": 1, "totalPages": 1 }
}
```

### GET `/api/tasks/{id}` — Buscar por ID

**Resposta (200):** mesmo formato acima. **Erros:** `404` não encontrada.

### POST `/api/tasks` — Criar tarefa simples

`recurrenceType` deve ser `UNICA`.

```json
{
  "title": "Estudar Spring Security",
  "description": "Implementar JWT authentication",
  "recurrenceType": "UNICA",
  "executionDate": "2026-05-15"
}
```

**Resposta (201):** tarefa criada com `status: PENDENTE`.

**Erros:**
- `400` — `recurrenceType` não informado
- `400` — `Use '/api/tasks/recurrent' para tarefas recorrentes.`

### POST `/api/tasks/recurrent` — Criar tarefas recorrentes

`recurrenceType` deve ser `MENSAL` e `totalRecurrences` entre 1 e 36.

```json
{
  "title": "Pagar aluguel",
  "description": "Aluguel do apartamento",
  "recurrenceType": "MENSAL",
  "totalRecurrences": 12,
  "executionDate": "2026-05-01"
}
```

**Resposta (201):** lista de tarefas, uma por ocorrência (`occurrence` incrementa +1 mês), agrupadas no mesmo `taskGroup`:

```json
[
  {
    "id": 3,
    "title": "Pagar aluguel",
    "status": "PENDENTE",
    "recurrenceType": "MENSAL",
    "occurrence": 1,
    "executionDate": "2026-05-01",
    "taskGroup": { "id": 1, "frequency": "MENSAL", "totalRecurrences": 12 }
  }
]
```

**Erros:**
- `400` — `Dados de recorrência inválidos.`
- `400` — `Total de recorrência não pode exceder 36 tarefas.`

### PUT `/api/tasks/{id}` — Atualizar tarefa

Atualização parcial (campos nulos são ignorados).

```json
{
  "title": "Estudar Spring Security (atualizado)",
  "status": "EM_ANDAMENTO",
  "executionDate": "2026-05-20"
}
```

**Resposta (200):** tarefa atualizada. **Erros:** `404` não encontrada.

### DELETE `/api/tasks/{id}` — Deletar tarefa

**Resposta (204):** No Content. **Erros:** `404` não encontrada.

---

## Formato de Erros

Todos os erros retornam:

```json
{
  "message": "Requisição inválida.",
  "status": 400,
  "timestamp": "2026-05-10T15:30:45",
  "errors": { "title": "O título não pode estar vazio" }
}
```

| Status | Causa                                    |
|--------|------------------------------------------|
| 400    | Validação de entrada / recorrência       |
| 401    | Credenciais inválidas                    |
| 404    | Recurso não encontrado                   |
| 409    | Conflito com dados existentes            |
| 500    | Erro interno (mensagem genérica)         |

---

## Status e Recorrência

| Status          | Descrição                                    |
|-----------------|----------------------------------------------|
| `PENDENTE`      | Tarefa não iniciada (padrão)                 |
| `EM_ANDAMENTO`  | Tarefa em execução                           |
| `CONCLUIDO`     | Tarefa concluída                             |
| `CANCELADO`     | Tarefa cancelada                             |
| `NAO_EXECUTADA` | Tarefa vencida (atualizada pelo job diário)  |

| Tipo          | Descrição                                  | Máximo |
|---------------|--------------------------------------------|--------|
| `UNICA`       | Tarefa singular, sem repetição             | 1      |
| `MENSAL`      | Incrementa 1 mês a cada ocorrência         | 36     |

---

## Agendamento Automático

Job diário que marca tarefas vencidas (`PENDENTE` com `executionDate` anterior a hoje) como `NAO_EXECUTADA`.

- **Cron:** `1 0 0 * * *` (00:00:01 todos os dias)
- **Timezone:** `America/Sao_Paulo`
- **Método:** `markOverdueTasks` em `TaskService`

---

## Estrutura do Projeto

```
taskhub/src/main/java/com/project/taskhub/
├── TaskhubApplication.java
├── controller/          # TaskController, AuthenticatorController
├── service/             # TaskService (CRUD, recorrência, job diário)
├── repository/          # TaskRepository, TaskGroupRepository, UserRepository
├── entity/              # Task, TaskBase, TaskGroup, User + enums/
├── dto/
│   ├── request/         # TaskRequestDTO, LoginRequestDTO, RegisterUserRequestDTO
│   ├── response/        # TaskResponseDTO, TaskGroupResponseDTO, LoginResponseDTO, RegisterUserResponseDTO, ErrorResponse
│   ├── update/          # TaskUpdateDTO
│   └── mapper/          # TaskMapper, UserMapper (MapStruct)
├── security/            # SecurityConfig, SecurityFilter, TokenConfiguration, AuthConfig, JWTUserData
├── exceptions/          # GlobalExceptionHandler, TaskNotFoundException, TaskRecurrenceException
└── audit/               # AuditJPA (@CreatedDate / @LastModifiedDate)
```

---

## Testes

```bash
mvn test
```

Cobertura atual (27 testes):

- `TaskServiceTest` — CRUD, validação de recorrência e job de vencidas
- `TaskMapperTest` — mapeamento DTO ↔ Entity
- `TaskControllerTest` — endpoints e validações via MockMvc

Formatação de código (obrigatória no build):

```bash
mvn spotless:apply
```

---

## Build e Deploy

```bash
mvn clean package
java -jar target/taskhub-0.0.1-SNAPSHOT.jar
```

---

## Autor

**André Luiz Santana**

- GitHub: [@andreluizsantana](https://github.com/andreluizsantana)
- LinkedIn: [andrelssr](https://www.linkedin.com/in/andrelssr/)
- Portfólio: [andreluiz.vercel.app](https://andreluiz.vercel.app)

## Licença

MIT
