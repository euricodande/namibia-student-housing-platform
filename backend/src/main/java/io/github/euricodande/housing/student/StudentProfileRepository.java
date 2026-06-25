package io.github.euricodande.housing.student;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentProfileRepository extends JpaRepository<StudentProfile, Long> {
    Optional<StudentProfile> findByUserId(Long userId);
    boolean existsByStudentNumber(String studentNumber);
}
