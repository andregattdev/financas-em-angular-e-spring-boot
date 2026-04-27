💰 Finanças em Angular e Spring Boot
Uma solução Full Stack completa para controle financeiro pessoal. Esta API REST robusta e sua interface intuitiva permitem o gerenciamento eficiente de receitas e despesas com foco em segurança e performance.

🚀 Tecnologias
Backend
Java 21 (LTS) e Spring Boot 4.0.5

Spring Security & JWT: Autenticação e autorização stateless.

Spring Data JPA: Abstração e persistência de dados.

PostgreSQL: Banco de dados relacional de alta confiabilidade.

Maven: Gerenciamento de dependências.

Frontend (Em desenvolvimento)
Angular: Framework para uma interface dinâmica e responsiva.

CSS Puro: Estilização focada em performance e agilidade de carregamento.

🏗️ Arquitetura do Sistema
O backend foi construído seguindo os princípios da Clean Architecture, garantindo que as regras de negócio sejam independentes de frameworks externos:

Model: Entidades JPA que representam o domínio (Usuário, Receita, Despesa).

Repository: Camada de acesso aos dados via JpaRepository.

Service: Camada onde reside a lógica de negócio, como validações de posse e cálculos de recorrência.

DTO (Data Transfer Objects): Segurança e desacoplamento na trafegação de dados entre frontend e backend.

Controller: Endpoints REST que expõem os recursos da aplicação.

🔒 Segurança e Regras de Negócio
Proteção de Dados: Senhas criptografadas antes do armazenamento.

JWT: Fluxo de login moderno com tokens de curta duração.

Isolamento: Cada usuário possui acesso estritamente aos seus próprios registros, validado na camada de Service.

📋 Funcionalidades
[x] Cadastro de Usuários: Registro seguro com criptografia de senha.

[x] Gestão de Receitas: CRUD completo de entradas financeiras.

[x] Gestão de Despesas: Controle total de saídas e gastos.

[x] Autenticação Stateless: Login via Token JWT.

