Finanças em Angular e Spring Boot
Uma solução Full Stack completa para controle financeiro pessoal. A aplicação permite que o usuário gerencie seu ciclo financeiro total, desde a organização por categorias até a análise de dados através de dashboards detalhados de receitas e despesas.

Tecnologias
Backend
Java 21 (LTS) e Spring Boot 4.0.5

Spring Security & JWT: Autenticação stateless com extração de identidade via SecurityContextHolder.

Spring Data JPA: Persistência de dados e consultas customizadas.

PostgreSQL: Banco de dados relacional de alta confiabilidade.

Maven: Gerenciamento de dependências e build.

Frontend
Angular: Framework para uma interface de usuário dinâmica e reativa.

CSS Puro: Estilização focada em performance, mantendo a interface leve e funcional.

Arquitetura do Sistema
O backend foi construído seguindo os princípios da Clean Architecture, garantindo que as regras de negócio sejam independentes e seguras:

Controller: Endpoints especializados (/api/categorias, /api/transacoes, /api/financeiro) que gerenciam a comunicação REST e filtros de busca.

Service: Centraliza a lógica de negócio, realizando a validação de posse (garantindo que um usuário não acesse dados de outro).

DTO (Data Transfer Objects): Segurança e desacoplamento total entre o banco de dados e o que é enviado para o Angular.

Model/Entity: Mapeamento objeto-relacional com JPA para representação de Usuários, Categorias e Transações.

Segurança e Regras de Negócio
Multi-tenancy Lógico: Toda transação é vinculada ao ID do usuário autenticado via JWT, garantindo isolamento total dos dados.

Validação de Payload: Uso de @Valid e DTOs para garantir a integridade dos dados recebidos.

Segurança de Métodos: Validação manual de propriedade em operações de UPDATE e DELETE.

Funcionalidades Implementadas
[x] Gestão de Usuários: Cadastro seguro com criptografia de senhas e autenticação via Token.

[x] Controle de Categorias: Organização personalizada de despesas e receitas por usuário.

[x] Gestão de Transações: CRUD completo com filtros por período (Mês/Ano).

[x] Dashboard Financeiro: - Histórico anual de movimentações.

Resumo de saldos (Entradas vs. Saídas).

Agrupamento de gastos por categoria.

Monitoramento de fluxo de caixa diário.

Como Executar o Projeto
Clonar Repositório:

Bash
git clone https://github.com/andregattdev/financas-em-angular-e-spring-boot.git
Backend:

Configure o PostgreSQL no application.properties.

Execute: ./mvnw spring-boot:run.

Frontend:

Instale as dependências: npm install.

Inicie o servidor: ng serve.

Próximos Passos (Roadmap)
[ ] Implementação de paginação nativa no Spring Data.

[ ] Geração de relatórios em PDF/Excel.

[ ] Notificações de contas a vencer.
