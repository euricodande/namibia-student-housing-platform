package io.github.euricodande.housing.listing;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ListingImageRepository extends JpaRepository<ListingImage, Long> {
    List<ListingImage> findByListingId(Long listingId);
    boolean existsByListingIdAndPrimaryTrue(Long listingId);
}
