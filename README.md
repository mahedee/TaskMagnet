# Task Magnet - A Project Management and Issue Tracking Application

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white) ![Spring Boot](https://img.shields.io/badge/spring%20boot-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white) ![Spring Security](https://img.shields.io/badge/spring%20security-%236DB33F.svg?style=for-the-badge&logo=springsecurity&logoColor=white) ![MySQL](https://img.shields.io/badge/mysql-%2300f.svg?style=for-the-badge&logo=mysql&logoColor=white) ![JWT](https://img.shields.io/badge/JWT-black?style=for-the-badge&logo=JSON%20web%20tokens) ![Maven](https://img.shields.io/badge/Apache%20Maven-C71A36?style=for-the-badge&logo=Apache%20Maven&logoColor=white) ![React](https://img.shields.io/badge/react-%2320232a.svg?style=for-the-badge&logo=react&logoColor=%2361DAFB) ![Redux](https://img.shields.io/badge/redux-%23593d88.svg?style=for-the-badge&logo=redux&logoColor=white) ![Swagger](https://img.shields.io/badge/-Swagger-%23Clojure?style=for-the-badge&logo=swagger&logoColor=white)

Task Magnet is a full-stack project management and issue tracking application designed to help teams manage their tasks, projects, and issues efficiently. 

> 🔥 If you find this project useful, please **give it a star ⭐** Thanks!

---


## 🧰 Tech Stack
- Java Spring Boot
- Spring Data JPA
- Spring Security
- Spring Boot Actuator
- JWT (JSON Web Tokens)
- MySQL
- Maven
- JPA (Java Persistence API)
- Lombok
- React
- Redux
- Axios
- React Router

## 🚀 Features
🚧 **Under Development** 
> 🔄 Feature documentation coming soon
*⭐ Watch this repo for feature updates!*

## 🏗️ System Architecture
> 🔄 Documentation in progress
*👀 Detailed architecture diagrams and documentation coming soon! Watch this repo for updates!*


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
