package tp1.film.Services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tp1.film.Entity.User;
import tp1.film.Repository.UserRepository;
import tp1.film.Services.interfaces.AuthService;
import tp1.film.Services.interfaces.SessionService;
import tp1.film.utils.TokenUtils;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenUtils tokenUtils;
    private final SessionService sessionService;
    private final EmailService emailService;

    @Override
    public User register(String name, String email, String password) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }
        validateUserInput(name, email, password);
        User user = User.builder()
                .name(name)
                .email(email)
                .password(passwordEncoder.encode(password))
                .role(User.RoleEnum.USER)
                .enabled(false)
                .build();
        // savec user
        User savedUser = userRepository.save(user);
        // savec toekn confiration

        String confToken = tokenUtils.generateToken(email, "conf");
        sessionService.createSession(savedUser, confToken, "conf");
        // envoire email
        emailService.sendEmail(email, "Verify Account",
                "Click here: http://localhost:8085/auth/validate?token=" + confToken);
        return savedUser;
    }
    // valide input

    private void validateUserInput(String name, String email, String password) {
        if (name.length() < 4 || name.length() > 20) {
            throw new RuntimeException("Le nom d'utilisateur doit contenir entre 4 et 20 caractères.");
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new RuntimeException("Le format de l'email est invalide.");
        }

        // Alphanumérique (au moins une lettre et un chiffre) + longueur min 6
        if (password.length() < 6 || !password.matches("^(?=.*[a-zA-Z])(?=.*[0-9]).+$")) {
            throw new RuntimeException("Le mot de passe doit être alphanumérique et faire au moins 6 caractères.");
        }
    }

    // login
    @Override
    public String login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        if (!user.isEnabled()) {
            throw new RuntimeException("Please confirm your email first");
        }

        String authToken = tokenUtils.generateToken(email, "auth");
        sessionService.createSession(user, authToken, "auth");

        return authToken;
    }

    // fonction pour confirm  acount
    @Override
    public boolean confirmAccount(String token) {
        String email = tokenUtils.extractUsername(token);

        if (sessionService.validateSession(token, "conf")) {
            User user = userRepository.findByEmail(email).orElseThrow();
            user.setEnabled(true);
            userRepository.save(user);
            sessionService.invalidateSession(token);
            return true;
        }
        return false;
    }

    @Override
    public void logout(String token) {
        sessionService.invalidateSession(token);
    }


}