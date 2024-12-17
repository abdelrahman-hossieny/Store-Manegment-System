
-- Table: customers
CREATE TABLE customers (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    name TEXT NOT NULL,
    email TEXT NOT NULL,
    username TEXT NOT NULL,
    orders_count INT NOT NULL,
    status TEXT NOT NULL
);

-- Table: products
CREATE TABLE products (
    id BIGINT PRIMARY KEY IDENTITY(1, 1),
    name TEXT NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    quantity INT NOT NULL,
    category TEXT NOT NULL
);

-- Table: orders
CREATE TABLE orders (
    id BIGINT PRIMARY KEY IDENTITY(1, 1),
    customer_id BIGINT NOT NULL,
    order_date DATE NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES customers (id) ON DELETE CASCADE
);

-- Table: order_items
CREATE TABLE order_items (
    id BIGINT PRIMARY KEY IDENTITY(1, 1),
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders (id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE
);


-- =====================================================
-- data
-- ========================================================

-- Insert data into customers
INSERT INTO customers (name, email, username, orders_count, status)
VALUES
('John Doe', 'johndoe@example.com', 'johndoe', 3, 'Active'),
('Jane Smith', 'janesmith@example.com', 'janesmith', 1, 'Active'),
('Bob Johnson', 'bobjohnson@example.com', 'bobjohnson', 0, 'Inactive'),
('Alice Davis', 'alicedavis@example.com', 'alicedavis', 5, 'Active');

-- Insert data into products
INSERT INTO products (name, price, quantity, category)
VALUES
('Apple iPhone 14', 799.99, 50, 'Electronics'),
('Samsung Galaxy S23', 749.99, 30, 'Electronics'),
('Sony WH-1000XM5 Headphones', 349.99, 100, 'Electronics'),
('Dell XPS 13 Laptop', 999.99, 20, 'Computers'),
('Logitech MX Master 3 Mouse', 99.99, 150, 'Accessories'),
('Apple Watch Series 8', 399.99, 80, 'Wearables');

-- Insert data into orders
INSERT INTO orders (customer_id, order_date, total_amount)
VALUES
(1, '2024-01-15', 1599.98),
(2, '2024-02-20', 749.99),
(1, '2024-03-10', 349.99),
(4, '2024-04-05', 1999.97);

-- Insert data into order_items
INSERT INTO order_items (order_id, product_id, quantity, price)
VALUES
-- Order 1 details
(1, 1, 2, 799.99),
-- Order 2 details
(2, 2, 1, 749.99),
-- Order 3 details
(3, 3, 1, 349.99),
-- Order 4 details
(4, 1, 1, 799.99),
(4, 4, 1, 999.99),
(4, 5, 1, 199.99);


SELECT * FROM products

SELECT * FROM customers

SELECT * FROM orders

SELECT * FROM order_items

