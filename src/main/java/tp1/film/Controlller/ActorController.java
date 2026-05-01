package tp1.film.Controlller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import tp1.film.Entity.Acteur;
import tp1.film.Services.interfaces.IServiceActeur;

@Controller
@RequestMapping("/actors")
@AllArgsConstructor
public class ActorController {

    private final IServiceActeur serviceActeur;

    @GetMapping("/all")
    public String listActors(Model model) {
        model.addAttribute("actors", serviceActeur.getAllActeurs());
        return "films/actors";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("actor", new Acteur());
        return "films/addactors"; // chemin: src/main/resources/templates/actors/form.html
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") int id, Model model) {
        model.addAttribute("actor", serviceActeur.getActeurById(id));
        return "films/addactors";
    }

    @PostMapping("/save")
    public String saveActor(@ModelAttribute("actor") Acteur acteur) {
        serviceActeur.addActeur(acteur);
        return "redirect:/actors/all";
    }

    @DeleteMapping("/delete/{id}")
    public String deleteActor(@PathVariable("id") int id) {

        serviceActeur.deleteActeur(id);
        return "redirect:/actors/all";
    }

    @PostMapping("/delete/{id}")
    public String deleteActorPost(@PathVariable("id") int id) {
        serviceActeur.deleteActeur(id);
        return "redirect:/actors/all";
    }
}