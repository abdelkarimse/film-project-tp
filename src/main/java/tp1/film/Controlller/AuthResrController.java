package tp1.film.Controlller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import tp1.film.Entity.User;
import tp1.film.Services.interfaces.AuthService;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthResrController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<User> register(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String password,
            Model model) {
        try {
            User user = authService.register(name, email, password);
            return ResponseEntity.ok().body(user);
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("name", name);
            model.addAttribute("email", email);
            return ResponseEntity.badRequest().body(null);
        }
    }


    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String username, @RequestParam String password, Model model) {
        try {
            String token = authService.login(username, password);
            if (!token.equals("")) {
                model.addAttribute("token", token);
                return ResponseEntity.ok().body(token);
            } else {
                model.addAttribute("error", "Login failed");
                return ResponseEntity.badRequest().body(null);
            }
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return ResponseEntity.badRequest().body(null);
        }
    }

    // Route POST pour logout - invalide le token
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestParam(required = false) String token) {
        if (token != null && !token.isEmpty()) {
            authService.logout(token);
        }
        return ResponseEntity.ok().body("Logged out successfully");
    }
}
