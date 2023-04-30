# Cart

## Description

The Cart microservice is responsible for storing the number of products in the user's shopping cart. It was developed as part of a course project on creating microservices using RabbitMQ for communication between services.

## Technologies Used

- Programming Language: Java with Spring Boot
- Database: Redis
- Message Queue: RabbitMQ
- Containerization: Docker

## Installation and Configuration

1. Clone the GitHub repository:

```bash
git clone git@github.com:RedbeanGit/polyshop-cart.git
```

2. Install Docker and Docker Compose on your machine if you haven't already. You can follow the installation instructions on Docker's official website: https://docs.docker.com/get-docker/ and https://docs.docker.com/compose/install/.

3. Navigate to the Cart microservice directory:

```bash
cd polyshop-cart
```

4. Launch Docker Compose to start the necessary containers:

```bash
docker-compose up -d
```

**Now you can choose to run the Cart service inside a docker container or directly on your host.**

### Running with docker

5. Build the Docker image for the microservice using the provided Dockerfile:

```bash
docker build -t polyshop-cart .
```

6. Run the container from the image you have just builded:

```bash
docker run --name polyshop_cart polyshop-cart
```

### Running on host

5. Start Spring Boot application:

```bash
./mvnw spring-boot:run
```

## API

List of routes/API endpoints available for this microservice:

- **GET** /cart : Retrieves the list of products in the shopping cart.
- **POST** /cart/add : Adds a product to the shopping cart.
- **POST** /cart/remove : Removes a product from the shopping cart.
- **POST** /cart/checkout : Places an order for the products currently in the shopping cart and empties the cart.

## Message Queue

The Cart microservice sends an event upon checkout using the routing key _cart.checkout_.
