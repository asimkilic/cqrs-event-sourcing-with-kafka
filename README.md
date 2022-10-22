- Handle commands and raise events
- Use the mediator pattern to implement command and query dispatchers
- Implemenet an event store/write database in MongoDB
- Create a read database in MySQL
- Apply event versioning
- Implement optimistic concurrency control
- Produce event to Apache Kafka
- Consume event from Apache Kafka to populate and alter the read database
- Replay the event strore to recreate the state of the aggregate
- Separate read and write concerns
- Structure your code using Domain-Driven-Design best practices

Microservices are small, loosely coupled applications or services that can fail independently from each other. When a microservice fails, only a single process or function in the system should become unavailable while the rest of the system remains unaffected.

- Microservices should not share code or data
- Avoid unnecessary coupling between services and sofware components
- Independence and automony are more important than code re-usability
- Each microservice should be responsible for a single system function or process
- Microservices should not communicate directly with each other, they should make use of an event/message bus to communicate with one another.

CQRS is a software design pattern that stands for command query responsibility segregation. CQRS suggests that an application should be divided into a command and query part  with commands alter the state of an object or entity both queries retrieves the state of an object or entity.

Why do we need CQRS ?

- Data is often more frequently queried than altered, or visa versa
- Segregating commands and queries enables us to optimise each for high performance 
- Read and write representations of data often differ substantially
- Executing command and query operations on the same model can cause data contention  
- Segregating read and write concerns enables you to manage read and write security separately  

Event sourcing defines an approach where all the changes that are made to an object or entity are stored as a sequence of immutable events to an event store, as opposed to just saving the current state of the object or entity.

Benefits of Event Sourcing

- The event store provided a complete log of every state change
- The state of an object/aggregate can be recreated by replaying the event store
- Improves write performance. All event types are simply appended to the event store. There are no update or delete operations.
- In the case of failure, the event store can be used to restore read database



<img src="https://raw.githubusercontent.com/asimkilic/cqrs-event-sourcing-with-kafka/master/assets/image-20221012204544521.png" />

We are going to build a bank account command API and a bank account query API. A bank account command API is responsible for handling the rights or commands and the bank account query API responsible for handling the reads. Notice the different command objects that the bank account API will be able to handle, including an  'OpenAccountCommand' used to open a new bank account, 'DepositFundsCommand' used to deposit funds into a bank account, 'WithdrawFundCommand' that will be used to withdraw funds from an account and ''ClosedAccountCommand' that will be used to close a bank account.

The bank account query API, on the other hand will be able to handle a 'FindAllAccountsQuery' which will be used to return all accounts from the read database, 'FindAccountByIdQuery' which will be used to find a bank account for a specified bank account ID, 'FindAccountsByHolderQuery' which will be used to find a bank account for a specified bank account holder and finally 'FindAccountsWithBalanceQuery' which will be used to find accounts with a certain balance greater than or less than specified amount.

Apache Kafka is an open-source distributed event streaming platform that enables the development of real-time, event-driven applications. 

Kasta was developed by LinkedIn in 2011 as a high-throughput message broker for its own use. It was then open-sourced and donated to the Apache Foundation.

Today, Kafka has evolved into the most widely-used streaming platform, and it is cabaple of ingesting and processing trillions of records per day without any noticeable performance lag.

Tools & Technologies

- Java Development Kit (JDK 8 >)
- Maven
- Docker
- Apache Kafka
- MongoDb
- MySQL



Docker Network:

```powershell
docker network create --attachable -d bridge techbankNet
```

Docker compose : https://docs.docker.com/compose/install

Docker MongoDb:

```powershell
docker run -it -d --name mongo-container -p 27017:27017 --network techbankNet --restart always -v mongodb_data_container:/data/ mongo:latest
```

Docker MySQL:

```powershell
docker run -it -d --name mysql-container -p 3306:3306 --network techbankNet -e MYSQL_ROOT_PASSWORD=techbankRootPsw --restart always -v mysql_data_container:/var/lib/mysql mysql:latest
```

Docker -Adminer:

```powershell
docker run -it -d --name adminer -p 8181:8080 --network techbankNet -e ADMINER_DEFAULT_SERVER=mysql-container --restart always adminer:latest
```



Spring Initializer

Bank Account Command API Project

<img src="https://raw.githubusercontent.com/asimkilic/cqrs-event-sourcing-with-kafka/master/assets/image-20221014160202640.png" />



Bank Account Query API Project

<img src="https://raw.githubusercontent.com/asimkilic/cqrs-event-sourcing-with-kafka/master/assets/image-20221014160315193.png" />



Bank Account Common Project

<img src="https://raw.githubusercontent.com/asimkilic/cqrs-event-sourcing-with-kafka/master/assets/image-20221014160459922.png" />



The core CQRS & ES Framework Project

<img src="https://raw.githubusercontent.com/asimkilic/cqrs-event-sourcing-with-kafka/master/assets/image-20221014160625614.png" />



Messages:

In CQRS and event sourcing, there are three message types that are important, including commands, events and queries.

What is  a Command ?

- A command is a combination of expressed intent. In other words it describes something that you want to be done. It also contains the information required to undertake action based on that intent.Commands are named with a verb in the imperative mood. For example open a card command or deposit funds command.

It is time for us to create a bank account objects both command and event objects are known as message objects, and both requires a unique identifier. Therefore, we will start by creating an abstract message class with an ID field.

What is an Event?

- Events are objects that describe something that has occured in the application. A typical source of events is the aggregate. When something important has occured within the aggregate, it will raise an event. 
- Events are always named in the past particle verb, for example account opened event.



Mediator Pattern

- Behavioral Design Pattern
- Promotes loose coupling by preventing objects from referring to each other explicitly
- Simplifies communication between objects by introducing a single object known as the mediator that manages the distribution of messages amoung other objects

<img src="https://raw.githubusercontent.com/asimkilic/cqrs-event-sourcing-with-kafka/master/assets/image-20221016183121298.png" />



UML class diagram that provides a visualization of all of the classes and objects that participates in the mediator pattern. At the top left, we have the mediator that defines an interface for communicating with colleague objects. Then we have the concrete mediator class that implements the mediator interface, which implements cooperative behaviour by coordinating colleague objects.

On the right hand side, we have the colleague classes. Each colleague class is aware of its mediator object, and each colleague class communicates with its  mediator whenever it would have otherwise communicated with another colleague.

<img src="https://raw.githubusercontent.com/asimkilic/cqrs-event-sourcing-with-kafka/master/assets/image-20221016183825739.png" />

Now let's look at another UML class diagram that is based on the command dispatching mechanism of a CQRS archirecture. In this class diagram, you'll notice that the command dispatcher is the mediator interace, and the command dispatcher is the concrete mediator. The command handler is the colleague and the account command handler, the concrete colleague. More specifically, our command dispatch, a mediator interface defines two methods one for registering a handler, which would be our command handler colleague and another for sending or dispatching a command to a command handler, and the account command dispatcher we'll keep track of all the command handling methods that have been registered and will be responsible for sending or dispatching a command which extends base command, that would be one of our bank account command objects as you can see in our command handler colleague interface there,you can see that we have a handle method for handling each of our bank account command objects and the accounbt command handler, as the concrete colleague will implement each of these handler methods. In other words,  the command dispatcher will be responsible for registering all of our different command handler methods, and it will make sure that the various conmtroller methods dispatches the command objects to the relevant account command handling methods.



What is an aggregate?

- It is an entity or group of entities that is always kept in consistent state. The Aggregate Root is the entity within the aggregate that is responsible for maintaining this consistent state. This makes the aggregate the primary building block for implementing a command model in any CQRS based application.



What is an event store?

- Event store is a database that is used to store data as a sequence of immutable events over time and events store is a key enabler of event sourcing and the following are key considerations when designing and event store

Event Sotre - Key Considerations

- An event store must be an append only store, no update or delete operations should be allowed.
- Each event that is saved should represent the version or state of an aggregate at any given point in time
- Event should be stored in chronological order, and new events should be appended to the previous event
- The state of the aggregate should be recreatable by replaying the event store
- Implement optimistic concurrency control



<img src="https://raw.githubusercontent.com/asimkilic/cqrs-event-sourcing-with-kafka/master/assets/image-20221016183825740.png" />

What a Kafka producer is and how it works ?

Consider the following Kafka architecture drawing, a Kafka producer is used to send our produce messages to one or more Kafka topics. Kafka produces also serializes compresses and load balances data amoung Kafka brokers through partitioning .

What is a broker? 

It is a server running in a Kafka cluster, usually in the form of a container. Kafka clusters are usually made up of one or more brokers. Having multiple Kafka brokers allows for load balancing redundancy and reliable fail over. Brokers are stateless and rely on Apache Zookeeper to manage the state of the cluster. Apache Zookeeper is thus responsible to manage the cluster and election of the broker leader. It is advised to utilize a minimum of three brokers to achieve reliable fail over. However, a single broker can handle hundreds of thousands of messages without a performance impact.

<img src="https://raw.githubusercontent.com/asimkilic/cqrs-event-sourcing-with-kafka/master/assets/image-20221016183825741.png" />

What is a partitioning in Kafka?

Topics are divided into partitions in a Kafka cluster, and partitions are replicated across brokers. But what exactly is a topic? You can view a Kafka topiac as a channel through which event data is streamed, produces, always publishes or produces event messages to topics while consumers read messages from a topic that they subscribe to. Some people like to compare a topic with a database table, while others compared to a log or a queue.

Let's say we have three producers, each writing a different topic and three consumers, each subscribing to one of the three topics. Let's say that producer A produces a message to topic one. As soon as a new message is produced to topic one, the consumer will detect that the topic of set has changed and it will consume the event message. Similarly, if producer B produces an event message to topic two, then consumer B will also take that the offset has changed, and it will consume the new event message that has been published to topic two. The same with producer three,

Domain Driven Design (DDD)

- The term "domain-driven design" was coined by Eric Evans in 2003
- An approach to structure and model software in a way that it matches the business domain
- It places the primary focus of a software project on the core area of the business (the core domain)
- Refers to prolems as domains and aims to establish a common language to talk about these problems 
- Describes independent problem areas as Bounded Contexts

What is a bounded context ?

- It is an independent problem area
- Describes a logical boundary within which a particular model is defined and applicable.
- Each bounded context correlates to a microservice. (e.g., Bank Account Microservice)



<img src="https://raw.githubusercontent.com/asimkilic/cqrs-event-sourcing-with-kafka/master/assets/image-20221016183825742.png" />

Let's say we have a topic called *FundsDepositEvent* . Once somebody deposits fund into a bank account, a producer produces the *FundsDepositedEvent* into Kafka,notice the Kafka commit log, that starts at offset zero all the way through to offset 1003. As we've mentioned before, the order is very important. Every time a producer produces a new event message to Kafka, it will append to the Kafka log. We have two consumers here an *Account Consumer* that is used to consume the *FundsDeposiedEvent* and update the bank account read database for example, and then another consumer called *Notification Consumer* which could send a sms to a customer when funds is deposited into his or her account. Notice that account consumer has already consumed messages zero through to 1001 and that it is busy consuming the funds deposited event at offset 1002, whereas the notification consumer has only consumed messages zero through to six and that could be for various reasons. Perhaps it was switched off or it failed and it was rebooted. It needs to catch up. Now remember the reason that the notification consumer and account consumers offsets differ is because they group IDs are different. Notice that the account consumer group id is bankaccConsumer and the notification consumer group id is called accNotifyConsumer. That is why the offsets are tracked separately. Now let's say that we want to enable the notification consumer to catch up. Container orchestration engines easily allows us to scale up microservices, either by manually configuring an amount of instances that you want to run or to tell Kubernetes to auto scale the amount of instances required. Kafka absolutely supports multiple instances running on the same consumer group. In other words, we could, for example start up four instances of the notification consumer and Kafka will allow them to effectively distribute the event messages between the different instances. Just a clarify, a. Kafka consumer must belong to a consumer group, and a consumer group can have one or more consumers. And then the offset is tracked per consumer group and not per consumer