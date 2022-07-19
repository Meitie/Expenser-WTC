package za.co.wethinkcode.weshare;

import com.google.common.collect.ImmutableList;
import za.co.wethinkcode.weshare.app.model.Expense;
import za.co.wethinkcode.weshare.app.model.Person;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ExpenseBuilder {
    private final List<Expense> expenses;
    private final Person currentPerson;
    private Expense currentExpense;

    public ExpenseBuilder(Person p) {
        currentPerson = p;
        expenses = new ArrayList<>();
    }

    public ImmutableList<Expense> build() {
        return ImmutableList.copyOf(expenses);
    }

    public ExpenseBuilder spent(double howMuch, String onWhat, LocalDate when) {
        expense(howMuch, onWhat, when);
        return this;
    }

    private void expense(double howMuch, String onWhat, LocalDate when) {
        currentExpense = new Expense(howMuch, when, onWhat, currentPerson);
        expenses.add(currentExpense);
    }

    public ExpenseBuilder claim(Person from, double amount, LocalDate due) {
        currentExpense.createClaim(from, amount, due);
        return this;
    }

}
