# Sistema de Controle de Produtos de um Supermercado

Projeto desenvolvido em grupo para a disciplina de **Programação Orientada a Objetos (POO)**, do curso de Sistemas de Informação do IFMA – Campus Monte Castelo.

O sistema permite gerenciar produtos, consultar o estoque e registrar vendas, realizando a baixa automática das quantidades vendidas e mantendo o registro das movimentações.

## Funcionalidades

- Cadastro e consulta de produtos.
- Visualização e organização dos produtos em estoque.
- Pesquisa e filtragem de produtos na interface.
- Registro de vendas com atualização automática do estoque.
- Validação da quantidade disponível antes de concluir uma venda.
- Registro das movimentações de estoque.
- Testes automatizados para verificar as principais regras de negócio.

## Tecnologias utilizadas

- **Java 26:** linguagem de programação.
- **Spring Boot:** estrutura principal da aplicação.
- **Spring Data JPA:** persistência e acesso aos dados.
- **Maven:** gerenciamento de dependências e execução do projeto.
- **H2 Database:** banco de dados.
- **HTML, CSS e JavaScript:** desenvolvimento da interface.

## Estrutura do projeto

```text
src/
├── main/
│   ├── java/com/equipe/Supermercado/
│   │   ├── controller/  # Endpoints da aplicação
│   │   ├── dto/         # Objetos de transferência de dados
│   │   ├── model/       # Entidades do sistema
│   │   ├── repository/  # Acesso ao banco de dados
│   │   ├── service/     # Regras de negócio
│   │   └── SupermercadoApplication.java
│   └── resources/
│       ├── static/      # Interface web
│       └── application.properties
└── test/
    └── java/            # Testes automatizados
```

## Como executar

**Pré-requisitos:** Git e JDK 26, conforme configurado no `pom.xml`.

1. Clone o repositório:

   ```bash
   git clone https://github.com/vit0rkk/Controle-Produtos-Supermercado.git
   ```

2. Entre na pasta do projeto:

   ```bash
   cd Controle-Produtos-Supermercado
   ```

3. Inicie a aplicação:

   **Linux:**
   ```bash
   ./mvnw spring-boot:run
   ```

   **Windows (CMD ou PowerShell):**
   ```bash
   mvnw.cmd spring-boot:run
   ```

4. Acesse a aplicação no navegador:

   http://localhost:8080

Para encerrar a aplicação, pressione `Ctrl + C` no terminal.

## Testes automatizados

Para executar os testes do projeto, utilize:

```bash
./mvnw test
```

No Windows:

```bash
mvnw.cmd test
```

Os testes verificam comportamentos dos serviços, incluindo validações de produtos, quantidades inválidas e tentativas de venda sem estoque suficiente.

## Banco de dados

O projeto utiliza o H2 Database, configurado para armazenar os dados em arquivo local.

Para acessar o console do banco, com a aplicação em execução, abra:

http://localhost:8080/h2-console

Utilize as seguintes configurações:

- **JDBC URL:** `jdbc:h2:file:./dados_supermercado`
- **Usuário:** `sa`
- **Senha:** deixe o campo vazio.

## Equipe

| Integrante | Responsabilidade |
|---|---|
| Bruno Costa Amorim | Modelagem e banco de dados |
| João Pedro Sousa dos Santos | Cadastro |
| Ana Cecília da Silva Ferreira | Operações |
| Jorge Felipe Oliveira Andrade | Frontend |
| Vitor Hugo Martins de Moraes | Git e testes |

---

Projeto acadêmico desenvolvido para aplicar conceitos de programação orientada a objetos, organização em camadas, persistência de dados e testes automatizados.
