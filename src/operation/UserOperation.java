package Operation;

import java.io.*;
import java.util.*;
import model.Admin;
import model.Customer;
import model.User;

public class UserOperation {
    private static UserOperation instance;
    private final String USER_FILE = "data/users.txt";

    private UserOperation() {}

    public static UserOperation getInstance() {
        if (instance == null) {
            instance = new UserOperation();
        }
        return instance;
    }

    public String generateUniqueUserId() {
        String id;
        Random random = new Random();
        do {
            long number = 1000000000L + (long) (random.nextDouble() * 8999999999L);
            id = "u_" + number;
        } while (userIdExists(id));
        return id;
    }

    private boolean userIdExists(String id) {
        try (BufferedReader br = new BufferedReader(new FileReader(USER_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("\"user_id\":\"" + id + "\"")) return true;
            }
        } catch (IOException ignored) {}
        return false;
    }

    public String encryptPassword(String password) {
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder randomStr = new StringBuilder();

        for (int i = 0; i < password.length() * 2; i++) {
            randomStr.append(chars.charAt(random.nextInt(chars.length())));
        }

        StringBuilder result = new StringBuilder("^^");
        for (int i = 0; i < password.length(); i++) {
            result.append(randomStr.charAt(i * 2));
            result.append(randomStr.charAt(i * 2 + 1));
            result.append(password.charAt(i));
        }
        result.append("$$");
        return result.toString();
    }

    public String decryptPassword(String encrypted) {
        if (!encrypted.startsWith("^^") || !encrypted.endsWith("$$")) return null;
        String core = encrypted.substring(2, encrypted.length() - 2);
        StringBuilder password = new StringBuilder();
        for (int i = 2; i < core.length(); i += 3) {
            password.append(core.charAt(i));
        }
        return password.toString();
    }

    public boolean checkUsernameExist(String userName) {
        try (BufferedReader br = new BufferedReader(new FileReader(USER_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("\"user_name\":\"" + userName + "\"")) return true;
            }
        } catch (IOException ignored) {}
        return false;
    }

    public boolean validateUsername(String name) {
        return name.matches("[A-Za-z_]{5,}");
    }

    public boolean validatePassword(String pass) {
        return pass.matches("(?=.*[A-Za-z])(?=.*\\d).{5,}");
    }

    public User login(String userName, String userPassword) {
        try (BufferedReader br = new BufferedReader(new FileReader(USER_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("\"user_name\":\"" + userName + "\"")) {
                    Map<String, String> data = parseJson(line);
                    String decrypted = decryptPassword(data.get("user_password"));
                    if (decrypted != null && decrypted.equals(userPassword)) {
                        return data.get("user_role").equals("admin")
                                ? new Admin(data.get("user_id"), data.get("user_name"),
                                            data.get("user_password"), data.get("user_register_time"),
                                            data.get("user_role"))
                                : new Customer(data.get("user_id"), data.get("user_name"),
                                               data.get("user_password"), data.get("user_register_time"),
                                               data.get("user_role"), data.get("user_email"),
                                               data.get("user_mobile"));
                    }
                }
            }
        } catch (IOException ignored) {}
        return null;
    }

    public Map<String, String> parseJson(String line) {
        Map<String, String> map = new HashMap<>();
        line = line.replaceAll("[{}\"]", "");
        for (String part : line.split(",")) {
            String[] kv = part.split(":", 2);
            if (kv.length == 2) map.put(kv[0].trim(), kv[1].trim());
        }
        return map;
    }
}
