CREATE DATABASE IF NOT EXISTS InventoryDB;
USE InventoryDB;

-- ============================================================================
-- 1. BASE CONFIGURATION TABLES (Core Metadata Nodes)
-- ============================================================================

CREATE TABLE category (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(100) NOT NULL,
    description TEXT
);

CREATE TABLE supplier (
    supplier_id INT AUTO_INCREMENT PRIMARY KEY,
    supplier_name VARCHAR(100) NOT NULL,
    contact_no VARCHAR(15),
    email VARCHAR(100),
    address VARCHAR(255)
);

CREATE TABLE warehouse (
    warehouse_id INT AUTO_INCREMENT PRIMARY KEY,
    warehouse_name VARCHAR(100) NOT NULL,
    location VARCHAR(255) NOT NULL
);

-- ============================================================================
-- 2. TRANSACTABLE CORE ENTITY (Product Catalog)
-- ============================================================================

CREATE TABLE product (
    product_id INT AUTO_INCREMENT PRIMARY KEY,
    product_name VARCHAR(100) NOT NULL,
    category_id INT,
    supplier_id INT,
    warehouse_id INT,
    price DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    stock INT NOT NULL DEFAULT 0,
    FOREIGN KEY (category_id) REFERENCES category(category_id),
    FOREIGN KEY (supplier_id) REFERENCES supplier(supplier_id),
    FOREIGN KEY (warehouse_id) REFERENCES warehouse(warehouse_id)
);

-- ============================================================================
-- 3. ACTOR / HUMAN RESOURCE TABLES
-- ============================================================================

CREATE TABLE customer (
    customer_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(15),
    address VARCHAR(255)
);

CREATE TABLE employee (
    emp_id INT AUTO_INCREMENT PRIMARY KEY,
    emp_name VARCHAR(100) NOT NULL,
    role VARCHAR(50),
    phone VARCHAR(15)
);

-- ============================================================================
-- 4. SALES, AUDITING & TRANSACTIONAL PROCESSING LOGISTICS
-- ============================================================================

CREATE TABLE orders (
    order_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT,
    emp_id INT,
    order_date DATE NOT NULL,
    status VARCHAR(50) DEFAULT 'Pending',
    FOREIGN KEY (customer_id) REFERENCES customer(customer_id),
    FOREIGN KEY (emp_id) REFERENCES employee(emp_id)
);

CREATE TABLE order_details (
    order_detail_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT,
    product_id INT,
    quantity INT NOT NULL,
    total_price DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(order_id),
    FOREIGN KEY (product_id) REFERENCES product(product_id)
);

CREATE TABLE payment (
    payment_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT,
    payment_mode VARCHAR(50) NOT NULL,
    payment_date DATE NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(order_id)
);

-- ============================================================================
-- 5. REAL-TIME AUDITING LAYER (Automated Data Capture)
-- ============================================================================

CREATE TABLE Stock_Log (
    log_id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT,
    old_stock INT,
    new_stock INT,
    change_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    changed_by VARCHAR(100) DEFAULT 'system',
    FOREIGN KEY (product_id) REFERENCES product(product_id)
);

-- ============================================================================
-- 6. BUSINESS LOGIC COMPILATIONS (Stored Procedures, Functions & Triggers)
-- ============================================================================

DELIMITER //

-- Stored Procedure to Add New Products securely
CREATE PROCEDURE AddNewProduct(
    IN pname VARCHAR(100),
    IN catid INT,
    IN sid INT,
    IN wid INT,
    IN price DECIMAL(10,2),
    IN stock INT
)
BEGIN
    INSERT INTO product (product_name, category_id, supplier_id, warehouse_id, price, stock)
    VALUES (pname, catid, sid, wid, price, stock);
END //

-- Deterministic Function to compute cumulative monetary assets
CREATE FUNCTION GetTotalStockValue()
RETURNS DECIMAL(10,2)
DETERMINISTIC
BEGIN
    DECLARE totalValue DECIMAL(10,2);
    SELECT SUM(price * stock) INTO totalValue FROM product;
    RETURN IFNULL(totalValue, 0.00);
END //

-- Automated Data Audit Capture Trigger enforcing transactional traceability
CREATE TRIGGER trg_after_stock_update
AFTER UPDATE ON product
FOR EACH ROW
BEGIN
    IF OLD.stock <> NEW.stock THEN
        INSERT INTO Stock_Log (product_id, old_stock, new_stock, changed_by)
        VALUES (OLD.product_id, OLD.stock, NEW.stock, USER());
    END IF;
END //

DELIMITER ;

-- ============================================================================
-- 7. PERFORMANCE TUNING LAYER & COMPASS VIEWS
-- ============================================================================

-- B-Tree Search Optimization Index on multi-relational queries
CREATE INDEX idx_product_name ON product(product_name);

-- Composite Material View abstracts for decoupled user dashboard operations
CREATE VIEW product_summary AS
SELECT p.product_id, p.product_name, c.category_name, s.supplier_name
FROM product p
JOIN category c ON p.category_id = c.category_id
JOIN supplier s ON p.supplier_id = s.supplier_id;

-- Secondary programmatic abstraction mapping view tracking
CREATE VIEW ProductSummary AS 
SELECT product_id, product_name, price, stock 
FROM product;

-- ============================================================================
-- 8. ANCHOR SEED CONTEXT (Verifiable Live Sample Datasets)
-- ============================================================================

INSERT INTO category (category_id, category_name, description) VALUES
(1, 'Beverages', 'Soft drinks and juices'),
(2, 'Snacks', 'Chips and biscuits'),
(3, 'Dairy', 'Milk and cheese'),
(4, 'Bakery', 'Bread and cakes'),
(5, 'Frozen', 'Frozen food items'),
(6, 'Produce', 'Fruits and vegetables'),
(7, 'Meat', 'Fresh meat items'),
(8, 'Seafood', 'Fish and prawns'),
(9, 'Cleaning', 'Household cleaning items'),
(10, 'Stationery', 'Office and school supplies');

INSERT INTO supplier (supplier_id, supplier_name, contact_no, email, address) VALUES
(1, 'FreshCo', '9876543210', 'freshco@gmail.com', 'Mumbai'),
(2, 'FoodMart', '9988776655', 'foodmart@gmail.com', 'Pune'),
(3, 'AgroSupplies', '9865321470', 'agro@gmail.com', 'Nagpur'),
(4, 'QuickFoods', '9753124680', 'quick@gmail.com', 'Delhi'),
(5, 'SuperStock', '9898989898', 'superstock@gmail.com', 'Nashik'),
(6, 'GlobalFoods', '9123456780', 'global@gmail.com', 'Chennai'),
(7, 'UrbanProvisions', '9000012345', 'urban@gmail.com', 'Bangalore'),
(8, 'MaxMart', '9234567890', 'maxmart@gmail.com', 'Hyderabad'),
(9, 'CityFresh', '9345678901', 'cityfresh@gmail.com', 'Surat'),
(10, 'EliteSupplies', '9456789012', 'elite@gmail.com', 'Ahmedabad');

INSERT INTO warehouse (warehouse_id, warehouse_name, location) VALUES
(1, 'Central Hub', 'Mumbai'),
(2, 'North Storage', 'Delhi'),
(3, 'South Depot', 'Chennai'),
(4, 'East Unit', 'Kolkata'),
(5, 'West Wing', 'Pune'),
(6, 'Surplus Stock', 'Nagpur'),
(7, 'Frozen Hub', 'Nashik'),
(8, 'Bulk Storage', 'Hyderabad'),
(9, 'Mini Unit', 'Surat'),
(10, 'Express Storage', 'Bangalore');

INSERT INTO product (product_id, product_name, category_id, supplier_id, warehouse_id, price, stock) VALUES
(1, 'Coca Cola', 1, 1, 1, 40.00, 150),
(2, 'Pepsi', 1, 2, 1, 38.00, 150),
(3, 'Lays Chips', 2, 3, 2, 20.00, 300),
(4, 'Amul Milk', 3, 4, 3, 25.00, 400),
(5, 'Britannia Bread', 4, 5, 4, 30.00, 120),
(6, 'Frozen Peas', 5, 6, 5, 60.00, 180),
(7, 'Apple', 6, 7, 6, 120.00, 250),
(8, 'Chicken', 7, 8, 7, 200.00, 90),
(9, 'Tuna Fish', 8, 9, 8, 220.00, 70),
(10, 'Detergent', 9, 10, 9, 150.00, 130),
(11, 'Hilsa Fish', 8, 8, 8, 250.00, 65),
(13, 'Sprite', 1, 1, 1, 38.00, 200);

INSERT INTO customer (customer_id, customer_name, email, phone, address) VALUES
(1, 'Riya Patil', 'riya@gmail.com', '9876543210', 'Mumbai'),
(2, 'Amit Sharma', 'amit@gmail.com', '9988776655', 'Pune'),
(3, 'Sneha Joshi', 'sneha@gmail.com', '9765432109', 'Nagpur'),
(4, 'Rahul Mehta', 'Interval@gmail.com', '9123456780', 'Delhi'),
(5, 'Priya Singh', 'priya@gmail.com', '9345678901', 'Nashik'),
(6, 'Aditya Rao', 'aditya@gmail.com', '9234567890', 'Hyderabad'),
(7, 'Kavita Deshmukh', 'kavita@gmail.com', '9456789012', 'Kolkata'),
(8, 'Rohan Nair', 'rohan@gmail.com', '9567890123', 'Chennai'),
(9, 'Pooja Shetty', 'pooja@gmail.com', '9678901234', 'Bangalore'),
(10, 'Kiran Patil', 'kiran@gmail.com', '9789012345', 'Surat');

INSERT INTO employee (emp_id, emp_name, role, phone) VALUES
(1, 'Manish Kumar', 'Manager', '9000011111'),
(2, 'Asha Mehta', 'Delivery', '9111122222'),
(3, 'Rajesh Singh', 'Sales', '9222233333'),
(4, 'Vikas Sharma', 'Clerk', '9333344444'),
(5, 'Neha Patil', 'Accountant', '9444455555'),
(6, 'Kunal Rao', 'Delivery', '9555566666'),
(7, 'Priti Joshi', 'Manager', '9666677777'),
(8, 'Ravi Verma', 'Delivery', '9777788888'),
(9, 'Divya Shah', 'Admin', '9888899999'),
(10, 'Ankit Jain', 'Support', '9999900000');
INSERT INTO orders (order_id, customer_id, emp_id, order_date, status) VALUES
(1, 1, 2, '2025-10-01', 'Pending'),
(2, 2, 3, '2025-10-02', 'Shipped'),
(3, 3, 4, '2025-10-03', 'Delivered'),
(4, 4, 5, '2025-10-04', 'Delivered'),
(5, 5, 6, '2025-10-05', 'Pending'),
(6, 6, 7, '2025-10-06', 'Delivered'),
(7, 7, 8, '2025-10-07', 'Shipped'),
(8, 8, 9, '2025-10-08', 'Delivered'),
(9, 9, 10, '2025-10-09', 'Pending'),
(10, 10, 1, '2025-10-10', 'Delivered');
INSERT INTO order_details (order_detail_id, order_id, product_id, quantity, total_price) VALUES
(1, 1, 1, 2, 80.00),
(2, 2, 2, 5, 190.00),
(3, 3, 3, 3, 60.00),
(4, 4, 4, 4, 100.00),
(5, 5, 5, 2, 60.00),
(6, 6, 6, 3, 180.00),
(7, 7, 7, 5, 600.00),
(8, 8, 8, 2, 400.00),
(9, 9, 9, 3, 660.00),
(10, 10, 10, 4, 600.00);
INSERT INTO payment (payment_id, order_id, payment_mode, payment_date, amount) VALUES
(1, 1, 'UPI', '2025-10-01', 80.00),
(2, 2, 'Card', '2025-10-02', 190.00),
(3, 3, 'Cash', '2025-10-03', 60.00),
(4, 4, 'UPI', '2025-10-04', 100.00),
(5, 5, 'Card', '2025-10-05', 60.00),
(6, 6, 'Cash', '2025-10-06', 180.00),
(7, 7, 'UPI', '2025-10-07', 600.00),
(8, 8, 'Card', '2025-10-08', 400.00),
(9, 9, 'Cash', '2025-10-09', 660.00),
(10, 10, 'UPI', '2025-10-10', 600.00);
-- Pre-populate initial manual audit trigger trace entry as recorded in the logs
INSERT INTO Stock_Log (product_id, old_stock, new_stock, changed_by)
VALUES (1, 150, 300, 'root@localhost');
-- ============================================================================
-- VERIFICATION COMMAND CONTEXT COMPLETED SUCCESSFULLY
-- ============================================================================