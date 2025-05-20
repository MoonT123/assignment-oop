package operation;

import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import model.Product;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.ScatterChart;
import java.io.*;
import java.util.*;



public class ProductOperation {
    // The instance variable for Singleton pattern

    private static final String FILE_PATH = "data/products.txt";
    // tạo đường dẫn cho data

    private static ProductOperation instance;
    // Private constructor to prevent instantiation

    private ProductOperation() {}

    /**
     * Returns the single instance of ProductOperation.
     * @return  ProductOperation instance
     */
    public static ProductOperation getInstance() {
        if (instance == null) {
            instance = new ProductOperation();
        }
        return instance;
    }

    
    public void extractProductsFromFiles() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH));
            String line;
            List<Product> productList = new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                String[] productData = line.split(",");
                String proId = productData[0];
                String proModel = productData[1];
                String proCategory = productData[2];
                String proName = productData[3];
                double proCurrentPrice = Double.parseDouble(productData[4]);
                double proRawPrice = Double.parseDouble(productData[5]);
                double proDiscount = Double.parseDouble(productData[6]);
                int proLikesCount = Integer.parseInt(productData[7]);

                Product product = new Product(proId, proModel, proCategory, proName, proCurrentPrice, proRawPrice, proDiscount, proLikesCount);
                productList.add(product);
            }
            reader.close();
            // You can use the productList for further operations or storing in memory
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves a page of products from the database.
     * Each page contains a maximum of 10 items.
     * @param pageNumber The page number to retrieve.
     * @return A list of Product objects on the specified page, 
     *         the current page number, and the total number of pages.
     */
    public ProductListResult getProductList(int pageNumber) {
        List<Product> allProducts = new ArrayList<>(); // Assume this is populated with product data
        int totalPages = (int) Math.ceil(allProducts.size() / 10.0);
        int startIndex = (pageNumber - 1) * 10;
        int endIndex = Math.min(startIndex + 10, allProducts.size());
        List<Product> pageProducts = allProducts.subList(startIndex, endIndex);
        return new ProductListResult(pageProducts, pageNumber, totalPages);
    }

    /**
     * Deletes a product from the system based on the provided productId.
     * @param productId The ID of the product to delete.
     * @return  true if successful, false otherwise.
     */
    public boolean deleteProduct(String productId) {
      List<Product> productList = new ArrayList<>();
        boolean productFound = false;

        try {
            // read and save the product into list
            BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] productData = line.split(",");
                String proId = productData[0];
                String proModel = productData[1];
                String proCategory = productData[2];
                String proName = productData[3];
                double proCurrentPrice = Double.parseDouble(productData[4]);
                double proRawPrice = Double.parseDouble(productData[5]);
                double proDiscount = Double.parseDouble(productData[6]);
                int proLikesCount = Integer.parseInt(productData[7]);
                //create a constructor
                Product product = new Product(proId, proModel, proCategory, proName, proCurrentPrice, proRawPrice, proDiscount, proLikesCount);
                productList.add(product);
            }
            reader.close();

            Iterator<Product> iterator = productList.iterator();
            while (iterator.hasNext()) {
                Product product = iterator.next();
                if (product.getProId().equals(productId)) {
                    iterator.remove(); 
                    productFound = true;
                    break;
                }
            }

            if (!productFound) {
                return false;
            }

            // write a list of changed products
            BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH));
            for (Product product : productList) {
                writer.write(product.toString() + "\n"); 
            }
            writer.close();

            return true;

        } catch (IOException e) {
            e.printStackTrace();  // Print Error if got failed
            return false;  
        }
    }

    /**
     * Retrieves all products whose name contains the keyword (case insensitive).
     * @param keyword The search keyword.
     * @return A list of products whose name contains the keyword.
     */
    public List<Product> getProductListByKeyword(String keyword) {
        List<Product> productList = new ArrayList<>(); // Assume this is populated with product data
        List<Product> result = new ArrayList<>();
        for (Product product : productList) {
            if (product.getProName().toLowerCase().contains(keyword.toLowerCase())) {
                result.add(product);
            }
        }
        return result;
    }

    /**
     *  Returns one product object based on the given product_id.
     * @param productId The ID of the product to retrieve.
     * @return  A Product object or null if not found
     */
    public Product getProductById(String productId) {
        List<Product> productList = new ArrayList<>(); // Assume this is populated with product data
        for (Product product : productList) {
            if (product.getProId().equals(productId)) {
                return product;
            }
        }
        return null; // Product not found
    }

    /**
     * Generates a bar chart showing the total number of products for each category in descending order.
     * Saves the figure into the data/figure folder.
     */
    public void generateCategoryFigure() {

        // take list  (use instructor loadProductsFromFile)
        List<Product> productList = loadProductsFromFile();

        // Tính số lượng sản phẩm theo danh mục
        Map<String, Integer> categoryCounts = new HashMap<>();
        for (Product product : productList) {
            String category = product.getProCategory();
            categoryCounts.put(category, categoryCounts.getOrDefault(category, 0) + 1);
        }

        // Tạo các trục cho biểu đồ
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Category");
        yAxis.setLabel("Number of Products");

        // Tạo biểu đồ cột (Bar Chart)
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Product Category Distribution");

        // Tạo chuỗi dữ liệu cho biểu đồ
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Categories");

        // Thêm dữ liệu vào biểu đồ
        for (Map.Entry<String, Integer> entry : categoryCounts.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        // Thêm series vào biểu đồ
        barChart.getData().add(series);

        // Lưu biểu đồ dưới dạng hình ảnh (PNG)
        saveChartAsImage(barChart);
    }

    // Phương thức đọc dữ liệu từ tệp txt và chuyển thành danh sách sản phẩm
    public List<Product> loadProductsFromFile() {
        List<Product> productList = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] productData = line.split(",");
                String proId = productData[0];
                String proModel = productData[1];
                String proCategory = productData[2];
                String proName = productData[3];
                double proCurrentPrice = Double.parseDouble(productData[4]);
                double proRawPrice = Double.parseDouble(productData[5]);
                double proDiscount = Double.parseDouble(productData[6]);
                int proLikesCount = Integer.parseInt(productData[7]);

                Product product = new Product(proId, proModel, proCategory, proName, proCurrentPrice, proRawPrice, proDiscount, proLikesCount);
                productList.add(product);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return productList;
    }

    // Phương thức lưu biểu đồ dưới dạng hình ảnh
    public void saveChartAsImage(BarChart<String, Number> chart) {
        // Tạo Scene và Stage cho biểu đồ
        Scene scene = new Scene(chart, 800, 600);
        Stage stage = new Stage();
        stage.setScene(scene);

        // Tạo tệp hình ảnh (tệp PNG) lưu biểu đồ
        File file = new File("data/figure/categoryFigure.png");
        try {
            // Đảm bảo thư mục data/figure đã tồn tại
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            // Sử dụng JavaFX để lưu biểu đồ thành ảnh PNG
            javafx.scene.image.WritableImage writableImage = chart.snapshot(null, null);
            javax.imageio.ImageIO.write(javafx.embed.swing.SwingFXUtils.fromFXImage(writableImage, null), "png", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Generates a pie chart showing the proportion of products with discounts in three categories:
     * - Products with discount less than 30%
     * - Products with discount between 30% and 60%
     * - Products with discount greater than 60%
     * Saves the figure into the data/figure folder.
     */
    public void generateDiscountFigure() {
          // Lấy danh sách sản phẩm từ tệp (dùng phương thức loadProductsFromFile)
        List<Product> productList = loadProductsFromFile();

        // Đếm số lượng sản phẩm theo từng loại giảm giá
        int lessThan30 = 0;
        int between30And60 = 0;
        int greaterThan60 = 0;

        for (Product product : productList) {
            double discount = product.getProDiscount();

            if (discount < 30) {
                lessThan30++;
            } else if (discount >= 30 && discount <= 60) {
                between30And60++;
            } else {
                greaterThan60++;
            }
        }

        // Tạo dữ liệu cho biểu đồ tròn (Pie Chart)
        PieChart.Data lessThan30Data = new PieChart.Data("Less than 30%", lessThan30);
        PieChart.Data between30And60Data = new PieChart.Data("30% - 60%", between30And60);
        PieChart.Data greaterThan60Data = new PieChart.Data("Greater than 60%", greaterThan60);

        // Tạo biểu đồ tròn
        PieChart pieChart = new PieChart();
        pieChart.getData().addAll(lessThan30Data, between30And60Data, greaterThan60Data);
        pieChart.setTitle("Product Discount Proportion");

        // Lưu biểu đồ thành hình ảnh (PNG)
        saveChartAsImage(pieChart);
    }


    /**
     * Lưu biểu đồ thành hình ảnh (PNG) vào thư mục data/figure
     */
    public void saveChartAsImage(PieChart chart) {
        // Tạo Scene và Stage cho biểu đồ
        Scene scene = new Scene(chart, 800, 600);
        Stage stage = new Stage();
        stage.setScene(scene);
        
        // Bạn có thể triển khai lưu biểu đồ thành hình ảnh ở đây
        // Ví dụ: sử dụng tính năng snapshot của JavaFX hoặc thư viện vẽ biểu đồ thành ảnh
    }


    /**
     * Generates a chart displaying the total number of likes for products in each category in ascending order.
     * Saves the figure into the data/figure folder.
     */
    public void generateLikesCountFigure() {
        // Lấy danh sách sản phẩm từ tệp (dùng phương thức loadProductsFromFile)
        List<Product> productList = loadProductsFromFile();

        // Tính tổng lượt thích (likes_count) cho mỗi danh mục
        Map<String, Integer> categoryLikesMap = new HashMap<>();

        for (Product product : productList) {
            String category = product.getProCategory();
            int likesCount = product.getProLikesCount();
            categoryLikesMap.put(category, categoryLikesMap.getOrDefault(category, 0) + likesCount);
        }

        // Sắp xếp theo thứ tự tăng dần
        categoryLikesMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .forEach(entry -> System.out.println(entry.getKey() + ": " + entry.getValue()));

        // Tạo dữ liệu cho biểu đồ cột (BarChart)
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Category");
        yAxis.setLabel("Total Likes");

        // Tạo biểu đồ cột
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Total Likes by Category");

        // Thêm dữ liệu vào biểu đồ cột
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (Map.Entry<String, Integer> entry : categoryLikesMap.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        barChart.getData().add(series);

        // Lưu biểu đồ thành hình ảnh (PNG)
        saveChartAsImage(barChart);
    }


    /**
     * Generates a scatter chart showing the relationship between likes count and discount for all products.
     * Saves the figure into the data/figure folder.
     */
    public void generateDiscountLikesCountFigure() {
          // Lấy danh sách sản phẩm từ tệp
        List<Product> productList = loadProductsFromFile();

        // Tạo trục x (giảm giá) và trục y (lượt thích)
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Discount (%)");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Likes Count");

        // Tạo biểu đồ phân tán
        ScatterChart<Number, Number> scatterChart = new ScatterChart<>(xAxis, yAxis);
        scatterChart.setTitle("Discount vs Likes Count");

        // Tạo một loạt dữ liệu (XYChart.Series) cho biểu đồ
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("Products");

        // Thêm dữ liệu vào biểu đồ
        for (Product product : productList) {
            double discount = product.getProDiscount();
            int likesCount = product.getProLikesCount();
            series.getData().add(new XYChart.Data<>(discount, likesCount));
        }

        // Thêm dữ liệu vào biểu đồ phân tán
        scatterChart.getData().add(series);

        // Lưu biểu đồ thành hình ảnh (PNG)
        saveChartAsImage(scatterChart);
    }
 
    /**
     * @param chart Biểu đồ cần lưu
     */
    public void saveChartAsImage(ScatterChart<Number, Number> chart) {
        // Tạo Scene và Stage cho biểu đồ
        Scene scene = new Scene(chart, 800, 600);
        Stage stage = new Stage();
        stage.setScene(scene);

        // Tạo tệp hình ảnh (PNG) lưu biểu đồ
        File file = new File("data/figure/discountLikesCountFigure.png");
        try {
            // Đảm bảo thư mục data/figure đã tồn tại
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            // Sử dụng JavaFX để lưu biểu đồ thành ảnh PNG
            javafx.scene.image.WritableImage writableImage = chart.snapshot(null, null);
            javax.imageio.ImageIO.write(javafx.embed.swing.SwingFXUtils.fromFXImage(writableImage, null), "png", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Removes all the products from the product database.
     * This method will delete all products from the data/products.txt file.
     */
    public void deleteAllProducts() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("data/products.txt"));
            writer.write(""); // Clear all contents from the file
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Helper class to hold the result of a product list with pagination details.
     */
    public static class ProductListResult {
        private List<Product> productList;
        private int currentPage;
        private int totalPages;

        public ProductListResult(List<Product> productList, int currentPage, int totalPages) {
            this.productList = productList;
            this.currentPage = currentPage;
            this.totalPages = totalPages;
        }

        public List<Product> getProductList() {
            return productList;
        }

        public int getCurrentPage() {
            return currentPage;
        }

        public int getTotalPages() {
            return totalPages;
        }
    }
}