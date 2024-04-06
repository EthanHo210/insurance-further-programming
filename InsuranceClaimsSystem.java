import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

enum ClaimStatus {
    NEW, PROCESSING, DONE;
}
class FileManager {
    private static final String POLICY_HOLDERS_FILE = "policyholders.txt";
    private static final String CLAIMS_FILE = "claims.txt";

    public static void readPolicyHoldersFromFile(List<PolicyHolder> policyHolders) {
        try (Scanner scanner = new Scanner(new File(POLICY_HOLDERS_FILE))) {
            while (scanner.hasNextLine()) {
                String[] policyHolderData = scanner.nextLine().split(",");
                String id = policyHolderData[0];
                String fullName = policyHolderData[1];
                long cardNumber = Long.parseLong(policyHolderData[2]);
                InsuranceCard insuranceCard = new InsuranceCard(cardNumber, "holder", "owner", new Date());
                PolicyHolder policyHolder = new PolicyHolder(id, fullName, insuranceCard);
                policyHolders.add(policyHolder);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void readClaimsFromFile(List<Claim> claims) {
        try (Scanner scanner = new Scanner(new File(CLAIMS_FILE))) {
            while (scanner.hasNextLine()) {
                String[] claimData = scanner.nextLine().split(",");
                String id = claimData[0];
                Date claimDate = parseDate(claimData[1]);
                String insuredPerson = claimData[2];
                long cardNumber = Long.parseLong(claimData[3]);
                Date examDate = parseDate(claimData[4]);
                double claimAmount = Double.parseDouble(claimData[5]);
                ClaimStatus status = ClaimStatus.valueOf(claimData[6]);
                ReceiverBankingInfo receiverBankingInfo = new ReceiverBankingInfo(claimData[7], claimData[8], claimData[9]);
                List<String> documents = new ArrayList<>();
                for (int i = 10; i < claimData.length; i++) {
                    documents.add(claimData[i]);
                }
                Claim claim = new Claim(id, claimDate, insuredPerson, cardNumber, examDate, documents, claimAmount, status, receiverBankingInfo);
                claims.add(claim);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void writePolicyHoldersToFile(List<PolicyHolder> policyHolders) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(POLICY_HOLDERS_FILE))) {
            for (PolicyHolder policyHolder : policyHolders) {
                writer.println(policyHolder.id + "," + policyHolder.fullName + "," + policyHolder.insuranceCard.getCardNumber());
            }
        } catch (IOException e) {
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
                        .append(claim.getInsuredPerson()).append(",")
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



abstract class Person {
    protected String id; // Format c-numbers (c-0000001)
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
    private String id; // Format f-numbers (f-0000000001)
    private Date claimDate;
    private String insuredPerson;
    private long cardNumber;
    private Date examDate;
    private List<String> documents; // Format ClaimId_CardNumber_DocumentName.pdf
    private double claimAmount;
    private ClaimStatus status;
    private ReceiverBankingInfo receiverBankingInfo;

    public Claim(String id, Date claimDate, String insuredPerson, long cardNumber, Date examDate,
                 List<String> documents, double claimAmount, ClaimStatus status, ReceiverBankingInfo receiverBankingInfo) {
        this.id = id;
        this.claimDate = claimDate;
        this.insuredPerson = insuredPerson;
        this.cardNumber = cardNumber;
        this.examDate = examDate;
        this.documents = documents;
        this.claimAmount = claimAmount;
        this.status = status;
        this.receiverBankingInfo = receiverBankingInfo;
    }

    public String getId() {
        return id;
    }

    public Date getClaimDate() {
        return claimDate;
    }

    public String getInsuredPerson() {
        return insuredPerson;
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

    public void setClaimDate(Date claimDate) {
        this.claimDate = claimDate;
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

    public static void addClaim(Scanner scanner, List<Claim> claims) {
        System.out.print("Enter claim ID: ");
        String id = scanner.nextLine();
        System.out.print("Enter claim date (YYYY-MM-DD): ");
        Date claimDate = parseDate(scanner.nextLine());
        System.out.print("Enter insured person: ");
        String insuredPerson = scanner.nextLine();
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
        Claim claim = new Claim(id, claimDate, insuredPerson, cardNumber, examDate, documents, claimAmount, status, receiverBankingInfo);
        claims.add(claim);
        System.out.println("Claim added successfully.");
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
            claimToUpdate.setClaimDate(parseDate(scanner.nextLine()));
            System.out.println("Claim updated successfully.");
        } else {
            System.out.println("Claim not found.");
        }
    }

    public static void deleteClaim(Scanner scanner, List<Claim> claims) {
        System.out.print("Enter claim ID to delete: ");
        String idToDelete = scanner.nextLine();
        Iterator<Claim> iterator = claims.iterator();
        while (iterator.hasNext()) {
            Claim claim = iterator.next();
            if (claim.getId().equals(idToDelete)) {
                iterator.remove();
                System.out.println("Claim deleted successfully.");
                return;
            }
        }
        System.out.println("Claim not found.");
    }

    public static void viewAllClaims(List<Claim> claims) {
        System.out.println("Viewing all claims...");
        for (Claim claim : claims) {
            System.out.println(claim);
        }
    }
}

public class InsuranceClaimsSystem {
    public static void main(String[] args) {
        // Initialize system components
        List<PolicyHolder> policyHolders = new ArrayList<>();
        List<Claim> claims = new ArrayList<>();
        FileManager.readPolicyHoldersFromFile(policyHolders);
        FileManager.readClaimsFromFile(claims);

        // Sample interaction (actual UI implementation may vary)
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        while (!exit) {
            System.out.println("\nInsurance Claims Management System");
            System.out.println("1. Add a claim");
            System.out.println("2. Update a claim");
            System.out.println("3. Delete a claim");
            System.out.println("4. View all claims");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline after number input

            switch (choice) {
                case 1:
                    ClaimManager.addClaim(scanner, claims);
                    break;
                case 2:
                    ClaimManager.updateClaim(scanner, claims);
                    break;
                case 3:
                    ClaimManager.deleteClaim(scanner, claims);
                    break;
                case 4:
                    ClaimManager.viewAllClaims(claims);
                    break;
                case 5:
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }


            // Write data back to files before exiting
            FileManager.writePolicyHoldersToFile(policyHolders);
            FileManager.writeClaimsToFile(claims);
        }
        // Close scanner
        scanner.close();
    }
}
