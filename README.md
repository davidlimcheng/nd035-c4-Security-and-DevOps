# Submission: Screenshots
## Jenkins

Created an EC2 instance, installed Docker and Jenkins following the instructions given in module. Added RSA credentials to both Github
and Jenkins, and successfully built the application using Jenkins.

![SuccessfulJenkinsBuild](./images/jenkins_successful_build.png)

## Splunk

Logs were generated using the `spring-boot-starter-log4j2` library and configured via `log4j2-spring.xml`.
Generated logs were manually uploaded to a local Splunk Enterprise instance, and fields were created, including "message".
Most of the SPL queries are achieved with the "message" custom field.

### CreateUser request successes

Panel

![CreateUser success graph](./images/create_user_success_graph.png)

Query

![CreateUser success query](./images/create_user_success_query.png)

SPL
```
source="spring-boot-logger-log4j2.log" | where like(message,"%"."Successfully created user"."%") | sort time | timechart span=1m count by rate
```

### CreateUser request failures

Panel

![CreateUser failure graph](./images/create_user_failure_graph.png)

Query

![CreateUser failure query](./images/create_user_failure_query.png)

SPL
```
source="spring-boot-logger-log4j2.log" | where like(message,"%"."Failed to create user"."%") | sort time | timechart span=1m count by rate
```

### Exceptions

Panel

![Exceptions](./images/exceptions_table.png)

Query

![Exceptions query](./images/exceptions_query.png)

SPL
```
source="spring-boot-logger-log4j2.log" | where like(message,"%"."Exception"."%") | sort time | chart count by message
```

### SubmitOrder request successes

Panel

![SubmitOrder success graph](./images/submit_order_successes_graph.png)

Query

![SubmitOrder success query](./images/submit_order_successes_query.png)

SPL
```
source="spring-boot-logger-log4j2.log" | where like(message,"%"."Successfully created user"."%") | sort time | timechart span=1m count by rate
```

### SubmitOrder request failures

No failure events recorded in the specified time frame!

Panel

![SubmitOrder failure graph](./images/submit_order_failures_graph.png)

Query

![SubmitOrder failure query](./images/submit_order_failures_query.png)

SPL
```
source="spring-boot-logger-log4j2.log" | where like(message,"%"."Failed submitted order"."%") | sort time
```

### Errors per second

Panel

![Errors per second graph](./images/errors_per_second_graph.png)

Query

![Errors per second query](./images/errors_per_second_query.png)

SPL
```
source="spring-boot-logger-log4j2.log" | where like(message,"%"."error"."%") | timechart span=1s count by rate
```

# eCommerce Application

In this project, you'll have an opportunity to demonstrate the security and DevOps skills that you learned in this lesson by completing an eCommerce application. You'll start with a template for the complete application, and your goal will be to take this template and add proper authentication and authorization controls so users can only access their data, and that data can only be accessed in a secure way. 

## Project Template
First, you'll want to get set up with the template. The template is written in Java using Spring Boot, Hibernate ORM, and the H2 database. H2 is an in memory database, so if you need to retry something, every application startup is a fresh copy.

To use the template, import it in the IDE of your choice as a Spring Boot application. Where required, this readme assumes the eclipse IDE.

Once the project is set up, you will see 5 packages:

* demo - this package contains the main method which runs the application

* model.persistence - this package contains the data models that Hibernate persists to H2. There are 4 models: Cart, for holding a User's items; Item , for defining new items; User, to hold user account information; and UserOrder, to hold information about submitted orders. Looking back at the application “demo” class, you'll see the `@EntityScan` annotation, telling Spring that this package contains our data models

* model.persistence.repositories - these contain a `JpaRepository` interface for each of our models. This allows Hibernate to connect them with our database so we can access data in the code, as well as define certain convenience methods. Look through them and see the methods that have been declared. Looking at the application “demo” class, you’ll see the `@EnableJpaRepositories` annotation, telling Spring that this package contains our data repositories.

* model.requests - this package contains the request models. The request models will be transformed by Jackson from JSON to these models as requests are made. Note the `Json` annotations, telling Jackson to include and ignore certain fields of the requests. You can also see these annotations on the models themselves.

* controllers - these contain the api endpoints for our app, 1 per model. Note they all have the `@RestController` annotation to allow Spring to understand that they are a part of a REST API

In resources, you'll see the application configuration that sets up our database and Hibernate, It also contains a data.sql file with a couple of items to populate the database with. Spring will run this file every time the application starts

In eclipse, you can right click the project and click  “run as” and select Spring Boot application. The application should tell you it’s starting in the console view. Once started, using a REST client, such as Postman, explore the APIs.

Some examples are as below:
To create a new user for example, you would send a POST request to:
http://localhost:8080/api/user/create with an example body like 

```
{
    "username": "test"
}
```


and this would return
```
{
    "id" 1,
    "username": "test"
}
```


Exercise:
Once you've created a user, try  to add items to cart (see the `ModifyCartRequest` class) and submit an order. 

## Adding Authentication and Authorization
We need to add proper authentication and authorization controls so users can only access their data, and that data can only be accessed in a secure way. We will do this using a combination of usernames and passwords for authentication, as well as JSON Web Tokens (JWT) to handle the authorization.

As stated prior, we will implement a password based authentication scheme. To do this, we need to store the users' passwords in a secure way. This needs to be done with hashing, and it's this hash which should be stored. Additionally when viewing their user information, the user's hash should not be returned to them in the response, You should also add some requirements and validation, as well as a confirm field in the request, to make sure they didn't make a typo. 

1. Add spring security dependencies: 
   * Spring-boot-starter-security
1. JWT does not ship as a part of spring security, so you will have to add the 
   * java-jwt dependency to your project. 
1. Spring Boot ships with an automatically configured security module that must be disabled, as we will be implementing our own. This must be done in the Application class.
2. Create password for the user
3. Once that is disabled, you will need to implement 4 classes (at minimum, you can break it down however you like):
   * a subclass of `UsernamePasswordAuthenticationFilter` for taking the username and password from a login request and logging in. This, upon successful authentication, should hand back a valid JWT in the `Authorization` header
   * a subclass of `BasicAuthenticationFilter`. 
   * an implementation of the `UserDetailsService` interface. This should take a username and return a userdetails User instance with the user's username and hashed password.
   *  a subclass of `WebSecurityConfigurerAdapter`. This should attach your user details service implementation to Spring's `AuthenticationManager`. It also handles session management and what endpoints are secured. For us, we manage the session so session management should be disabled. Your filters should be added to the authentication chain and every endpoint but 1 should have security required. The one that should not is the one responsible for creating new users.


Once all this is setup, you can use Spring's default /login endpoint to login like so

```
POST /login 
{
    "username": "test",
    "password": "somepassword"
}
```

and that should, if those are valid credentials, return a 200 OK with an Authorization header which looks like "Bearer <data>" this "Bearer <data>" is a JWT and must be sent as a Authorization header for all other rqeuests. If it's not present, endpoints should return 401 Unauthorized. If it's present and valid, the endpoints should function as normal.

## Testing
You must implement unit tests demonstrating at least 80% code coverage.
