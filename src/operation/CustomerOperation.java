package operation;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import model.Customer;

public class CustomerListResult {
    private List<Customer> customers;
    private int currentPage;
    private int totalPages;

    public CustomerListResult(List<Customer> customers, int currentPage, int totalPages) {
        this.customers = customers;
        this.currentPage = currentPage;
        this.totalPages = totalPages;
    }

    public List<Customer> getCustomers() {
        return customers;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getTotalPages() {
        return totalPages;
    }
    public CustomerListResult getCustomerList(int pageNumber) {
        List<Customer> allCustomers = new ArrayList<>();
        File file = new File("data/users.txt");
        int PAGE_SIZE = 10;

        if (!file.exists()) {
            return new CustomerListResult(new ArrayList<>(), 1, 1);
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 6) {
                    Customer customer = new Customer(
                        parts[0], parts[1], parts[2], parts[3], parts[4], parts[5]
                    );
                    allCustomers.add(customer);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new CustomerListResult(new ArrayList<>(), 1, 1);
        }

        int totalCustomers = allCustomers.size();
        int totalPages = (int) Math.ceil((double) totalCustomers / PAGE_SIZE);
        if (totalPages == 0) totalPages = 1;

        if (pageNumber < 1) pageNumber = 1;
        if (pageNumber > totalPages) pageNumber = totalPages;

        int fromIndex = (pageNumber - 1) * PAGE_SIZE;
        int toIndex = Math.min(fromIndex + PAGE_SIZE, totalCustomers);

        List<Customer> pageCustomers = allCustomers.subList(fromIndex, toIndex);
        return new CustomerListResult(pageCustomers, pageNumber, totalPages);
    }
}
