package za.co.wethinkcode.weshare.user.login;

import org.junit.jupiter.api.Test;
import za.co.wethinkcode.weshare.app.db.DataRepository;
import za.co.wethinkcode.weshare.app.model.Person;
import za.co.wethinkcode.weshare.user.UserTestRunner;
import za.co.wethinkcode.weshare.user.pages.LoginPage;
import za.co.wethinkcode.weshare.user.pages.NettExpensesPage;

public class LogoutTest extends UserTestRunner {
    private final Person person = generateRandomPerson();
    @Test
    void logout() {
        LoginPage loginPage = new LoginPage(this);
        loginPage.open();
        NettExpensesPage nettExpensesPage = login(person.getEmail());

        loginPage = nettExpensesPage.logout();
        shouldBeOnPage(loginPage);
    }

    @Override
    protected void setupTestData() {
        DataRepository.getInstance().addPerson(person);
    }
}
