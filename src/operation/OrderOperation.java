package operation;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.json.*;

import model.Customer;
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
    public boolean registerCustomer(String username, String userpassword, String userEmail, String userMobile) {
    if (username == null || userpassword == null || userEmail == null || userMobile == null ||
        username.isEmpty() || userpassword.isEmpty() || userEmail.isEmpty() || userMobile.isEmpty()) {
        return false;
    }

    if (!userEmail.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$")) {
        return false;
    }

    if (!userMobile.matches("^\\d{10}$")) {
        return false;
    }

    File file = new File("data/users.txt");
    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.contains("\"user_name\":\"" + username + "\"")) {
                return false; // Username tồn tại
            }
        }
    } catch (IOException e) {
        e.printStackTrace();
        return false;
    }

    String userid = UsersOperation.generateIds("data/users.txt");
    String encryptedPassword = UsersOperation.encryptPassword(userpassword);
    String registerTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy_HH:mm:ss"));

    String jsonString = String.format(
        "{\"user_id\":\"%s\",\"user_name\":\"%s\",\"user_password\":\"%s\",\"user_register_time\":\"%s\",\"user_role\":\"customer\",\"user_email\":\"%s\",\"user_mobile\":\"%s\"}",
        userid, username, encryptedPassword, registerTime, userEmail, userMobile
    );

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
        writer.write(jsonString);
        writer.newLine();
    } catch (IOException e) {
        e.printStackTrace();
        return false;
    }

    return true;
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
    

  


    


    
    
