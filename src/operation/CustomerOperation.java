import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;
public class CustomerOperation {
    private static CustomerOperation instance;
    CustomerOperation(){

    }
    public static CustomerOperation getInstance(){
        if(instance == null){
            instance = new CustomerOperation();

        }
        return instance;

        
    }
    public boolean validateEmail(String userEmail){
        if (userEmail == null) {
        return false;}
        if (!userEmail.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")){
            return false;
        }
        return true;
    }
    public boolean validateMobile(String userMobile) {
        if (userMobile.length() == 10 ){
            return true;}
        if (!userMobile.matches("^(04|03)\\d{8}$")){
            return false;
        
}return true;
    } 
    


    
    public boolean registerCustomer(String username, String userpassword,
                                    String userEmail, String userMobile) {
        // Basic validation
        if (username == null || userpassword == null || userEmail == null || userMobile == null ||
            username.isEmpty() || userpassword.isEmpty() || userEmail.isEmpty() || userMobile.isEmpty()) {
            return false;
        }

        // Validate email format
        if (!userEmail.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$")) {
            return false;
        }

        // Validate mobile number (example: 10-digit number)
        if (!userMobile.matches("^\\d{10}$")) {
            return false;
        }

        // Check if the username already exists
        try {
            File file = new File("data/users.txt");
            if (file.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length > 1 && parts[1].equalsIgnoreCase(username)) {
                        reader.close();
                        return false; 
                    }
                }
                reader.close();
            }

            // Generate unique ID and register time
            String userid = UsersOperation.generateIds("data/users.txt");
            LocalDateTime now = LocalDateTime.now();
            String userRegisterTime = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            // Write user info to file
            BufferedWriter writer = new BufferedWriter(new FileWriter("data/users.txt", true));
            String userData = String.join(",", userid, username, userpassword, userEmail, userMobile, userRegisterTime);
            writer.write(userData);
            writer.newLine();
            writer.close();

            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false; 
        }
        }
      public boolean deleteCustomer(String customerId) {
        File file = new File("data/users.txt");
        if (!file.exists()) return false;

        List<String> updatedLines = new ArrayList<>();
        boolean found = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length > 0 && parts[0].equals(customerId)) {
                    found = true; 
                    continue;
                }
                updatedLines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        if (!found) return false; 

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String l : updatedLines) {
                writer.write(l);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
    

    private static final String USER_FILE = "data/users.txt";
    private static final int PAGE_SIZE = 10;

    public CustomerListResult getCustomerList(int pageNumber) {
        List<Customer> allCustomers = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                JSONObject json = new JSONObject(line);

                String role = json.optString("user_role", "");
                if (!"customer".equalsIgnoreCase(role)) {
                    continue; 
                }

                
                String userId = json.optString("user_id", "");
                String userName = json.optString("user_name", "");
                String userPassword = json.optString("user_password", "");
                String userEmail = json.optString("user_email", "");
                String userMobile = json.optString("user_mobile", "");

                Customer customer = new Customer(userId, userName, userPassword, role, userEmail, userMobile);
                allCustomers.add(customer);
            }
        } catch (IOException e) {
            System.err.println("Error when reading users.txt: " + e.getMessage());
            return new CustomerListResult(new ArrayList<>(), 1, 1);
        }

        int totalCustomers = allCustomers.size();
        int totalPages = (int) Math.ceil(totalCustomers / (double) PAGE_SIZE);

        if (pageNumber < 1) pageNumber = 1;
        if (pageNumber > totalPages && totalPages > 0) pageNumber = totalPages;

        int fromIndex = (pageNumber - 1) * PAGE_SIZE;
        int toIndex = Math.min(fromIndex + PAGE_SIZE, totalCustomers);

        List<Customer> pageCustomers = new ArrayList<>();
        if (fromIndex < toIndex) {
            pageCustomers = allCustomers.subList(fromIndex, toIndex);
        }

        return new CustomerListResult(pageCustomers, pageNumber, totalPages);
    }
}
    

  


    


    
    
