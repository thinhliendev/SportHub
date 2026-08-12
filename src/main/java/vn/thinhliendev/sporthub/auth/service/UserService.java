package vn.thinhliendev.sporthub.auth.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.thinhliendev.sporthub.auth.dto.RegistrationForm;
import vn.thinhliendev.sporthub.user.entity.Role;
import vn.thinhliendev.sporthub.user.entity.RoleName;
import vn.thinhliendev.sporthub.user.entity.User;
import vn.thinhliendev.sporthub.user.repository.RoleRepository;
import vn.thinhliendev.sporthub.user.repository.UserRepository;

import java.util.Locale;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User register(RegistrationForm form) {
        String normalizedEmail = form.getEmail().trim().toLowerCase(Locale.ROOT);
        if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new EmailAlreadyExistsException();
        }

        Role customerRole = roleRepository.findByName(RoleName.CUSTOMER)
                .orElseGet(() -> roleRepository.save(new Role(RoleName.CUSTOMER)));
        String phone = form.getPhone() == null || form.getPhone().isBlank() ? null : form.getPhone().trim();
        User user = new User(form.getFullName().trim(), normalizedEmail,
                passwordEncoder.encode(form.getPassword()), phone);
        user.getRoles().add(customerRole);
        return userRepository.save(user);
    }
}
