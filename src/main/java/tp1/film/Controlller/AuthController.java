package tp1.film.Controlller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import tp1.film.Services.interfaces.AuthService;

@Controller
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/register")
    public String showRegisterForm() {
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String password,
            Model model) {
        try {
            authService.register(name, email, password);
            model.addAttribute("message", "Inscription réussie ! Veuillez vérifier votre boîte mail pour confirmer votre compte.");
            return "auth/login";

        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("name", name);
            model.addAttribute("email", email);
            return "auth/register";
        }
    }

    // Route GET pour valider le compte via token
    @GetMapping("/validate")
    public String validate(@RequestParam String token, Model model) {
        if (authService.confirmAccount(token)) {
            model.addAttribute("message", "Account activated! You can now login.");
            return "auth/login";
        }
        return "error";
    }

    // Route POST pour traiter le login (traité par Spring Security)
    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, Model model) {
        try {
            String token = authService.login(username, password);
            if (!token.equals("")) {
                model.addAttribute("token", token);
                return "redirect:/films/page/1";
            } else {
                model.addAttribute("error", "Login failed");
                return "auth/login";
            }

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "auth/login";
        }
    }

    // Route POST pour logout - invalide le token
    @PostMapping("/logout")
    public String logout(@RequestParam(required = false) String token) {
        if (token != null && !token.isEmpty()) {
            authService.logout(token);
        }
        return "redirect:/login";
    }
}
