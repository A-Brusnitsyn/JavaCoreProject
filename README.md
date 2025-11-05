 🏦 Bank Transaction System

A comprehensive Java application for processing bank transactions and transfers between accounts with robust validation and logging capabilities.

 ✨ Features

- 📁 **File Management** - Read account data and transactions from text files
- ✅ **Data Validation** - Validate account numbers and transfer amounts
- 🔄 **Transaction Processing** - Execute transfers between accounts
- 📊 **Comprehensive Logging** - Detailed operation logging with timestamps
- 🛡️ **Error Handling** - Custom exceptions and robust error management
- 💾 **Automatic Archiving** - Move processed files to archive folder
- 📈 **Account Management** - Real-time balance tracking and updates
- 🔍 **Log Filtering** - Filter operations by date range

 🏗️ System Architecture

 📦 Package Structure

 `service` Package Classes

1. **`FileProcessing`** - File I/O operations manager
   - `readBankAccounts()` - Reads bank accounts from TXT files
   - `writeHashMapToFile()` - Saves accounts state to file
   - `moneyTransferRead()` - Reads transfer files with automatic archiving
   - `moveToArchive()` - Moves processed files to archive
   - `writeLogToFile()` - Writes log entries to file
   - `readLogFile()` - Reads log file for analysis

2. **`AccountsService`** - Account management via HashMap
   - `createAccountsMap()` - Creates validated accounts collection
   - `printAccountsMap()` - Displays current account states with totals
   - `getAccountAmount()` - Retrieves specific account balance

3. **`Validator`** - Data integrity and format validation
   - `accValidation()` - Validates account data and transfer formats
   - `isValidAccount()` - Static validation with regex pattern matching
   - Supports both account creation and transfer validation

4. **`TransferService`** - Core transaction processor
   - `transferFile()` - Processes transfers from single file
   - `transferMultipleFiles()` - Handles multiple transfer files
   - `transfer()` - Executes individual transfers with balance updates
   - `logTransferError()` - Comprehensive error logging

5. **`LogService`** - Operation tracking and reporting
   - `logSuccess()` - Logs successful transactions
   - `logError()` - Logs failed operations
   - `logErrorWithRawLine()` - Handles parsing errors
   - `filterLogByDateRange()` - Date-based log filtering
   - `printFilteredLog()` - Formatted log display

6. **`BankService`** - Main application coordinator
   - `run()` - Main application loop with menu interface
   - `processTransfers()` - Complete transfer processing workflow
   - `viewOperationHistory()` - Log analysis by date range
   - `printAccounts()` - Account status display

 `exceptions` Package
- **`IncorrectAmountException`** - Handles invalid amount scenarios
- **`WrongAccountException`** - Manages account-related errors

 `resources` Package Structure
- **`bank_accounts.txt`** - Master account database
- **`input/`** - Folder for incoming transfer files
- **`archive/`** - Automated storage for processed files
- **`operations_log.txt`** - Comprehensive transaction log

 🔄 Workflow Process

 1. **Initialization Phase**
```java
// Load and validate accounts
List<String> bankAccounts = fileProcessing.readBankAccounts();
List<String> validAccounts = validator.accValidation(bankAccounts);
currentAccountsMap = accountsService.createAccountsMap(validAccounts);
```

 2. **Transfer Processing**
- Reads multiple transfer files from `input/` folder
- Validates each transfer against business rules
- Executes valid transfers with real-time balance updates
- Automatically archives processed files

 3. **Logging & Reporting**
- Tracks all operations with timestamps
- Records both successes and failures
- Provides date-range filtering for analysis
- Maintains complete audit trail

 🚨 Exception Handling

 Custom Exceptions
- **`IncorrectAmountException`**: Invalid amounts, negative values, insufficient funds
- **`WrongAccountException`**: Invalid account formats, non-existent accounts, self-transfers

 Validation Rules
- **Account Format**: `-` (5 digits, hyphen, 5 digits)
- **Amount Validation**: Positive numbers only, no alphabetic characters
- **Balance Checks**: Sufficient funds verification
- **Data Integrity**: Proper format and structure validation

 💻 User Interface

 Main Menu Options
```
1. Process Transfers
2. View Operation History by Date
3. Show Current Accounts
4. Exit
```

 Key Features
- **Interactive Console Interface**
- **Real-time Account Status**
- **Filtered Log Viewing**
- **Comprehensive Error Messages**
- **Automatic File Management**


 Key Workflows
1. **Account Loading** → Validation → HashMap Creation
2. **Transfer Processing** → Validation → Execution → Logging
3. **Log Management** → Filtering → Reporting → Archiving

 🔧 Technical Highlights

- **Pattern-based Validation** using regex for account format checking
- **HashMap-based Account Management** for efficient balance tracking
- **Automatic File Archiving** to maintain clean working directories
- **Comprehensive Logging** with structured error handling
- **Date-based Filtering** for operational analysis
- **Duplicate Account Handling** with automatic balance consolidation

The system ensures financial transaction integrity through multi-layered validation, comprehensive auditing, and robust error handling while maintaining user-friendly operation through clear menu interfaces and detailed reporting.
