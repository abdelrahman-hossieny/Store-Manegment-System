import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.sql.*;
import java.time.LocalDate;
import java.util.Optional;

public class Main extends Application {

    private static final String USERNAME = "a";
    private static final String PASSWORD = "a";

    private BorderPane mainLayout;
    private Scene mainScene;
    private Stage stage;

    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;

        // Set the application icon
        stage.getIcons().add(new Image(getClass().getResourceAsStream("logo.png")));

        // Show login page on application start
        stage.setTitle("Login");
        Scene loginScene = createLoginScene();
        stage.setScene(loginScene);
        stage.show();
    }

    private Scene createLoginScene() {
        Label titleLabel = new Label("Welcome to Store Management System");
        styleLabel(titleLabel, 30, "#2c3e50", true);

        Label usernameLabel = new Label("Username:");
        styleLabel(usernameLabel, 18, "#34495e", true);

        TextField usernameField = createStyledTextField("Enter your username");

        Label passwordLabel = new Label("Password:");
        styleLabel(passwordLabel, 18, "#34495e", true);

        PasswordField passwordField = createStyledPasswordField("Enter your password");

        Label messageLabel = new Label();
        styleLabel(messageLabel, 14, "red", false);

        Button loginButton = createStyledButton("Login");
        loginButton.setMaxWidth(200);
        loginButton.setStyle("-fx-background-color: #2c3e50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10;");
        loginButton.setOnMouseEntered(e -> loginButton.setStyle("-fx-background-color: #34495e; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10;"));
        loginButton.setOnMouseExited(e -> loginButton.setStyle("-fx-background-color: #2c3e50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10;"));
        loginButton.setOnAction(event -> {
            String inputUsername = usernameField.getText().trim();
            String inputPassword = passwordField.getText().trim();

            if (inputUsername.isEmpty() || inputPassword.isEmpty()) {
                messageLabel.setText("Please fill in all fields!");
            } else if (inputUsername.equals(USERNAME) && inputPassword.equals(PASSWORD)) {
                stage.setTitle("Store Management System");
                initializeMainPage();
                stage.setScene(mainScene);
            } else {
                messageLabel.setText("Invalid username or password!");
            }
        });

        VBox formLayout = new VBox(20, titleLabel, usernameLabel, usernameField, passwordLabel, passwordField, loginButton, messageLabel);
        formLayout.setAlignment(Pos.CENTER);

        StackPane cardPane = new StackPane(formLayout);
        cardPane.setStyle("-fx-background-color: white; -fx-padding: 30; -fx-border-radius: 20; -fx-background-radius: 20; "
                + "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.25), 20, 0, 0, 10);");

        StackPane root = new StackPane(cardPane);
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #e0f7fa, #ffffff);");
        root.setPadding(new Insets(20));

        return new Scene(root, 1024, 768);
    }
    private void initializeMainPage() {
        mainLayout = new BorderPane();
        VBox sidebar = createSidebar();
        mainLayout.setLeft(sidebar);

        showHomePage();

        mainScene = new Scene(mainLayout, 1024, 768);
    }
    private VBox createSidebar() {
        // Create navigation buttons
        Button homeButton = createStyledButton("Home");
        homeButton.setOnAction(e -> showHomePage());

        Button productsButton = createStyledButton("Products");
        productsButton.setOnAction(e -> showProductsPage());

        Button ordersButton = createStyledButton("Orders");
        ordersButton.setOnAction(e -> showOrdersPage());

        Button customersButton = createStyledButton("Customers");
        customersButton.setOnAction(e -> showCustomersPage());

        Button signOutButton = createStyledSignOutButton("Sign Out");
        signOutButton.setOnAction(e -> {
            stage.setTitle("Login");
            stage.setScene(createLoginScene());
        });

        // Style buttons and make them bold
        for (Button button : new Button[]{homeButton, productsButton, ordersButton, customersButton}) {
            button.setMaxWidth(Double.MAX_VALUE); // Make buttons expand to fill width
            // Set button styles including bold
            button.setStyle("-fx-font-weight: bold; -fx-background-color: #2c3e50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10;");

            // Set hover effects for buttons
            button.setOnMouseEntered(e -> button.setStyle("-fx-font-weight: bold; -fx-background-color: #34495e; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10;"));
            button.setOnMouseExited(e -> button.setStyle("-fx-font-weight: bold; -fx-background-color: #2c3e50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10;"));
        }

        // Spacer to push the Sign Out button to the bottom
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS); // Allow spacer to grow and push other elements up

        // Create sidebar layout
        VBox sidebar = new VBox(15, homeButton, productsButton, ordersButton, customersButton, spacer, signOutButton);
        sidebar.setAlignment(Pos.TOP_CENTER);
        sidebar.setStyle("-fx-background-color: #2c3e50; -fx-padding: 20;");
        sidebar.setPrefWidth(200);

        return sidebar;
    }


    private void showHomePage() {
        Label homeLabel = new Label("Welcome to the Home Page");
        styleLabel(homeLabel, 24, "#34495e", true);

        VBox homePage = new VBox(homeLabel);
        homePage.setAlignment(Pos.CENTER);
        mainLayout.setCenter(homePage);
    }
    // Add this helper method to establish a database connection with Windows Authentication
    private Connection getConnection() throws SQLException {
        String url = "jdbc:sqlserver://ABDULRHMAN;databaseName=store_db;integratedSecurity=true;trustServerCertificate=true;";
        return DriverManager.getConnection(url);
    }
    // Update the `showProductsPage` method
    private void showProductsPage() {
        TableView<Product> productTable = new TableView<>();
        ObservableList<Product> products = FXCollections.observableArrayList();

        // Fetch data from the database
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT name, price, quantity, category FROM products")) {

            while (rs.next()) {
                products.add(new Product(
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getInt("quantity"),
                        rs.getString("category")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        productTable.setItems(products);

        // Define table columns
        TableColumn<Product, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Product, Double> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));

        TableColumn<Product, Integer> quantityCol = new TableColumn<>("Quantity");
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        TableColumn<Product, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));

        // Edit Button Column
        TableColumn<Product, Void> editCol = new TableColumn<>("Edit");
        editCol.setCellFactory(param -> new TableCell<Product, Void>() {
            private final Button editButton = new Button("Edit");

            {
                editButton.setStyle("-fx-background-color: #0078D7; -fx-text-fill: white; -fx-padding: 5 10 5 10;");
                editButton.setOnAction(e -> {
                    Product product = (Product) getTableView().getItems().get(getIndex());
                    showAddProductDialog(product, products, productTable);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(editButton);
                }
            }
        });

        // Delete Button Column
        TableColumn<Product, Void> deleteCol = new TableColumn<>("Delete");
        deleteCol.setCellFactory(param -> new TableCell<Product, Void>() {
            private final Button deleteButton = new Button("Delete");

            {
                deleteButton.setStyle("-fx-background-color: #DC3545; -fx-text-fill: white; -fx-padding: 5 10 5 10;");
                deleteButton.setOnAction(e -> {
                    Product product = getTableView().getItems().get(getIndex());
                    if (deleteProductFromDatabase(product)) {
                        products.remove(product); // Remove from ObservableList
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                }
            }
        });

        // Add columns to the table
        productTable.getColumns().addAll(nameCol, priceCol, quantityCol, categoryCol, editCol, deleteCol);

        // Search Bar
        TextField searchField = new TextField();
        searchField.setPromptText("Search Products");
        searchField.setStyle("-fx-pref-width: 300px;");

        Button searchButton = new Button("Search");
        searchButton.setStyle("-fx-background-color: #28A745; -fx-text-fill: white; -fx-padding: 5 10 5 10;");
        searchButton.setOnAction(e -> {
            String searchQuery = searchField.getText().trim().toLowerCase();
            if (!searchQuery.isEmpty()) {
                ObservableList<Product> filteredProducts = products.filtered(product ->
                        product.getName().toLowerCase().contains(searchQuery) ||
                                product.getCategory().toLowerCase().contains(searchQuery));
                productTable.setItems(filteredProducts);
            } else {
                productTable.setItems(products); // Reset to all products
            }
        });

        // Add Product Button
        Button addButton = new Button("Add Product");
        addButton.setStyle("-fx-background-color: #0078D7; -fx-text-fill: white; -fx-padding: 5 10 5 10;");
        addButton.setOnAction(e -> showAddProductDialog(null, products, productTable));

        // Top Control Bar
        HBox topBar = new HBox(10, searchField, searchButton, addButton);
        topBar.setPadding(new Insets(10, 10, 10, 10));
        topBar.setAlignment(Pos.CENTER_LEFT);

        // Main Layout
        VBox productsPage = new VBox(10, topBar, productTable);
        productsPage.setPadding(new Insets(10, 10, 10, 10));

        // Set the table to grow vertically
        VBox.setVgrow(productTable, Priority.ALWAYS);

        mainLayout.setCenter(productsPage);
    }
    private boolean deleteProductFromDatabase(Product product) {
        String query = "DELETE FROM products WHERE name = ? AND price = ? AND quantity = ? AND category = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, product.getName());
            pstmt.setDouble(2, product.getPrice());
            pstmt.setInt(3, product.getQuantity());
            pstmt.setString(4, product.getCategory());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0; // Return true if a row was deleted
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Return false if there was an error
        }
    }


    private void showAddProductDialog(Product productToEdit, ObservableList<Product> products, TableView<Product> productTable) {
        Stage dialog = new Stage();
        dialog.setTitle(productToEdit == null ? "Add New Product" : "Edit Product");

        // Form Fields with consistent sizes
        TextField nameField = new TextField(productToEdit == null ? "" : productToEdit.getName());
        nameField.setPrefWidth(250);

        TextField priceField = new TextField(productToEdit == null ? "" : String.valueOf(productToEdit.getPrice()));
        priceField.setPrefWidth(250);

        TextField quantityField = new TextField(productToEdit == null ? "" : String.valueOf(productToEdit.getQuantity()));
        quantityField.setPrefWidth(250);

        TextField categoryField = new TextField(productToEdit == null ? "" : productToEdit.getCategory());
        categoryField.setPrefWidth(250);

        // Save Button with Attractive Green Style
        Button saveButton = new Button("Save");
        saveButton.setPrefWidth(100);
        saveButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 8 15 8 15; -fx-border-radius: 5; -fx-background-radius: 5;");
        saveButton.setOnAction(e -> {
            try {
                String name = nameField.getText().trim();
                double price = Double.parseDouble(priceField.getText().trim());
                int quantity = Integer.parseInt(quantityField.getText().trim());
                String category = categoryField.getText().trim();

                if (productToEdit == null) {
                    // Add New Product
                    try (Connection conn = getConnection();
                         PreparedStatement pstmt = conn.prepareStatement(
                                 "INSERT INTO products (name, price, quantity, category) VALUES (?, ?, ?, ?)")) {
                        pstmt.setString(1, name);
                        pstmt.setDouble(2, price);
                        pstmt.setInt(3, quantity);
                        pstmt.setString(4, category);
                        pstmt.executeUpdate();

                        // Update TableView
                        products.add(new Product(name, price, quantity, category));
                    }
                } else {
                    // Edit Existing Product
                    try (Connection conn = getConnection();
                         PreparedStatement pstmt = conn.prepareStatement(
                                 "UPDATE products SET name = ?, price = ?, quantity = ?, category = ? WHERE name = ? AND price = ? AND quantity = ? AND category = ?")) {
                        pstmt.setString(1, name);
                        pstmt.setDouble(2, price);
                        pstmt.setInt(3, quantity);
                        pstmt.setString(4, category);
                        pstmt.setString(5, productToEdit.getName());
                        pstmt.setDouble(6, productToEdit.getPrice());
                        pstmt.setInt(7, productToEdit.getQuantity());
                        pstmt.setString(8, productToEdit.getCategory());
                        pstmt.executeUpdate();

                        // Update TableView
                        productToEdit.setName(name);
                        productToEdit.setPrice(price);
                        productToEdit.setQuantity(quantity);
                        productToEdit.setCategory(category);
                        productTable.refresh(); // Refresh the table to reflect the changes
                    }
                }

                dialog.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter valid numeric values for price and quantity.");
            }
        });

        // Layout with adjusted spacing
        VBox layout = new VBox(15, // Adjust spacing between elements
                new Label("Name:"), nameField,
                new Label("Price:"), priceField,
                new Label("Quantity:"), quantityField,
                new Label("Category:"), categoryField,
                saveButton
        );
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        // Set dialog dimensions
        Scene scene = new Scene(layout, 350, 400); // Adjust width and height
        dialog.setScene(scene);
        dialog.setResizable(false); // Prevent resizing
        dialog.initModality(Modality.APPLICATION_MODAL); // Make it modal
        dialog.showAndWait();
    }



/********************************************************************************************************/
/*                                                  *Orders*                                            */
/********************************************************************************************************/

private void showOrdersPage() {
    TableView<Order> orderTable = createOrderTable();
    ObservableList<Order> orders = FXCollections.observableArrayList();

    // Fetch orders from the database
    try (Connection conn = getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery("SELECT id, customer_name, order_date, total_amount FROM orders")) {
        while (rs.next()) {
            orders.add(new Order(
                    rs.getInt("id"),
                    rs.getString("customer_name"),
                    rs.getDate("order_date").toLocalDate(),
                    rs.getDouble("total_amount")
            ));
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }

    // Add Columns for TableView
    TableColumn<Order, Integer> idCol = new TableColumn<>("Order ID");
    idCol.setCellValueFactory(new PropertyValueFactory<>("orderId"));

    TableColumn<Order, String> customerNameCol = new TableColumn<>("Customer Name");
    customerNameCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));

    TableColumn<Order, LocalDate> orderDateCol = new TableColumn<>("Order Date");
    orderDateCol.setCellValueFactory(new PropertyValueFactory<>("orderDate"));

    TableColumn<Order, Double> totalAmountCol = new TableColumn<>("Total Amount");
    totalAmountCol.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));

    // Edit Button Column
    TableColumn<Order, Void> editCol = new TableColumn<>("Edit");
    editCol.setCellFactory(param -> new TableCell<Order, Void>() {
        private final Button editButton = new Button("Edit");

        {
            editButton.setStyle("-fx-background-color: #0078D7; -fx-text-fill: white;");
            editButton.setOnAction(e -> {
                Order selectedOrder = getTableView().getItems().get(getIndex());
                showEditOrderDialog(selectedOrder, orders);
            });
        }

        @Override
        protected void updateItem(Void item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setGraphic(null);
            } else {
                setGraphic(editButton);
            }
        }
    });

    // Delete Button Column
    TableColumn<Order, Void> deleteCol = new TableColumn<>("Delete");
    deleteCol.setCellFactory(param -> new TableCell<Order, Void>() {
        private final Button deleteButton = new Button("Delete");

        {
            deleteButton.setStyle("-fx-background-color: #DC3545; -fx-text-fill: white;");
            deleteButton.setOnAction(e -> {
                Order selectedOrder = getTableView().getItems().get(getIndex());
                if (deleteOrderFromDatabase(selectedOrder)) {
                    orders.remove(selectedOrder); // Update TableView after deletion
                    orderTable.refresh(); // Refresh the TableView
                }
            });
        }

        @Override
        protected void updateItem(Void item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setGraphic(null);
            } else {
                setGraphic(deleteButton);
            }
        }
    });

    // Add Columns to TableView
    orderTable.getColumns().addAll(idCol, customerNameCol, orderDateCol, totalAmountCol, editCol, deleteCol);
    orderTable.setItems(orders);

    // Search Field and Button
    TextField searchField = new TextField();
    searchField.setPromptText("Search Orders");
    searchField.setStyle("-fx-font-size: 14px; -fx-pref-width: 300px;");

    Button searchButton = new Button("Search");
    searchButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-size: 14px;");
    searchButton.setOnAction(e -> {
        String query = searchField.getText().trim();
        if (query.isEmpty()) {
            orderTable.setItems(orders); // Reset table if query is empty
        } else {
            ObservableList<Order> filteredOrders = FXCollections.observableArrayList();
            for (Order order : orders) {
                if (String.valueOf(order.getOrderId()).contains(query) ||
                        order.getCustomerName().toLowerCase().contains(query.toLowerCase())) {
                    filteredOrders.add(order);
                }
            }
            orderTable.setItems(filteredOrders);
        }
    });

    // Add Order Button
    Button addOrderButton = new Button("Add Order");
    addOrderButton.setStyle("-fx-background-color: #0078D7; -fx-text-fill: white; -fx-font-size: 14px;");
    addOrderButton.setOnAction(e -> {
        Order newOrder = showAddOrderDialog(orders);
        if (newOrder != null) {
            orders.add(newOrder); // Add the new order to the observable list
            orderTable.refresh(); // Refresh the table to recognize the new item
        }
    });

    // Top Bar Layout
    HBox topBar = new HBox(10, searchField, searchButton, addOrderButton);
    topBar.setAlignment(Pos.CENTER_LEFT);
    topBar.setPadding(new Insets(10));

    // Ensure TableView Resizes Correctly
    VBox ordersPage = new VBox(10, topBar, orderTable);
    ordersPage.setPadding(new Insets(10));
    VBox.setVgrow(orderTable, Priority.ALWAYS); // Ensures the table expands to fill available space

    mainLayout.setCenter(ordersPage);
}


/************************          Helper Methods for orders          **************************/

private void showEditOrderDialog(Order order, ObservableList<Order> orders) {
    Stage dialog = new Stage();
    dialog.setTitle("Edit Order");
    dialog.setResizable(false);

    // Fields for editing order details
    TextField customerNameField = new TextField(order.getCustomerName());
    customerNameField.setPromptText("Enter Customer Name");
    customerNameField.setStyle("-fx-font-size: 14px;");

    DatePicker orderDatePicker = new DatePicker(order.getOrderDate());
    orderDatePicker.setPromptText("Select Order Date");
    orderDatePicker.setStyle("-fx-font-size: 14px;");
    orderDatePicker.setPrefWidth(300); // Ensure same width as other fields

    TextField totalAmountField = new TextField(String.valueOf(order.getTotalAmount()));
    totalAmountField.setPromptText("Enter Total Amount");
    totalAmountField.setStyle("-fx-font-size: 14px;");

    Button saveButton = new Button("Save");
    saveButton.setStyle("-fx-background-color: #0078D7; -fx-text-fill: white; -fx-font-size: 14px; -fx-pref-width: 100px;");
    saveButton.setOnAction(e -> {
        String customerName = customerNameField.getText();
        LocalDate orderDate = orderDatePicker.getValue();
        double totalAmount;

        // Validate total amount input
        try {
            totalAmount = Double.parseDouble(totalAmountField.getText());
        } catch (NumberFormatException ex) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Total amount must be a valid number.");
            return;
        }

        // Update the database
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "UPDATE orders SET customer_name = ?, order_date = ?, total_amount = ? WHERE id = ?")) {
            pstmt.setString(1, customerName);
            pstmt.setDate(2, Date.valueOf(orderDate));
            pstmt.setDouble(3, totalAmount);
            pstmt.setInt(4, order.getOrderId());
            pstmt.executeUpdate();

            // Update the TableView
            order.setCustomerName(customerName);
            order.setOrderDate(orderDate);
            order.setTotalAmount(totalAmount);
            orders.set(orders.indexOf(order), order);

            showAlert(Alert.AlertType.INFORMATION, "Success", "Order updated successfully!");
            dialog.close();
        } catch (SQLException ex) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to update order.");
            ex.printStackTrace();
        }
    });

    // Labels with bold styling
    Label customerNameLabel = new Label("Customer Name:");
    customerNameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

    Label orderDateLabel = new Label("Order Date:");
    orderDateLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

    Label totalAmountLabel = new Label("Total Amount:");
    totalAmountLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

    // Layout for the dialog
    VBox layout = new VBox(15,
            customerNameLabel, customerNameField,
            orderDateLabel, orderDatePicker,
            totalAmountLabel, totalAmountField,
            saveButton
    );
    layout.setAlignment(Pos.CENTER);
    layout.setPadding(new Insets(20));
    layout.setStyle("-fx-background-color: #f4f4f4; -fx-border-color: #dcdcdc; -fx-border-radius: 5; -fx-border-width: 1;");

    // Scene and stage
    Scene scene = new Scene(layout, 350, 400);
    dialog.setScene(scene);
    dialog.show();
}

    private boolean deleteOrderFromDatabase(Order order) {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM orders WHERE id = ?")) {
            pstmt.setInt(1, order.getOrderId());
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0; // Return true if a row was deleted
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Return false if the operation failed
    }

    private Order showAddOrderDialog(ObservableList<Order> orders) {
        Stage dialog = new Stage();
        dialog.setTitle("Add New Order");
        dialog.setResizable(false);

        // Fields for entering new order details
        TextField customerNameField = new TextField();
        customerNameField.setPromptText("Enter Customer Name");
        customerNameField.setStyle("-fx-font-size: 14px;");

        DatePicker orderDatePicker = new DatePicker();
        orderDatePicker.setPromptText("Select Order Date");
        orderDatePicker.setStyle("-fx-font-size: 14px;");
        orderDatePicker.setPrefWidth(300); // Set the same width as other fields

        TextField totalAmountField = new TextField();
        totalAmountField.setPromptText("Enter Total Amount");
        totalAmountField.setStyle("-fx-font-size: 14px;");

        Button saveButton = new Button("Save");
        saveButton.setStyle("-fx-background-color: #0078D7; -fx-text-fill: white; -fx-font-size: 14px; -fx-pref-width: 100px;");
        final Order[] newOrder = {null}; // Use an array to store the created order

        saveButton.setOnAction(e -> {
            String customerName = customerNameField.getText();
            LocalDate orderDate = orderDatePicker.getValue();
            double totalAmount;

            // Validate total amount input
            try {
                totalAmount = Double.parseDouble(totalAmountField.getText());
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input", "Total amount must be a valid number.");
                return;
            }

            // Insert into the database
            try (Connection conn = getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(
                         "INSERT INTO orders (customer_name, order_date, total_amount) VALUES (?, ?, ?)",
                         Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, customerName);
                pstmt.setDate(2, Date.valueOf(orderDate));
                pstmt.setDouble(3, totalAmount);
                pstmt.executeUpdate();

                // Get the generated order ID
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int orderId = generatedKeys.getInt(1);
                    newOrder[0] = new Order(orderId, customerName, orderDate, totalAmount);
                }

                showAlert(Alert.AlertType.INFORMATION, "Success", "Order added successfully!");
                dialog.close();
            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to add order.");
                ex.printStackTrace();
            }
        });

        // Labels with bold styling
        Label customerNameLabel = new Label("Customer Name:");
        customerNameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Label orderDateLabel = new Label("Order Date:");
        orderDateLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Label totalAmountLabel = new Label("Total Amount:");
        totalAmountLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        // Layout for the dialog
        VBox layout = new VBox(15,
                customerNameLabel, customerNameField,
                orderDateLabel, orderDatePicker,
                totalAmountLabel, totalAmountField,
                saveButton
        );
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #f4f4f4; -fx-border-color: #dcdcdc; -fx-border-radius: 5; -fx-border-width: 1;");

        // Scene and stage
        Scene scene = new Scene(layout, 350, 400);
        dialog.setScene(scene);
        dialog.showAndWait();

        return newOrder[0]; // Return the created order, or null if creation failed
    }
/********************************************************************************************************/
/*                                                  *customer*                                          */
/********************************************************************************************************/
// Fetch customers from the database
private void showCustomersPage() {
    TableView<Customer> customerTable = createCustomerTable();
    ObservableList<Customer> customers = FXCollections.observableArrayList();

    // Fetch data from the database
    try (Connection conn = getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery("SELECT id, name, email, username, orders_count, status FROM customers")) {

        while (rs.next()) {
            customers.add(new Customer(
                    rs.getInt("id"),               // ID
                    rs.getString("name"),          // Name
                    rs.getString("email"),         // Email
                    rs.getString("username"),      // Username
                    rs.getInt("orders_count"),     // Orders Count
                    rs.getString("status")         // Status
            ));
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }

    customerTable.setItems(customers);

    // Search Field
    TextField searchField = new TextField();
    searchField.setPromptText("Search Customers");
    searchField.setStyle("-fx-font-size: 14px; -fx-padding: 5px; -fx-border-radius: 5px; -fx-pref-width: 300px;");

    // Search Button
    Button searchButton = new Button("Search");
    searchButton.setStyle("-fx-background-color: #0078D7; -fx-text-fill: white; -fx-font-size: 14px; -fx-pref-width: 80px;");
    searchButton.setOnAction(e -> {
        String query = searchField.getText().toLowerCase().trim();
        if (query.isEmpty()) {
            customerTable.setItems(customers);
        } else {
            ObservableList<Customer> filteredCustomers = FXCollections.observableArrayList();
            for (Customer customer : customers) {
                if (customer.getName().toLowerCase().contains(query) ||
                        customer.getEmail().toLowerCase().contains(query) ||
                        customer.getUsername().toLowerCase().contains(query)) {
                    filteredCustomers.add(customer);
                }
            }
            customerTable.setItems(filteredCustomers);
        }
    });

    // Add Customer Button
    Button addCustomerButton = new Button("Add Customer");
    addCustomerButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-size: 14px; -fx-pref-width: 120px;");
    addCustomerButton.setOnAction(e -> showAddCustomerDialog(customers));

    // Add Top Bar (Search + Add Customer)
    HBox topBar = new HBox(10, searchField, searchButton, addCustomerButton);
    topBar.setAlignment(Pos.CENTER_LEFT);
    topBar.setPadding(new Insets(10, 10, 10, 10));
    HBox.setHgrow(searchField, Priority.ALWAYS);

    // Add Buttons Column
    addCustomerButtonsColumn(customerTable, customers);

    // Spacer below the table
    Region spacer = new Region();
    spacer.setMinHeight(10);

    // Main Layout: Combine Top Bar, Table, and Spacer
    VBox customersPage = new VBox(10, topBar, customerTable, spacer);
    customersPage.setPadding(new Insets(10));
    VBox.setVgrow(customerTable, Priority.ALWAYS);
    VBox.setVgrow(spacer, Priority.NEVER);

    // Set Customers Page in Main Layout
    mainLayout.setCenter(customersPage);
}

    // Add Edit and Delete Buttons to Table
    private void addCustomerButtonsColumn(TableView<Customer> customerTable, ObservableList<Customer> customers) {
        // Edit Button Column
        TableColumn<Customer, Void> editColumn = new TableColumn<>("Edit");
        editColumn.setCellFactory(param -> new TableCell<Customer, Void>() {
            private final Button editButton = new Button("Edit");

            {
                editButton.setStyle("-fx-background-color: #0078D7; -fx-text-fill: white;");
                editButton.setOnAction(event -> {
                    Customer selectedCustomer = getTableView().getItems().get(getIndex());
                    showEditCustomerDialog(selectedCustomer, customers);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(editButton);
                }
            }
        });

        // Delete Button Column
        TableColumn<Customer, Void> deleteColumn = new TableColumn<>("Delete");
        deleteColumn.setCellFactory(param -> new TableCell<Customer, Void>() {
            private final Button deleteButton = new Button("Delete");

            {
                deleteButton.setStyle("-fx-background-color: #D32F2F; -fx-text-fill: white;");
                deleteButton.setOnAction(event -> {
                    Customer selectedCustomer = getTableView().getItems().get(getIndex());
                    boolean confirmed = showConfirmationDialog("Delete Customer", "Are you sure you want to delete this customer?");
                    if (confirmed) {
                        deleteCustomerFromDatabase(selectedCustomer);
                        customers.remove(selectedCustomer);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                }
            }
        });

        // Add columns to the table
        customerTable.getColumns().addAll(editColumn, deleteColumn);
    }
    private TableView<Customer> customerTable; // Declare it as a class field

    private void showEditCustomerDialog(Customer customer, ObservableList<Customer> customers) {
        // Create a new dialog stage
        Stage dialog = new Stage();
        dialog.setTitle("Edit Customer");
        dialog.setResizable(false);

        // Text fields for editing customer details
        TextField nameField = new TextField(customer.getName());
        nameField.setPromptText("Enter Name");
        nameField.setStyle("-fx-font-size: 14px;");

        TextField emailField = new TextField(customer.getEmail());
        emailField.setPromptText("Enter Email");
        emailField.setStyle("-fx-font-size: 14px;");

        TextField usernameField = new TextField(customer.getUsername());
        usernameField.setPromptText("Enter Username");
        usernameField.setStyle("-fx-font-size: 14px;");

        TextField ordersCountField = new TextField(String.valueOf(customer.getOrdersCount()));
        ordersCountField.setPromptText("Enter Orders Count");
        ordersCountField.setStyle("-fx-font-size: 14px;");

        TextField statusField = new TextField(customer.getStatus());
        statusField.setPromptText("Enter Status");
        statusField.setStyle("-fx-font-size: 14px;");

        // Save button
        Button saveButton = new Button("Save");
        saveButton.setStyle("-fx-background-color: #0078D7; -fx-text-fill: white; -fx-font-size: 14px; -fx-pref-width: 100px;");
        saveButton.setOnAction(e -> {
            // Get field values
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String username = usernameField.getText().trim();
            String status = statusField.getText().trim();
            int ordersCount;

            // Validate Orders Count input
            try {
                ordersCount = Integer.parseInt(ordersCountField.getText().trim());
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Orders Count must be a valid number.");
                return;
            }

            // Validate fields are not empty
            if (name.isEmpty() || email.isEmpty() || username.isEmpty() || status.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "All fields must be filled.");
                return;
            }

            // Update the customer in the database
            try (Connection conn = getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(
                         "UPDATE customers SET name = ?, email = ?, username = ?, orders_count = ?, status = ? WHERE id = ?")) {

                pstmt.setString(1, name);
                pstmt.setString(2, email);
                pstmt.setString(3, username);
                pstmt.setInt(4, ordersCount);
                pstmt.setString(5, status);
                pstmt.setInt(6, customer.getId());

                int rowsAffected = pstmt.executeUpdate();

                if (rowsAffected == 0) {
                    showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to update customer in the database.");
                    return;
                }

                // Update the customer object and the ObservableList
                customer.setName(name);
                customer.setEmail(email);
                customer.setUsername(username);
                customer.setOrdersCount(ordersCount);
                customer.setStatus(status);
                customers.set(customers.indexOf(customer), customer);

                showAlert(Alert.AlertType.INFORMATION, "Success", "Customer updated successfully!");
                dialog.close();

            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "Database Error", "An error occurred while updating the database: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        // Labels with bold styling
        Label nameLabel = new Label("Name:");
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Label emailLabel = new Label("Email:");
        emailLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Label usernameLabel = new Label("Username:");
        usernameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Label ordersCountLabel = new Label("Orders Count:");
        ordersCountLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Label statusLabel = new Label("Status:");
        statusLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        // Layout for the dialog
        VBox layout = new VBox(15,
                nameLabel, nameField,
                emailLabel, emailField,
                usernameLabel, usernameField,
                ordersCountLabel, ordersCountField,
                statusLabel, statusField,
                saveButton
        );
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #f4f4f4; -fx-border-color: #dcdcdc; -fx-border-radius: 5; -fx-border-width: 1;");

        // Create and show the dialog scene
        Scene scene = new Scene(layout, 350, 450);
        dialog.setScene(scene);
        dialog.show();
    }


    // Delete Customer from Database
    private void deleteCustomerFromDatabase(Customer customer) {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM customers WHERE id = ?")) {
            pstmt.setInt(1, customer.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private boolean showConfirmationDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        // Wait for the user's response
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }



    private void showAddCustomerDialog(ObservableList<Customer> customers) {
        Stage dialog = new Stage();
        dialog.setTitle("Add New Customer");

        // Labels and Fields
        Label nameLabel = new Label("Name:");
        TextField nameField = createStyledTextField("Enter customer name");

        Label emailLabel = new Label("Email:");
        TextField emailField = createStyledTextField("Enter customer email");

        Label usernameLabel = new Label("Username:");
        TextField usernameField = createStyledTextField("Enter customer username");

        Label ordersCountLabel = new Label("Orders Count:");
        TextField ordersCountField = createStyledTextField("Enter orders count");

        Label statusLabel = new Label("Status:");
        TextField statusField = createStyledTextField("Enter customer status");

        // Save Button (Made larger)
        Button saveButton = createStyledButton("Save");
        saveButton.setStyle(
                "-fx-background-color: #0078D7; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 18px; " + // Increased font size
                        "-fx-padding: 15px 30px; " + // Increased padding for a larger button
                        "-fx-border-radius: 10px; " +
                        "-fx-background-radius: 10px;"
        );
        saveButton.setOnMouseEntered(e -> saveButton.setStyle(
                "-fx-background-color: #005A9E; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 18px; " +
                        "-fx-padding: 15px 30px; " +
                        "-fx-border-radius: 10px; " +
                        "-fx-background-radius: 10px;"
        ));
        saveButton.setOnMouseExited(e -> saveButton.setStyle(
                "-fx-background-color: #0078D7; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 18px; " +
                        "-fx-padding: 15px 30px; " +
                        "-fx-border-radius: 10px; " +
                        "-fx-background-radius: 10px;"
        ));
        saveButton.setOnAction(e -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String username = usernameField.getText().trim();
            String ordersCountText = ordersCountField.getText().trim();
            String status = statusField.getText().trim();

            // Validate inputs
            if (name.isEmpty() || email.isEmpty() || username.isEmpty() || ordersCountText.isEmpty() || status.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "All fields must be filled!");
                return;
            }

            try {
                int ordersCount = Integer.parseInt(ordersCountText);

                // Save to the database
                try (Connection conn = getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(
                             "INSERT INTO customers (name, email, username, orders_count, status) VALUES (?, ?, ?, ?, ?)")) {
                    pstmt.setString(1, name);
                    pstmt.setString(2, email);
                    pstmt.setString(3, username);
                    pstmt.setInt(4, ordersCount);
                    pstmt.setString(5, status);
                    pstmt.executeUpdate();
                }

                // Update table and close dialog
                customers.add(new Customer(0, name, email, username, ordersCount, status)); // ID set to 0 for simplicity
                showAlert(Alert.AlertType.INFORMATION, "Success", "Customer added successfully!");
                dialog.close();
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Orders Count must be a number!");
            } catch (SQLException ex) {
                ex.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to save customer!");
            }
        });

        // Form layout
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(15);
        form.setPadding(new Insets(20));

        // Add labels and fields to the layout
        form.add(nameLabel, 0, 0);
        form.add(nameField, 1, 0);

        form.add(emailLabel, 0, 1);
        form.add(emailField, 1, 1);

        form.add(usernameLabel, 0, 2);
        form.add(usernameField, 1, 2);

        form.add(ordersCountLabel, 0, 3);
        form.add(ordersCountField, 1, 3);

        form.add(statusLabel, 0, 4);
        form.add(statusField, 1, 4);

        // Add Save Button to the center
        HBox buttonBox = new HBox(saveButton);
        buttonBox.setAlignment(Pos.CENTER);
        form.add(buttonBox, 0, 5, 2, 1);

        Scene scene = new Scene(form, 450, 400);
        dialog.setScene(scene);
        dialog.show();
    }


    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }



    private <T> HBox createSearchBar(String placeholder, TableView<T> table, ObservableList<T> originalData, String... filterableProperties) {
        TextField searchField = createStyledTextField(placeholder);
        Button searchButton = createStyledButton("Search");

        searchButton.setOnAction(event -> {
            String searchTerm = searchField.getText().trim().toLowerCase();

            if (searchTerm.isEmpty()) {
                table.setItems(originalData);
            } else {
                ObservableList<T> filteredList = FXCollections.observableArrayList();

                for (T item : originalData) {
                    for (String property : filterableProperties) {
                        try {
                            // Use reflection to dynamically get property values
                            String value = item.getClass().getMethod("get" + capitalize(property)).invoke(item).toString().toLowerCase();
                            if (value.contains(searchTerm)) {
                                filteredList.add(item);
                                break; // Match found, move to the next item
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                table.setItems(filteredList);
            }
        });

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                table.setItems(originalData);
            }
        });

        HBox searchBox = new HBox(10, searchField, searchButton);
        searchBox.setAlignment(Pos.CENTER);

        return searchBox;
    }


    private String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    private TableView<Product> createProductTable() {
        TableView<Product> table = new TableView<>();

        table.getColumns().addAll(
                createColumn("Name", "name"),
                createColumn("Price", "price"),
                createColumn("Quantity", "quantity"),
                createColumn("Category", "category")
        );
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        return table;
    }

    private TableView<Order> createOrderTable() {
        TableView<Order> table = new TableView<>();

        table.getColumns().addAll(
                createColumn("Order ID", "orderId"),
                createColumn("Customer Name", "customerName"),
                createColumn("Order Date", "orderDate"),
                createColumn("Total Amount", "totalAmount")
        );
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        return table;
    }

    private TableView<Customer> createCustomerTable() {
        TableView<Customer> table = new TableView<>();

        table.getColumns().addAll(
                createColumn("Name", "name"),
                createColumn("Email", "email"),
                createColumn("Username", "username"),
                createColumn("Orders", "ordersCount"),
                createColumn("Status", "status")
        );
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        return table;
    }

    private <T> TableColumn<T, ?> createColumn(String title, String property) {
        TableColumn<T, Object> column = new TableColumn<>(title);
        column.setCellValueFactory(new PropertyValueFactory<>(property));
        return column;
    }

    private TextField createStyledTextField(String placeholder) {
        TextField textField = new TextField();
        textField.setPromptText(placeholder);
        textField.setStyle("-fx-background-radius: 15; -fx-padding: 10;");
        return textField;
    }

    private PasswordField createStyledPasswordField(String placeholder) {
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText(placeholder);
        passwordField.setStyle("-fx-background-radius: 15; -fx-padding: 10;");
        return passwordField;
    }

    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        button.setTextFill(Color.WHITE);
        button.setStyle("-fx-background-color: #2980b9; -fx-background-radius: 15;");
        return button;
    }

    private Button createStyledSignOutButton(String text) {
        Button button = new Button(text);
        button.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        button.setTextFill(Color.WHITE);
        button.setStyle("-fx-background-color: #c0392b; -fx-background-radius: 15;");
        return button;
    }

    private void styleLabel(Label label, int fontSize, String color, boolean bold) {
        label.setFont(Font.font("Arial", bold ? FontWeight.BOLD : FontWeight.NORMAL, fontSize));
        label.setTextFill(Color.web(color));
    }

    public static class Product {
        private String name;
        private double price;
        private int quantity;
        private String category;

        // Constructor
        public Product(String name, double price, int quantity, String category) {
            this.name = name;
            this.price = price;
            this.quantity = quantity;
            this.category = category;
        }

        // Getters
        public String getName() {
            return name;
        }

        public double getPrice() {
            return price;
        }

        public int getQuantity() {
            return quantity;
        }

        public String getCategory() {
            return category;
        }

        // Setters
        public void setName(String name) {
            this.name = name;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public void setCategory(String category) {
            this.category = category;
        }
    }
    public static class Order {
        private int orderId;
        private String customerName;
        private LocalDate orderDate;
        private double totalAmount;

        public Order(int orderId, String customerName, LocalDate orderDate, double totalAmount) {
            this.orderId = orderId;
            this.customerName = customerName;
            this.orderDate = orderDate;
            this.totalAmount = totalAmount;
        }

        public int getOrderId() {
            return orderId;
        }

        public void setOrderId(int orderId) {
            this.orderId = orderId;
        }

        public String getCustomerName() {
            return customerName;
        }

        public void setCustomerName(String customerName) {
            this.customerName = customerName;
        }

        public LocalDate getOrderDate() {
            return orderDate;
        }

        public void setOrderDate(LocalDate orderDate) {
            this.orderDate = orderDate;
        }

        public double getTotalAmount() {
            return totalAmount;
        }

        public void setTotalAmount(double totalAmount) {
            this.totalAmount = totalAmount;
        }
    }


    public class Customer {
        private int id;
        private String name;
        private String email;
        private String username;
        private int ordersCount;
        private String status;

        // Constructor
        public Customer(int id, String name, String email, String username, int ordersCount, String status) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.username = username;
            this.ordersCount = ordersCount;
            this.status = status;
        }

        // Getters and Setters
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public int getOrdersCount() {
            return ordersCount;
        }

        public void setOrdersCount(int ordersCount) {
            this.ordersCount = ordersCount;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}
