# TaskHub API

Uma REST API robusta para gerenciamento de tarefas com suporte a tarefas recorrentes, agendamento automático e autenticação JWT.

**Stack:** Java 17 • Spring Boot 3.4.4 • PostgreSQL • JPA/Hibernate • MapStruct • Spring Security • JWT

## 🚀 Funcionalidades

### Tarefas
- ✅ CRUD completo de tarefas
- ✅ Tarefas simples e recorrentes (mensais)
- ✅ Atualização automática de status (tarefas vencidas → NÃO_EXECUTADA)
- ✅ Paginação de resultados
- ✅ Validações com Bean Validation

### Segurança
- ✅ Autenticação com JWT
- ✅ Registro de novos usuários (com BCrypt)
- ✅ Login com geração de token
- ✅ Proteção de endpoints autenticados

### Arquitetura
- ✅ Padrão de camadas (Controller → Service → Repository)
- ✅ DTOs com MapStruct
- ✅ Exception handling global com `@RestControllerAdvice`
- ✅ Transações gerenciadas
- ✅ Logging com Log4j2

## 📋 Pré-requisitos

- **Java 17+** (testado com JDK 21)
- **PostgreSQL 12+**
- **Maven 3.8+**
- **Git**

## ⚙️ Instalação e Configuração

### 1. Clone o repositório

```bash
git clone https://github.com/andreluizsantana/task-manager-api.git
cd task-manager-api
```

### 2. Configure o banco de dados

Crie um banco PostgreSQL:

```sql
CREATE DATABASE tasks;
```

### 3. Configure `application.properties`

Edite `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/tasks?reWriteBatchedInserts=true
spring.datasource.username=postgres
spring.datasource.password=root
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.open-in-view=false

spring.data.web.pageable.default-page-size=100

spring.jpa.properties.hibernate.order_inserts=true
spring.jpa.properties.hibernate.jdbc.batch_size=50
```

### 4. Instale as dependências

```bash
mvn clean install
```

### 5. Execute a aplicação

```bash
mvn spring-boot:run
```

A API estará disponível em: `http://localhost:8080`

## 🔐 Autenticação

### Registrar novo usuário

**POST** `/api/auth/register`

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

```json
{
  "email": "andre@example.com",
  "password": "senha123"
}
```

**Resposta (200 OK):**

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhbmRyZUBleGFtcGxlLmNvbSIsInVzZXJJZCI6MSwiaWF0IjoxNjY2Njc2ODAwLCJleHAiOjE2NjY3NjMyMDB9.signature"
}
```

Use o token em todas as requisições autenticadas:

```
Authorization: Bearer <seu_token_aqui>
```

## 📚 Endpoints da API

### Tarefas

#### Listar tarefas (paginado)

**GET** `/api/tasks`

**Query params:**
- `page=0` (padrão)
- `size=100` (padrão, máx 100)

**Headers:**
```
Authorization: Bearer <token>
```

**Resposta (200 OK):**

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
      "dataExecucao": "2026-04-25",
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

#### Buscar tarefa por ID

**GET** `/api/tasks/{id}`

**Headers:**
```
Authorization: Bearer <token>
```

**Resposta (200 OK):**

```json
{
  "id": 1,
  "titulo": "Comprar pão",
  "descricao": "Pão integral no mercado",
  "status": "PENDENTE",
  "tipoRecorrencia": "UNICA",
  "ocorrencia": 1,
  "dataExecucao": "2026-04-25",
  "criadoEm": "2026-04-25T14:30:00",
  "atualizadoEm": "2026-04-25T14:30:00",
  "taskGroup": null
}
```

#### Criar tarefa simples

**POST** `/api/tasks`

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
  "dataExecucao": "2026-05-01"
}
```

**Resposta (201 Created):**

```json
{
  "id": 2,
  "titulo": "Estudar Spring Security",
  "descricao": "Implementar JWT authentication",
  "status": "PENDENTE",
  "tipoRecorrencia": null,
  "ocorrencia": 1,
  "dataExecucao": "2026-05-01",
  "criadoEm": "2026-04-25T14:35:00",
  "atualizadoEm": "2026-04-25T14:35:00",
  "taskGroup": null
}
```

#### Criar tarefas recorrentes (mensais)

**POST** `/api/tasks/recurrent`

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

**Resposta (201 Created):**

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
    "taskGroup": {"id": 1, "frequencia": "MENSAL", "totalRecorrencia": 12}
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
    "taskGroup": {"id": 1, "frequencia": "MENSAL", "totalRecorrencia": 12}
  }
]
```

#### Atualizar tarefa

**PUT** `/api/tasks/{id}`

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
  "dataExecucao": "2026-05-05"
}
```

**Resposta (200 OK):**

```json
{
  "id": 2,
  "titulo": "Estudar Spring Security (atualizado)",
  "descricao": "Implementar JWT e OAuth2",
  "status": "EM_ANDAMENTO",
  "tipoRecorrencia": null,
  "ocorrencia": 1,
  "dataExecucao": "2026-05-05",
  "criadoEm": "2026-04-25T14:35:00",
  "atualizadoEm": "2026-04-25T14:50:00",
  "taskGroup": null
}
```

#### Deletar tarefa

**DELETE** `/api/tasks/{id}`

**Headers:**
```
Authorization: Bearer <token>
```

**Resposta (204 No Content)**

## 📊 Status das Tarefas

| Status | Descrição |
|--------|-----------|
| `PENDENTE` | Tarefa não iniciada |
| `EM_ANDAMENTO` | Tarefa em execução |
| `CONCLUIDO` | Tarefa concluída |
| `CANCELADO` | Tarefa cancelada |
| `NAO_EXECUTADA` | Tarefa vencida (atualizada automaticamente) |

## 📅 Agendamentos

### Job automático: Atualizar tarefas vencidas

**Cron:** `1 0 0 * * *` (01:00 AM, todos os dias - Horário de São Paulo)

Busca todas as tarefas com:
- Status: `PENDENTE`
- Data de execução: anterior a hoje

E atualiza para `NAO_EXECUTADA`.

**Logs:**
```
INFO: Inicio da tarefa... 2026-04-26T00:00:01
INFO: Atualizadas 5 tarefas vencidas
INFO: Fim da tarefa... 2026-04-26T00:00:02
```

## 🗂️ Estrutura do Projeto

```
src/main/java/com/project/taskhub/
├── controller/
│   ├── AuthenticatorController.java
│   └── TaskController.java
├── service/
│   └── TaskService.java
├── repository/
│   ├── TaskRepository.java
│   ├── TaskGroupRepository.java
│   └── UserRepository.java
├── entity/
│   ├── Task.java
│   ├── TaskBase.java (superclass)
│   ├── TaskGroup.java
│   ├── User.java
│   └── enums/
│       ├── StatusTask.java
│       └── TipoRecorrencia.java
├── dto/
│   ├── TaskRequestDTO.java
│   ├── TaskResponseDTO.java
│   ├── TaskUpdateDTO.java
│   ├── TaskMapper.java
│   ├── TaskGroupMapper.java
│   └── securitydto/
│       ├── LoginRequestDTO.java
│       ├── LoginResponseDTO.java
│       ├── RegisterUserRequestDTO.java
│       ├── RegisterUserResponseDTO.java
│       └── UserMapper.java
├── security/
│   ├── SecurityConfig.java
│   ├── TokenConfiguration.java
│   └── AuthConfig.java
├── exceptions/
│   ├── TaskNotFoundException.java
│   ├── TaskRecurrenceException.java
│   └── GlobalExceptionHandler.java
└── TaskhubApplication.java
```

## 🛠️ Tecnologias e Dependências

| Dependência | Versão | Propósito |
|-------------|--------|----------|
| Spring Boot | 3.4.4 | Framework web |
| Spring Security | 6.x | Autenticação e autorização |
| Spring Data JPA | 3.x | ORM e queries |
| Hibernate | 6.6.11 | Persistência |
| PostgreSQL Driver | 42.x | Banco de dados |
| MapStruct | 1.6.3 | Mapeamento de objetos |
| Lombok | 1.18.x | Redução de boilerplate |
| Java-JWT (Auth0) | 4.4.0 | Geração de tokens JWT |
| Bean Validation | 3.x | Validações |
| Log4j2 | 2.x | Logging |

## 🔒 Segurança

### Password Encoding

Senhas são encoded com **BCrypt** (`PasswordEncoder` bean):

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

### JWT Configuration

**Secret:** `tasktask` (⚠️ Mude em produção!)

**Claims do token:**
- `userId` — ID do usuário
- `sub` (subject) — Email do usuário
- `iat` (issued at) — Hora de emissão
- `exp` (expiration) — Expira em 24h

**Algoritmo:** HMAC256

## 🚨 Tratamento de Erros

### Exceptions Customizadas

```json
{
  "timestamp": "2026-04-25T14:30:00.000+00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Task com ID 999 não encontrada.",
  "path": "/api/tasks/999"
}
```

```json
{
  "timestamp": "2026-04-25T14:30:00.000+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Total de recorrência não pode exceder 36 tarefas.",
  "path": "/api/tasks/recurrent"
}
```

## 📈 Performance

### Otimizações implementadas

- ✅ Paginação padrão de 100 registros
- ✅ `reWriteBatchedInserts=true` (PostgreSQL batch inserts)
- ✅ `order_inserts=true` (Hibernate batch optimization)
- ✅ `jdbc.batch_size=50`
- ✅ Queries otimizadas com named parameters

## 🔄 Git Workflow

```bash
# Feature branch
git checkout -b feature/seu-feature

# Commit
git commit -m "type: descrição"

# Push
git push origin feature/seu-feature

# Pull request no GitHub
# Merge quando aprovado
# Delete branch
```

## 📝 Commits

Siga o padrão:

```
feat: adiciona novo endpoint
fix: corrige bug na validação
docs: atualiza README
refactor: reorganiza código
test: adiciona testes unitários
```

## 🧪 Testes (em desenvolvimento)

```bash
mvn test
```

## 📦 Build e Deploy

### Build JAR

```bash
mvn clean package
```

### Executar JAR

```bash
java -jar target/taskhub-0.0.1-SNAPSHOT.jar
```

## 🐛 Troubleshooting

### Erro: "No qualifying bean of type 'AuthenticationManager'"

**Solução:** Verifique se `AuthenticationManager` está definido em `SecurityConfig`:

```java
@Bean
public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
    return config.getAuthenticationManager();
}
```

### Erro: "User not found"

**Solução:** Verifique se o usuário existe no banco com:

```sql
SELECT * FROM users WHERE email = 'seu@email.com';
```

### Erro de validação no campo `inativo`

**Solução:** Remove `@NotBlank` do campo boolean. Use apenas `@NotBlank` em Strings.

## 📚 Referências

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Security Documentation](https://spring.io/projects/spring-security)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [MapStruct](https://mapstruct.org/)
- [JWT Introduction](https://jwt.io/introduction)

## 👤 Autor

**André Luiz Santana**

- GitHub: [@andreluizsantana](https://github.com/andreluizsantana)
- LinkedIn: [andrelssr](https://www.linkedin.com/in/andrelssr/)

## 📄 Licença

Este projeto está sob a licença MIT.

---

**Última atualização:** 25 de abril de 2026

**Versão da API:** 1.0.0
