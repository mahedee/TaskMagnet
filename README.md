# Task Magnet
## A Project Management Application
### Tools and Technology
* Java Spring boot
* Spring Data JPA
* Spring Security
* Spring Boot Actuator
* React.js
* Redux
* Redux-Thunk

### How to run the application
* Run MySQL using XAMPP
* Create a database like: taskmagnetdb
* Change connection string in src->main->resources->application.properties
* Go to the directory whre pom.xml file exists
* Change the MySQL database password to "root"
* Run mvn spring-boot:run
* Now see corresponding table is created already
* Now insert the following data in role table

```sql
INSERT INTO roles(name) VALUES('ROLE_USER');
INSERT INTO roles(name) VALUES('ROLE_MODERATOR');
INSERT INTO roles(name) VALUES('ROLE_ADMIN');
```
* Browse back end application using swagger using following URL
* Swagger: http://localhost:8080/swagger-ui/index.html
* Register a new user
* Sample user: admin, password: mahedee.net and role as admin
Sample registration json like below.
{
  "username": "admin",
  "email": "admin@gmail.com",
  "role": [
    "admin"
  ],
  "password": "mahedee.net"
}
