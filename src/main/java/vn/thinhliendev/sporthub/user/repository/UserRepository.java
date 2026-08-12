package vn.thinhliendev.sporthub.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.thinhliendev.sporthub.user.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCase(String email);
}
