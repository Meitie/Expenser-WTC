package za.co.wethinkcode.weshare.app.model;

import java.time.LocalDate;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;

public class Settlement{

    private final UUID claimUUID;
    private final LocalDate settlementDate;

    Settlement( UUID claim, LocalDate settlementDate ){
        this.claimUUID = claim;
        this.settlementDate = checkNotNull( settlementDate );
    }

    public LocalDate getSettlementDate() {
        return settlementDate;
    }

    public UUID getClaimUUID() {
        return claimUUID;
    }
}