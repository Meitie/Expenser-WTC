package za.co.wethinkcode.weshare.app.db;

import com.google.common.collect.ImmutableList;
import za.co.wethinkcode.weshare.app.db.memory.ClaimsMemoryDb;
import za.co.wethinkcode.weshare.app.model.Claim;
import za.co.wethinkcode.weshare.app.model.Settlement;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * DataRepository:
 * I define the protocol -- the set of methods -- that other parts of the application can use to
 * persist instances of the domain model (Persons, Expenses, Claims and Settlements).
 * <p>
 * My name, {@code DataRepository}, ins intended to indicate the abstract idea that <em>this</em>
 * is where data lives, without binding to any specific implementation like memory, file or database.
 * <p>
 * For the purposes of our Iteration 2 "WeShare" exercise, a simple, memory-only implementation is
 * provided in {@link ClaimsMemoryDb}, accessible by calling
 * {@code DataRepository.getInstance()}.
 */
public interface DataRepository {

    DataRepository INSTANCE = new ClaimsMemoryDb();

    static DataRepository getInstance(){
        return INSTANCE;
    }

    //<editor-fold desc="Claims">
    /** Get a Claim given its id. If no Claim exists in the db with the given id, we'll answer
     * with an empty Optional.
     * @param id The (non-null) id of a Claim.
     * @return An Optional containing the Claim, or empty if it does not exist in the db.
     */
    Optional<Claim> getClaim(UUID id);

    /**
     * Answer a Set containing all Claims.
     * @return A non-null, but maybe empty, ImmutableSet of all Claims in the db.
     */
    ImmutableList<Claim> getClaims();

    /**
     * Answer a List of all Claims made by a Person, identified by their email id.
     * @param claimedBy The email id of the Person whose claims you're looking for.
     * @param onlyUnsettled if true, only return the claims which are not yet settled
     * @return A non-null ImmutableList which may be empty.
     */
    ImmutableList<Claim> getClaimsBy( String claimedBy, boolean onlyUnsettled );

    /**
     * Answer a List of Claims FROM a Person, identified by their email id.
     * @param claimedFrom A non-null, non-empty email id.
     * @param onlyUnsettled if true, only return the claims which are not yet settled
     * @return An ImmutableList of Claims, possible empty.
     */
    ImmutableList<Claim> getClaimsFrom( String claimedFrom, boolean onlyUnsettled );

    /**
     * Answer the Set of all settled Claims claimed FROM a given Person.
     * @param person The Person whose settled Claims we want. May not be {@literal null}.
     * @return A non-null (but possibly empty) Set of Claims.
     */
    ImmutableList<Claim> findSettledClaimsClaimedFrom( String person );

    /**
     * Answer the (money) total of all settled Claims claimed FROM a given Person.
     * @param person A Person who owes zero or more Claims. May not be {@literal null}.
     * @return The total of all Claim amounts owed by the given Person.
     */
    double getTotalSettledClaimsClaimedFrom( String person );

    /**
     * Answer the Set of all unsettled Claims claimed FROM a given Person.
     * @param person The Person whose unsettled Claims we want. May not be {@literal null}.
     * @return A non-null (but possibly empty) Set of Claims.
     */
    ImmutableList<Claim> findUnsettledClaimsClaimedFrom( String person );

    /**
     * Answer the (money) total of all unsettled Claims claimed FROM a given Person.
     * @param person A Person who owes zero or more Claims. May not be {@literal null}.
     * @return The total of all Claim amounts owed by the given Person.
     */
    double getTotalUnsettledClaimsClaimedFrom( String person );

    /**
     * Answer the Set of all settled Claims owed BY a given Person.
     * @param person The Person whose settled Claims we want. May not be {@literal null}.
     * @return A non-null (but possibly empty) Set of Claims.
     */
    List<Claim> findSettledClaimsClaimedBy(String person);

    /**
     * Answer the (money) total of all settled Claims claimed BY a given Person.
     * @param person A Person who owes zero or more Claims. May not be {@literal null}.
     * @return The total of all Claim amounts owed by the given Person.
     */
    double getTotalSettledClaimsClaimedBy( String person );

    /**
     * Answer the Set of all unsettled Claims owed BY a given Person.
     * @param person The Person whose unsettled Claims we want. May not be {@literal null}.
     * @return A non-null (but possibly empty) Set of Claims.
     */
    List<Claim> findUnsettledClaimsClaimedBy( String person );

    /**
     * Answer the (money) total of all unsettled Claims claimed BY a given Person.
     * @param person A Person who owes zero or more Claims. May not be {@literal null}.
     * @return The total of all Claim amounts owed by the given Person.
     */
    double getTotalUnsettledClaimsClaimedBy( String person );

    /**
     * Add a given Claim instance to the db. If it already exists (as determined by Claim::equals),
     * I will ignore you and return the existing Claim. If you give me a {@literal null} Claim I will
     * throw my NullPointerException toys.
     * @param claim A non-null Claim object.
     * @return The Claim added to the db or that was already there.
     */
    Claim addClaim( Claim claim );

    /**
     * Delete a given Claim from the db if it exists. If you give me a {@literal null} Claim I will throw a
     * NullPointerException at you.
     * @param claim A non-null Claim. I will silently ignore a {@literal null} Claim.
     */
    void removeClaim(Claim claim );

    /**
     * Update the fields of a Claim. If the Claim does not exist in the db I will silently ignore you. If you give
     * me a {@literal null} Claim I will throw a NullPointerException.
     * @param updatedClaim A non-null Claim.
     */
    void updateClaim(Claim updatedClaim );
    //</editor-fold>

    //<editor-fold desc="Settlements">
    /**
     * Store a given Settlement in the db if it's not already in there. If it is I will ignore your
     * request, and if you give me a {@literal null} Settlement I will throw a NullPointerException.
     * @param settleClaim A non-null Settlement instance.
     * @return The same Settlement object after it's been persisted.
     */
    Settlement addSettlement(Settlement settleClaim);
    //</editor-fold>

    /**
     * Drop all claims without any check
     * WARNING! This can cause data integrity violations!!!
     */
    void dropClaims();

    /**
     * Drop all settlements without any check
     * WARNING! This can cause data integrity violations!!!
     */
    void dropSettlements();

    //</editor-fold>
}