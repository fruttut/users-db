# Installation
## Using docker-compose
The easiest (and preferred) way to run this application is via docker-compose:
* Create a file named `.env` at the root directory of the project.
  The `.env.template` file contains all supported configuration options,
  so you can just copy-paste it and adjust the parameters as you see fit
* Run `docker-compose up --build`
## Using docker without docker-compose
If you don't feel like installing `docker-compose`, you can manually run
all needed containers. There is a `Dockerfile` for each microservice under
their respective root directories, and you'll also need to run
RabbitMQ and PostgreSQL either by installing them locally or running
their docker containers. Don't forget to provide environment variables
to your containers.
## Without docker
* `cd registry && mvn clean package` to build the registry microservice
* `cd db && mvn clean package` to build the db microservice
* Run RabbitMQ and PostgreSQL
* `java -jar registry/target/registry-1.0-SNAPSHOT.jar`
* `java -jar db/target/db-1.0-SNAPSHOT.jar`

The REST API should be available at port 8080.

# Usage
The REST API provides two endpoints:
* `GET /user/{id}` returns the user with the specified ID,
or 404 if there is no such user
* `POST /user` creates a new user with the data in the request body and
returns the newly created user, or 403 if the data provided is invalid

A user entity has the following format:
```
{
  id: number,
  login: string,
  password: string,
  name: string,
  surname: string,
  email: string,
  creationDate: string
}
```

User creation request body should be of the following format:
```
{
  login: string,
  password: string,
  name: string,
  surname: string,
  email: string
}
```

The `id` and `creationDate` fields will be populated by the database.

# Implementation Notes
* Every REST endpoint from the `registry` microservice communicates with
RabbitMQ via its own queue. This was the easiest way I could find that
  allows automatic JSON to DTO conversions.
* `registry` and `db` microservices communicate by means of RabbitMQ's
direct reply-to feature. I think it's the optimal way to go considering
the business requirement for the `registry` service to work synchronously.
* Errors from the `db` service are returned to the same queue as
normal responses, dead-letter-queues are not used.
* There is a good amount of code duplication between both microservices
  (`User`, `UserReply`, `UserError`, `CreateUserRequest`, etc).
  This is intentional and not at all in conflict with the DRY principle.
  Every microservice should be developed as an independent module,
  and it should only rely on other microservices' contracts when
  communicating with them. Sharing DTO classes between the modules
  might seem like a good idea, but it tightly couples them together, thus
  defeating the whole purpose of the microservices approach.
