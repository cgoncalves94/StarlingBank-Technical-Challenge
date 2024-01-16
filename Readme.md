# RoundUp Application

The RoundUp Application is a Java-based application that calculates the total round-up amount from transactions, creates a savings goal, and transfers the round-up amount to the savings goal.

## Main Classes

- `api/StarlingClient.java`: This class is a client for interacting with the Starling Bank API. It provides methods to perform various operations, such as retrieving account details, getting transactions between specific timestamps, checking savings goals, creating savings goals, and adding money to savings goals. This class requires an access token to authenticate with the Starling Bank API. The access token should be provided when constructing an instance of `StarlingClient`.

- `service/RoundUpCalculator.java`: This class provides methods to calculate the round-up amount for transactions and the total round-up amount for a list of transactions.

- `RoundUpApplication.java`: This class represents the RoundUpApplication which is responsible for calculating the total round-up amount from transactions, creating a savings goal, and transferring the round-up amount to the savings goal.

## Dependencies

This project uses Maven for dependency management. The main dependencies include:

- `org.apache.httpcomponents:httpclient:4.5.14` for making HTTP requests.
- `com.fasterxml.jackson.core:jackson-databind:2.16.1` for JSON processing.
- `org.json:json:20231013` for handling JSON data.

Please refer to the `pom.xml` file for the complete list of dependencies.

## How to Run

1. Clone the repository to your local machine.
2. Navigate to the project directory.
3. Ensure you have a valid access token set in the `roundup/resources/config.properties` file.
4. Build the project using Maven by running the command `mvn clean install`.
5. Run the application using the command `java -cp target/roundup-1.0-SNAPSHOT.jar com.goncalves.RoundUpApplication`.
6. When prompted, enter the start and end dates for the transactions in the format `YYYY-MM-DD`.
7. If there are no existing savings goals, you will be prompted to create a new one by entering a name, target amount, and currency for the goal.

## Note

This application assumes that the Starling API responds with JSON data in a specific format. If the API response format changes, the application may not work as expected.