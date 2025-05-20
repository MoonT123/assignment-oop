package operation;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import model.Order;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;




public class OrderOperation {
    private static final String FILE_PATH = "data/orders.txt";

    // Singleton instance
    private static OrderOperation instance;

    private OrderOperation() {}

    // 2.10.1 - Get Singleton instance
    /**
     * Returns the single instance of OrderOperation.
     * @return The OrderOperation instance.
     */
    public static OrderOperation getInstance() {
        if (instance == null) {
            instance = new OrderOperation();
        }
        return instance;
    }

    // 2.10.2 - Generate Unique Order ID
    /**
     * Generates a unique order ID.
     * The ID format will be like "o_00001".
     * @return A unique order ID.
     */
    public String generateUniqueOrderId() {
        String orderId = "o_" + String.format("%05d", (int) (Math.random() * 99999 + 1));
        return orderId;
    }

    // 2.10.3 - Create an Order
    /**
     * Creates a new order and saves it to the orders.txt file.
     * @param customerId The customer ID who is placing the order.
     * @param productId The product ID being ordered.
     * @param createTime The time of order creation. If null, the current time is used.
     * @return true if the order is created successfully, false otherwise.
     */
    public boolean createAnOrder(String customerId, String productId, String createTime) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("data/orders.txt", true));
            String orderId = generateUniqueOrderId();
            String orderTime = createTime != null ? createTime : new SimpleDateFormat("dd-MM-yyyy_HH:mm:ss").format(new Date());
            String orderData = orderId + "," + customerId + "," + productId + "," + orderTime;
            writer.write(orderData);
            writer.newLine();
            writer.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 2.10.4 - Delete an Order
    /**
     * Deletes an order from orders.txt based on the order ID.
     * @param orderId The ID of the order to delete.
     * @return true if the order was deleted successfully, false otherwise.
     */
    public boolean deleteOrder(String orderId) {
        try {
            File file = new File("data/orders.txt");
            BufferedReader reader = new BufferedReader(new FileReader(file));
            StringBuilder fileContent = new StringBuilder();
            String line;

            // Read all lines except the one that matches the orderId
            while ((line = reader.readLine()) != null) {
                if (!line.startsWith(orderId)) {
                    fileContent.append(line).append("\n");
                }
            }

            reader.close();
            // Rewrite the file with the updated content
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(fileContent.toString());
            writer.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
      // 2.10.5 - Get List of Orders by Customer ID and Page Number
    /**
     * Retrieves a page of orders for a specific customer.
     * Each page contains a maximum of 10 orders.
     * @param customerId The customer ID whose orders are being retrieved.
     * @param pageNumber The page number to retrieve.
     * @return A list of orders, current page number, and total pages.
     */
    public List<String> getOrderList(String customerId, int pageNumber) {
        List<String> orderList = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader("data/orders.txt"));
            String line;
            int count = 0;
            int startIndex = (pageNumber - 1) * 10;
            int endIndex = startIndex + 10;

            // Read orders from the file
            while ((line = reader.readLine()) != null) {
                if (line.contains(customerId)) {
                    if (count >= startIndex && count < endIndex) {
                        orderList.add(line);
                    }
                    count++;
                }
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return orderList;
    }

    // 2.10.6 - Generate Test Order Data
    /**
     * Generates test data for orders and customers.
     * Creates 10 customers, each having between 50 and 200 orders.
     * The order times are distributed across different months of the year.
     */
    public void generateTestOrderData() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("data/orders.txt", true));

            for (int customerIndex = 1; customerIndex <= 10; customerIndex++) {
                String customerId = "u_00000000" + customerIndex;
                int orderCount = (int) (Math.random() * 150) + 50; // Random between 50 and 200 orders

                for (int i = 0; i < orderCount; i++) {
                    String productId = "p" + String.format("%03d", (int) (Math.random() * 20 + 1)); // Random product ID
                    String orderTime = generateRandomOrderTime();
                    createAnOrder(customerId, productId, orderTime);
                }
            }

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Helper method to generate random order time
    private String generateRandomOrderTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2024, (int) (Math.random() * 12), (int) (Math.random() * 28) + 1, 
                (int) (Math.random() * 24), (int) (Math.random() * 60), (int) (Math.random() * 60));
        return new SimpleDateFormat("dd-MM-yyyy_HH:mm:ss").format(calendar.getTime());
    }

    /**
     * Generates a chart showing the consumption (sum of order prices)
     * across 12 different months for the given customer.
     * @param customerId The ID of the customer
     */
    public void generateSingleCustomerConsumptionFigure(String customerId) {
        // Lấy danh sách các đơn hàng của khách hàng
        List<Order> orders = getOrdersByCustomerId(customerId);

        // Tính tổng chi tiêu cho từng tháng (từ tháng 1 đến tháng 12)
        double[] monthlyConsumption = new double[12];

        for (Order order : orders) {
            String orderTime = order.getOrderTime();
            double orderPrice = order.getOrderPrice();
            int month = getMonthFromOrderTime(orderTime);  // Lấy tháng từ thời gian của đơn hàng
            monthlyConsumption[month - 1] += orderPrice;  // Cộng dồn chi tiêu cho tháng đó
        }

        // Tạo trục X và trục Y cho biểu đồ
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Month");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Total Consumption");

        // Tạo BarChart để hiển thị dữ liệu
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Customer Consumption Over 12 Months");

        // Tạo series dữ liệu cho biểu đồ
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Consumption");

        // Thêm dữ liệu vào series (mỗi tháng và tổng chi tiêu của khách hàng)
        for (int i = 0; i < 12; i++) {
            series.getData().add(new XYChart.Data<>(getMonthName(i + 1), monthlyConsumption[i]));
        }

        // Thêm series vào biểu đồ
        barChart.getData().add(series);

        // Hiển thị biểu đồ
        showChart(barChart);
    }

    /**
     * Lấy danh sách các đơn hàng của một khách hàng từ tệp orders.txt
     * @param customerId The ID of the customer
     * @return A list of orders made by the customer
     */
    private List<Order> getOrdersByCustomerId(String customerId) {
        List<Order> orders = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                String orderId = data[0];
                String orderCustomerId = data[1];
                String productId = data[2];
                String orderTime = data[3];
                double orderPrice = getOrderPriceByProductId(productId);  // Giả sử bạn có phương thức để lấy giá sản phẩm
                if (orderCustomerId.equals(customerId)) {
                    orders.add(new Order(orderId, orderCustomerId, productId, orderTime, orderPrice));
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return orders;
    }

    /**
     * Giả sử phương thức này trả về giá của sản phẩm dựa trên ID sản phẩm.
     * @param productId The product ID
     * @return The price of the product
     */
    private double getOrderPriceByProductId(String productId) {
        // Cần phải thực hiện tìm giá sản phẩm từ cơ sở dữ liệu hoặc tệp
        return Math.random() * 1000;  // Giả lập giá sản phẩm
    }

    /**
     * Lấy tháng từ thời gian của đơn hàng (dưới định dạng "dd-MM-yyyy_HH:mm:ss")
     * @param orderTime The order's time string
     * @return The month extracted from the order time
     */
    private int getMonthFromOrderTime(String orderTime) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy_HH:mm:ss");
            Date date = sdf.parse(orderTime);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            return cal.get(Calendar.MONTH) + 1;  // Tháng (1-12)
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Trả về tên tháng từ số tháng (1-12)
     * @param month The month number (1-12)
     * @return The name of the month
     */
    private String getMonthName(int month) {
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        return months[month - 1];
    }

    /**
     * Hiển thị biểu đồ trên màn hình
     * @param chart The chart to be displayed
     */
    private void showChart(BarChart<String, Number> chart) {
        Scene scene = new Scene(chart, 800, 600);
        Stage stage = new Stage();
        stage.setTitle("Customer Consumption Chart");
        stage.setScene(scene);
        stage.show();
    }

  public void generateAllCustomersConsumptionFigure() {
        // Get all orders from the orders.txt file
        List<Order> orders = getAllOrders();

        // Calculate total consumption for each month (from month 1 to month 12)
        double[] monthlyConsumption = new double[12];

        // Iterate through all orders and calculate monthly consumption
        for (Order order : orders) {
            String orderTime = order.getOrderTime();
            double orderPrice = order.getOrderPrice();  // Get order price
            int month = getMonthFromOrderTime(orderTime);  // Get month from order time
            monthlyConsumption[month - 1] += orderPrice;  // Add to the respective month's consumption
        }

        // Create X and Y axes for the chart
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Month");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Total Consumption");

        // Create BarChart to display the data
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("All Customers' Consumption Over 12 Months");

        // Create series data for the chart
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Total Consumption");

        // Add data to the series (each month and total consumption for all customers)
        for (int i = 0; i < 12; i++) {
            series.getData().add(new XYChart.Data<>(getMonthName(i + 1), monthlyConsumption[i]));
        }

        // Add series to the chart
        barChart.getData().add(series);

        // Display the chart
        showChart(barChart);
    }

    /**
     * Retrieves all orders from orders.txt file.
     * @return A list of all orders.
     */
    private List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                String orderId = data[0];
                String customerId = data[1];
                String productId = data[2];
                String orderTime = data[3];
                double orderPrice = getOrderPriceByProductId(productId);  // Assume a method to get product price
                orders.add(new Order(orderId, customerId, productId, orderTime, orderPrice));
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return orders;
    }
  public void generateAllTop10BestSellersFigure() {
        // Get all orders from orders.txt
        List<Order> orders = getAllOrders();

        // Map to store the total quantity sold for each product
        Map<String, Integer> productSalesMap = new HashMap<>();

        // Calculate the total quantity sold for each product
        for (Order order : orders) {
            String productId = order.getProId();
            productSalesMap.put(productId, productSalesMap.getOrDefault(productId, 0) + 1);
        }

        // Sort products by total quantity sold in descending order
        List<Map.Entry<String, Integer>> sortedProductList = new ArrayList<>(productSalesMap.entrySet());
        sortedProductList.sort((entry1, entry2) -> entry2.getValue() - entry1.getValue());  // Sort by value

        // Get the top 10 products
        List<String> topProducts = new ArrayList<>();
        List<Integer> topSales = new ArrayList<>();
        for (int i = 0; i < Math.min(10, sortedProductList.size()); i++) {
            topProducts.add(sortedProductList.get(i).getKey());
            topSales.add(sortedProductList.get(i).getValue());
        }

        // Create X and Y axes for the chart
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Product");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Total Sales");

        // Create BarChart to display the data
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Top 10 Best-Selling Products");

        // Create series data for the chart
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Best-Sellers");

        // Add data to the series (product names and their total sales)
        for (int i = 0; i < topProducts.size(); i++) {
            series.getData().add(new XYChart.Data<>(topProducts.get(i), topSales.get(i)));
        }

        // Add series to the chart
        barChart.getData().add(series);

        // Display the chart
        showChart(barChart);
    }

 /**
     * Removes all data in the data/orders.txt file.
     */
    public void deleteAllOrders() {
        try {
            // Create a FileWriter object in overwrite mode
            BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH));
            
            // Clear the contents of the file by writing nothing to it
            writer.write(""); // Overwrite the file with an empty string
            
            // Close the writer
            writer.close();
            
            System.out.println("All orders have been deleted successfully.");
        } catch (IOException e) {
            // Print any IO exceptions that occur
            e.printStackTrace();
        }
    }
}