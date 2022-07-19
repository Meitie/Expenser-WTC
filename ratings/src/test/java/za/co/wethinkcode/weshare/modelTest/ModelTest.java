package za.co.wethinkcode.weshare.modelTest;

import org.junit.jupiter.api.Test;
import za.co.wethinkcode.weshare.app.model.Person;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ModelTest {
    @Test
    void newPerson() {
        Person person = new Person("test@test.com");

        assertEquals(person.getEmail(), "test@test.com");
        assertEquals(person.getExpense(), 0d);
        assertEquals(person.getClaims(), 0d);
        assertEquals(person.getUnsettled(), 0d);
        assertEquals(person.getSettled(), 0d);
        assertEquals(person.getNet(), 0d);
    }

    @Test
    void makeTwoExpenses() {
        Person person = new Person("test@test.com");
        person.person_expense(100.50);
        person.person_expense(100.50);

        assertEquals(person.getEmail(), "test@test.com");
        assertEquals(person.getExpense(), 201);
        assertEquals(person.getClaims(), 0d);
        assertEquals(person.getUnsettled(), 0d);
        assertEquals(person.getSettled(), 0d);
        assertEquals(person.getNet(), 201);
    }
}
