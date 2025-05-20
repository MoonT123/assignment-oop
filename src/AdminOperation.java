package Operation;

import java.io.*;
import model.Admin;

public class AdminOperation {
    private static AdminOperation instance;
    private final String USER_FILE = "data/users.txt";

    private AdminOperation() {}

    public static AdminOperation getInstance() {
        if (instance == null) {
            instance = new AdminOperation();
        }
        return instance;
    }

    public void registerAdmin() {
        if (UserOperation.getInstance().checkUsernameExist("admin")) return;

        String userId = "u_0000000001";
        String password = "admin123";
        String encryptedPass = UserOperation.getInstance().encryptPassword(password);
        String registerTime = "01-01-2024_09:00:00";
        Admin admin = new Admin(userId, "admin", encryptedPass, registerTime, "admin");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USER_FILE, true))) {
            writer.write(admin.toString());
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error writing admin to file.");
        }
    }
}
