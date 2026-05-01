package tp1.film.Controlller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {
    @GetMapping
    public Authentication getProfile(Authentication authentication) {
        // Logic to retrieve and return user profile information
        return authentication;
    }
}