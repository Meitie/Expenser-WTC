package za.co.wethinkcode.weshare.app.db;

import com.google.common.collect.ImmutableList;
import za.co.wethinkcode.weshare.app.db.memory.MemoryDb;
import za.co.wethinkcode.weshare.app.model.Person;

import java.util.Optional;

public interface DataRepository {

    DataRepository INSTANCE = new MemoryDb();

    // Getting the instance of the in memory database
    static DataRepository getInstance(){
        return INSTANCE;
    }

    // Find the person's email that is logged in.
    Optional<Person> findPerson(String email );

    // Answer with a Set of all Person instances in the db.
    ImmutableList<Person> allPersons();

    // Add a new person obj to the system if it doesn't exist already
    Person addPerson(String email);
}
