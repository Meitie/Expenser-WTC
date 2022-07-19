package za.co.wethinkcode.weshare.app.db.memory;

import com.google.common.collect.ImmutableList;
import za.co.wethinkcode.weshare.app.db.DataRepository;
import za.co.wethinkcode.weshare.app.model.Claim;
import za.co.wethinkcode.weshare.app.model.Settlement;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class ClaimsMemoryDb implements DataRepository {
    private final Set<Claim> claims = new HashSet<>();
    private final Set<Settlement> settlements = new HashSet<>();

    volatile long lastPersonId = 0L;

    public ClaimsMemoryDb() {
        setupClaimTestData();
    }

    //<editor-fold desc="Claims">

    /**
     * {@inheritDoc}
     */
    @Override
    public ImmutableList<Claim> getClaims() {
        return ImmutableList.copyOf(claims);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ImmutableList<Claim> getClaimsBy(String claimedBy, boolean onlyUnsettled) {
//        System.out.println(claims);
        return claims.stream().filter(claim -> claim.getClaimedBy().equals(claimedBy)
                        && (!onlyUnsettled || !claim.isSettled()))
                .sorted(Comparator.comparing(Claim::getDueDate))
                .collect(ImmutableList.toImmutableList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Claim> getClaim(UUID id) {
        return claims.stream()
                .filter(Claim -> Claim.getId().equals(id))
                .findFirst();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Claim addClaim(Claim claim) {
        claims.add(claim);
        return claim;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeClaim(Claim claim) {
        claims.remove(claim);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateClaim(Claim updatedClaim) {
        Optional<Claim> ClaimOpt = claims.stream().filter(Claim -> Claim.equals(updatedClaim)).findFirst();
        if (ClaimOpt.isPresent()) {
            claims.remove(ClaimOpt.get());
            claims.add(updatedClaim);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Claim> findUnsettledClaimsClaimedBy(String person) {
        return claims.stream()
                .filter(Claim -> Claim.getClaimedBy().equalsIgnoreCase(person) && !Claim.isSettled())
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Claim> findSettledClaimsClaimedBy(String person) {
        return claims.stream()
                .filter(Claim -> Claim.getClaimedBy().equalsIgnoreCase(person) && Claim.isSettled())
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getTotalSettledClaimsClaimedBy(String person) {
        return findSettledClaimsClaimedBy(person).stream().mapToDouble(Claim::getAmount).sum();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getTotalSettledClaimsClaimedFrom(String person) {
        return findSettledClaimsClaimedFrom(person).stream().mapToDouble(Claim::getAmount).sum();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getTotalUnsettledClaimsClaimedBy(String person) {
        return findUnsettledClaimsClaimedBy(person).stream().mapToDouble(Claim::getAmount).sum();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getTotalUnsettledClaimsClaimedFrom(String person) {
        return findUnsettledClaimsClaimedFrom(person).stream().mapToDouble(Claim::getAmount).sum();
    }

    /**
     * {@inheritDoc}
     *
     * @return
     */
    @Override
    public ImmutableList<Claim> getClaimsFrom(String claimedFrom, boolean onlyUnsettled) {
        return claims.stream()
                .filter(claim -> claim.getClaimedFrom().equals(claimedFrom) && (!onlyUnsettled || !claim.isSettled()))
                .sorted(Comparator.comparing(Claim::getDueDate))
                .collect(ImmutableList.toImmutableList());
    }

    @Override
    public ImmutableList<Claim> findSettledClaimsClaimedFrom(String person) {
        ImmutableList<Claim> claims = getClaimsFrom(person, false);
        return claims.stream()
                .filter(claim -> claim.getClaimedFrom().equals(person) && (claim.isSettled()))
                .sorted(Comparator.comparing(Claim::getDueDate))
                .collect(ImmutableList.toImmutableList());
    }

    /**
     * {@inheritDoc}
     *
     * @return
     */
    @Override
    public ImmutableList<Claim> findUnsettledClaimsClaimedFrom(String person) {
        return getClaimsFrom(person, true);
    }
    //</editor-fold>

    //<editor-fold desc="Settlements">

    /**
     * {@inheritDoc}
     */
    @Override
    public Settlement addSettlement(Settlement settlement) {
        settlements.add(settlement);
        return settlement;
    }

    @Override
    public void dropClaims() {
        claims.clear();
    }

    @Override
    public void dropSettlements() {
        settlements.clear();
    }
    //</editor-fold>

    //<editor-fold desc="Helpers">

    /**
     * Answer the next available ID for a Person.
     * <p>
     * Incrementing a {@code long} value has to be synchronized because it is not an atomic
     * operation. See the Java Language Specification (§17.7 in Java SE17 Ed.) for details.
     *
     * @return The new ID.
     */
    private long nextPersonId() {
        synchronized (this) {
            return ++lastPersonId;
        }
    }
    //</editor-fold>

    //<editor-fold desc="Claim Test Data">
    private void setupClaimTestData() {
        String herman = "herman@wethinkcode.co.za";
        String mike = "mike@wethinkcode.co.za";

        /// herman's expense id
        UUID hermansExpenseUUID = UUID.randomUUID();

        // herman claims from mike
//        addClaim(new Claim(hermansExpenseUUID.toString(), mike, herman,0.0, LocalDate.of(2021, 11, 1)));

        //mikes expense id
        UUID mikesExpenseUUID = UUID.randomUUID();

        // mike claim from herman
//        addClaim(new Claim(mikesExpenseUUID.toString(), herman, mike,200.00, LocalDate.of(2021, 11, 1)));
    }
    //</editor-fold>
}