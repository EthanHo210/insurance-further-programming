/**
 Ho Anh Khoa - s3978965
 */

 import java.io.*;
 import java.text.ParseException;
 import java.text.SimpleDateFormat;
 import java.util.*;
 
 // Enum for different claim statuses
 enum ClaimStatus {
     NEW, PROCESSING, DONE;
 }
 
 // Class responsible for file I/O operations
 class FileManager {
     // File names for storing data
     private static final String CLAIMS_FILE = "claims.txt";
     private static final String CUSTOMERS_FILE = "customers.txt";
 
     // Method to read customers from file and populate the list
     public static void readCustomersFromFile(List<Customer> customers) {
         try (Scanner scanner = new Scanner(new File(CUSTOMERS_FILE))) {
             while (scanner.hasNextLine()) {
                 String[] customerData = scanner.nextLine().split(",");
                 if (customerData.length < 5) {
                     System.out.println("Invalid line format: " + Arrays.toString(customerData)); // Debug output
                     continue; // Skip this line and proceed to the next one
                 }
                 // Extract customer data fields
                 String fullName = customerData[0];
                 int age = Integer.parseInt(customerData[1]);
                 String gender = customerData[2];
                 String address = customerData[3];
                 String phoneNumber = customerData[4];
                 // Create Customer object and add to list
                 Customer customer = new Customer(fullName, age, gender, address, phoneNumber);
                 customers.add(customer);
             }
         } catch (FileNotFoundException e) {
             e.printStackTrace();
         }
     }
 
     // Method to write customers to file
     public static void writeCustomersToFile(List<Customer> customers) {
         try (PrintWriter writer = new PrintWriter(new FileWriter(CUSTOMERS_FILE))) {
             // Write each customer's data to file
             for (Customer customer : customers) {
                 writer.println(customer.getFullName() + "," + customer.getAge() + "," +
                         customer.getGender() + "," + customer.getAddress() + "," + customer.getPhoneNumber());
             }
         } catch (IOException e) {
             e.printStackTrace();
         }
     }
 
     // Method to read claims from file and populate the list
     public static void readClaimsFromFile(List<Claim> claims) {
         try (Scanner scanner = new Scanner(new File(CLAIMS_FILE))) {
             while (scanner.hasNextLine()) {
                 String[] claimData = scanner.nextLine().split(",");
                 if (claimData.length < 11) {
                     System.out.println("Invalid line format: " + Arrays.toString(claimData)); // Debug output
                     continue; // Skip this line and proceed to the next one
                 }
                 // Extract claim data fields
                 String id = claimData[0];
                 Date claimDate = parseDate(claimData[1]);
                 long cardNumber = Long.parseLong(claimData[2]);
                 Date examDate = parseDate(claimData[3]);
                 double claimAmount = Double.parseDouble(claimData[4]);
                 ClaimStatus status = ClaimStatus.valueOf(claimData[5]);
                 ReceiverBankingInfo receiverBankingInfo = new ReceiverBankingInfo(claimData[6], claimData[7], claimData[8]);
                 List<String> documents = new ArrayList<>();
                 for (int i = 9; i < claimData.length - 1; i++) {
                     documents.add(claimData[i]);
                 }
                 String fullName = claimData[claimData.length - 1]; // Full Name is the last element
                 // Create Claim object and add to list
                 Claim claim = new Claim(id, claimDate, cardNumber, examDate, documents, claimAmount, status, receiverBankingInfo, fullName);
                 claims.add(claim);
             }
         } catch (FileNotFoundException e) {
             e.printStackTrace();
         }
     }
 
     // Method to write claims to file
     public static void writeClaimsToFile(List<Claim> claims) {
         try (PrintWriter writer = new PrintWriter(new FileWriter(CLAIMS_FILE))) {
             SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
             // Write each claim's data to file
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
                         .append(claim.getReceiverBankingInfo().getAccountNumber());
 
                 for (String document : claim.getDocuments()) {
                     claimData.append(",").append(document);
                 }
 
                 claimData.append(",").append(claim.getFullName()); // Adding Full Name to the claim data
 
                 writer.println(claimData.toString());
             }
         } catch (IOException e) {
             e.printStackTrace();
         }
     }
 
     // Method to parse date string into Date object
     private static Date parseDate(String dateString) {
         SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
         try {
             return dateFormat.parse(dateString);
         } catch (ParseException e) {
             System.err.println("Error parsing date. Please enter date in format YYYY-MM-DD.");
             return null;
         }
     }
 }
 
 // Class representing a Customer
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
 
 // Abstract class representing a Person
 abstract class Person {
     protected String id;
     protected String fullName;
 
     public Person(String id, String fullName) {
         this.id = id;
         this.fullName = fullName;
     }
 }
 
 // Class representing a Policy Holder, inherits from Person
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
 
 // Class representing a Dependent, inherits from Person
 class Dependent extends Person {
     public Dependent(String id, String fullName) {
         super(id, fullName);
     }
 }
 
 // Class representing an Insurance Card
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
 
 // Class representing a Claim
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
 
     // Constructor
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
 
     // Setters for other attributes
     public void setCardNumber(long cardNumber) {
         this.cardNumber = cardNumber;
     }
 
     public void setExamDate(Date examDate) {
         this.examDate = examDate;
     }
 
     public void setDocuments(List<String> documents) {
         this.documents = documents;
     }
 
     public void setClaimAmount(double claimAmount) {
         this.claimAmount = claimAmount;
     }
 
     public void setStatus(ClaimStatus status) {
         this.status = status;
     }
 
     public void setReceiverBankingInfo(ReceiverBankingInfo receiverBankingInfo) {
         this.receiverBankingInfo = receiverBankingInfo;
     }
 }
 
 // Class representing banking information for claim receivers
 class ReceiverBankingInfo {
     private String bankName;
     private String accountName;
     private String accountNumber;
 
     public ReceiverBankingInfo(String bankName, String accountName, String accountNumber) {
         this.bankName = bankName;
         this.accountName = accountName;
         this.accountNumber = accountNumber;
     }
 
     // Getters
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
 
 // Interface defining operations for claim process management
 interface ClaimProcessManager {
     void addClaim(Claim claim);
     void updateClaim(String id, Claim updatedClaim);
     void deleteClaim(String id);
     Claim getClaimById(String id);
     List<Claim> getAllClaims();
 }
 
 // Class responsible for managing claims
 class ClaimManager {
     // Method to parse date string into Date object
     private static Date parseDate(String dateString) {
         SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
         try {
             return dateFormat.parse(dateString);
         } catch (ParseException e) {
             System.err.println("Error parsing date: " + e.getMessage());
             return null;
         }
     }
 
     // Method to add a new claim
     public static void addClaim(Scanner scanner, List<Claim> claims, List<Customer> customers) {
         // Prompt for customer details
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
 
         // Create Customer object and add to list
         Customer customer = new Customer(fullName, age, gender, address, phoneNumber);
         customers.add(customer);
 
         // Prompt for claim details
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
         // Create Claim object and add to list
         Claim claim = new Claim(id, claimDate, cardNumber, examDate, documents, claimAmount, status, receiverBankingInfo, fullName);
         claims.add(claim);
         System.out.println("Claim added successfully.");
 
         // Save customers and claims immediately after addition
         FileManager.writeCustomersToFile(customers);
         FileManager.writeClaimsToFile(claims);
     }
 
     // Method to update an existing claim
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
             // Prompt for new claim details
             System.out.print("Enter new claim date (YYYY-MM-DD): ");
             Date newClaimDate = parseDate(scanner.nextLine());
             System.out.print("Enter new card number: ");
             long newCardNumber = Long.parseLong(scanner.nextLine());
             System.out.print("Enter new exam date (YYYY-MM-DD): ");
             Date newExamDate = parseDate(scanner.nextLine());
             System.out.print("Enter new claim amount: ");
             double newClaimAmount = Double.parseDouble(scanner.nextLine());
             System.out.print("Enter new status (NEW, PROCESSING, DONE): ");
             ClaimStatus newStatus = ClaimStatus.valueOf(scanner.nextLine());
             System.out.print("Enter new bank name: ");
             String newBankName = scanner.nextLine();
             System.out.print("Enter new account name: ");
             String newAccountName = scanner.nextLine();
             System.out.print("Enter new account number: ");
             String newAccountNumber = scanner.nextLine();
             System.out.print("Enter new number of documents: ");
             int newNumDocuments = Integer.parseInt(scanner.nextLine());
             List<String> newDocuments = new ArrayList<>();
             for (int i = 0; i < newNumDocuments; i++) {
                 System.out.print("Enter new document name: ");
                 newDocuments.add(scanner.nextLine());
             }
             ReceiverBankingInfo newReceiverBankingInfo = new ReceiverBankingInfo(newBankName, newAccountName, newAccountNumber);
 
             // Update claim attributes
             claimToUpdate.setClaimDate(newClaimDate);
             claimToUpdate.setCardNumber(newCardNumber);
             claimToUpdate.setExamDate(newExamDate);
             claimToUpdate.setClaimAmount(newClaimAmount);
             claimToUpdate.setStatus(newStatus);
             claimToUpdate.setReceiverBankingInfo(newReceiverBankingInfo);
             claimToUpdate.setDocuments(newDocuments);
 
             System.out.println("Claim updated successfully.");
 
             // Save claims immediately after update
             FileManager.writeClaimsToFile(claims);
         } else {
             System.out.println("Claim not found.");
         }
     }
 
     // Method to delete a claim
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
 
     // Method to view all customers and their associated claims
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
             boolean foundClaim = false;
             for (Claim claim : claims) {
                 if (claim.getFullName().equals(customer.getFullName())) {
                     foundClaim = true;
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
             if (!foundClaim) {
                 System.out.println("\tThis customer currently has no claims.");
             }
         }
     }
 
     // Method to view details of one customer and their associated claims
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
                 boolean foundClaim = false;
                 for (Claim claim : claims) {
                     if (claim.getFullName().equals(customer.getFullName())) {
                         foundClaim = true;
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
                 if (!foundClaim) {
                     System.out.println("\tThis customer currently has no claims.");
                 }
                 break;
             }
         }
         if (!found) {
             System.out.println("Customer not found.");
         }
     }
 }
 
 // Main class representing the Insurance Claims System
 public class InsuranceClaimsSystem {
     public static void main(String[] args) {
         List<Customer> customers = new ArrayList<>();
         List<Claim> claims = new ArrayList<>();
 
         // Load existing data from files
         FileManager.readCustomersFromFile(customers);
         FileManager.readClaimsFromFile(claims);
 
         Scanner scanner = new Scanner(System.in);
         boolean exit = false;
 
         while (!exit) {
             System.out.println("\nInsurance Claims System");
             System.out.println("1. Add a claim");
             System.out.println("2. Update a claim");
             System.out.println("3. Delete a claim");
             System.out.println("4. View all customers and their claims");
             System.out.println("5. View details of one customer and their claims");
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
                     ClaimManager.viewAllCustomersAndClaims(customers, claims);
                     break;
                 case 5:
                     ClaimManager.viewOneCustomerAndClaim(scanner, customers, claims);
                     break;
                 case 6:
                     System.out.println("Exiting...");
                     exit = true;
                     break;
                 default:
                     System.out.println("Invalid choice. Please enter a number between 1 and 6.");
             }
         }
 
         scanner.close();
     }
 }
 