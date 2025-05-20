package operation;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AdminOperation {
    private static AdminOperation instance;
    AdminOperation(){}
    public static AdminOperation getInstance(){
        if(instance == null){
            instance = new AdminOperation();

        }
        return instance;
}
public void registerAdmin() {
        String defaultUsername = "admin";
        String defaultPassword = "admin123";

        File file = new File("data/users.txt");

        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("\"user_name\":\"" + defaultUsername + "\"") &&
                    line.contains("\"user_role\":\"admin\"")) {
                   
                    return;
                }
            }
        } catch (FileNotFoundException e) {
            
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        String userid = UsersOperation.generateIds("data/users.txt");
        String encryptedPassword = UsersOperation.encryptPassword(defaultPassword);
        String registerTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy_HH:mm:ss"));

        String jsonString = String.format(
            "{\"user_id\":\"%s\",\"user_name\":\"%s\",\"user_password\":\"%s\",\"user_register_time\":\"%s\",\"user_role\":\"admin\"}",
            userid, defaultUsername, encryptedPassword, registerTime
        );
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(jsonString);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
