package io.github.euricodande.housing.rental;

import io.github.euricodande.housing.common.enums.RentalStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RentalRepository extends JpaRepository<Rental, Long> {
    List<Rental> findByStudent_Id(Long studentId);
    List<Rental> findByListing_Landlord_Id(Long landlordId);
    Optional<Rental> findByIdAndStudent_Id(Long rentalId, Long studentId);
    Optional<Rental> findByIdAndListing_Landlord_Id(Long rentalId, Long landlordId);
    boolean existsByListing_IdAndStatus(Long listingId, RentalStatus status);
}
