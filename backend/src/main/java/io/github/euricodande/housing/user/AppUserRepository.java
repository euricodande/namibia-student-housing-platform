package io.github.euricodande.housing.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    List<AppUser> findByFirstNameContainingIgnoreCase(String firstName);
    List<AppUser> findByLastNameContainingIgnoreCase(String lastName);
    Optional<AppUser> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
}
