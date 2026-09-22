package az.project.walletapi.data.repository;

import az.project.walletapi.data.model.Role;
import az.project.walletapi.data.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;


    @Test
    public void saveUser_shouldPersistUser() {
        User user = new User();
        user.setFirstName("Azeez");
        user.setLastName("Walter");
        user.setEmail("az@gmail.com");
        user.setPassword("password123");
        user.setRole(Role.CUSTOMER);

        userRepository.save(user);
        assertEquals(1,  userRepository.count());
    }

    @Test
    public void findByEmail_whenUserExists_shouldReturnUser() {
        User user = new User();
        user.setFirstName("Azeez");
        user.setLastName("Walter");
        user.setEmail("az@gmail.com");
        user.setPassword("password123");
        user.setRole(Role.CUSTOMER);

        userRepository.save(user);

        Optional<User> foundUser = userRepository.findByEmail(user.getEmail());
        assertEquals(user, foundUser.get());
    }

    @Test
    public void findByEmail_whenUserDoesNotExist_shouldReturnEmpty() {
        Optional<User> foundUser = userRepository.findByEmail("unknown@gmail.com");
        assertEquals(Optional.empty(), foundUser);
    }

    @Test
    public void existsByEmail_whenEmailExists_shouldReturnTrue() {
        User user = new User();
        user.setFirstName("Azeez");
        user.setLastName("Walter");
        user.setEmail("az@gmail.com");
        user.setPassword("password123");
        user.setRole(Role.CUSTOMER);

        userRepository.save(user);
        boolean exists = userRepository.existsByEmail(user.getEmail());
        assertTrue(exists);
    }

    @Test
    public void existsByEmail_whenUserDoesNotExist_shouldReturnFalse() {
        boolean exists = userRepository.existsByEmail("unknown@gmail.com");
        assertFalse(exists);
    }

}