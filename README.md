# Sistema de Controle de Produtos de um Supermercado

Trabalho em grupo da disciplina de Programação Orientada a Objetos (POO) – Sistemas de Informação, IFMA Campus Monte Castelo.

O sistema permite cadastrar produtos, consultar o estoque e registrar vendas, com baixa automática no estoque e registro de cada movimentação.

## Equipe

| Integrante | Responsabilidade |
|---|---|
| Bruno Costa Amorim | Modelagem e banco de dados |
| João Pedro Sousa dos Santos | Cadastro |
| Ana Cecília da Silva Ferreira | Operações |
| Jorge Felipe Oliveira Andrade | Frontend |
| Vitor Hugo Martins de Moraes | Git e testes |

## Tecnologias

Java, Spring Boot, Maven, banco H2 e frontend em HTML/JavaScript.

## Como executar

**Pré-requisitos:** Git e JDK (versão definida em `java.version` no arquivo `pom.xml`).

1. Clone o repositório e entre na pasta:
```bash
git clone https://github.com/vit0rkk/Controle-Produtos-Supermercado.git
cd Controle-Produtos-Supermercado
```

2. Execute a aplicação:
```bash
./mvnw spring-boot:run
```
No Windows (CMD ou PowerShell): `mvnw.cmd spring-boot:run`

3. Abra no navegador: **http://localhost:8080**

Para encerrar, pressione `Ctrl + C` no terminal.

## Como executar os testes

```bash
./mvnw test
```

## Banco de dados

Para ver as tabelas, acesse **http://localhost:8080/h2-console** com:

- JDBC URL: `jdbc:h2:file:./dados_supermercado`
- Usuário: `sa`
- Senha: (vazia)
