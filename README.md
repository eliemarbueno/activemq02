# ActiveMQ Sample Application

This is a Java 1.8 application that demonstrates the use of ActiveMQ for message queue production and consumption. The application follows Clean Architecture principles and implements two queues for handling Products and Categories.

## Project Structure

```
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── ebueno/
│                   ├── domain/           # Domain entities
│                   ├── application/      # Application business rules
│                   ├── infrastructure/   # External frameworks and tools
│                   └── interfaces/       # Controllers and external interfaces
├── config/
│   └── application.properties           # Application configuration
└── pom.xml                             # Project dependencies and build config
```

## Prerequisites

- Java 8 JDK
- Maven
- Docker (for running ActiveMQ)

## ActiveMQ Setup

Start ActiveMQ using Docker with the following command:

```bash
docker run -d --name activemq \
  -p 61616:61616 \
  -p 8161:8161 \
  rmohr/activemq:5.15.9
```

Default credentials:
- Username: admin
- Password: admin

ActiveMQ Web Console: http://localhost:8161/admin
- Default credentials are the same as above

## Configuration

The application configuration is stored in `config/application.properties`. You can modify the following settings:

```properties
# ActiveMQ Connection Settings
activemq.broker.url=tcp://localhost:61616
activemq.username=admin
activemq.password=admin

# Queue Names
queue.product=PRODUCT_QUEUE
queue.category=CATEGORY_QUEUE

# Consumer Settings
consumer.interval.ms=60000  # 1 minute
```

## Building the Application

To build the application, run:

```bash
mvn clean package
```

This will create two JAR files in the `target` directory:
- `sample-active-mq-1.0-SNAPSHOT.jar`: The application JAR
- `sample-active-mq-1.0-SNAPSHOT-jar-with-dependencies.jar`: The application JAR with all dependencies included

## Running the Application

Run the application using the following command:

```bash
java -jar target/sample-active-mq-1.0-SNAPSHOT-jar-with-dependencies.jar
```

## Features

1. XML Message Format
   - All messages are formatted as XML using JAXB
   - Supports both Product and Category entities

2. Two Different JMS Consumer Approaches
   a. Persistent Connection (MessageListener) - Used for Products
      - Maintains an active connection to ActiveMQ
      - Better for high-frequency messaging
      - Lower latency in message processing
      - Higher resource usage (constant TCP connection)
      - Used in ProductJmsService
   
   b. Polling Connection (Connection per request) - Used for Categories
      - Opens/closes connection on each poll
      - Better for low-frequency messaging
      - Higher latency in message processing
      - Lower resource usage
      - Used in CategoryJmsService

3. Clean Architecture
   - Separation of concerns with distinct layers
   - Domain-driven design principles
   - SOLID principles implementation

3. Configurable Settings
   - Connection parameters
   - Queue names
   - Consumer intervals

4. Controllers for Entity Management
   - ProductController for handling product operations
   - CategoryController for handling category operations

5. Message Consumers
   - Separate threads for Product and Category consumption
   - Configurable polling interval (default: 1 minute)

## Usage Example

### Using the REST API

The application exposes a REST API on port 8080 (configurable in application.properties) that accepts XML payloads.

#### Sending a Product

```bash
curl -X POST http://localhost:8080/api/product \
  -H "Content-Type: application/xml" \
  -d '<?xml version="1.0" encoding="UTF-8"?>
      <product>
          <id>1</id>
          <name>Sample Product</name>
          <description>Product Description</description>
          <price>29.99</price>
          <categoryId>1</categoryId>
      </product>'
```

#### Sending a Category

```bash
curl -X POST http://localhost:8080/api/category \
  -H "Content-Type: application/xml" \
  -d '<?xml version="1.0" encoding="UTF-8"?>
      <category>
          <id>1</id>
          <name>Sample Category</name>
          <description>Category Description</description>
      </category>'
```

### Using Java Code

```java
// Creating and sending a product
Product product = new Product(1L, "Sample Product", "Description", 29.99, 1L);
ProductController productController = new ProductController();
productController.sendProduct(product);

// Creating and sending a category
Category category = new Category(1L, "Sample Category", "Description");
CategoryController categoryController = new CategoryController();
categoryController.sendCategory(category);
```

## Error Handling

The application includes comprehensive error handling for:
- JMS connection issues
- XML marshalling/unmarshalling
- Configuration loading
- Message processing

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License.