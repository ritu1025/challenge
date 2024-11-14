# Account Transfer Service

This project is a simple REST API service for managing account transfers, built using Spring Boot. The API allows for secure transfers between accounts and includes exception handling for scenarios like insufficient funds.

## Features

- **Account Management**: Basic account management functionality to view account balances and manage account details.
- **Account Transfers**: Supports money transfers between accounts, with error handling for insufficient funds.
- **Notification Service**: Sends notifications to account holders after successful transactions.
- **Data Validation**: Validates incoming transfer requests to ensure accurate and safe transfers.
  
## Technologies Used

- **Java 17**
- **Spring Boot** (REST, Validation)
- **JUnit 5** for unit and integration testing
- **Lombok** for cleaner Java code

## Prerequisites

- **Java 17** (Make sure Java is installed and `JAVA_HOME` is set)
- **Gradle** (included in the project)

## Project Structure

- **com.dws.challenge1.web**: Contains REST controllers, including:
  - **`AccountTransferController`**: Handles account transfer requests.
  - **`AccountController`**: Manages account operations, including account creation and retrieving account details.
- **com.dws.challenge1.service**: Holds service classes for business logic, such as:
  - **`AccountsService`**: Manages account operations, including transfers.
  - **`NotificationService`**: Sends notifications on successful transfers.
- **com.dws.challenge1.model**: Contains model classes like `Account` and `TransferRequest`.
- **com.dws.challenge1.exception**: Defines custom exceptions, such as `InsufficientFundsException`.

## Running the Application

1. Clone the repository:

    ```bash
    git clone <repository-url>
    cd account-transfer-service
    ```

2. Run the application using Gradle:

    ```bash
    ./gradlew bootRun
    ```

3. The application will start on `http://localhost:8080`.

## API Endpoints

### 1. Account Management
**URL**: `/v1/accounts`  
**Method**: `GET`  
**Description**: Retrieves account information.

### 2. Account Transfer
**URL**: `/v1/accounts/transfer`  
**Method**: `POST`  
**Consumes**: `application/json`  
**Request Body**: 
```json
{
  "accountFromId": "12345",
  "accountToId": "67890",
  "amount": 100.50
}
```

**Response Codes**:
- **200 OK**: Transfer successful.
- **400 Bad Request**: Insufficient funds for the transfer.
- **500 Internal Server Error**: Unexpected server error.

## Running Tests

The project includes JUnit tests to ensure the functionality of key components:

1. Run tests using Gradle:

    ```bash
    ./gradlew test
    ```

2. Key test classes:
   - **`AccountTransferControllerTest`**: Tests `AccountTransferController` for handling successful and failed transfers, along with exception handling.
   - **`AccountsServiceTest`**: Tests `AccountsService`'s `transferMoney` method for correct balance updates and notifications.
   - **`AccountControllerTest`**: Tests `AccountController` for basic account operations like creating and retrieving account details, ensuring data is correctly managed.

### AccountController

The `AccountController` class is responsible for managing individual account operations. Key endpoints include:

- **Create Account**: Allows for creating new accounts.
- **Get Account**: Retrieves details for a specific account by ID.

### AccountControllerTest

The `AccountControllerTest` class contains unit tests for `AccountController`. Key tests include:

- **Account Creation Test**: Verifies that new accounts are correctly created and stored.
- **Get Account Test**: Ensures that account details are retrieved accurately by ID.

These tests confirm that `AccountController` operates as expected and handles requests correctly.
## Contributing

1. Fork the repository
2. Create a new branch
3. Make your changes
4. Submit a pull request

