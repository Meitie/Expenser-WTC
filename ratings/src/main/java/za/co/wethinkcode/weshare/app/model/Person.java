package za.co.wethinkcode.weshare.app.model;

import java.time.LocalDate;
import java.util.Objects;
import static com.google.common.base.Preconditions.checkNotNull;

public class Person {
    private final String email;
    private double expense;
    private double claims;
    private double unsettled;
    private double settled;
    private double net;
    private LocalDate time_stamp;
    
    public Person( String email ){
        this.email = checkNotNull( email );
        this.expense = 0;
        this.claims = 0;
        this.unsettled = 0;
        this.settled = 0;
        this.net = 0;
        this.time_stamp = LocalDate.now();
    }
    

    @Override
    public String toString(){
        return "Person{" +
                "id='" + email + '\'' +
                '}';
    }

    @Override
    public boolean equals( Object o ){
        if( this == o ) return true;
        if( o == null || getClass() != o.getClass() ) return false;
        Person person = (Person) o;
        return email.equalsIgnoreCase( person.email );
    }

    @Override
    public int hashCode(){
        return Objects.hash( email );
    }

    public String getEmail(){
        return email;
    }

    public double getExpense() {
        return expense;
    }

    public double getClaims() {
        return claims;
    }

    public double getUnsettled() {
        return unsettled;
    }

    public double getSettled() {
        return settled;
    }

    public double getNet() {
        return net;
    }

    public LocalDate getTime_stamp() {
        return time_stamp;
    }

    public void person_expense(Double person_capital) {
        this.expense += person_capital;
        this.net += person_capital;
        person_update_time_stamp();
    }

    public void person_claimed_against(Double person_capital){
//        System.out.println("bloodddyyy foking hell");
        this.claims += person_capital;
        this.unsettled += person_capital;
        this.net += person_capital;
        person_update_time_stamp();
    }

    public void person_reimburse(Double person_capital){
        this.expense += person_capital;
        this.unsettled -= person_capital;
        this.settled += person_capital;
        person_update_time_stamp();
    }

    public void person_reimbursed(Double person_capital){
        this.expense += person_capital;
        this.net -= person_capital;
        person_update_time_stamp();
    }

    public void person_update_time_stamp() {
        this.time_stamp = LocalDate.now();
    }

}
