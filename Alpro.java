
import java.util.Scanner;
import java.time.LocalDate;

public class Alpro  {

    // Product's information
    public static class Product {
        String sku;
        String name;
        int quantity;
        LocalDate expiryDate;

        Product(String sku, String name, int quantity, LocalDate expiryDate) {
            this.sku = sku;
            this.name = name;
            this.quantity = quantity;
            this.expiryDate = expiryDate;
        }
    }

    // Product examples
    public static Product[] products = {
        new Product("P001", "Hand Sanitizer", 20, LocalDate.now().plusDays(180)),
        new Product("P002", "Face Mask", 10, LocalDate.now().plusDays(90)),
        new Product("P003", "Vitamin C", 5, LocalDate.now().plusDays(20))
    };

    // Low stock threshold
    public static final int LOW_STOCK_LIMIT = 5;

    // Alert if product expires within 30 days
    public static final int EXPIRY_ALERT_DAYS = 30;

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        // Repeat the menu until the user chooses to exit
        while (true) {

            displayProducts();

            System.out.println("\n===== STOCK MANAGEMENT MENU =====");
            System.out.println("1. Adjust Stock");
            System.out.println("2. Exit");

            System.out.print("Choose an option: ");
            String choice = scanner.nextLine().trim();

            if (choice.equals("1")) {
                adjustStock(scanner);

            } else if (choice.equals("2")) {
                System.out.println("Goodbye!");
                break;

            } else {
                System.out.println("Error: Invalid option.");
            }
        }

        scanner.close();
    }

    // Method to display current stock with low-stock and expiry alerts
    public static void displayProducts() {

        System.out.println("\n========== CURRENT STOCK ==========");
        System.out.println("SKU | Product | Quantity | Expiry Date | Status");

        for (Product product : products) {

            String status = "";

            // Check whether stock is low
            if (product.quantity <= LOW_STOCK_LIMIT) {
                status += "[LOW STOCK] ";
            }

            // Check the expiry date
            LocalDate today = LocalDate.now();

            if (!product.expiryDate.isAfter(today)) {
                status += "[EXPIRED]";

            } else if (!product.expiryDate.isAfter(
                    today.plusDays(EXPIRY_ALERT_DAYS))) {
                status += "[EXPIRING SOON]";
            }

            // Display product details
            System.out.println(
                    product.sku + " | " +
                    product.name + " | " +
                    product.quantity + " | " +
                    product.expiryDate + " | " +
                    status
            );
        }
    }

    // Search for a product using its SKU
    public static Product findProduct(String sku) {

        // Search through the array one by one
        for (Product product : products) {

            if (product.sku.equalsIgnoreCase(sku)) {
                return product;
            }
        }

        // Return null if no matching product is found
        return null;
    }

    // Allow staff to add or deduct stock
    public static void adjustStock(Scanner scanner) {

        System.out.print("\nEnter product SKU: ");
        String sku = scanner.nextLine().trim();

        // Find the product selected by the user
        Product product = findProduct(sku);

        if (product == null) {
            System.out.println("Error: Product not found.");
            return;
        }

        System.out.print("Enter adjustment: ");
        String input = scanner.nextLine().trim();

        int adjustment;

        try {
            // Convert String input into an integer
            adjustment = Integer.parseInt(input);

        } catch (NumberFormatException e) {
            // Reject text such as "abc"
            System.out.println("Error: Please enter a valid number.");
            return;
        }

        // Reject zero because no stock would change
        if (adjustment == 0) {
            System.out.println("Error: Adjustment cannot be zero.");
            return;
        }

        // Calculate the quantity before updating stock
        // Use long to prevent integer overflow
        long newQuantity = (long) product.quantity + adjustment;

        // Reject deduction if stock would become negative
        if (newQuantity < 0) {
            System.out.println("Error: Insufficient stock!");
            System.out.println("Current quantity: " + product.quantity);
            return;
        }

        // Reject quantities too large for an int
        if (newQuantity > Integer.MAX_VALUE) {
            System.out.println("Error: Quantity is too large.");
            return;
        }

        // Update stock after all validations pass
        product.quantity = (int) newQuantity;

        System.out.println("Success! " + product.name
                + " quantity is now " + product.quantity);

        // Warn staff after stock adjustment
        if (product.quantity <= LOW_STOCK_LIMIT) {
            System.out.println("WARNING: Low stock! Please restock soon.");
        }
    }
}
