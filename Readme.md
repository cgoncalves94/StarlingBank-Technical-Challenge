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
- `Account`, `Transaction`, `SavingGoal`, `Amount` - Representations of the respective domain entities. The `Amount` class typically encapsulates monetary values, handling currency and minor units.
- `ApplicationRunner` - Orchestrates the flow of the application logic.
- `Main` - Entry point for the application, responsible for initializing and starting the application.

## Dependencies

The project uses Maven for managing dependencies. Key libraries include:

- `httpclient` - For HTTP protocol support.
- `jackson-databind` - For JSON parsing and serialization.
- `junit-jupiter-api`, `mockito-core`, `assertj-core` - For unit testing and assertions.
- `slf4j-api` - For logging.
- `json` - For additional JSON handling capabilities.

## Validate the Access Token
- Go to the Starling Bank Developers Account and refresh/generate a new access token.

## How to Run

Ensure you have Java and Maven installed on your system before proceeding.

1. Clone the repository:
2. Navigate to the project directory:
3. Add a valid access token to `config.properties` in the value `ACCESS_TOKEN`.
4. Build the project: `mvn clean install`
5. Run the application: `java -cp target/roundup-1.0-SNAPSHOT.jar com.starlingbank.Main`

## Author

Cesar Goncalves

## Note

The application's functionality is dependent on the Starling Bank API's current schema. Any changes to the API may require updates to the application code.