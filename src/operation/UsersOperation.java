package operation;
import java.util.Random;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.json.*;

import model.Customer;
import model.Users;;
public class UsersOperation {
    
     
    private static UsersOperation instance;
    UsersOperation(){

    }
    public static UsersOperation getInstance(){
        if(instance == null){
            instance = new UsersOperation();

        }
        return instance;

        
    }
    public static String generateIds( String users) {
    String userid;

    do {
        userid = generateUserId();
    } while (isUserIdExists(users, userid));
    return userid;
}
 private static String generateUserId() {
        StringBuilder sb = new StringBuilder("u_");
        
        for (int i = 0; i < 10; i++) {
            int digit = (int) (Math.random() * 10);
            sb.append(digit);
        }
        return sb.toString();
    }


public static boolean isUserIdExists(String datausers, String userid) {
    File file = new File("users.txt");
    if (!file.exists()) return false;

    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split("\\|");
            if (parts.length > 0 && parts[0].equals(userid)) {
                return true;
            }
        }
    } catch (IOException e) {
        e.printStackTrace();
    }

    return false;
}
    
    
//encrypt password
    
    public static String encryptPassword(String userpassword){
        Random random = new Random();
       int passwordlength = userpassword.length();
       int randompassword = passwordlength * 2;
       String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
       StringBuilder randomString = new StringBuilder();
       for (int i = 0; i < randompassword; i++){
        randomString.append(chars.charAt(random.nextInt(chars.length())));
        
       }
       StringBuilder encrypted = new StringBuilder("^^");
       for  (int i = 0; i<passwordlength; i++){
       encrypted.append(randomString.charAt(i * 2));
        encrypted.append(randomString.charAt(i * 2 + 1));
        encrypted.append(userpassword.charAt(i));
       }
       encrypted.append("$$");
       return encrypted.toString();
       //decrypt password
    } 
    public static String decryptpassword(String encryptedPassword){
        String newpass = encryptedPassword.substring(2, encryptedPassword.length() -2 );
        StringBuilder decrypt = new StringBuilder();
        for(int i=0;i<newpass.length();i+=3){
            decrypt.append((newpass).charAt(i));
            
        }
        return decrypt.toString();



       }
       //checkUsernameExist
       private Set<String> registeredUsers = new HashSet<>();
        public void UserNameCheck(String username) {
        registeredUsers.add(username);
        System.out.println("User added: " + username);
    }

    public boolean checkUserExists(String username) {
        return registeredUsers.contains(username);
        
    }
    // validateUsername

public boolean validateName(String username) {
    if (username == null|| username.isEmpty() ){
        throw new IllegalArgumentException("your name can not be empty");
    }
    if (username.length() < 5){
        return false;
    }
    if (!username.matches("^[a-zA-Z_]+$")) {
        return false;
    }
 return true;
    }
    //validiatePassword
    
    public boolean validatePassword(String userpassword) {
        if (userpassword == null|| userpassword.isEmpty() ){
        throw new IllegalArgumentException("your password can not be empty");
    }
        if(userpassword.length() < 5 ){
            return false;
        }
        if(!userpassword.matches(".*[a-zA-Z].*")){
            return false;
        }
        if (!userpassword.matches(".*[0-9].*")){
            return false;
        }
    return true;
        }
      public Users login(String userName, String userPassword) {
    File file = new File("users.txt");
    if (!file.exists()) return null;

    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
        String line;
        while ((line = reader.readLine()) != null) {
            JSONObject obj = new JSONObject(line);

            if (obj.getString("user_name").equals(userName)) {
                String encryptedPassword = obj.getString("user_password");
                String decrypted = decryptpassword(encryptedPassword);

                if (decrypted.equals(userPassword)) {
                    String userId = obj.getString("user_id");
                    String role = obj.getString("user_role");

                    if (role.equals("admin")) {
                        return new Admin(userId, userName, encryptedPassword, role);
                    } else {
                        String email = obj.optString("user_email", "");
                        String mobile = obj.optString("user_mobile", "");
                        return new Customer(userId, userName, encryptedPassword, role, email, mobile);
                    }
                }
            }
        }
    } catch (IOException e) {
        e.printStackTrace();
    }

    return null;
}
}





