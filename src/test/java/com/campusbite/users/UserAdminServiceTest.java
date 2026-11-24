package com.campusbite.users;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.campusbite.common.BusinessException;

/**
 * M2 User & Role Administration (owner: Lawk).
 * Unit tests for listing users and creating staff accounts.
 */
class UserAdminServiceTest {

    private InMemoryUserRepository repository;
    private UserAdminService service;

    @BeforeEach
    void setUp() {
        repository = new InMemoryUserRepository();
        service = new UserAdminService(repository, new BCryptPasswordEncoder());
    }

    private StaffForm staff(String email, Role role) {
        StaffForm form = new StaffForm();
        form.setFullName("New Cook");
        form.setEmail(email);
        form.setRole(role);
        form.setTemporaryPassword("Temp1234");
        return form;
    }

    @Test
    void searchFindsByNameOrEmailIgnoringCase() {
        List<User> found = service.listUsers("KITCHEN");

        assertThat(found).hasSize(1);
        assertThat(found.get(0).getEmail()).isEqualTo("kitchen@campusbite.test");
        assertThat(service.listUsers("  ")).hasSize(3);
    }

    @Test
    void createStaffSavesKitchenAccount() {
        User created = service.createStaff(staff("Cook@CampusBite.test", Role.KITCHEN));

        assertThat(created.getId()).isEqualTo(4L);
        assertThat(created.getRole()).isEqualTo(Role.KITCHEN);
        assertThat(created.getEmail()).isEqualTo("cook@campusbite.test");
        assertThat(created.isActive()).isTrue();
    }

    @Test
    void createStaffRejectsStudentRole() {
        assertThatThrownBy(() -> service.createStaff(staff("x@campusbite.test", Role.STUDENT)))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("KITCHEN or ADMIN");
    }

    @Test
    void createStaffRejectsDuplicateEmail() {
        assertThatThrownBy(() -> service.createStaff(staff("admin@campusbite.test", Role.ADMIN)))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("already exists");
    }
}
