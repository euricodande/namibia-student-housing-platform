package io.github.euricodande.housing.student;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SavedListingRepository extends JpaRepository<SavedListing, Long> {
    List<SavedListing> findByStudent_Id(Long studentId);
    boolean existsByStudent_IdAndListing_Id(Long studentId, Long listingId);
    void deleteByStudent_IdAndListing_Id(Long studentId, Long listingId);
}
