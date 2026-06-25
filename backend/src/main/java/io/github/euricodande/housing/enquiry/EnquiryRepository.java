package io.github.euricodande.housing.enquiry;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EnquiryRepository extends JpaRepository<Enquiry, Long> {
    List<Enquiry> findByStudent_Id(Long studentId);
    List<Enquiry> findByListing_Id(Long listingId);
    List<Enquiry> findByListing_Landlord_Id(Long landlordId);
    Optional<Enquiry> findByIdAndStudent_Id(Long enquiryId, Long studentId);
    Optional<Enquiry> findByIdAndListing_Landlord_Id(Long enquiryId, Long landlordId);
}
