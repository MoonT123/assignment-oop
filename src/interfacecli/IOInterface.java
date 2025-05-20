import java.util.*;
public class IOInterface {
    
    private static IOInterface instance;
    private Scanner scanner;

    
    private IOInterface() {
        scanner = new Scanner(System.in);
    }

    
    public static IOInterface getInstance() {
        if (instance == null) {
            instance = new IOInterface();
        }
        return instance;
    }

    // getUserInput()
    public String[] getUserInput(String message, int numOfArgs) {
        System.out.print(message + ": ");
        String inputLine = scanner.nextLine().trim();
        String[] inputs = inputLine.split("\\s+");

        String[] result = new String[numOfArgs];
        for (int i = 0; i < numOfArgs; i++) {
            result[i] = (i < inputs.length) ? inputs[i] : "";
        }

        return result;
    }

    //mainMenu()
    public void mainMenu() {
        System.out.println("\n===== Main Menu =====");
        System.out.println("(1) Login");
        System.out.println("(2) Register");
        System.out.println("(3) Quit");
        System.out.print("Choose an option: ");
    }

    //adminMenu()
    public void adminMenu() {
        System.out.println("\n===== Admin Menu =====");
        System.out.println("(1) Show products");
        System.out.println("(2) Add customers");
        System.out.println("(3) Show customers");
        System.out.println("(4) Show orders");
        System.out.println("(5) Generate test data");
        System.out.println("(6) Generate all statistical figures");
        System.out.println("(7) Delete all data");
        System.out.println("(8) Logout");
        System.out.print("Choose an option: ");
    }

    //customerMenu()
    public void customerMenu() {
        System.out.println("\n===== Customer Menu =====");
        System.out.println("(1) Show profile");
        System.out.println("(2) Update profile");
        System.out.println("(3) Show products (optionally with keyword)");
        System.out.println("(4) Show history orders");
        System.out.println("(5) Generate all consumption figures");
        System.out.println("(6) Logout");
        System.out.print("Choose an option: ");
    }

    //showList()
    public void showList(String userRole, String listType, List<?> objectList, int pageNumber, int totalPages) {
        System.out.println("\n--- " + listType + " List for " + userRole + " ---");
        int index = 1;
        for (Object obj : objectList) {
            System.out.println((index++) + ". " + obj.toString());
        }
        System.out.println("Page " + pageNumber + " of " + totalPages);
    }

    //printErrorMessage()
    public void printErrorMessage(String errorSource, String errorMessage) {
        System.out.println("[ERROR] " + errorSource + ": " + errorMessage);
    }

    //printMessage()
    public void printMessage(String message) {
        System.out.println(message);
    }
    public static void main(String[] args) {
        IOInterface io = IOInterface.getInstance();
        io.mainMenu();
    }

}
 