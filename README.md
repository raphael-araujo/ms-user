# Challenge III - PB Springboot Dez 2023

-------------------------------------------------------------------------------------------------------

O projeto consiste em um microsserviço desenvolvido em Spring Boot para gerenciamento de usuários.

-------------------------------------------------------------------------------------------------------

### Tecnologias Utilizadas

<table>
  <tr>
    <td>Java</td>
    <td>Spring</td>
    <td>MySql</td>
    <td>RabbitMQ</td>
  </tr>
  <tr>
    <td>17.*</td>
    <td>3.2</td>
    <td>8.0</td>
    <td>3.12</td>
  </tr>
</table>

-------------------------------------------------------------------------------------------------------

### Setup

<br>
1 - Clone o repositório

```
git clone https://github.com/raphael-araujo/ms-user.git
```

<br>

2 - Crie e Configure o banco de dados de acordo com o arquivo `application.yml`

<br>
3 - Suba o container RabbitMQ no docker

````
docker run -it --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3.12-management
````

4 - Execute a aplicação

```
mvn spring-boot:run
```

A aplicação deverá estar em execução e acessível em http://localhost:8080/
<br>

##### OBS:

- Para executar os testes automatizados, é necessário que o [msaddress](https://github.com/raphael-araujo/ms-address) e
  o [msnotification](https://github.com/raphael-araujo/ms-notification) estejam em execução, junto com o rabbitMQ via
  docker.

-------------------------------------------------------------------------------------------------------

### Documentação

- Collections do Postman da raiz do projeto

- Swagger: http://localhost:8080/swagger-ui/index.html



