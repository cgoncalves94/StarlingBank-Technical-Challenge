# Starling Bank Technical Challenge

The RoundUp feature is a Java-based application designed to help users automatically round up transactions to the nearest pound and save the difference.


## Project Structure

The project adheres to the separation of concerns principle and is organized into the following packages:

- `api` - Classes for interacting with the Starling Bank API.
- `service` - Business logic for managing accounts, transactions, and savings goals.
- `exceptions` - Custom exceptions for error handling.
- `util` - Utility classes for common functionalities like rounding up calculations and user input handling.
- `model` - Data models representing the core business objects.
  

## Main Classes

- `StarlingClient` - Facilitates communication with the Starling Bank API endpoints.
- `AccountService` - Manages account-related operations.
- `TransactionService` - Handles retrieval and processing of transaction data.
- `SavingsGoalService` - Oversees the creation and updating of savings goals.
- `ApiException` and `ServiceException` - Defines exceptions for API and service layer errors.
- `RoundUpCalculator` - Calculates the round-up amount from a list of transactions.
- `UserInputHandler` - Provides methods for handling user input within the application.
- `Account`, `Transaction`, `SavingGoal`, `Amount` - Representations of the respective domain entities. 
- `ApplicationRunner` - Orchestrates the flow of the application logic.
- `Main` - Entry point for the application, responsible for initializing and starting the application.


## Test Classes

The project includes unit tests to verify the functionality of the main classes. The test classes are located in the src/test/java/com/starlingbank directory and follow the naming convention ClassNameTest.java. Key test classes include:

- `AccountServiceTest` - Tests the functionality of the AccountService class.
- `TransactionServiceTest` - Tests the functionality of the TransactionService class.
- `SavingsGoalServiceTest` - Tests the functionality of the SavingsGoalService class.
- `RoundUpCalculatorTest` - Tests the functionality of the RoundUpCalculator class.

These tests use Mockito to mock the StarlingClient class, simulating the behavior of the Starling Bank API without making actual HTTP requests.

To run the tests, use the following Maven command: `mvn test`


## Validate the Access Token
- Go to the Starling Bank Developers Account and refresh/generate a new access token.
  

## Prerequisites

Ensure you have the following installed on your system:

- Java 21 
- Maven 3.6.3 
  

## Dependencies

The project uses Maven for managing dependencies. Key libraries include:

- `httpclient` - For HTTP protocol support.
- `junit-jupiter-api`, `mockito-core`, `assertj-core` - For unit testing and assertions.
- `json` - For additional JSON handling capabilities.
  

## How to Run

Ensure you have Java and Maven installed on your system before proceeding.

1. Clone the repository:
2. Navigate to the project directory:
3. Add a valid access token to `config.properties` in the value `ACCESS_TOKEN`.
4. Build the project: `mvn clean install`
5. Run the application: `java -jar target/starlingbank.challenge-1.0-RELEASE.jar`

## Author

Cesar Goncalves


## Note

The application's functionality is dependent on the Starling Bank API's current schema. Any changes to the API may require updates to the application code.