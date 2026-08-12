package vn.thinhliendev.sporthub.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.thinhliendev.sporthub.user.entity.Role;
import vn.thinhliendev.sporthub.user.entity.RoleName;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
}
