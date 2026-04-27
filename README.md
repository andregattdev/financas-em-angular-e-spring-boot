API Controle de gastos
Uma API REST robusta e segura para gerenciamento de receitas e despesas, construída com o ecossistema Spring e foco em alta performance e segurança.

Tecnologias Utilizadas
Java 21 (LTS)

Spring Boot 4.0.5 (Framework base)

Spring Security & JWT (Autenticação e Autorização Stateless)

Spring Data JPA (Persistência de dados)

PostgreSQL (Banco de dados de produção)

Maven (Gerenciamento de dependências)

Arquitetura do Projeto
O projeto segue os princípios da Clean Architecture e separação de responsabilidades em camadas:

Model: Entidades JPA que representam o usuario, a receita e a despesa

Repository: Interfaces que estendem JpaRepository para abstração do banco de dados.

Service: Camada de regras de negócio, onde reside a inteligência de validação de posse e lógica de recorrência.

DTO (Data Transfer Objects): Objetos de transferência para desacoplar a API do modelo de dados.

Controller: Endpoints REST que gerenciam a comunicação com o front-end.

Segurança
A API implementa um fluxo de segurança moderno:

As senhas dos usuários são criptografadas antes de serem armazenadas no banco.


Autenticação baseada em Tokens JWT (JSON Web Tokens).

📋 Funcionalidades Principais
[x] Cadastro de Usuários: Registro seguro.

[x] Gestão de Despesas e receitas: CRUD completo.
