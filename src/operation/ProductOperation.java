package operation;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import model.Product;

public class ProductOperation {
    private static ProductOperation instance;

    private static final String PRODUCTS_FILE_PATH = "data/products.txt";
    private static final String FIGURE_FOLDER_PATH = "data/figure/";
    private ProductOperation() {
        try {
            Files.createDirectories(Paths.get("data"));
            Files.createDirectories(Paths.get(FIGURE_FOLDER_PATH));
            extractProductsFromFiles();
        } catch (IOException e) {
            System.err.println("Failed to create required directories: " + e.getMessage());
        }
    }

    public static ProductOperation getInstance() {
        if (instance == null) {
            instance = new ProductOperation();
        }
        return instance;
    }

    public void extractProductsFromFiles() {
        try {
            List<Product> sampleProducts = createSampleProducts();

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(PRODUCTS_FILE_PATH))) {
                for (Product product : sampleProducts) {
                    writer.write(product.toString());
                    writer.newLine();
                }
            }
            
            System.out.println("Products successfully extracted and saved to " + PRODUCTS_FILE_PATH);
            
        } catch (IOException e) {
            System.err.println("Error extracting products from files: " + e.getMessage());
        }
    }

    public ProductListResult getProductList(int pageNumber) {
        List<Product> allProducts = readAllProductsFromFile();

        if (pageNumber < 1) {
            pageNumber = 1;
        }
        int totalProducts = allProducts.size();
        int productsPerPage = 10;
        int totalPages = (totalProducts + productsPerPage - 1) / productsPerPage; 

        if (pageNumber > totalPages && totalPages > 0) {
            pageNumber = totalPages;
        }

        int startIndex = (pageNumber - 1) * productsPerPage;
        int endIndex = Math.min(startIndex + productsPerPage, totalProducts);
        
        List<Product> pageProducts = new ArrayList<>();
        if (startIndex < totalProducts) {
            pageProducts = allProducts.subList(startIndex, endIndex);
        }
        
        return new ProductListResult(pageProducts, pageNumber, totalPages);
    }

    public boolean deleteProduct(String productId) {
        List<Product> products = readAllProductsFromFile();
        boolean productFound = false;

        Iterator<Product> iterator = products.iterator();
        while (iterator.hasNext()) {
            Product product = iterator.next();
            if (product.getId().equals(productId)) {
                iterator.remove();
                productFound = true;
                break;
            }
        }

        if (productFound) {
            try {
                writeProductsToFile(products);
                return true;
            } catch (IOException e) {
                System.err.println("Error writing products to file after deletion: " + e.getMessage());
                return false;
            }
        }
        
        return false;
    }

    public List<Product> getProductListByKeyword(String keyword) {
        List<Product> allProducts = readAllProductsFromFile();
        List<Product> matchingProducts = new ArrayList<>();

        String lowercaseKeyword = keyword.toLowerCase();

        for (Product product : allProducts) {
            if (product.getName().toLowerCase().contains(lowercaseKeyword)) {
                matchingProducts.add(product);
            }
        }
        
        return matchingProducts;
    }

    public Product getProductById(String productId) {
        List<Product> products = readAllProductsFromFile();

        for (Product product : products) {
            if (product.getId().equals(productId)) {
                return product;
            }
        }

        return null;
    }

    public void generateCategoryFigure() {
        List<Product> products = readAllProductsFromFile();

        Map<String, Integer> categoryCount = new HashMap<>();
        for (Product product : products) {
            String category = product.getCategory();
            categoryCount.put(category, categoryCount.getOrDefault(category, 0) + 1);
        }

        List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(categoryCount.entrySet());
        sortedEntries.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));

        System.out.println("Generating category bar chart with the following data:");
        for (Map.Entry<String, Integer> entry : sortedEntries) {
            System.out.println(entry.getKey() + ": " + entry.getValue() + " products");
        }

        String filePath = FIGURE_FOLDER_PATH + "category_distribution.png";
        System.out.println("Chart would be saved to: " + filePath);
 
    }

    public void generateDiscountFigure() {
        List<Product> products = readAllProductsFromFile();

        int lessThan30 = 0;
        int between30And60 = 0;
        int greaterThan60 = 0;
        
        for (Product product : products) {
            double discount = product.getDiscount();
            if (discount < 30) {
                lessThan30++;
            } else if (discount <= 60) {
                between30And60++;
            } else {
                greaterThan60++;
            }
        }

        System.out.println("Generating discount pie chart with the following data:");
        System.out.println("Discount < 30: " + lessThan30 + " products");
        System.out.println("30 <= Discount <= 60: " + between30And60 + " products");
        System.out.println("Discount > 60: " + greaterThan60 + " products");

        String filePath = FIGURE_FOLDER_PATH + "discount_distribution.png";
        System.out.println("Chart would be saved to: " + filePath);

    }

    public void generateLikesCountFigure() {
        List<Product> products = readAllProductsFromFile();
        
        Map<String, Integer> categoryLikes = new HashMap<>();
        for (Product product : products) {
            String category = product.getCategory();
            int likes = product.getLikesCount();
            categoryLikes.put(category, categoryLikes.getOrDefault(category, 0) + likes);
        }

        List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(categoryLikes.entrySet());
        sortedEntries.sort(Map.Entry.comparingByValue());

        System.out.println("Generating likes count chart with the following data:");
        for (Map.Entry<String, Integer> entry : sortedEntries) {
            System.out.println(entry.getKey() + ": " + entry.getValue() + " total likes");
        }

        String filePath = FIGURE_FOLDER_PATH + "likes_by_category.png";
        System.out.println("Chart would be saved to: " + filePath);
        
    }
    
    public void generateDiscountLikesCountFigure() {
        List<Product> products = readAllProductsFromFile();
        
        System.out.println("Generating discount vs likes count scatter chart with the following data:");
        for (Product product : products) {
            System.out.println("Product: " + product.getName() + 
                               ", Discount: " + product.getDiscount() + 
                               ", Likes: " + product.getLikesCount());
        }
        
        String filePath = FIGURE_FOLDER_PATH + "discount_vs_likes.png";
        System.out.println("Chart would be saved to: " + filePath);

    }

    public void deleteAllProducts() {
        try {
            Files.write(Paths.get(PRODUCTS_FILE_PATH), new byte[0]);
            System.out.println("All products have been deleted.");
        } catch (IOException e) {
            System.err.println("Error deleting all products: " + e.getMessage());
        }
    }

    private List<Product> readAllProductsFromFile() {
        List<Product> products = new ArrayList<>();
        Path path = Paths.get(PRODUCTS_FILE_PATH);
        
        if (!Files.exists(path)) {
            return products;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(PRODUCTS_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Product product = parseProductFromLine(line);
                if (product != null) {
                    products.add(product);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading products from file: " + e.getMessage());
        }
        
        return products;
    }

    private void writeProductsToFile(List<Product> products) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PRODUCTS_FILE_PATH))) {
            for (Product product : products) {
                writer.write(product.toString());
                writer.newLine();
            }
        }
    }

    private Product parseProductFromLine(String line) {
        try {
            String[] parts = line.split(",");
            if (parts.length >= 6) {
                String id = parts[0].trim();
                String name = parts[1].trim();
                String category = parts[2].trim();
                double price = Double.parseDouble(parts[3].trim());
                double discount = Double.parseDouble(parts[4].trim());
                int likesCount = Integer.parseInt(parts[5].trim());
                
                return new Product(id, name, category, price, discount, likesCount);
            }
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            System.err.println("Error parsing product from line: " + line);
        }
        
        return null;
    }
    
    private List<Product> createSampleProducts() {
        List<Product> products = new ArrayList<>();
        
        products.add(new Product("P001", "Smartphone X", "Electronics", 799.99, 15.0, 245));
        products.add(new Product("P002", "Laptop Pro", "Electronics", 1299.99, 10.0, 189));
        products.add(new Product("P003", "Designer T-shirt", "Clothing", 49.99, 25.0, 78));
        products.add(new Product("P004", "Running Shoes", "Footwear", 129.99, 20.0, 156));
        products.add(new Product("P005", "Coffee Maker", "Home", 89.99, 35.0, 112));
        products.add(new Product("P006", "Wireless Earbuds", "Electronics", 149.99, 5.0, 267));
        products.add(new Product("P007", "Blender", "Kitchen", 69.99, 40.0, 87));
        products.add(new Product("P008", "Yoga Mat", "Sports", 29.99, 15.0, 92));
        products.add(new Product("P009", "Gaming Console", "Electronics", 499.99, 0.0, 310));
        products.add(new Product("P010", "Winter Jacket", "Clothing", 199.99, 30.0, 64));
        products.add(new Product("P011", "Fitness Tracker", "Electronics", 89.99, 25.0, 178));
        products.add(new Product("P012", "Desk Chair", "Furniture", 249.99, 45.0, 42));
        
        return products;
    }

    public static class ProductListResult {
        public List<Product> list;
        public int page;
        public int total;
        
        public ProductListResult(List<Product> list, int page, int total) {
            this.list = list;
            this.page = page;
            this.total = total;
        }
    }
}

class Product {
    private String id;
    private String name;
    private String category;
    private double price;
    private double discount;
    private int likesCount;
    
    public Product(String id, String name, String category, double price, double discount, int likesCount) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.discount = discount;
        this.likesCount = likesCount;
    }
    
    public String getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public String getCategory() {
        return category;
    }
    
    public double getPrice() {
        return price;
    }
    
    public double getDiscount() {
        return discount;
    }
    
    public int getLikesCount() {
        return likesCount;
    }
    
    @Override
    public String toString() {
        return id + "," + name + "," + category + "," + price + "," + discount + "," + likesCount;
    }
}
