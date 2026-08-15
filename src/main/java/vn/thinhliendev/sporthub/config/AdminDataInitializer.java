package vn.thinhliendev.sporthub.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.thinhliendev.sporthub.user.entity.Role;
import vn.thinhliendev.sporthub.user.entity.RoleName;
import vn.thinhliendev.sporthub.user.entity.User;
import vn.thinhliendev.sporthub.user.repository.RoleRepository;
import vn.thinhliendev.sporthub.user.repository.UserRepository;

import java.util.Locale;

@Component
@Profile("dev")
public class AdminDataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminEmail;
    private final String adminPassword;

    public AdminDataInitializer(UserRepository userRepository,
                                RoleRepository roleRepository,
                                PasswordEncoder passwordEncoder,
                                @Value("${app.admin.email:}") String adminEmail,
                                @Value("${app.admin.password:}") String adminPassword) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (adminEmail.isBlank() || adminPassword.isBlank()) {
            return;
        }
        if (adminPassword.length() < 8 || adminPassword.length() > 72) {
            throw new IllegalStateException("APP_ADMIN_PASSWORD must contain 8 to 72 characters");
        }

        String normalizedEmail = adminEmail.trim().toLowerCase(Locale.ROOT);
        Role adminRole = roleRepository.findByName(RoleName.ADMIN)
                .orElseGet(() -> roleRepository.save(new Role(RoleName.ADMIN)));

        User admin = userRepository.findByEmailIgnoreCase(normalizedEmail)
                .orElseGet(() -> new User("SportHub Admin", normalizedEmail,
                        passwordEncoder.encode(adminPassword), null));
        admin.getRoles().add(adminRole);
        userRepository.save(admin);
    }
}
