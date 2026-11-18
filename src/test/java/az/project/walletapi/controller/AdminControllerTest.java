package az.project.walletapi.controller;

import az.project.walletapi.config.JwtAuthenticationFilter;
import az.project.walletapi.config.SecurityConfig;
import az.project.walletapi.data.model.Role;
import az.project.walletapi.data.model.Status;
import az.project.walletapi.data.model.User;
import az.project.walletapi.data.repository.UserRepository;
import az.project.walletapi.dtos.response.AdminCustomerResponse;
import az.project.walletapi.dtos.response.AdminWalletResponse;
import az.project.walletapi.dtos.response.TransactionResponse;
import az.project.walletapi.exception.GlobalExceptionHandler;
import az.project.walletapi.exception.UserException;
import az.project.walletapi.exception.WalletException;
import az.project.walletapi.service.AdminService;
import az.project.walletapi.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.security.autoconfigure.web.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.security.autoconfigure.web.servlet.ServletWebSecurityAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, GlobalExceptionHandler.class})
@ImportAutoConfiguration({SecurityAutoConfiguration.class, ServletWebSecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class})
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdminService adminService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserRepository userRepository;

    private static final String ADMIN_TOKEN = "Bearer admin-token";
    private static final String CUSTOMER_TOKEN = "Bearer customer-token";

    @BeforeEach
    void setUp() {
        User admin = new User();
        admin.setEmail("admin@wallet.com");
        admin.setRole(Role.ADMIN);

        User customer = new User();
        customer.setEmail("customer@wallet.com");
        customer.setRole(Role.CUSTOMER);

        when(jwtService.extractEmail("admin-token")).thenReturn("admin@wallet.com");
        when(jwtService.isTokenValid("admin-token", "admin@wallet.com")).thenReturn(true);
        when(userRepository.findByEmail("admin@wallet.com")).thenReturn(Optional.of(admin));

        when(jwtService.extractEmail("customer-token")).thenReturn("customer@wallet.com");
        when(jwtService.isTokenValid("customer-token", "customer@wallet.com")).thenReturn(true);
        when(userRepository.findByEmail("customer@wallet.com")).thenReturn(Optional.of(customer));
    }

    // 1. Unauthenticated requests return 401
    @Test
    void unauthenticatedRequests_shouldReturn401() throws Exception {
        mockMvc.perform(get("/api/admin/customers")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/admin/customers/test@mail.com")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/admin/wallets")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/admin/wallets/12345")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/admin/transactions")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/admin/transactions/ref-1")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/admin/wallets/12345/transactions")).andExpect(status().isUnauthorized());
        mockMvc.perform(patch("/api/admin/wallets/12345/freeze")).andExpect(status().isUnauthorized());
        mockMvc.perform(patch("/api/admin/wallets/12345/unfreeze")).andExpect(status().isUnauthorized());
    }

    // 2. Customer accessing admin endpoints returns 403
    @Test
    void customerAccessingAdminEndpoints_shouldReturn403() throws Exception {
        mockMvc.perform(get("/api/admin/customers").header("Authorization", CUSTOMER_TOKEN))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/api/admin/customers/test@mail.com").header("Authorization", CUSTOMER_TOKEN))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/api/admin/wallets").header("Authorization", CUSTOMER_TOKEN))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/api/admin/wallets/12345").header("Authorization", CUSTOMER_TOKEN))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/api/admin/transactions").header("Authorization", CUSTOMER_TOKEN))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/api/admin/transactions/ref-1").header("Authorization", CUSTOMER_TOKEN))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/api/admin/wallets/12345/transactions").header("Authorization", CUSTOMER_TOKEN))
                .andExpect(status().isForbidden());
        mockMvc.perform(patch("/api/admin/wallets/12345/freeze").header("Authorization", CUSTOMER_TOKEN))
                .andExpect(status().isForbidden());
        mockMvc.perform(patch("/api/admin/wallets/12345/unfreeze").header("Authorization", CUSTOMER_TOKEN))
                .andExpect(status().isForbidden());
    }

    // 3. Admin can access all admin endpoints
    @Test
    void adminCanAccessAllAdminEndpoints_shouldReturn200() throws Exception {
        when(adminService.getAllCustomers()).thenReturn(List.of(new AdminCustomerResponse()));
        mockMvc.perform(get("/api/admin/customers").header("Authorization", ADMIN_TOKEN))
                .andExpect(status().isOk());

        when(adminService.getCustomerByEmail("cust@mail.com")).thenReturn(new AdminCustomerResponse());
        mockMvc.perform(get("/api/admin/customers/cust@mail.com").header("Authorization", ADMIN_TOKEN))
                .andExpect(status().isOk());

        when(adminService.getAllWallets()).thenReturn(List.of(new AdminWalletResponse()));
        mockMvc.perform(get("/api/admin/wallets").header("Authorization", ADMIN_TOKEN))
                .andExpect(status().isOk());

        when(adminService.getWalletByAccountNumber("12345")).thenReturn(new AdminWalletResponse());
        mockMvc.perform(get("/api/admin/wallets/12345").header("Authorization", ADMIN_TOKEN))
                .andExpect(status().isOk());

        when(adminService.getAllTransactions()).thenReturn(List.of(new TransactionResponse()));
        mockMvc.perform(get("/api/admin/transactions").header("Authorization", ADMIN_TOKEN))
                .andExpect(status().isOk());

        when(adminService.getTransactionByReference("ref-1")).thenReturn(new TransactionResponse());
        mockMvc.perform(get("/api/admin/transactions/ref-1").header("Authorization", ADMIN_TOKEN))
                .andExpect(status().isOk());

        when(adminService.getTransactionsByWallet("12345")).thenReturn(List.of(new TransactionResponse()));
        mockMvc.perform(get("/api/admin/wallets/12345/transactions").header("Authorization", ADMIN_TOKEN))
                .andExpect(status().isOk());

        when(adminService.freezeWallet("12345")).thenReturn(new AdminWalletResponse());
        mockMvc.perform(patch("/api/admin/wallets/12345/freeze").header("Authorization", ADMIN_TOKEN))
                .andExpect(status().isOk());

        when(adminService.unfreezeWallet("12345")).thenReturn(new AdminWalletResponse());
        mockMvc.perform(patch("/api/admin/wallets/12345/unfreeze").header("Authorization", ADMIN_TOKEN))
                .andExpect(status().isOk());
    }

    // 4. Missing records return 404
    @Test
    void missingCustomer_shouldReturn404() throws Exception {
        when(adminService.getCustomerByEmail("unknown@mail.com")).thenThrow(new UserException("User not found"));
        mockMvc.perform(get("/api/admin/customers/unknown@mail.com").header("Authorization", ADMIN_TOKEN))
                .andExpect(status().isNotFound());
    }

    @Test
    void missingWallet_shouldReturn404() throws Exception {
        when(adminService.getWalletByAccountNumber("nonexistent")).thenThrow(new WalletException("Wallet does not exist"));
        mockMvc.perform(get("/api/admin/wallets/nonexistent").header("Authorization", ADMIN_TOKEN))
                .andExpect(status().isNotFound());
    }

    @Test
    void missingTransaction_shouldReturn404() throws Exception {
        when(adminService.getTransactionByReference("unknown-ref")).thenThrow(new WalletException("Transaction not found"));
        mockMvc.perform(get("/api/admin/transactions/unknown-ref").header("Authorization", ADMIN_TOKEN))
                .andExpect(status().isNotFound());
    }

    @Test
    void missingWalletTransactions_shouldReturn404() throws Exception {
        when(adminService.getTransactionsByWallet("nonexistent")).thenThrow(new WalletException("Wallet does not exist"));
        mockMvc.perform(get("/api/admin/wallets/nonexistent/transactions").header("Authorization", ADMIN_TOKEN))
                .andExpect(status().isNotFound());
    }

    @Test
    void freezeNonExistingWallet_shouldReturn404() throws Exception {
        when(adminService.freezeWallet("nonexistent")).thenThrow(new WalletException("Wallet does not exist"));
        mockMvc.perform(patch("/api/admin/wallets/nonexistent/freeze").header("Authorization", ADMIN_TOKEN))
                .andExpect(status().isNotFound());
    }

    @Test
    void unfreezeNonExistingWallet_shouldReturn404() throws Exception {
        when(adminService.unfreezeWallet("nonexistent")).thenThrow(new WalletException("Wallet does not exist"));
        mockMvc.perform(patch("/api/admin/wallets/nonexistent/unfreeze").header("Authorization", ADMIN_TOKEN))
                .andExpect(status().isNotFound());
    }
}
