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
import javafx.stage.Stage;
import java.sql.*;
import java.time.LocalDate;

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
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/7656399.png")));

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
        for (Button button : new Button[]{homeButton, productsButton, ordersButton, customersButton}) {
            button.setMaxWidth(Double.MAX_VALUE);
            button.setStyle("-fx-background-color: #2c3e50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10;");
            button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #34495e; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10;"));
            button.setOnMouseExited(e -> button.setStyle("-fx-background-color: #2c3e50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10;"));
        }

        VBox sidebar = new VBox(15, homeButton, productsButton, ordersButton, customersButton, signOutButton);
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
        String url = "jdbc:sqlserver://yourhost;databaseName=store_db;integratedSecurity=true;trustServerCertificate=true;";
        return DriverManager.getConnection(url);
    }
    // Update the `showProductsPage` method
    private void showProductsPage() {
        TableView<Product> productTable = createProductTable();
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

        HBox searchBar = createSearchBar("Search Products", productTable, products, "name", "category");
        VBox productsPage = new VBox(10, searchBar, productTable);
        productsPage.setPadding(new Insets(10));

        mainLayout.setCenter(productsPage);
    }
    // Fetch orders from the database
    private void showOrdersPage() {
        TableView<Order> orderTable = createOrderTable();
        ObservableList<Order> orders = FXCollections.observableArrayList();

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

        orderTable.setItems(orders);

        HBox searchBar = createSearchBar("Search Orders", orderTable, orders, "customerName");
        VBox ordersPage = new VBox(10, searchBar, orderTable);
        ordersPage.setPadding(new Insets(10));

        mainLayout.setCenter(ordersPage);
    }
    // Fetch customers from the database
    private void showCustomersPage() {
        TableView<Customer> customerTable = createCustomerTable();
        ObservableList<Customer> customers = FXCollections.observableArrayList();

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT name, email, username, orders_count, status FROM customers")) {
            while (rs.next()) {
                customers.add(new Customer(
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("username"),
                        rs.getInt("orders_count"),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        customerTable.setItems(customers);

        HBox searchBar = createSearchBar("Search Customers", customerTable, customers, "name", "email", "username");
        VBox customersPage = new VBox(10, searchBar, customerTable);
        customersPage.setPadding(new Insets(10));

        mainLayout.setCenter(customersPage);
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

        public Product(String name, double price, int quantity, String category) {
            this.name = name;
            this.price = price;
            this.quantity = quantity;
            this.category = category;
        }

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

        public String getCustomerName() {
            return customerName;
        }

        public LocalDate getOrderDate() {
            return orderDate;
        }

        public double getTotalAmount() {
            return totalAmount;
        }
    }

    public static class Customer {
        private String name;
        private String email;
        private String username;
        private int ordersCount;
        private String status;

        public Customer(String name, String email, String username, int ordersCount, String status) {
            this.name = name;
            this.email = email;
            this.username = username;
            this.ordersCount = ordersCount;
            this.status = status;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }

        public String getUsername() {
            return username;
        }

        public int getOrdersCount() {
            return ordersCount;
        }

        public String getStatus() {
            return status;
        }
    }
}
