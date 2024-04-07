import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

enum ClaimStatus {
    NEW, PROCESSING, DONE;
}

class FileManager {
    private static final String CLAIMS_FILE = "claims.txt";
    private static final String CUSTOMERS_FILE = "customers.txt";

    public static void readCustomersFromFile(List<Customer> customers) {
        try (Scanner scanner = new Scanner(new File(CUSTOMERS_FILE))) {
            while (scanner.hasNextLine()) {
                String[] customerData = scanner.nextLine().split(",");
                if (customerData.length < 5) {
                    System.out.println("Invalid line format: " + Arrays.toString(customerData)); // Debug output
                    continue; // Skip this line and proceed to the next one
                }
                String fullName = customerData[0];
                int age = Integer.parseInt(customerData[1]);
                String gender = customerData[2];
                String address = customerData[3];
                String phoneNumber = customerData[4];
                Customer customer = new Customer(fullName, age, gender, address, phoneNumber);
                customers.add(customer);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void writeCustomersToFile(List<Customer> customers) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(CUSTOMERS_FILE))) {
            for (Customer customer : customers) {
                writer.println(customer.getFullName() + "," + customer.getAge() + "," +
                        customer.getGender() + "," + customer.getAddress() + "," + customer.getPhoneNumber());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void readClaimsFromFile(List<Claim> claims) {
        try (Scanner scanner = new Scanner(new File(CLAIMS_FILE))) {
            while (scanner.hasNextLine()) {
                String[] claimData = scanner.nextLine().split(",");
                if (claimData.length < 10) {
                    System.out.println("Invalid line format: " + Arrays.toString(claimData)); // Debug output
                    continue; // Skip this line and proceed to the next one
                }
                String id = claimData[0];
                Date claimDate = parseDate(claimData[1]);
                long cardNumber = Long.parseLong(claimData[2]);
                Date examDate = parseDate(claimData[3]);
                double claimAmount = Double.parseDouble(claimData[4]);
                ClaimStatus status = ClaimStatus.valueOf(claimData[5]);
                ReceiverBankingInfo receiverBankingInfo = new ReceiverBankingInfo(claimData[6], claimData[7], claimData[8]);
                List<String> documents = new ArrayList<>();
                for (int i = 9; i < claimData.length; i++) {
                    documents.add(claimData[i]);
                }
                String fullName = claimData[9]; // Assuming Full Name is at index 9
                Claim claim = new Claim(id, claimDate, cardNumber, examDate, documents, claimAmount, status, receiverBankingInfo, fullName);
                claims.add(claim);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void writeClaimsToFile(List<Claim> claims) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(CLAIMS_FILE))) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            for (Claim claim : claims) {
                StringBuilder claimData = new StringBuilder();
                claimData.append(claim.getId()).append(",")
                        .append(dateFormat.format(claim.getClaimDate())).append(",")
                        .append(claim.getCardNumber()).append(",")
                        .append(dateFormat.format(claim.getExamDate())).append(",")
                        .append(claim.getClaimAmount()).append(",")
                        .append(claim.getStatus()).append(",")
                        .append(claim.getReceiverBankingInfo().getBankName()).append(",")
                        .append(claim.getReceiverBankingInfo().getAccountName()).append(",")
                        .append(claim.getReceiverBankingInfo().getAccountNumber()).append(",")
                        .append(claim.getFullName()); // Adding Full Name to the claim data

                for (String document : claim.getDocuments()) {
                    claimData.append(",").append(document);
                }

                writer.println(claimData.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Date parseDate(String dateStr) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}

class Customer {
    private String fullName;
    private int age;
    private String gender;
    private String address;
    private String phoneNumber;

    public Customer(String fullName, int age, String gender, String address, String phoneNumber) {
        this.fullName = fullName;
        this.age = age;
        this.gender = gender;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }

    // Getters and setters
    public String getFullName() {
        return fullName;
    }

    public int getAge() {
        return age;
    }

    public String getGender() {
        return gender;
    }

    public String getAddress() {
        return address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}

abstract class Person {
    protected String id;
    protected String fullName;

    public Person(String id, String fullName) {
        this.id = id;
        this.fullName = fullName;
    }
}

class PolicyHolder extends Person {
    private List<Dependent> dependents;
    protected InsuranceCard insuranceCard;

    public PolicyHolder(String id, String fullName, InsuranceCard insuranceCard) {
        super(id, fullName);
        this.insuranceCard = insuranceCard;
        this.dependents = new ArrayList<>();
    }

    public void addDependent(Dependent dependent) {
        dependents.add(dependent);
    }
}

class Dependent extends Person {
    public Dependent(String id, String fullName) {
        super(id, fullName);
    }
}

class InsuranceCard {
    private long cardNumber;
    private String cardHolder;
    private String policyOwner;
    private Date expirationDate;

    public InsuranceCard(long cardNumber, String cardHolder, String policyOwner, Date expirationDate) {
        this.cardNumber = cardNumber;
        this.cardHolder = cardHolder;
        this.policyOwner = policyOwner;
        this.expirationDate = expirationDate;
    }

    public long getCardNumber() {
        return cardNumber;
    }
}

class Claim {
    private String id;
    private Date claimDate;
    private long cardNumber;
    private Date examDate;
    private List<String> documents;
    private double claimAmount;
    private ClaimStatus status;
    private ReceiverBankingInfo receiverBankingInfo;
    private String fullName; // Adding Full Name attribute

    public Claim(String id, Date claimDate, long cardNumber, Date examDate,
                 List<String> documents, double claimAmount, ClaimStatus status,
                 ReceiverBankingInfo receiverBankingInfo, String fullName) {
        this.id = id;
        this.claimDate = claimDate;
        this.cardNumber = cardNumber;
        this.examDate = examDate;
        this.documents = documents;
        this.claimAmount = claimAmount;
        this.status = status;
        this.receiverBankingInfo = receiverBankingInfo;
        this.fullName = fullName;
    }

    // Getters and setters for other attributes
    public String getId() {
        return id;
    }

    public Date getClaimDate() {
        return claimDate;
    }

    public void setClaimDate(Date claimDate) {
        this.claimDate = claimDate;
    }

    public long getCardNumber() {
        return cardNumber;
    }

    public Date getExamDate() {
        return examDate;
    }

    public List<String> getDocuments() {
        return documents;
    }

    public double getClaimAmount() {
        return claimAmount;
    }

    public ClaimStatus getStatus() {
        return status;
    }

    public ReceiverBankingInfo getReceiverBankingInfo() {
        return receiverBankingInfo;
    }

    public String getFullName() {
        return fullName;
    }
}

class ReceiverBankingInfo {
    private String bankName;
    private String accountName;
    private String accountNumber;

    public ReceiverBankingInfo(String bankName, String accountName, String accountNumber) {
        this.bankName = bankName;
        this.accountName = accountName;
        this.accountNumber = accountNumber;
    }

    public String getBankName() {
        return bankName;
    }

    public String getAccountName() {
        return accountName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }
}

interface ClaimProcessManager {
    void addClaim(Claim claim);
    void updateClaim(String id, Claim updatedClaim);
    void deleteClaim(String id);
    Claim getClaimById(String id);
    List<Claim> getAllClaims();
}

class ClaimManager {
    private static Date parseDate(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            System.err.println("Error parsing date: " + e.getMessage());
            return null;
        }
    }

    public static void addClaim(Scanner scanner, List<Claim> claims, List<Customer> customers) {
        System.out.println("Enter customer details:");
        System.out.print("Full Name: ");
        String fullName = scanner.nextLine();
        System.out.print("Age: ");
        int age = Integer.parseInt(scanner.nextLine());
        System.out.print("Gender: ");
        String gender = scanner.nextLine();
        System.out.print("Address: ");
        String address = scanner.nextLine();
        System.out.print("Phone Number: ");
        String phoneNumber = scanner.nextLine();

        Customer customer = new Customer(fullName, age, gender, address, phoneNumber);
        customers.add(customer);

        System.out.println("Enter claim details:");
        System.out.print("Enter claim ID: ");
        String id = scanner.nextLine();
        System.out.print("Enter claim date (YYYY-MM-DD): ");
        Date claimDate = parseDate(scanner.nextLine());
        System.out.print("Enter card number: ");
        long cardNumber = Long.parseLong(scanner.nextLine());
        System.out.print("Enter exam date (YYYY-MM-DD): ");
        Date examDate = parseDate(scanner.nextLine());
        System.out.print("Enter claim amount: ");
        double claimAmount = Double.parseDouble(scanner.nextLine());
        System.out.print("Enter status (NEW, PROCESSING, DONE): ");
        ClaimStatus status = ClaimStatus.valueOf(scanner.nextLine());
        System.out.print("Enter bank name: ");
        String bankName = scanner.nextLine();
        System.out.print("Enter account name: ");
        String accountName = scanner.nextLine();
        System.out.print("Enter account number: ");
        String accountNumber = scanner.nextLine();
        System.out.print("Enter number of documents: ");
        int numDocuments = Integer.parseInt(scanner.nextLine());
        List<String> documents = new ArrayList<>();
        for (int i = 0; i < numDocuments; i++) {
            System.out.print("Enter document name: ");
            documents.add(scanner.nextLine());
        }
        ReceiverBankingInfo receiverBankingInfo = new ReceiverBankingInfo(bankName, accountName, accountNumber);
        Claim claim = new Claim(id, claimDate, cardNumber, examDate, documents, claimAmount, status, receiverBankingInfo, fullName);
        claims.add(claim);
        System.out.println("Claim added successfully.");

        // Save customers and claims immediately after addition
        FileManager.writeCustomersToFile(customers);
        FileManager.writeClaimsToFile(claims);
    }

    public static void updateClaim(Scanner scanner, List<Claim> claims) {
        System.out.print("Enter claim ID to update: ");
        String idToUpdate = scanner.nextLine();
        Claim claimToUpdate = null;
        for (Claim claim : claims) {
            if (claim.getId().equals(idToUpdate)) {
                claimToUpdate = claim;
                break;
            }
        }
        if (claimToUpdate != null) {
            System.out.print("Enter new claim date (YYYY-MM-DD): ");
            Date newClaimDate = parseDate(scanner.nextLine());
            claimToUpdate.setClaimDate(newClaimDate);
            System.out.println("Claim updated successfully.");

            // Save claims immediately after update
            FileManager.writeClaimsToFile(claims);
        } else {
            System.out.println("Claim not found.");
        }
    }

    public static void deleteClaim(Scanner scanner, List<Claim> claims, List<Customer> customers) {
        System.out.print("Enter claim ID to delete: ");
        String idToDelete = scanner.nextLine();
        boolean found = false;
        Iterator<Claim> claimIterator = claims.iterator();
        while (claimIterator.hasNext()) {
            Claim claim = claimIterator.next();
            if (claim.getId().equals(idToDelete)) {
                claimIterator.remove();
                System.out.println("Claim deleted successfully.");
                found = true;
                break;
            }
        }
        if (!found) {
            System.out.println("Claim not found.");
            return;
        }

        // Remove associated customer if no other claims are associated with them
        Iterator<Customer> customerIterator = customers.iterator();
        while (customerIterator.hasNext()) {
            Customer customer = customerIterator.next();
            boolean associatedClaimExists = false;
            for (Claim claim : claims) {
                if (claim.getFullName().equalsIgnoreCase(customer.getFullName())) {
                    associatedClaimExists = true;
                    break;
                }
            }
            if (!associatedClaimExists) {
                customerIterator.remove();
                System.out.println("Customer '" + customer.getFullName() + "' removed as no associated claims exist.");
            }
        }
        // Save customers and claims immediately after deletion
        FileManager.writeCustomersToFile(customers);
        FileManager.writeClaimsToFile(claims);
    }

    public static void viewAllCustomersAndClaims(List<Customer> customers, List<Claim> claims) {
        System.out.println("Viewing all customers and claims...");
        for (Customer customer : customers) {
            System.out.println("Customer Information:");
            System.out.println("Full Name: " + customer.getFullName());
            System.out.println("Age: " + customer.getAge());
            System.out.println("Gender: " + customer.getGender());
            System.out.println("Address: " + customer.getAddress());
            System.out.println("Phone Number: " + customer.getPhoneNumber());

            System.out.println("\nAssociated Claims:");
            for (Claim claim : claims) {
                if (claim.getFullName().equals(customer.getFullName())) {
                    System.out.println("\tClaim ID: " + claim.getId());
                    System.out.println("\tClaim date: " + claim.getClaimDate());
                    System.out.println("\tCard number: " + claim.getCardNumber());
                    System.out.println("\tExam date: " + claim.getExamDate());
                    System.out.println("\tClaim amount: $" + claim.getClaimAmount());
                    System.out.println("\tStatus: " + claim.getStatus());
                    ReceiverBankingInfo receiverInfo = claim.getReceiverBankingInfo();
                    System.out.println("\tBank name: " + receiverInfo.getBankName());
                    System.out.println("\tAccount name: " + receiverInfo.getAccountName());
                    System.out.println("\tAccount number: " + receiverInfo.getAccountNumber());
                    List<String> documents = claim.getDocuments();
                    System.out.println("\tNumber of Documents: " + documents.size());
                    if (!documents.isEmpty()) {
                        System.out.println("\tDocuments:");
                        for (String document : documents) {
                            System.out.println("\t" + document);
                        }
                    }
                    System.out.println();
                }
            }
        }
    }

    public static void viewOneCustomerAndClaim(Scanner scanner, List<Customer> customers, List<Claim> claims) {
        System.out.print("Enter the full name of the customer to view: ");
        String fullName = scanner.nextLine();
        boolean found = false;
        for (Customer customer : customers) {
            if (customer.getFullName().equalsIgnoreCase(fullName)) {
                found = true;
                System.out.println("Customer Information:");
                System.out.println("Full Name: " + customer.getFullName());
                System.out.println("Age: " + customer.getAge());
                System.out.println("Gender: " + customer.getGender());
                System.out.println("Address: " + customer.getAddress());
                System.out.println("Phone Number: " + customer.getPhoneNumber());

                System.out.println("\nAssociated Claims:");
                for (Claim claim : claims) {
                    if (claim.getFullName().equals(customer.getFullName())) {
                        System.out.println("\tClaim ID: " + claim.getId());
                        System.out.println("\tClaim date: " + claim.getClaimDate());
                        System.out.println("\tCard number: " + claim.getCardNumber());
                        System.out.println("\tExam date: " + claim.getExamDate());
                        System.out.println("\tClaim amount: $" + claim.getClaimAmount());
                        System.out.println("\tStatus: " + claim.getStatus());
                        ReceiverBankingInfo receiverInfo = claim.getReceiverBankingInfo();
                        System.out.println("\tBank name: " + receiverInfo.getBankName());
                        System.out.println("\tAccount name: " + receiverInfo.getAccountName());
                        System.out.println("\tAccount number: " + receiverInfo.getAccountNumber());
                        List<String> documents = claim.getDocuments();
                        System.out.println("\tNumber of Documents: " + documents.size());
                        if (!documents.isEmpty()) {
                            System.out.println("\tDocuments:");
                            for (String document : documents) {
                                System.out.println("\t" + document);
                            }
                        }
                        System.out.println();
                    }
                }
                break;
            }
        }
        if (!found) {
            System.out.println("Customer not found.");
        }
    }
}

public class InsuranceClaimsSystem {
    public static void main(String[] args) {
        List<Customer> customers = new ArrayList<>();
        List<Claim> claims = new ArrayList<>();

        FileManager.readCustomersFromFile(customers);
        FileManager.readClaimsFromFile(claims);

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Insurance Claims System");
            System.out.println("1. Add a claim");
            System.out.println("2. Update a claim");
            System.out.println("3. Delete a claim");
            System.out.println("4. View one customer and claim");
            System.out.println("5. View all customers and claims");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");
            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1:
                    ClaimManager.addClaim(scanner, claims, customers);
                    break;
                case 2:
                    ClaimManager.updateClaim(scanner, claims);
                    break;
                case 3:
                    ClaimManager.deleteClaim(scanner, claims, customers);
                    break;
                case 4:
                    ClaimManager.viewOneCustomerAndClaim(scanner, customers, claims);
                    break;
                case 5:
                    ClaimManager.viewAllCustomersAndClaims(customers, claims);
                    break;
                case 6:
                    FileManager.writeCustomersToFile(customers);
                    FileManager.writeClaimsToFile(claims);
                    System.out.println("Exiting... Goodbye!");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice. Please enter a number between 1 and 6.");
            }
        }
    }
}
