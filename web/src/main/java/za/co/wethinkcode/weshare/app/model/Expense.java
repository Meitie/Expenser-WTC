package za.co.wethinkcode.weshare.app.model;

import com.google.common.base.Strings;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * I record the details of an Expense, created when a person records an actual expense paid by them.
 * The person can then choose to (as a next step) lodge one or more claims from this expense against others.
 *
 * I use Person instances to record who incurred the Expense.
 *
 * Note that my instances are immutable.
 */
// TODO: to think about: does an expense automatically start with a claim from a person to him/herself? or is that unnecessary overhead?
// To keep things simple at the start we are assuming uniqueness based on id.

public class Expense extends AbstractTransactionModelBase {
    public final LocalDate date;
    public String description;
    public Person paidBy;

    public double sumOfSettledClaims;
    public double sumOfAllClaimsAgainstExpense;
    public double sumOfAllExpensesCaptured;
    public double sumOfUnsettledClaims;
    public double nettAmountOfExpenses;
    public double nettAmountOfExpensesLessClaims;
    public int numberOfClaims;
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

    public int getNumberOfClaims() {
        return this.numberOfClaims;
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
                ", sumofsettledclaims=" + sumOfSettledClaims +
                ", sumofallexpensescaptured=" + sumOfAllExpensesCaptured +
                ", sumofunsettledclaims=" + sumOfUnsettledClaims +
                ", nettamountofexpenses=" + nettAmountOfExpenses +
                ", nettamountofexpenseslessclaims=" + nettAmountOfExpensesLessClaims +
                ", numberofclaims=" + numberOfClaims +
                ", sumofallclaimsagainstexpense=" + sumOfAllClaimsAgainstExpense +
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
    //</editor-fold>
}