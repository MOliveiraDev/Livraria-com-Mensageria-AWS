# Livraria-com-Mensageria-AWS
### Este é um projeto que irá demonstrar como é implementado a mensageria usando SQS e SNS 

<hr>

## Arquitetura do Projeto

<img src="https://github.com/MOliveiraDev/Livraria-com-Mensageria-AWS/blob/main/assets/Livraria%20-%20AWS.jpg>"</img>

<hr>

## Tecnologias Utilizadas

- Java 21
- Spring Boot
- Spring Data JPA
- Java Mail Sender
- AWS: SQS / SNS
- Jackson (para JSON)
- Banco de dados relacional: MySQL
- Banco de dados Não Relacional: MongoDB

## Principais componentes
- NotificationService / EmailService — responsável por criar e enviar eventos/ e‑mails.
- snsService — wrapper para publicação em AWS SNS (usa topic ARN).
- RentalRepository — repositório JPA para registros de aluguel.
- Entidades JPA para Rental/Book (persistência).
- Configuração via application.yml / variáveis de ambiente.

## Requisitos
- JDK 21+
- Maven
- Conta AWS com permissões SNS (Publish)
- Banco de dados configurado (dados de conexão via properties/env)

### Configuração (exemplo)
    - application.yml ou variáveis de ambiente necessárias:
    - AWS_ACCESS_KEY_ID
    - AWS_SECRET_ACCESS_KEY
    - AWS_REGION
    - BOOK_RETURNED_TOPIC_ARN (ARN do tópico SNS usado para eventos de devolução)
    - spring.datasource.url
    - spring.datasource.username
    - spring.datasource.password

