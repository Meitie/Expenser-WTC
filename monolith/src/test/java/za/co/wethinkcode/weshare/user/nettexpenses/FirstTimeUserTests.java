package za.co.wethinkcode.weshare.user.nettexpenses;

import org.junit.jupiter.api.Test;
import za.co.wethinkcode.weshare.app.db.DataRepository;
import za.co.wethinkcode.weshare.app.model.Person;
import za.co.wethinkcode.weshare.user.UserTestRunner;
import za.co.wethinkcode.weshare.user.pages.NettExpensesPage;

import static org.assertj.core.api.Assertions.assertThat;

public class FirstTimeUserTests extends UserTestRunner {
    private final Person person = generateRandomPerson();

    @Test
    void hasNoExpenses() {
        NettExpensesPage page = login(person.getEmail());
        assertThat(page.hasNoExpenses()).isTrue();
    }

    @Test
    void hasNoClaims() {
        NettExpensesPage page = login(person.getEmail());
        assertThat(page.hasNoClaims()).isTrue();
    }

    @Test
    void hasNoSettlements() {
        NettExpensesPage page = login(person.getEmail());
        assertThat(page.hasNoSettlements()).isTrue();
    }

    @Override
    protected void setupTestData() {
        DataRepository.getInstance().addPerson(person);
    }
}
