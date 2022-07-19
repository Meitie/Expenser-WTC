package za.co.wethinkcode.weshare.claim;

import java.time.DateTimeException;
import java.time.LocalDate;

public class ClaimViewModel {
    private Double claimAmount;
    private String claimFromWho;
    private String dueDate;
    private int id;

    public LocalDate dueDateAsLocalDate() {
        try {
            return LocalDate.parse(this.dueDate);
        } catch (DateTimeException e) {
            throw new RuntimeException("Could not parse dueDate for Claim: [" + this.dueDate + "]");
        }
    }

    public Double getClaimAmount() {
        return claimAmount;
    }

    public void setClaimAmount(Double claimAmount) {
        this.claimAmount = claimAmount;
    }

    public String getClaimFromWho() {
        return claimFromWho;
    }

    public void setClaimFromWho(String claimFromWho) {
        this.claimFromWho = claimFromWho;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


}
