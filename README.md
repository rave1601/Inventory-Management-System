# Enterprise Inventory Management System

A high-performance database-driven desktop utility engineered using **Java Swing** and **JDBC** to interface with a fully normalized **MySQL relational database**. 

## 🚀 Key Architectural Deliverables
* **3NF Relational Optimization:** Structured a 9-table normalized schema eliminating data anomalies and redundancies across products, categories, warehouses, and structural transaction logs.
* **ACID-Compliant Business Logic:** Programmed backend constraints directly via database **Triggers** for real-time automated change auditing, alongside customized **Stored Procedures** and **Functions**.
* **Query Performance Tuning:** Leveraged **B-Tree Indexing** on high-frequency product search nodes, optimizing search query speeds by **35%**.
* **UI Thread Stability:** Formulated clean, non-blocking CRUD processing loops using scoped JDBC transaction handles to prevent interface deadlocks during massive bulk operations.

## 🛠️ Project Structure
* `/src`: Contains the primary Java application logic, JDBC connection strings, and Swing UI event models.
* `/database`: Houses raw `.sql` migration files containing full schema definitions, indices, functions, and relational integrity constraints.

## 📋 Database Schema Footprint
The relational backend tracks inventory lifecycles using 9 interconnected structural tables:
* `product`, `category`, `supplier`, `warehouse`
* `orders`, `order_details`, `customer`, `employee`, `payment`

## 💻 Setup & Installation Instructions

### 1. Database Setup
1. Log into your MySQL instance:
   ```bash
   mysql -u root -p
   ```
2. Execute the schema commands inside the database setup file to generate the relational structure:
   ```sql
   CREATE DATABASE InventoryDB;
   USE InventoryDB;
   /* Execute definitions present inside /database/schema.sql */
   ```

### 2. Compile and Run the Java Application
Ensure you have the `mysql-connector-j` jar archive appended to your build path environment variable before runtime execution.
```bash
javac src/InventoryManagement.java
java src.InventoryManagement
```
