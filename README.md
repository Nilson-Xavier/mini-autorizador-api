# Mini Autorizador API

## Visão Geral
API RESTful para processamento de transações financeiras com cartões, implementando controle de concorrência e validações de negócio.

## 🚀 Tecnologias Utilizadas
- **Java 17**
- **Spring Boot 3.x**
- **Spring Data JPA**
- **Maven**
- **JUnit 5** e **MockMvc** para testes

## 🔧 Padrões de Projeto

### 1. Padrão Repository
- Implementado através das interfaces `CartaoRepository` e `TransacaoRepository`
- Isola a camada de acesso a dados, fornecendo uma API limpa para operações de banco de dados

### 2. Service Layer
- Separação clara entre controladores e lógica de negócio
- `CartaoService` e [TransacaoService](cci:2://file:///D:/Users/nilson/my_Projects/java/intelli_J/miniautorizador-api/src/main/java/br/com/vr/miniautorizador/service/TransacaoService.java:11:0-28:1) encapsulam as regras de negócio

### 3. DTOs (Data Transfer Objects)
- Uso de DTOs para transferência de dados entre camadas
- Exemplo: `CartaoRequest`, `TransacaoRequest`

### 4. Tratamento de Exceções
- `@ControllerAdvice` para tratamento centralizado de exceções
- Classes de exceção específicas como `CartaoInexistenteException`
- Mensagens de erro padronizadas

## 🛡️ Controle de Concorrência

### Isolamento de Transações
- Uso de `@Transactional(isolation = Isolation.SERIALIZABLE)` para garantir consistência em operações concorrentes
- Implementação de bloqueio otimista/pessimista conforme necessário

### Sincronização
- Uso de `synchronized` em métodos críticos
- Testes de concorrência com `CountDownLatch` e `ExecutorService`

## 🚀 Decisões de Projeto e Arquitetura

Para atender aos desafios propostos e garantir um código limpo e escalável, utilizei as seguintes estratégias:

### 1. Programação Sem "Ifs" (Strategy & Chain of Responsibility)

A lógica de autorização foi construída utilizando polimorfismo em vez de estruturas condicionais (`if/else`):

* **Interface `ValidadorRegra**`: Define o contrato para validações.
* **Implementações Específicas**: Cada regra (Saldo, Senha, Existência) é uma classe isolada.
* **Fluxo Funcional**: O serviço de transação percorre a lista de regras via `Stream API`, lançando exceções caso encontre a primeira falha de validação.

### 2. Controle de Concorrência (Lock Pessimista)

Para garantir a consistência do saldo em transações simultâneas no mesmo cartão, foi implementado o **Pessimistic Locking** (`SELECT FOR UPDATE`) no nível do banco de dados MySQL:

* Isso evita o problema de "Lost Update", garantindo que apenas uma transação altere o saldo por vez.

## ✅ Testes

### Testes Unitários
- Cobertura de testes para serviços e controladores
- Uso de mock objects com Mockito

### Testes de Integração
- Testes end-to-end com `@SpringBootTest`
- Testes de concorrência para validar o comportamento em cenários simultâneos

## 🚀 Como Executar

### Pré-requisitos
- Java 17+
- Maven 3.6+

### Executando a Aplicação
```bash
mvn spring-boot:run

Executando os Testes
bash
mvn test

📚 Documentação da API
Criar Cartão
http
POST /cartoes
Content-Type: application/json

{
  "numeroCartao": "1234567890123456",
  "senha": "1234"
}
Consultar Saldo
http
GET /cartoes/{numeroCartao}
Realizar Transação
http
POST /transacoes
Content-Type: application/json

{
  "numeroCartao": "1234567890123456",
  "senhaCartao": "1234",
  "valor": 100.00
}

📝 Licença

Desenvolvido por [Nilson de Oliveira Xavier] - Jan/2026


### Como adicionar ao seu projeto:

1. No IntelliJ:
   - Clique com o botão direito no diretório raiz do projeto
   - Selecione "New" > "File"
   - Digite `README.md` e pressione Enter
   - Cole o conteúdo acima
   - Ajuste as seções conforme necessário

2. Personalize:
   - Substitua `[Seu Nome]` pelo seu nome
   - Atualize a seção de tecnologias se necessário
   - Adicione informações específicas do seu projeto

3. Visualize:
   - O IntelliJ tem uma visualização integrada de Markdown
   - Clique no ícone de olho no canto superior direito do editor para pré-visualizar
   
