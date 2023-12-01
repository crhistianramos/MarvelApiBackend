# marvel-api-backend

## Description

Aplicacionrealizada en lenguaje java 17 usando el framework de spring boot 3.0.5, y gestor de dependencias maven.El cual permite consumir el api de http://developer.marvel.com/ para consultar por dos endpoints el listado general de personajes 
de marvel y de manera individual por id, se cuenta con almacenamieto de cada una de las consultas realizadas en jpa spring data, al igual que el sistema 
de autenticacion por tokens jwt para servicios privados realizado en spring security , sus pruebas son realizadas en junit y mockito

## Contents

1. [Installation](#installation)
2. [Usage](#usage)
3. [Features](#features)
4. [Contribution](#contribution)
5. [License](#license)
6. [Contact](#contact)

## Installation

mvn spring-boot:run


## Usage

Endpoint habilitados:

Autenticacion
Metodo Post
localhost:8081/public/authenticate

Cuerpo esperado
body:
{
"usuario" : "marvelAdmin",
"clave": "secreto"
}

Enpoint de consulta a la base de datos H2 
Necesita autenticacion por bearer token y no recibe cuerpo

Metodo Get:
localhost:8081/admin/logs

Endpoint de consulta de personaje en general
No necesita autenticacion , son publicos
Metodo: GET

localhost:8081/api/marvel/characters

Endpoint de consulta de personaje individual 
No necesita autenticacion , se debe pasarle el id del personaje
Metodo: GET

localhost:8081//{characterId}

## Features

Java 17 Open Jdk
Spring boot 3.0.5
Maven 
Spring JPA
Spring Secutity
Junit 
Mockito

## Contribution

Contributions to the project are welcome. Please follow these general guidelines:

Fork the repository.
Create a new branch for your feature/bugfix: git checkout -b feature-name.
Make your changes and commit them: git commit -m 'Description of changes'.
Push the changes to your branch: git push origin feature-name.
Open a pull request with a detailed description of your changes.

## Contact
For any inquiries or feedback, please contact [Crhistian Ramos Lopez] at [crhistianramos91@gmail.com].


## Licencia

Este proyecto está bajo la licencia MIT. Consulta el archivo [LICENSE](LICENSE) para obtener más detalles.