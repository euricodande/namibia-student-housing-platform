package io.github.euricodande.housing.maintenance;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MaintenanceRequestRepository extends JpaRepository<MaintenanceRequest, Long> {
    List<MaintenanceRequest> findByRental_Id(Long rentalId);
    List<MaintenanceRequest> findByRental_Student_Id(Long studentId);
    List<MaintenanceRequest> findByRental_Listing_Landlord_Id(Long landlordId);
    Optional<MaintenanceRequest> findByIdAndRental_Student_Id(Long requestId, Long studentId);
    Optional<MaintenanceRequest> findByIdAndRental_Listing_Landlord_Id(Long requestId, Long landlordId);
}
