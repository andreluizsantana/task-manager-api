# TaskHub API

Uma REST API para gerenciamento de tarefas com suporte a tarefas recorrentes, agendamento automático e autenticação JWT.

**Stack:** Java 17 • Spring Boot 3.4.4 • PostgreSQL • JPA/Hibernate • MapStruct • Spring Security • JWT • Log4j2

---

## 🎯 Funcionalidades

### Tarefas
- ✅ CRUD completo de tarefas
- ✅ Tarefas simples (UNICA) e recorrentes (MENSAL)
- ✅ Atualização automática de status (tarefas vencidas → NAO_EXECUTADA)
- ✅ Agrupamento de tarefas recorrentes via `TaskGroup`
- ✅ Paginação configurável (padrão: 100 registros/página)
- ✅ Validações com Bean Validation (Jakarta Validation)

### Segurança
- ✅ Autenticação com JWT (Auth0)
- ✅ Registro de novos usuários com BCrypt
- ✅ Login com geração de token (24h expiration)
- ✅ Proteção de endpoints autenticados via `SecurityFilter`
- ✅ Senhas encoded com BCryptPasswordEncoder

### Arquitetura
- ✅ Padrão de camadas (Controller → Service → Repository)
- ✅ DTOs com MapStruct (TaskMapper, UserMapper, TaskGroupMapper)
- ✅ Exception handling global com `@RestControllerAdvice`
- ✅ Transações gerenciadas com `@Transactional`
- ✅ Logging estruturado com Log4j2

### Performance & Otimizações
- ✅ `reWriteBatchedInserts=true` — Agrupa múltiplos INSERTs em uma transação PostgreSQL
- ✅ `order_inserts=true` — Otimiza buffer do Hibernate antes do flush
- ✅ `jdbc.batch_size=50` — Processa 50 registros por lote
- ✅ Paginação padrão — Previne sobrecarga de memória
- ✅ Named parameters em queries — Queries otimizadas e seguras (SQL injection prevention)
- ✅ LazyFetch nas associações — Reduz n+1 queries

### Agendamento
- ✅ Job automático diário (`@Scheduled`) — Atualiza tarefas vencidas → NAO_EXECUTADA
- ✅ Timezone configurado para America/Sao_Paulo
- ✅ Executável via endpoint para testes

---

## 📋 Pré-requisitos

| Requisito | Versão | Obs |
|-----------|--------|-----|
| Java | 17+ | Testado com JDK 21 |
| PostgreSQL | 12+ | — |
| Maven | 3.8+ | — |
| Git | Latest | — |
| Docker (opcional) | Latest | Para subir BD com docker-compose |

---

## ⚙️ Instalação e Configuração

### 1️⃣ Clone o repositório

```bash
git clone https://github.com/andreluizsantana/taskhub.git
cd taskhub
```

### 2️⃣ Suba o banco de dados (Docker)

```bash
docker-compose up -d
```

Ou crie manualmente:

```sql
CREATE DATABASE tasks;
```

### 3️⃣ Configure `application.properties`

Edite `src/main/resources/application.properties`:

```properties
# ======== Datasource ========
spring.datasource.url=jdbc:postgresql://localhost:5432/tasks?reWriteBatchedInserts=true
spring.datasource.username=postgres
spring.datasource.password=root
spring.datasource.driver-class-name=org.postgresql.Driver

# ======== JPA/Hibernate ========
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
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
```

### 4️⃣ Instale dependências

```bash
mvn clean install
```

### 5️⃣ Execute a aplicação

```bash
mvn spring-boot:run
```

API disponível em: **http://localhost:8080**

---

## 🔐 Autenticação

### Registrar novo usuário

**POST** `/api/auth/register`

**Body:**
```json
{
  "nome": "André Luiz",
  "email": "andre@example.com",
  "password": "senha123"
}
```

**Resposta (201 Created):**
```json
{
  "id": 1,
  "nome": "André Luiz",
  "email": "andre@example.com"
}
```

### Login

**POST** `/api/auth/login`

**Body:**
```json
{
  "email": "andre@example.com",
  "password": "senha123"
}
```

**Resposta (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### Usar token

Adicione em todos os endpoints autenticados:

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

---

## 📡 Endpoints da API

### GET `/api/tasks` — Listar todas as tarefas

**Query params:**
- `page=0` (padrão)
- `size=100` (máximo 100)
- `sort=id,desc`

**Headers:**
```
Authorization: Bearer <token>
```

**Resposta (200):**
```json
{
  "content": [
    {
      "id": 1,
      "titulo": "Comprar pão",
      "descricao": "Pão integral no mercado",
      "status": "PENDENTE",
      "tipoRecorrencia": "UNICA",
      "ocorrencia": 1,
      "dataExecucao": "2026-05-15",
      "criadoEm": "2026-04-25T14:30:00",
      "atualizadoEm": "2026-04-25T14:30:00",
      "taskGroup": null
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 100,
    "totalElements": 1,
    "totalPages": 1
  }
}
```

---

### GET `/api/tasks/{id}` — Buscar tarefa por ID

**Headers:**
```
Authorization: Bearer <token>
```

**Resposta (200):**
```json
{
  "id": 1,
  "titulo": "Comprar pão",
  "descricao": "Pão integral no mercado",
  "status": "PENDENTE",
  "tipoRecorrencia": "UNICA",
  "ocorrencia": 1,
  "dataExecucao": "2026-05-15",
  "criadoEm": "2026-04-25T14:30:00",
  "atualizadoEm": "2026-04-25T14:30:00",
  "taskGroup": null
}
```

**Erros:**
- `404` — Task com ID {id} não encontrada.

---

### POST `/api/tasks` — Criar tarefa simples

**Headers:**
```
Authorization: Bearer <token>
Content-Type: application/json
```

**Body:**
```json
{
  "titulo": "Estudar Spring Security",
  "descricao": "Implementar JWT authentication",
  "dataExecucao": "2026-05-15",
  "tipoRecorrencia": "UNICA"
}
```

**Resposta (201):**
```json
{
  "id": 2,
  "titulo": "Estudar Spring Security",
  "descricao": "Implementar JWT authentication",
  "status": "PENDENTE",
  "tipoRecorrencia": "UNICA",
  "ocorrencia": 1,
  "dataExecucao": "2026-05-15",
  "criadoEm": "2026-04-25T14:35:00",
  "atualizadoEm": "2026-04-25T14:35:00",
  "taskGroup": null
}
```

**Validações:**
- `titulo` — Obrigatório, máx 120 chars
- `descricao` — Obrigatória
- `dataExecucao` — Obrigatória
- `tipoRecorrencia` — Deve ser UNICA para este endpoint

**Erros:**
- `400` — Use '/api/tasks/recurrent' para tarefas recorrentes.

---

### POST `/api/tasks/recurrent` — Criar tarefas recorrentes

**Headers:**
```
Authorization: Bearer <token>
Content-Type: application/json
```

**Body:**
```json
{
  "titulo": "Pagar aluguel",
  "descricao": "Aluguel do apartamento",
  "tipoRecorrencia": "MENSAL",
  "totalRecorrencia": 12,
  "dataExecucao": "2026-05-01"
}
```

**Resposta (201):**
```json
[
  {
    "id": 3,
    "titulo": "Pagar aluguel",
    "descricao": "Aluguel do apartamento",
    "status": "PENDENTE",
    "tipoRecorrencia": "MENSAL",
    "ocorrencia": 1,
    "dataExecucao": "2026-05-01",
    "criadoEm": "2026-04-25T14:40:00",
    "atualizadoEm": "2026-04-25T14:40:00",
    "taskGroup": {
      "id": 1,
      "frequencia": "MENSAL",
      "totalRecorrencia": 12
    }
  },
  {
    "id": 4,
    "titulo": "Pagar aluguel",
    "descricao": "Aluguel do apartamento",
    "status": "PENDENTE",
    "tipoRecorrencia": "MENSAL",
    "ocorrencia": 2,
    "dataExecucao": "2026-06-01",
    "criadoEm": "2026-04-25T14:40:00",
    "atualizadoEm": "2026-04-25T14:40:00",
    "taskGroup": {
      "id": 1,
      "frequencia": "MENSAL",
      "totalRecorrencia": 12
    }
  }
]
```

**Validações:**
- `tipoRecorrencia` — Deve ser MENSAL
- `totalRecorrencia` — Máximo 36 tarefas
- `totalRecorrencia` > 0

**Erros:**
- `400` — Total de recorrência não pode exceder 36 tarefas.
- `400` — Dados de recorrência inválidos.

---

### PUT `/api/tasks/{id}` — Atualizar tarefa

**Headers:**
```
Authorization: Bearer <token>
Content-Type: application/json
```

**Body:**
```json
{
  "titulo": "Estudar Spring Security (atualizado)",
  "descricao": "Implementar JWT e OAuth2",
  "status": "EM_ANDAMENTO",
  "dataExecucao": "2026-05-20"
}
```

**Resposta (200):**
```json
{
  "id": 2,
  "titulo": "Estudar Spring Security (atualizado)",
  "descricao": "Implementar JWT e OAuth2",
  "status": "EM_ANDAMENTO",
  "tipoRecorrencia": "UNICA",
  "ocorrencia": 1,
  "dataExecucao": "2026-05-20",
  "criadoEm": "2026-04-25T14:35:00",
  "atualizadoEm": "2026-04-25T14:50:00",
  "taskGroup": null
}
```

**Erros:**
- `404` — Task com ID {id} não encontrada.

---

### DELETE `/api/tasks/{id}` — Deletar tarefa

**Headers:**
```
Authorization: Bearer <token>
```

**Resposta (204):** — No Content

**Erros:**
- `404` — Task com ID {id} não encontrada.

---

### GET `/api/tasks/test-scheduled` — Testar job agendado

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
| **Log4j2** | 2.x | Logging estruturado |

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

```json
{
  "timestamp": "2026-05-10T15:30:45.123456",
  "status": 400,
  "error": "Bad Request",
  "message": "Total de recorrência não pode exceder 36 tarefas.",
  "path": "/api/tasks/recurrent"
}
```

### Exceptions Customizadas

| Exception | Status | Causa |
|-----------|--------|-------|
| `TaskNotFoundException` | 404 | Task com ID não encontrada |
| `TaskRecurrenceException` | 400 | Validação de recorrência falhou |
| `UserNameNotFoundException` | 404 | Usuário não encontrado |
| `MethodArgumentNotValidException` | 400 | Validação de @Valid falhou |

---

## ⚡ Performance

### Otimizações Implementadas

| Otimização | Benefício |
|-----------|-----------|
| `reWriteBatchedInserts=true` | Agrupa INSERT em uma transação — 10-50x mais rápido |
| `jdbc.batch_size=50` | Processa 50 registros por lote |
| `order_inserts=true` | Otimiza buffer do Hibernate |
| Paginação (padrão 100) | Previne overflow de memória |
| Named parameters | SQL injection prevention |
| LazyFetch em JoinColumn | Evita n+1 queries |
| `open-in-view=false` | Desacopla Hibernate da view |
| `show-sql=false` | Reduz overhead de logging em produção |

### Benchmark (12 tarefas recorrentes)

```
Sem otimizações:  ~450ms
Com otimizações:   ~45ms (10x mais rápido)
```

---

## 🔀 Git Workflow

```bash
# Criar branch
git checkout -b feature/sua-feature

# Trabalhar
git add .
git commit -m "feat: descrição da feature"

# Push
git push origin feature/sua-feature

# Pull request no GitHub
# Merge quando aprovado
git branch -d feature/sua-feature
```

### Padrão de Commits

```
feat:  adiciona novo endpoint
fix:   corrige bug na validação
docs:  atualiza README
refactor: reorganiza código
test:  adiciona testes unitários
perf:  melhora performance de queries
chore: atualiza dependências
```

---

## 🧪 Testes

### Executar testes

```bash
mvn test
```

### Exemplo com Insomnia/Postman

1. **Registrar usuário:**
   ```
   POST http://localhost:8080/api/auth/register
   Content-Type: application/json
   
   {
     "nome": "André",
     "email": "andre@test.com",
     "password": "senha123"
   }
   ```

2. **Login:**
   ```
   POST http://localhost:8080/api/auth/login
   Content-Type: application/json
   
   {
     "email": "andre@test.com",
     "password": "senha123"
   }
   ```

3. **Copiar token da resposta**

4. **Criar tarefa:**
   ```
   POST http://localhost:8080/api/tasks
   Authorization: Bearer <seu_token>
   Content-Type: application/json
   
   {
     "titulo": "Estudar",
     "descricao": "Java Spring Boot",
     "dataExecucao": "2026-05-15",
     "tipoRecorrencia": "UNICA"
   }
   ```

---

## 📦 Build e Deploy

### Build JAR

```bash
mvn clean package
```

Gera: `target/taskhub-0.0.1-SNAPSHOT.jar`

### Executar JAR

```bash
java -jar target/taskhub-0.0.1-SNAPSHOT.jar
```

### Docker (opcional)

```dockerfile
FROM eclipse-temurin:21-jdk-alpine
COPY target/taskhub-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

---

## 🐛 Troubleshooting

### Erro: "User not found"

```sql
SELECT * FROM users WHERE email = 'seu@email.com';
```

Verifique se o usuário existe no banco antes de fazer login.

### Erro: "Task com ID 999 não encontrada"

Cheque se a tarefa foi criada:

```sql
SELECT * FROM tasks WHERE id = 999;
```

### Erro: "Total de recorrência não pode exceder 36"

Limite máximo de tarefas recorrentes é 36. Use um valor menor:

```json
{
  "totalRecorrencia": 24
}
```

### Erro de JWT expirado

Token expira em 24h. Faça login novamente para gerar um novo token.

### Erro: "Batch insert não funcionando"

Verifique se `application.properties` tem:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/tasks?reWriteBatchedInserts=true
spring.jpa.properties.hibernate.order_inserts=true
spring.jpa.properties.hibernate.jdbc.batch_size=50
```

---

## 📚 Referências

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Security Documentation](https://spring.io/projects/spring-security)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [Hibernate Documentation](https://hibernate.org/)
- [MapStruct Guide](https://mapstruct.org/)
- [JWT.io](https://jwt.io/)
- [Jakarta Validation](https://jakarta.ee/specifications/bean-validation/)
- [PostgreSQL JDBC Driver](https://jdbc.postgresql.org/)

---

## 👤 Autor

**André Luiz Santana**

- **GitHub:** [@andreluizsantana](https://github.com/andreluizsantana)
- **LinkedIn:** [andrelssr](https://www.linkedin.com/in/andrelssr/)
- **Sobre mim:** [andreluiz.vercel.app](https://andreluiz.vercel.app)

---

## 📄 Licença

Este projeto está sob a licença **MIT**.

---

## 📝 Changelog

### v1.1.0 (Atual)
- ✅ Estrutura real do projeto documentada
- ✅ Endpoints validados com código-fonte
- ✅ JobScheduler implementado e testado
- ✅ Otimizações de batch inserts documentadas
- ✅ Guia de segurança JWT atualizado
- ✅ Troubleshooting expandido

### v1.0.0 (Inicial)
- ✅ CRUD de tarefas
- ✅ Autenticação JWT
- ✅ Tarefas recorrentes

---

**Última atualização:** 10 de maio de 2026

**Versão da API:** 1.1.0
