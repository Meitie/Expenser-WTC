package za.co.wethinkcode.weshare.app.db.memory;

import com.google.common.collect.ImmutableList;
import za.co.wethinkcode.weshare.app.db.DataRepository;
import za.co.wethinkcode.weshare.app.model.Person;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class MemoryDb implements DataRepository {

    private final Set<Person> people = new HashSet<>();

    @Override
    public Optional<Person> findPerson(String email) {
        return people.stream()
                .filter(Person -> Person.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    @Override
    public ImmutableList<Person> allPersons() {
        return ImmutableList.copyOf(people);
    }

    @Override
    public Person addPerson(String email) {
        Optional<Person> alreadyExists = findPerson(email);

        return alreadyExists.orElseGet(() -> {
            Person person = new Person(email);
            people.add(person);
            return person;
        });
    }
}