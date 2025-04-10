Para facilitar a execução do projeto, recomendo utilizar o Docker Compose com o comando docker compose up --build. Isso vai levantar automaticamente o Redis e o banco de dados, além de popular as tabelas com dados iniciais sem que você precise se preocupar com configurações manuais.

Também incluí uma coleção e um ambiente do Postman prontos para uso. Eles estão na raiz do projeto e podem ser importados diretamente no Postman. O ambiente foi configurado para capturar automaticamente o token gerado no login ou registro e aplicá-lo na autorização da maioria das requisições, tornando os testes corriqueiros mais fáceis.

Ou, se preferir, acessar o swagger via localhost:8080/swagger-ui/index.html.
# Desafio Backend - Requisitos

## 1. Validações

Você deve ajustar as entidades (model e sql) de acordo com as regras abaixo: 

- `Product.name` é obrigatório, não pode ser vazio e deve ter no máximo 100 caracteres.
- `Product.description` é opcional e pode ter no máximo 255 caracteres.
- `Product.price` é obrigatório deve ser > 0.
- `Product.status` é obrigatório.
- `Product.category` é obrigatório.
- `Category.name` deve ter no máximo 100 caracteres.
- `Category.description` é opcional e pode ter no máximo 255 caracteres.

## 2. Otimização de Performance
- Analisar consultas para identificar possíveis gargalos.
- Utilizar índices e restrições de unicidade quando necessário.
- Implementar paginação nos endpoints para garantir a escala conforme o volume de dados crescer.
- Utilizar cache com `Redis` para o endpoint `/auth/context`, garantindo que a invalidação seja feita em caso de alteração dos dados.

## 3. Logging
- Registrar logs em arquivos utilizando um formato estruturado (ex.: JSON).
- Implementar níveis de log: DEBUG, INFO, WARNING, ERROR, CRITICAL.
- Utilizar logging assíncrono.
- Definir estratégias de retenção e compressão dos logs.

## 4. Refatoração
- Atualizar a entidade `Product`:
  - Alterar o atributo `code` para o tipo inteiro.
- Versionamento da API:
  - Manter o endpoint atual (v1) em `/api/products` com os códigos iniciados por `PROD-`.
  - Criar uma nova versão (v2) em `/api/v2/products` onde `code` é inteiro.

## 5. Integração com Swagger
- Documentar todos os endpoints com:
  - Descrições detalhadas.
  - Exemplos de JSON para requisições e respostas.
  - Listagem de códigos HTTP e mensagens de erro.

## 6. Autenticação e Gerenciamento de Usuários
- Criar a tabela `users` com as colunas:
  - `id` (chave primária com incremento automático)
  - `name` (obrigatório)
  - `email` (obrigatório, único e com formato válido)
  - `password` (obrigatório)
  - `role` (obrigatório e com valores permitidos: `admin` ou `user`)
- Inserir um usuário admin inicial:
  - Email: `contato@simplesdental.com`
  - Password: `KMbT%5wT*R!46i@@YHqx`
- Endpoints:
  - `POST /auth/login` - Realiza login.
  - `POST /auth/register` - Registra novos usuários (se permitido).
  - `GET /auth/context` - Retorna `id`, `email` e `role` do usuário autenticado.
  - `PUT /users/password` - Atualiza a senha do usuário autenticado.

## 7. Permissões e Controle de Acesso
- Usuários com `role` admin podem criar, alterar, consultar e excluir produtos, categorias e outros usuários.
- Usuários com `role` user podem:
  - Consultar produtos e categorias.
  - Atualizar apenas sua própria senha.
  - Não acessar ou alterar dados de outros usuários.

## 8. Testes
- Desenvolver testes unitários para os módulos de autenticação, autorização e operações CRUD.

---

# Perguntas

1. **Se tivesse a oportunidade de criar o projeto do zero ou refatorar o projeto atual, qual arquitetura você utilizaria e por quê?**
   R. Eu adotaria uma arquitetura de microsserviços, pois ela permite escalar e implantar componentes de forma independente. Em paralelo, consideraria começar com um monólito modular bem estruturado por domínios de negócio, facilitando uma futura migração gradual para microsserviços conforme o sistema cresça.
2. **Qual é a melhor estratégia para garantir a escalabilidade do código mantendo o projeto organizado?**
   R. A melhor estratégia é modularizar o código por domínios de negócio (por exemplo, módulos separados para Produto, Usuário etc.), aplicando princípios SOLID e mantendo uma suíte de testes automatizados. Dessa forma, o projeto permanece organizado e escalável, evitando dependências excessivas entre módulos.
3. **Quais estratégias poderiam ser utilizadas para implementar multitenancy no projeto?**
   R. Para isolar dados de clientes diferentes, existem três abordagens principais. A primeira é usar um banco de dados separado para cada tenant, garantindo o máximo de isolamento. A segunda é usar um schema separado dentro do mesmo banco de dados para cada cliente. Por fim, uma opção mais simples seria usar uma coluna de "tenant_id" em tabelas compartilhadas, onde todos os tenants compartilham o mesmo banco e schema, mas com dados isolados por esse identificador.
4. **Como garantir a resiliência e alta disponibilidade da API durante picos de tráfego e falhas de componentes?**
   R. Para garantir resiliência e alta disponibilidade da API, uma estratégia fundamental seria o escalonamento horizontal, onde múltiplas instâncias da API são executadas simultaneamente e balanceadas por um load balancer. Usar padrões como Circuit Breaker e timeout para garantir que falhas em componentes externos não afetem o sistema como um todo é essencial. Além disso, um sistema de cache distribuído, como o Redis, alivia a carga no banco de dados durante picos de uso, e o monitoramento contínuo com alertas ajuda a detectar e corrigir falhas rapidamente.
5. **Quais práticas de segurança essenciais você implementaria para prevenir vulnerabilidades como injeção de SQL e XSS?**
   R. Para prevenir SQL Injection, eu usaria sempre consultas parametrizadas ou ORMs (evitando concatenar dados diretamente em queries) e faria validação rigorosa de entrada de dados (por exemplo, com Bean Validation). Já para mitigar XSS, adotaria a sanitização de entradas e saídas e configuraria cabeçalhos HTTP de segurança adequados (como Content-Security-Policy e X-XSS-Protection), garantindo que scripts maliciosos não sejam executados no cliente.

6. **Qual a abordagem mais eficaz para estruturar o tratamento de exceções de negócio, garantindo um fluxo contínuo desde sua ocorrência até o retorno da API?**
   R. O tratamento de exceções de negócios deve ser centralizado usando @ControllerAdvice, o que permite capturar as exceções e retornar respostas padronizadas e claras para o cliente da API. Lançar exceções específicas, como BusinessException, ajuda a identificar falhas de negócio e gerar mensagens apropriadas. A geração de logs detalhados também é importante para facilitar o diagnóstico de problemas.


7. **Considerando uma aplicação composta por múltiplos serviços, quais componentes você considera essenciais para assegurar sua robustez e eficiência?**
   R. Em uma arquitetura composta por múltiplos serviços, alguns componentes essenciais incluem um API Gateway, que atua como ponto único de entrada e pode gerenciar autenticação e roteamento de requisições. A descoberta de serviços é importante para permitir que os microsserviços localizem uns aos outros de maneira dinâmica. A comunicação assíncrona por meio de mensageria, usando ferramentas como RabbitMQ ou Kafka, pode reduzir o acoplamento entre os serviços e melhorar a performance. Também é necessário um sistema de monitoramento centralizado, como o ELK Stack, para coletar logs e métricas de todos os serviços de forma integrada.

8. **Como você estruturaria uma pipeline de CI/CD para automação de testes e deploy, assegurando entregas contínuas e confiáveis?**
   R. Eu estruturaria a pipeline de CI/CD com build e testes automatizados a cada commit, garantindo que todo novo código passe pelos testes unitários e de integração antes de ser integrado. Também integraria ao processo ferramentas de análise de qualidade de código (como SonarQube). Em seguida, automatizaria o deploy contínuo em staging e produção usando contêineres Docker, o que facilita a padronização e portabilidade dos ambientes. Por fim, implementaria um mecanismo de rollback automático para reverter rapidamente em caso de falhas, e monitoraria cada release com logs e alertas para obter feedback imediato da saúde da aplicação.


Obs: Forneça apenas respostas textuais; não é necessário implementar as perguntas acima.

