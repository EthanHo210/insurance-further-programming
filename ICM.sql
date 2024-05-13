-- Create a table for managing insurance claims
CREATE TABLE Claims (
    ClaimID SERIAL PRIMARY KEY,
    ClaimDate DATE NOT NULL,
    InsuredPerson VARCHAR(255) NOT NULL,
    CardNumber VARCHAR(16) NOT NULL,
    ExaminationDate DATE NOT NULL,
    ClaimAmount NUMERIC(10, 2) NOT NULL,
    Status VARCHAR(50) NOT NULL,
    ReceiverBankInfo VARCHAR(255) NOT NULL
);

-- Insert data to ensure the table can store 30 records 
DO $$
DECLARE
    counter INT := 0;
BEGIN
    WHILE counter < 30 LOOP
        INSERT INTO Claims (ClaimDate, InsuredPerson, CardNumber, ExaminationDate, ClaimAmount, Status, ReceiverBankInfo)
        VALUES (CURRENT_DATE, 'Insured Person ' || counter, '123456789012345' || counter, CURRENT_DATE, 1000.00, 'New', 'Receiver Bank Info ' || counter);
        counter := counter + 1;
    END LOOP;
END $$;
