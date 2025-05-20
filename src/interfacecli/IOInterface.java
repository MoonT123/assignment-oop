package interfacecli;


import java.util.List;
import java.util.Scanner;



public class IOInterface {
    private static IOInterface instance = null;
    private Scanner scanner;

    private IOInterface() {
        scanner = new Scanner(System.in);
    }

    /**
     * Returns the single instance of IOInterface.
     * @return IOInterface instance
     */
    public static IOInterface getInstance() {
        if (instance == null) {
            instance = new IOInterface();
        }
        return instance;
    }

    /**
     * Accept user input.
     * @param message The message to display for input prompt
     * @param numOfArgs The number of arguments expected
     * @return An array of strings containing the arguments
     */
    public String[] getUserInput(String message, int numOfArgs) {
        System.out.print(message);
        String input = scanner.nextLine().trim();
        String[] tokens = input.split("\\s+");
        String[] result = new String[numOfArgs];
        for (int i = 0; i < numOfArgs; i++) {
            if (i < tokens.length) {
                result[i] = tokens[i];
            } else {
                result[i] = "";
            }
        }
        return result;
    }

    /**
     * Display the login menu with options: (1) Login, (2) Register, (3) Quit.
     * The admin account cannot be registered.
     */
    public void mainMenu() {
        System.out.println("===== Main Menu =====");
        System.out.println("1. Login");
        System.out.println("2. Register (Admin account cannot be registered)");
        System.out.println("3. Quit");
        System.out.print("Select an option: ");
    }

    /**
     * Display the admin menu with options.
     */
    public void adminMenu() {
        System.out.println("===== Admin Menu =====");
        System.out.println("1. Show products");
        System.out.println("2. Add customers");
        System.out.println("3. Show customers");
        System.out.println("4. Show orders");
        System.out.println("5. Generate test data");
        System.out.println("6. Generate all statistical figures");
        System.out.println("7. Delete all data");
        System.out.println("8. Logout");
        System.out.print("Select an option: ");
    }

    /**
     * Display the customer menu with options.
     */
    public void customerMenu() {
        System.out.println("===== Customer Menu =====");
        System.out.println("1. Show profile");
        System.out.println("2. Update profile");
        System.out.println("3. Show products (user input could be \"3 keyword\" or \"3\")");
        System.out.println("4. Show history orders");
        System.out.println("5. Generate all consumption figures");
        System.out.println("6. Logout");
        System.out.print("Select an option: ");
    }

    /**
     * Prints out different types of lists (Customer, Product, Order).
     * Shows row number, page number, and total page number.
     * @param userRole The role of the current user
     * @param listType The type of list to display
     * @param objectList The list of objects to display
     * @param pageNumber The current page number
     * @param totalPages The total number of pages
     */
    public void showList(String userRole, String listType, List<?> objectList,
                        int pageNumber, int totalPages) {
        System.out.println("===== " + listType + " List (Page " + pageNumber + " of " + totalPages + ") =====");
        int rowNum = 1;
        for (Object obj : objectList) {
            System.out.println((rowNum++) + ". " + obj.toString());
        }
        if (objectList.isEmpty()) {
            System.out.println("No " + listType.toLowerCase() + "s to display.");
        }
    }

    /**
     * Prints out an error message and shows where the error occurred.
     * @param errorSource The source of the error
     * @param errorMessage The error message
     */
    public void printErrorMessage(String errorSource, String errorMessage) {
        System.out.println("[ERROR] (" + errorSource + "): " + errorMessage);
    }

    /**
     * Print out the given message.
     * @param message The message to print
     */
    public void printMessage(String message) {
        System.out.println(message);
    }

    /**
     * Print out the object using the toString() method.
     * @param targetObject The object to print
     */
    public void printObject(Object targetObject) {
        if (targetObject != null) {
            System.out.println(targetObject.toString());
        } else {
            System.out.println("null");
        }
    }
}