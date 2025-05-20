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

    // getUserInput
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

    //mainMenu
    public void mainMenu() {
        System.out.println("\n===== Main Menu =====");
        System.out.println("(1) Login");
        System.out.println("(2) Register");
        System.out.println("(3) Quit");
        System.out.print("Choose an option: ");
    }

    //adminMenu
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

    //customerMenu
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

    //showList
    public void showList(String userRole, String listType, List<?> objectList, int pageNumber, int totalPages) {
        System.out.println("\n--- " + listType + " List for " + userRole + " ---");
        int index = 1;
        for (Object obj : objectList) {
            System.out.println((index++) + ". " + obj.toString());
        }
        System.out.println("Page " + pageNumber + " of " + totalPages);
    }

    //printErrorMessage
    public void printErrorMessage(String errorSource, String errorMessage) {
        System.out.println("[ERROR] " + errorSource + ": " + errorMessage);
    }

    //printMessage
    public void printMessage(String message) {
        System.out.println(message);
    }
    public static void main(String[] args) {
        IOInterface io = IOInterface.getInstance();
        
        
UsersOperation userOps = UsersOperation.getInstance();
    Scanner scanner = new Scanner(System.in);

    switch (choice) {
                case "1": {
                    String[] loginInfo = io.getUserInput("Enter username and password", 2);
                    String username = loginInfo[0];
                    String password = loginInfo[1];

                    Users user = userOps.login(username, password);
                    if (user != null) {
                        io.printMessage("Login successful");

                        if (user instanceof Admin) {
                            while (true) {
                                io.adminMenu();
                                String[] adminInput = io.getUserInput("", 1);
                                String adminChoice = adminInput[0];

                                switch (adminChoice) {
                                    case "2": {
                                        String[] regInfo = io.getUserInput("Enter username password email mobile", 4);
                                        boolean registered = customerOps.registerCustomer(
                                            regInfo[0], regInfo[1], regInfo[2], regInfo[3]);

                                        if (registered) {
                                            io.printMessage("Customer registered successfully.");
                                        } else {
                                            io.printErrorMessage("Register", "Failed to register customer.");
                                        }
                                        break;
                                    }

                                    case "3": {
                                        int page = 1;
                                        while (true) {
                                            CustomerListResult result = customerOps.getCustomerList(page);
                                            List<Customer> customers = result.getCustomers();

                                            if (customers.isEmpty()) {
                                                io.printMessage("No customers found.");
                                                break;
                                            }

                                            io.showList("Admin", "Customer", customers,
                                                result.getCurrentPage(), result.getTotalPages());

                                            String[] nav = io.getUserInput("Enter N (next), P (previous), or Q (quit)", 1);
                                            String navChoice = nav[0].trim().toUpperCase();

                                            if (navChoice.equals("N") && page < result.getTotalPages()) {
                                                page++;
                                            } else if (navChoice.equals("P") && page > 1) {
                                                page--;
                                            } else if (navChoice.equals("Q")) {
                                                break;
                                            } else {
                                                io.printMessage("Invalid input or no more pages.");
                                            }
                                        }
                                        break;
                                    }

                                    case "8":
                                        io.printMessage("Logging out...");
                                        break;

                                    default:
                                        io.printErrorMessage("Admin Menu", "Invalid option");
                                }

                                if (adminChoice.equals("8")) break;
                            }

                        } else if (user instanceof Customer) {
                            io.customerMenu();
                            // Optional: add customer menu logic
                        }

                    } else {
                        io.printErrorMessage("Login", "Invalid username or password");
                    }
                    break;
                }

                case "2": {
                    String[] registerInfo = io.getUserInput("Enter username password email mobile", 4);
                    boolean registered = customerOps.registerCustomer(
                        registerInfo[0], registerInfo[1], registerInfo[2], registerInfo[3]);

                    if (registered) {
                        io.printMessage("Register successful");
                    } else {
                        io.printErrorMessage("Register", "Failed to register user");
                    }
                    break;
                }

                case "3": {
                    io.printMessage("Exiting the program.");
                    scanner.close();
                    System.exit(0);
                    break;
                }

                default:
                    io.printErrorMessage("Main Menu", "Invalid option");
            }
        }
    }
}
}


}
}
}
