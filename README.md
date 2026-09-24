# StudyFlow -Back-End

O **StudyFlow** é uma aplicação web em desenvolvimento para auxiliar estudantes na organização e gerenciamento de suas rotinas de estudos.

A proposta do projeto é oferecer uma plataforma simples e visual para organizar disciplinas, tarefas, trabalhos, provas e prazos, utilizando uma abordagem inspirada em ferramentas de gerenciamento de tarefas.


## 🎯 Objetivo


O objetivo do StudyFlow é proporcionar aos estudantes uma ferramenta que facilite a organização de suas atividades acadêmicas, permitindo acompanhar tarefas, prazos e o progresso das atividades de forma centralizada e intuitiva.


## 🛠️ Stack Tecnológica


### Back End

- SpringBoot - Version: 4.1.0

- Java - Version: 25.0.4

- PostgreSQL - Version: 18.6


### Ferramentas

- Git

- GitHub

- GitHub Projects
  
- Maven - Version: 3.9.16

- Docker - Containerização da aplicação


### Serviços

- Render — Deploy e hospedagem do Back-end
- Brevo — Envio de e-mails transacionais
  



## 🏗️ Arquitetura

O projeto utiliza uma arquitetura com **Front-end e Back-end separados**, que se comunicam por meio de uma **API REST**.

O Back-end foi estruturado seguindo uma **arquitetura em camadas**, com separação de responsabilidades entre os principais componentes da aplicação:

* **Controller** — responsável pelo recebimento das requisições HTTP e comunicação com o cliente.
* **Service** — concentra as regras de negócio e o processamento das funcionalidades.
* **Repository** — responsável pelo acesso e persistência dos dados no banco de dados.
* **Model** — representa as entidades e os dados persistidos pela aplicação.
* **Validation** — responsável pelas validações dos dados recebidos nas requisições.
* **Security** — concentra os mecanismos de autenticação, autorização e gerenciamento de tokens JWT.
* **Exception** — responsável pelo tratamento e padronização das exceções da aplicação.

Essa organização utiliza conceitos do padrão MVC (Model-View-Controller), adaptados para uma aplicação baseada em API REST, juntamente com o Repository Pattern, utilizado para separar o acesso ao banco de dados das demais partes da aplicação. No projeto, os Repositorys são responsáveis por realizar consultas e operações de persistência das entidades no PostgreSQL, mantendo essa responsabilidade separada dos controllers e services.




Este repositório é responsável exclusivamente pelo desenvolvimento do **Back-end**, enquanto o Front-end será mantido em um repositório separado.

[Front-End do Projeto](https://github.com/Higor-Castro/StudyFlow-FrontEnd)

## Desenvolvedores
 - Higor de Castro Venancio da Silva
 - João Vitor Rodrigues



