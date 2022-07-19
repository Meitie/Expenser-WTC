package za.co.wethinkcode.expenses.weshare.app.model;

import com.google.common.base.Strings;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class Expense extends AbstractTransactionModelBase {
    private final LocalDate date;
    private String description;
    private Person paidBy;

    private double sumOfSettledClaims;
    private double sumOfAllClaimsAgainstExpense;
    private double sumOfAllExpensesCaptured;
    private double sumOfUnsettledClaims;
    private double nettAmountOfExpenses;
    private double nettAmountOfExpensesLessClaims;
    private int numberOfClaims;
    /**
     * Initialise an Expense instance with all it's needed data.
     *
     * @param amount      R value of the expense item
     * @param date        Date on which the expense was paid
     * @param description Some descriptive text, possibly null or empty.
     * @param paidBy      The Person who paid for the expense item.
     */
    public Expense(double amount, LocalDate date, String description, Person paidBy,
                   double sumOfUnsettledClaims, double sumOfSettledClaims) {
        super(UUID.randomUUID(), amount);
        this.date = date;
        this.description = Strings.isNullOrEmpty(description) ? "Unspecified" : description;
        this.paidBy = paidBy;
        this.sumOfUnsettledClaims = sumOfUnsettledClaims;
        this.sumOfSettledClaims = sumOfSettledClaims;
        this.sumOfAllClaimsAgainstExpense = sumOfSettledClaims + sumOfUnsettledClaims;
        this.numberOfClaims = 0;
    }

    public Expense(double amount, LocalDate date, String description, Person paidBy) {
        super(UUID.randomUUID(), amount);
        this.date = date;
        this.description = Strings.isNullOrEmpty(description) ? "Unspecified" : description;
        this.paidBy = paidBy;
    }

    public LocalDate getDate() {
        return date;
    }

    public Person getPaidBy() {
        return paidBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Expense expense = (Expense) o;

        if (!date.equals(expense.date)) return false;
        if (!description.equals(expense.description)) return false;
        return paidBy.equals(expense.paidBy);
    }

    @Override
    public int hashCode() {
        int result = date.hashCode();
        result = 31 * result + description.hashCode();
        result = 31 * result + paidBy.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Expense{" +
                "date=" + date +
                ", description='" + description + '\'' +
                ", paidBy=" + paidBy +
                ", id=" + id +
                ", amount=" + amount +
                '}';
    }

    public String getFormattedAmount() {
        return String.format("R %,.2f", getNettAmount());
    }

    public double getNettAmount() {
        return this.amount - this.sumOfSettledClaims;
    }

    public double getSumOfUnsettledClaims() {
        return this.sumOfAllClaimsAgainstExpense - this.sumOfSettledClaims;
    }

    public double getSumOfSettledClaims() {
        return this.sumOfSettledClaims;
    }

    public void addClaim(double claimAmount)   {
        if (this.sumOfAllClaimsAgainstExpense + claimAmount > this.getAmount()) {
            throw new RuntimeException("Total claims exceeds the amount of the expense");
        }
        this.numberOfClaims++;
        this.sumOfAllClaimsAgainstExpense = this.sumOfAllClaimsAgainstExpense + claimAmount;
    }

    public void settleClaim(double amountToSettle)   {
        if (this.sumOfAllClaimsAgainstExpense - this.sumOfSettledClaims < amountToSettle) {
            throw new RuntimeException("Amount to settle exceeds the claims made against the expense");
        }

        this.numberOfClaims--;
        this.sumOfSettledClaims = this.sumOfSettledClaims + amountToSettle;
    }

    public String getDescription() {
        return this.description;
    }

//    public Claim createClaim( Person claimedFrom, Double amount, LocalDate dueDate ){
//        Double currentTotalClaimed = this.claims.stream().mapToDouble(Claim::getAmount).sum();
//        if (currentTotalClaimed + amount > this.getAmount()) {
//            throw new RuntimeException("Total claims exceeds the amount of the expense");
//        }
//        Claim claim = new Claim( this, claimedFrom, amount, dueDate );
//        this.claims.add(claim);
//        return claim;
//    }

//    public void removeClaim(Claim claim){
//        this.claims.remove(claim);
//    }
//    public Set<Claim> getClaims() {
//        return claims.stream()
//                .sorted(Comparator.comparing(Claim::getDueDate))
//                .collect(Collectors.toCollection(LinkedHashSet::new));
//    }




//    public Double getUnclaimedAmount() {
//        return this.amount - getTotalClaims();
//    }

}
