package tp1.film.Controlller;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import tp1.film.Entity.Film;
import tp1.film.Services.interfaces.AuthService;
import tp1.film.Services.interfaces.CategoryInterfaces;
import tp1.film.Services.interfaces.FilmInterfaces;


@Controller
@AllArgsConstructor
public class HomeController {

    private final CategoryInterfaces categoryService;
    private final FilmInterfaces filmService;
    private final AuthService authService;

    @GetMapping("/")
    public String showRoot(Model model) {
        return "redirect:/home/page/1";
    }

    @GetMapping("/home")
    public String showHome(Model model) {
        return "redirect:/home/page/1";
    }

    @GetMapping("/home/page/{pageNum}")
    public String showHomePaginated(
            @PathVariable int pageNum,
            @RequestParam(value = "sortField", defaultValue = "titre") String sortField,
            @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir,
            Model model) {

        int pageSize = 8;
        Page<Film> page = filmService.findPaginetedFilms(pageNum, pageSize, sortField, sortDir, "", 0);

        model.addAttribute("films", page.getContent());
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("currentPage", pageNum);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        return "home/home";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String showRegisterForm() {
        return "auth/register";
    }

    @PostMapping("/logout")
    public String logout(@RequestParam(required = false) String token) {
        if (token != null && !token.isEmpty()) {
            authService.logout(token);
        }
        return "redirect:/login";
    }
}
