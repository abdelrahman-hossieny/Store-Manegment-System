-- Table: customers
CREATE TABLE customers (
    id BIGINT PRIMARY KEY,
    name TEXT NOT NULL,
    email TEXT NOT NULL,
    username TEXT NOT NULL,
    orders_count INT NOT NULL,
    status TEXT NOT NULL
);

-- Table: products
CREATE TABLE products (
    id BIGINT PRIMARY KEY,
    name TEXT NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    quantity INT NOT NULL,
    category TEXT NOT NULL
);

-- Table: orders
CREATE TABLE orders (
    id BIGINT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    order_date DATE NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES customers (id)
);

-- Table: order_items
CREATE TABLE order_items (
    id BIGINT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders (id),
    FOREIGN KEY (product_id) REFERENCES products (id)
);

EXEC sp_fkeys @pktable_name = 'orders';

INSERT INTO customers (id, name, email, username, orders_count, status) VALUES
(1, 'Alice Johnson', 'alice.johnson@example.com', 'alicej', 5, 'Active'),
(2, 'Bob Smith', 'bob.smith@example.com', 'bobsmith', 3, 'Active'),
(3, 'Charlie Brown', 'charlie.brown@example.com', 'charlieb', 1, 'Inactive');
INSERT INTO products (id, name, price, quantity, category) VALUES
(1, 'Espresso Machine', 250.00, 10, 'Kitchen'),
(2, 'Smartphone', 699.99, 50, 'Electronics'),
(3, 'Running Shoes', 120.00, 30, 'Sportswear'),
(4, 'Wireless Headphones', 150.00, 20, 'Electronics');
INSERT INTO orders (id, customer_id, order_date, total_amount) VALUES
(1, 1, '2024-12-01', 400.00),
(2, 2, '2024-12-02', 699.99),
(3, 1, '2024-12-03', 370.00),
(4, 3, '2024-12-04', 250.00);
INSERT INTO order_items (id, order_id, product_id, quantity, price) VALUES
(1, 1, 1, 1, 250.00),
(2, 1, 3, 1, 120.00),
(3, 1, 4, 1, 30.00),
(4, 2, 2, 1, 699.99),
(5, 3, 3, 2, 240.00),
(6, 4, 1, 1, 250.00);

