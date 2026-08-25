<img width="960" height="564" alt="image" src="https://github.com/user-attachments/assets/87bbbfbe-38c1-440e-aa8e-7851a47ebd2d" /># online-banking-system
Java Online Banking System using JDBC and MySQL


 Online Banking System

📌 Project Description

The **Online Banking System** is a Java-based banking application developed to perform basic banking operations digitally. The system allows users to manage their bank accounts, check balances, transfer money, view transaction history, and perform other banking-related operations through a simple and secure application.

The project uses **Java** for application development and **MySQL** for database management. **JDBC (Java Database Connectivity)** is used to establish a connection between the Java application and the MySQL database.

🚀 Features

* User account management
* Secure login and password handling
* Check account balance
* Deposit money
* Withdraw money
* Transfer money between accounts
* View transaction history
* Account details management
* MySQL database integration
* JDBC-based database connectivity

🛠️ Technologies Used

* **Java**
* **MySQL**
* **JDBC**
* **VS Code**
* **MySQL Connector/J**

## 📂 Project Structure

text
OnlineBankingSystem/
│
├── src/
│   ├── AccountService.java
│   ├── App.java
│   ├── DatabaseConnection.java
│   ├── HistoryService.java
│   ├── PasswordUtil.java
│   ├── Test.java
│   ├── TransactionService.java
│   ├── TransferService.java
│   └── UserService.java
│
├── lib/
│   └── mysql-connector-j-26.7.0.jar
│
├── online_banking.sql
├── README.md
└── .gitignore


##Database

The project uses **MySQL** as the database. The online_banking.sql file contains the required database structure and SQL queries needed to set up the project.

⚙️ How to Run

1. Install Java JDK.
2. Install MySQL Server.
3. Create the required database using online_banking.sql.
4. Add the MySQL Connector/J library to the project.
5. Update the MySQL username and password in DatabaseConnection.java.
6. Compile and run App.java.

## 🔐 Security

The project includes password utility functionality and database authentication. Sensitive information such as database passwords should not be uploaded to GitHub.

## 🎯 Objective

The main objective of this project is to demonstrate how a banking application can be developed using **Java, JDBC, and MySQL**, while implementing common banking operations and database management.

👨‍💻 Author
Jayesh Tayade
Computer Science & Engineering Student

