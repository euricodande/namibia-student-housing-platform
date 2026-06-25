package io.github.euricodande.housing.listing;

import io.github.euricodande.housing.common.enums.ListingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ListingRepository extends JpaRepository<Listing, Long> {
    List<Listing> findByStatusAndAvailableTrue(ListingStatus status);
    List<Listing> findByLandlordId(Long landlordId);
    boolean existsByIdAndLandlordId(Long listingId, Long landlordId);
}
