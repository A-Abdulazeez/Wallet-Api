package az.project.walletapi.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private final JwtService jwtService =
            new JwtService(
                    "ThisIsATestSecretKeyThatIsAtLeast32BytesLong123456",
                    3600000
            );

    @Test
    void generateToken_withValidEmail_shouldGenerateToken() {
        String email = "az@gmail.com";

        String token = jwtService.generateToken(email);

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void extractEmail_fromValidToken_shouldReturnEmail() {
        String email = "az@gmail.com";

        String token = jwtService.generateToken(email);

        String extractedEmail = jwtService.extractEmail(token);

        assertEquals(email, extractedEmail);
    }

    @Test
    void isTokenValid_withValidToken_shouldReturnTrue() {
        String email = "az@gmail.com";

        String token = jwtService.generateToken(email);

        boolean valid = jwtService.isTokenValid(token, email);

        assertTrue(valid);
    }

    @Test
    void isTokenValid_withWrongEmail_shouldReturnFalse() {

        String email = "az@gmail.com";

        String token = jwtService.generateToken(email);

        boolean valid = jwtService.isTokenValid(
                token,
                "someoneelse@gmail.com"
        );

        assertFalse(valid);
    }
}