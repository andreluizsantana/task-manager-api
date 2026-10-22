# TaskHub API

Uma REST API para gerenciamento de tarefas com suporte a tarefas recorrentes, agendamento automático e autenticação JWT.

<<<<<<< HEAD
**Stack:** Java 17 • Spring Boot 3.4.4 • PostgreSQL • JPA/Hibernate • MapStruct • Spring Security • JWT (Auth0) • Log4j2
=======
**Stack:** Java 17 • Spring Boot 3.4.4 • PostgreSQL • JPA/Hibernate • MapStruct • Spring Security • JWT • Logback
>>>>>>> a9f6966 (Corrige segurança, isolamento de tarefas e migrações do banco.)

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

<<<<<<< HEAD
> **Segurança:** nunca commite segredos. Em produção, defina `DB_PASSWORD` e `JWT_SECRET` via variáveis de ambiente ou seu gerenciador de segredos.

Instale as dependências e execute:
=======
```properties
# ======== Datasource ========
spring.datasource.url=jdbc:postgresql://localhost:5432/tasks?reWriteBatchedInserts=true
spring.datasource.username=postgres
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=org.postgresql.Driver

# ======== JPA/Hibernate ========
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=validate
spring.flyway.baseline-on-migrate=true
spring.flyway.baseline-version=1
spring.jpa.show-sql=false
spring.jpa.open-in-view=false

# ======== Paginação ========
spring.data.web.pageable.default-page-size=100
spring.data.web.pageable.max-page-size=100

# ======== Batch Optimization ========
spring.jpa.properties.hibernate.order_inserts=true
spring.jpa.properties.hibernate.jdbc.batch_size=50

# ======== Logging ========
logging.level.root=INFO
logging.level.com.project.taskhub=DEBUG
app.security.jwt.secret=${JWT_SECRET}
app.security.jwt.expiration=86400
app.cors.allowed-origin=http://localhost:3000
```

Defina `DB_PASSWORD` e `JWT_SECRET` no ambiente antes de iniciar a API. Em bancos existentes,
revise e corrija e-mails duplicados antes da primeira migração para permitir o índice único.
Tarefas já existentes ficam sem proprietário e não serão exibidas pelos endpoints autenticados;
atribua-as a usuários por uma migração de dados caso queira preservá-las na API.

### 4️⃣ Instale dependências
>>>>>>> a9f6966 (Corrige segurança, isolamento de tarefas e migrações do banco.)

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

<<<<<<< HEAD
`recurrenceType` deve ser `MENSAL` e `totalRecurrences` entre 1 e 36.
=======
Envie `executionDate` com a data da primeira ocorrência; cada ocorrência mensal parte dessa data.

**Headers:**
```
Authorization: Bearer <token>
Content-Type: application/json
```
>>>>>>> a9f6966 (Corrige segurança, isolamento de tarefas e migrações do banco.)

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

<<<<<<< HEAD
Todos os erros retornam:
=======
Dispara manualmente o job de atualização de tarefas vencidas.

**Headers:**
```
Authorization: Bearer <token>
```

**Resposta (200):**
```json
"Agendamento testado"
```

**Logs esperados:**
```
INFO: Início do jobTaskVencida em 2026-05-10T15:30:45.123456
INFO: Atualizadas 5 tarefas vencidas
INFO: Fim do jobTaskVencida em 2026-05-10T15:30:46.987654
```

---

## 📊 Status das Tarefas

| Status | Descrição | Transições |
|--------|-----------|-----------|
| `PENDENTE` | Tarefa não iniciada | → EM_ANDAMENTO, CANCELADO, NAO_EXECUTADA (automático) |
| `EM_ANDAMENTO` | Tarefa em execução | → CONCLUIDO, CANCELADO |
| `CONCLUIDO` | Tarefa concluída | Nenhuma |
| `CANCELADO` | Tarefa cancelada | Nenhuma |
| `NAO_EXECUTADA` | Tarefa vencida (automático) | Nenhuma |

---

## 🔄 Tipos de Recorrência

| Tipo | Padrão | Máximo | Observação |
|------|--------|--------|-----------|
| `UNICA` | Padrão | 1 | Tarefa singular, sem repetição |
| `MENSAL` | Customizável | 36 | Incrementa 1 mês a cada ocorrência |

---

## ⏰ Agendamento Automático

### Job: `jobTaskVencida`

**Cron:** `1 0 0 * * *` (00:00:01 AM todos os dias)

**Timezone:** `America/Sao_Paulo`

**Lógica:**
1. Busca tarefas com `status = PENDENTE` E `dataExecucao < hoje`
2. Atualiza status para `NAO_EXECUTADA`
3. Registra em log o resultado

**Exemplo de log:**
```
INFO  TaskService - Início do jobTaskVencida em 2026-05-11T00:00:01.234567
INFO  TaskService - Atualizadas 5 tarefas vencidas
INFO  TaskService - Fim do jobTaskVencida em 2026-05-11T00:00:02.456789
```

---

## 📁 Estrutura do Projeto

```
src/main/java/com/project/taskhub/
│
├── TaskhubApplication.java ..................... Main class
│
├── controller/
│   ├── TaskController.java ..................... REST endpoints para tarefas
│   └── AuthenticatorController.java ............ REST endpoints de autenticação
│
├── service/
│   ├── TaskService.java ........................ Lógica de tarefas (CRUD, recorrências, agendamento)
│   └── NotificationService.java ............... Notificações (auxiliar)
│
├── repository/
│   ├── TaskRepository.java ..................... Query methods customizadas para Task
│   ├── TaskGroupRepository.java ............... Persistência de TaskGroup
│   └── UserRepository.java ..................... Query methods customizadas para User
│
├── entity/
│   ├── Task.java .............................. Entidade Task (@Entity @Table)
│   ├── TaskBase.java .......................... Superclass com campos auditáveis
│   ├── TaskGroup.java ......................... Agrupador de tarefas recorrentes
│   ├── User.java .............................. Entidade de usuário
│   └── enums/
│       ├── StatusTask.java ................... Enumeração: PENDENTE, EM_ANDAMENTO, CONCLUIDO, CANCELADO, NAO_EXECUTADA
│       └── TipoRecorrencia.java .............. Enumeração: UNICA, MENSAL
│
├── dto/
│   ├── request/
│   │   ├── TaskRequestDTO.java ............... DTO para criar tarefas
│   │   ├── LoginRequestDTO.java ............. DTO para login
│   │   └── RegisterUserRequestDTO.java ...... DTO para registro
│   │
│   ├── response/
│   │   ├── TaskResponseDTO.java ............. DTO para resposta de tarefas
│   │   ├── TaskGroupResponseDTO.java ........ DTO para resposta de grupos
│   │   ├── LoginResponseDTO.java ............ DTO para token JWT
│   │   ├── RegisterUserResponseDTO.java .... DTO para resposta de registro
│   │   └── ErrorResponse.java ............... DTO para erros
│   │
│   ├── update/
│   │   └── TaskUpdateDTO.java ............... DTO para atualizar tarefas
│   │
│   └── mapper/
│       ├── TaskMapper.java ................... MapStruct: Task ↔ DTO
│       ├── UserMapper.java ................... MapStruct: User ↔ DTO
│       └── TaskGroupMapper.java ............. MapStruct: TaskGroup ↔ DTO
│
├── security/
│   ├── SecurityConfig.java ................... @Configuration de Spring Security
│   ├── SecurityFilter.java ................... Filtro JWT customizado
│   ├── TokenConfiguration.java .............. Configuração de JWT (secret, expiration)
│   ├── AuthConfig.java ....................... AuthenticationManager + PasswordEncoder beans
│   └── JWTUserData.java ...................... Classe para extrair dados do token
│
├── exceptions/
│   ├── GlobalExceptionHandler.java ........... @RestControllerAdvice para tratamento centralizado
│   ├── TaskNotFoundException.java ............ Exception customizada 404
│   ├── TaskRecurrenceException.java ......... Exception customizada 400
│   └── UserNameNotFoundException.java ....... Exception customizada 404
│
└── audit/
    └── AuditJPA.java .......................... Auditoria com @CreatedDate @LastModifiedDate
```

---

## 🛠️ Tecnologias e Dependências

| Dependência | Versão | Propósito |
|-------------|--------|----------|
| **Spring Boot** | 3.4.4 | Framework web |
| **Spring Security** | 6.x | Autenticação e autorização |
| **Spring Data JPA** | 3.x | ORM e queries |
| **Hibernate** | 6.6.11 | Persistência de dados |
| **PostgreSQL Driver** | 42.x | Driver JDBC |
| **MapStruct** | 1.6.3 | Mapeamento de objetos (DTO ↔ Entity) |
| **Lombok** | 1.18.x | Redução de boilerplate |
| **Java-JWT (Auth0)** | 4.4.0 | Geração e validação de tokens JWT |
| **Jakarta Validation** | 3.x | Bean Validation (@Valid, @NotBlank, etc) |
| **Logback** | Gerenciado pelo Spring Boot | Logging da aplicação |

---

## 🔒 Segurança

### Password Encoding

Senhas são encoded com **BCryptPasswordEncoder**:

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

### JWT Configuration

**Secret:** `tasktask` (⚠️ **Mude em produção!**)

**Propriedades do token:**
- **Algorithm:** HMAC256
- **Issuer:** TaskHub
- **Subject (sub):** Email do usuário
- **Claims customizados:**
  - `userId` — ID do usuário
  - `iat` (issued at) — Timestamp de emissão
  - `exp` (expiration) — Expira em 24h

**Exemplo de payload decodificado:**
```json
{
  "sub": "andre@example.com",
  "userId": 1,
  "iat": 1666676800,
  "exp": 1666763200
}
```

### SecurityFilter

Filtra requisições autenticadas:
1. Extrai token do header `Authorization: Bearer <token>`
2. Valida assinatura e expiração
3. Extrai usuário do banco
4. Define `Authentication` no contexto

---

## ❌ Tratamento de Erros

### GlobalExceptionHandler

Todos os erros retornam no formato:
>>>>>>> a9f6966 (Corrige segurança, isolamento de tarefas e migrações do banco.)

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
