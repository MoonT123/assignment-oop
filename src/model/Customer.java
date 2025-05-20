package model;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Customer extends Users {
    private String userEmail;
    private String userMobile;
    
    public Customer(String userid, String username, String userpassword, String userRole, String userEmail, String userMobile) {
        super(userid, username, userpassword, userRole, userRole);
        this.userEmail = userEmail;
        this.userMobile = userMobile;
       
    }
    @Override
public String toString() {
    return String.format(
            "{\"user_id\":\"%s\", \"user_name\":\"%s\", \"user_password\":\"%s\", \"user_register_time\":\"%s\", \"user_role\":\"%s\", \"user_email\":\"%s\", \"user_mobile\":\"%s\"}",
            userid, username, userpassword, userRegisterTime, userRole, userEmail, userMobile
    );
        }
    public void saveCustomerToFile(Customer customer, String filePath) {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
        writer.write(customer.toString());
        writer.newLine();  
    } catch (IOException e) {
        e.printStackTrace();
    }
}    
    }

