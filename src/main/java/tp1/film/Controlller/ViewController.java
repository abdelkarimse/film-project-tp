package tp1.film.Controlller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class ViewController {
    /// routee view page  login

    @GetMapping("/login")
    public String showLoginPage() {
        return "auth/login";
    }
}