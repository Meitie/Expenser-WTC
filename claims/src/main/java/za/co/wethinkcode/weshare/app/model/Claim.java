package za.co.wethinkcode.weshare.app.model;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

/**
 * Record the details of a claim of an expense against a person.
 * The claim might be for smaller amount than the expense amount, if split between people.
 * <p>
 * If a person logs an expense against themselves, then that expense is in confirmed state,
 * and a Claim needs to be created where `claimedBy` and `claimedFrom` is the same person.
 */

public class Claim {

    public UUID id;
    public final String expenseUUID;
    public final Double amount;
    public final LocalDate dueDate;
    public final String claimedBy;
    public final String claimedFrom;
    public boolean settled;

    public Claim(String expenseUUID, String claimedBy, String claimedFrom, Double amount, LocalDate dueDate) {
        this.id = UUID.randomUUID();
        this.expenseUUID = expenseUUID;
        this.claimedFrom = claimedFrom;
        this.claimedBy = claimedBy;
        this.dueDate = dueDate;
        this.amount = amount;
        this.settled = false;
    }

    public String getExpenseUUID() {
        return expenseUUID;
    }

    public String getClaimedBy() {
        return claimedBy;
    }

    public String getClaimedFrom() {
        return claimedFrom;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public UUID getId() {
        return id;
    }

    public Double getAmount() {
        return amount;
    }

    public Settlement settleClaim(LocalDate settlementDate) {
        settled = true;
        return new Settlement(this.id, settlementDate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Claim claim = (Claim) o;
        return getId().equals(claim.getId()) && claimedBy.equals(claim.claimedBy) && claimedFrom.equals(claim.claimedFrom);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), claimedBy, claimedFrom);
    }

    @Override
    public String toString() {
        return "Claim{" +
                "expenseUUID=" + getId() +
                ", claimedBy='" + claimedBy + '\'' +
                ", claimedFrom='" + claimedFrom + '\'' +
                ", amount=" + getAmount() +
                ", dueDate=" + dueDate +
                '}';
    }

    public boolean isSettled() {
        return settled;
    }

    public void setId(String uuid){
        this.id = UUID.fromString(uuid);
    }

}