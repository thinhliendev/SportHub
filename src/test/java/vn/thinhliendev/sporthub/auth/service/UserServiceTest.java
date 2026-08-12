package vn.thinhliendev.sporthub.auth.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import vn.thinhliendev.sporthub.auth.dto.RegistrationForm;
import vn.thinhliendev.sporthub.user.entity.Role;
import vn.thinhliendev.sporthub.user.entity.RoleName;
import vn.thinhliendev.sporthub.user.entity.User;
import vn.thinhliendev.sporthub.user.repository.RoleRepository;
import vn.thinhliendev.sporthub.user.repository.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock UserRepository userRepository;
    @Mock RoleRepository roleRepository;
    @Mock PasswordEncoder passwordEncoder;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, roleRepository, passwordEncoder);
    }

    @Test
    void registerEncryptsPasswordNormalizesEmailAndAssignsCustomerRole() {
        RegistrationForm form = validForm();
        Role customer = new Role(RoleName.CUSTOMER);
        when(userRepository.existsByEmailIgnoreCase("user@example.com")).thenReturn(false);
        when(roleRepository.findByName(RoleName.CUSTOMER)).thenReturn(Optional.of(customer));
        when(passwordEncoder.encode("password123")).thenReturn("bcrypt-hash");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userService.register(form);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User saved = captor.getValue();
        assertThat(saved.getEmail()).isEqualTo("user@example.com");
        assertThat(saved.getPassword()).isEqualTo("bcrypt-hash");
        assertThat(saved.getPassword()).isNotEqualTo(form.getPassword());
        assertThat(saved.getRoles()).extracting(Role::getName).containsExactly(RoleName.CUSTOMER);
    }

    @Test
    void registerRejectsDuplicateEmail() {
        RegistrationForm form = validForm();
        when(userRepository.existsByEmailIgnoreCase("user@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.register(form))
                .isInstanceOf(EmailAlreadyExistsException.class);
        verify(userRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(any());
    }

    private RegistrationForm validForm() {
        RegistrationForm form = new RegistrationForm();
        form.setFullName("Nguyen Van A");
        form.setEmail(" User@Example.com ");
        form.setPhone("0901234567");
        form.setPassword("password123");
        form.setConfirmPassword("password123");
        return form;
    }
}
