package az.project.walletapi.data.repository;

import az.project.walletapi.data.model.Role;
import az.project.walletapi.data.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    List<User> findAllByRole(Role role);
    Optional<User> findByEmailAndRole(String email, Role role);
}
