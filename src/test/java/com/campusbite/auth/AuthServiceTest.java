package com.campusbite.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.campusbite.common.BusinessException;
import com.campusbite.users.InMemoryUserRepository;
import com.campusbite.users.Role;
import com.campusbite.users.User;

/**
 * M1 Authentication & Accounts (owner: Lawk).
 * Unit tests for AuthService. No Spring: we create the objects ourselves with "new",
 * which is exactly what constructor injection makes possible.
 */
class AuthServiceTest {

    private InMemoryUserRepository repository;
    private PasswordEncoder encoder;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        repository = new InMemoryUserRepository();
        encoder = new BCryptPasswordEncoder();
        authService = new AuthService(repository, encoder);
    }

    private RegisterForm form(String name, String email, String password, String confirm) {
        RegisterForm form = new RegisterForm();
        form.setFullName(name);
        form.setEmail(email);
        form.setPassword(password);
        form.setConfirmPassword(confirm);
        return form;
    }

    @Test
    void startsWithOneDemoUserPerRole() {
        assertThat(repository.findAll(null)).hasSize(3);
        assertThat(repository.findByEmail("admin@campusbite.test").get().getRole()).isEqualTo(Role.ADMIN);
    }

    @Test
    void registerCreatesActiveStudentWithHashedPassword() {
        User user = authService.register(form("Sara Ali", "Sara@Uni.edu", "secret123", "secret123"));

        assertThat(user.getId()).isNotNull();
        assertThat(user.getRole()).isEqualTo(Role.STUDENT);
        assertThat(user.isActive()).isTrue();
        assertThat(user.getEmail()).isEqualTo("sara@uni.edu");          // stored in lower case
        assertThat(user.getPasswordHash()).isNotEqualTo("secret123");   // never plain text
        assertThat(encoder.matches("secret123", user.getPasswordHash())).isTrue();
    }

    @Test
    void registerRejectsDuplicateEmailIgnoringCase() {
        assertThatThrownBy(() -> authService.register(
                form("Copy", "STUDENT@campusbite.test", "secret123", "secret123")))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    void registerRejectsDifferentPasswords() {
        assertThatThrownBy(() -> authService.register(
                form("Sara Ali", "sara@uni.edu", "secret123", "secret124")))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("do not match");
    }

    @Test
    void loginWithRightPasswordReturnsSessionUser() {
        SessionUser user = authService.login(" Kitchen@CampusBite.test ", "Pass123!");

        assertThat(user.getRole()).isEqualTo(Role.KITCHEN);
        assertThat(user.isStaff()).isTrue();
        assertThat(user.getFullName()).isEqualTo("Demo Kitchen");
    }

    @Test
    void loginWithWrongPasswordOrUnknownEmailGivesSameMessage() {
        assertThatThrownBy(() -> authService.login("student@campusbite.test", "wrong"))
                .isInstanceOf(BusinessException.class)
                .hasMessage(AuthService.LOGIN_FAILED);
        assertThatThrownBy(() -> authService.login("nobody@campusbite.test", "Pass123!"))
                .isInstanceOf(BusinessException.class)
                .hasMessage(AuthService.LOGIN_FAILED);
    }

    @Test
    void loginFailsForDeactivatedUser() {
        User student = repository.findByEmail("student@campusbite.test").get();
        student.setActive(false);
        repository.update(student);

        assertThatThrownBy(() -> authService.login("student@campusbite.test", "Pass123!"))
                .isInstanceOf(BusinessException.class)
                .hasMessage(AuthService.LOGIN_FAILED);
    }

    @Test
    void updateProfileSavesTheNewName() {
        ProfileForm profile = new ProfileForm();
        profile.setFullName("  New Name ");

        authService.updateProfile(1L, profile);

        assertThat(authService.getUser(1L).getFullName()).isEqualTo("New Name");
    }
}
