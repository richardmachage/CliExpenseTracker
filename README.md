# Personal Expense Tracker CLI

This is a command-line expense tracker built in Kotlin. 
It runs entirely in-memory for a single session and allows users to add, list, summarize, and delete expenses.
The application is designed to demonstrate idiomatic Kotlin, robust error handling, and clean architecture.

## How to Build and Run

This project uses the Gradle wrapper. 
Ensure you have a JDK (version 21 or higher) installed.

**Clone the repository**  
`git clone https://github.com/richardmachage/CliExpenseTracker.git` 

**Run the application:**  
Execute the following command to start the interactive CLI session.  
`./gradlew run -q --console=plain`

**To run the unit tests:**  
`./gradlew test`

## Architectural Choices

The prompt requested a simple solution while also offering bonus points for Coroutines, `Result<T>` types, and a clean separation of concerns.  
To balance this, I implemented a simple functional architecture divided into Data, Domain, and Cli layers.

**Note:**  
While a single-threaded CLI does not strictly require concurrent state management, I used Coroutines and `Mutex` locks in the data layer explicitly to demonstrate how to make it thread-safe.   
This simply ensures the underlying data architecture is robust enough to be lifted and shifted into a concurrent environment like a real world android app In-Memory cache.  
I used Mutex because it is native to Kotlin’s coroutine ecosystem. Unlike Java's synchronized which blocks threads, Mutex suspends coroutines, making the data layer more efficient and idiomatic for Kotlin-first applications.

## Assumptions
I assumed that descriptions consisting entirely of whitespace should be sanitized to `null`.
And also for empty whitespace Categories it defaults to `Uncategorized`

## Free will?
If I were to expand this beyond the required scope, my immediate next step would be to implement `Persistent Storage.` I'd probably Swap the in-memory for a simple SQLite db.  
The domain layer is already cleanly separated, this would only require swapping the data implementation without touching domain business logic.