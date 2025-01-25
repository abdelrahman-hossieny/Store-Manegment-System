CREATE DATABASE MarketDb
USE MarketDb 

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
