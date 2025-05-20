package Operation;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import model.Customer;
import model.User;

public class CustomerOperation {
    private static CustomerOperation instance;
    private final String USER_FILE = "data/users.txt";

    private CustomerOperation() {}

    public static CustomerOperation getInstance() {
        if (instance == null) {
            instance = new CustomerOperation();
        }
        return instance;
    }

    public boolean validateEmail(String email) {
        return email.matches("^[\\w.-]+@[\\w.-]+\\.[A-Za-z]{2,}$");
    }

    public boolean validateMobile(String mobile) {
        return mobile.matches("^(04|03)\\d{8}$");
    }

    public boolean registerCustomer(String userName, String userPassword,
                                    String userEmail, String userMobile) {
        UserOperation userOp = UserOperation.getInstance();

        if (!userOp.validateUsername(userName) || !userOp.validatePassword(userPassword)
            || !validateEmail(userEmail) || !validateMobile(userMobile)
            || userOp.checkUsernameExist(userName)) {
            return false;
        }

        String userId = userOp.generateUniqueUserId();
        String encryptedPass = userOp.encryptPassword(userPassword);
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy_HH:mm:ss"));
        Customer customer = new Customer(userId, userName, encryptedPass, time, "customer", userEmail, userMobile);

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(USER_FILE, true))) {
            bw.write(customer.toString());
            bw.newLine();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean updateProfile(String attributeName, String value, Customer customer) {
        switch (attributeName) {
            case "user_name":
                if (!UserOperation.getInstance().validateUsername(value)) return false;
                customer.setUserName(value); break;
            case "user_password":
                if (!UserOperation.getInstance().validatePassword(value)) return false;
                String encrypted = UserOperation.getInstance().encryptPassword(value);
                customer.setUserPassword(encrypted); break;
            case "user_email":
                if (!validateEmail(value)) return false;
                customer.setUserEmail(value); break;
            case "user_mobile":
                if (!validateMobile(value)) return false;
                customer.setUserMobile(value); break;
            default: return false;
        }
        return overwriteCustomer(customer);
    }

    private boolean overwriteCustomer(Customer updatedCustomer) {
        File inputFile = new File(USER_FILE);
        File tempFile = new File("data/users_temp.txt");

        boolean success = false;
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("\"user_id\":\"" + updatedCustomer.getUserId() + "\"")) {
                    writer.write(updatedCustomer.toString());
                } else {
                    writer.write(line);
                }
                writer.newLine();
            }
            success = true;
        } catch (IOException e) {
            return false;
        }

        if (success) {
            inputFile.delete();
            tempFile.renameTo(inputFile);
        }

        return success;
    }

    public boolean deleteCustomer(String customerId) {
        File inputFile = new File(USER_FILE);
        File tempFile = new File("data/users_temp.txt");

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.contains("\"user_id\":\"" + customerId + "\"")) {
                    writer.write(line);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            return false;
        }

        inputFile.delete();
        return tempFile.renameTo(inputFile);
    }

    public void deleteAllCustomers() {
        try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE));
             BufferedWriter writer = new BufferedWriter(new FileWriter("data/users_temp.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("\"user_role\":\"admin\"")) {
                    writer.write(line);
                    writer.newLine();
                }
            }
        } catch (IOException ignored) {}

        new File(USER_FILE).delete();
        new File("data/users_temp.txt").renameTo(new File(USER_FILE));
    }

    public CustomerListResult getCustomerList(int pageNumber) {
        List<Customer> customers = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("\"user_role\":\"customer\"")) {
                    Map<String, String> data = UserOperation.getInstance().parseJson(line);
                    String username = data.get("user_name");
                    String decryptedPassword = UserOperation.getInstance().decryptPassword(data.get("user_password"));
                    User user = UserOperation.getInstance().login(username, decryptedPassword);
                    
                    if (user instanceof Customer) {
                        customers.add((Customer) user);
                    }
                }
            }
        } catch (IOException ignored) {}

        int totalPages = (int) Math.ceil(customers.size() / 10.0);
        int from = (pageNumber - 1) * 10;
        int to = Math.min(pageNumber * 10, customers.size());
        return new CustomerListResult(customers.subList(from, to), pageNumber, totalPages);
    }

    // Helper result class
    public static class CustomerListResult {
        public List<Customer> customers;
        public int pageNumber;
        public int totalPages;

        public CustomerListResult(List<Customer> customers, int pageNumber, int totalPages) {
            this.customers = customers;
            this.pageNumber = pageNumber;
            this.totalPages = totalPages;
        }
    }
}
