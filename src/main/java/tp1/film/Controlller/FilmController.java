package tp1.film.Controlller;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import tp1.film.Entity.Category;
import tp1.film.Entity.Film;
import tp1.film.Services.ServiceActeur;
import tp1.film.Services.interfaces.CategoryInterfaces;
import tp1.film.Services.interfaces.FilmInterfaces;


@Controller
@RequestMapping("/films")
@AllArgsConstructor
public class FilmController {

    private final FilmInterfaces filmService;
    private final CategoryInterfaces categoryService;
    private final ServiceActeur serviceActeur;

    private final String uploadDirectory = System.getProperty("user.dir") + "/src/main/resources/static/photos";

    @GetMapping
    public String listFilms() {
        return "redirect:/films/page/1?sortField=titre&sortDir=asc";
    }

    @GetMapping("/page/{pageNum}")
    public String listFilmsPaginated(
            @PathVariable int pageNum,
            @RequestParam(value = "sortField", defaultValue = "titre") String sortField,
            @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir,
            @RequestParam(value = "keyword", defaultValue = "") String keyword,
            @RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId,
            Model model) {

        int pageSize = 8;
        Page<Film> page = filmService.findPaginetedFilms(pageNum, pageSize, sortField, sortDir, keyword, categoryId);

        model.addAttribute("films", page.getContent());
        model.addAttribute("currentPage", pageNum);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("keyword", keyword);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("categories", categoryService.findAll());

        return "films/list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("film", new Film());
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("actors", serviceActeur.getAllActeurs());
        return "films/add"; // template : add.html
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        return filmService.findById(id)
                .map(film -> {
                    model.addAttribute("film", film);
                    model.addAttribute("categories", categoryService.findAll());
                    model.addAttribute("actors", serviceActeur.getAllActeurs());
                    return "films/add";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Film introuvable.");
                    return "redirect:/films";
                });
    }

    @PostMapping("/save")
    public String saveFilm(@ModelAttribute Film film, @RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        try {
            if (!file.isEmpty()) {
                String fileName = tp1.film.utils.ImageUtils.saveImage(uploadDirectory, file);
                if (fileName != null) {
                    film.setPhoto(fileName);
                }
            } else if (film.getId() != 0) {
                filmService.findById(film.getId()).ifPresent(existingFilm -> film.setPhoto(existingFilm.getPhoto()));
            }
            filmService.save(film);
            redirectAttributes.addFlashAttribute("success", "Film enregistré avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de l'enregistrement : " + e.getMessage());

            return "redirect:/films/add";
        }

        return "redirect:/films";
    }

    @DeleteMapping("/delete/{id}")
    public String deleteFilm(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            filmService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Le film a été supprimé avec succès.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Impossible de supprimer le film. Il est peut-être utilisé ailleurs.");
            System.err.println("Error deleting film: " + e.getMessage());
        }
        return "redirect:/films";
    }

    @PostMapping("/delete/{id}")
    public String deleteFilmPost(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        return deleteFilm(id, redirectAttributes);
    }

    @GetMapping("/categories")
    public String listcategories(Model model) {
        model.addAttribute("categories", categoryService.findAll());
        System.out.println(categoryService.findAll());

        return "films/categories";
    }


    @GetMapping("/addcategories")
    public String showAddCategoryForm(Model model) {
        model.addAttribute("category", new Category());
        return "films/addcategories";
    }

    @PostMapping("/addcategories")
    public String addcategories(@ModelAttribute Category category) {
        categoryService.save(category);
        return "films/categories";
    }

    @GetMapping("/film/{id}")
    public String showFilmDetail(@PathVariable int id, Model model) {
        filmService.findById(id).ifPresent(film -> {
            model.addAttribute("film", film);
        });
        return "films/detail";
    }

    @GetMapping("/categorie/{id}")
    public String showCategoryProducts(@PathVariable int id, Model model) {
        model.addAttribute("category", categoryService.findById(id).orElse(null));
        model.addAttribute("films", filmService.findAll().stream()
                .filter(f -> f.getCategory() != null && f.getCategory().getId() == id)
                .toList());
        return "films/category-products";
    }

    private static final String SUCCESS_MSG = "Catégorie supprimée avec succès !";
    private static final String REDIRECT_PATH = "redirect:/films/categories";

    @GetMapping("/categories/delete/{id}")
    public String deleteCategory(@PathVariable int id, RedirectAttributes redirectAttributes) {
        if (filmService.existsByCategoryId(id)) {
            redirectAttributes.addFlashAttribute("error", "Impossible : Des films sont encore associés à cette catégorie !");
            redirectAttributes.addFlashAttribute("categorieid", id);
            return REDIRECT_PATH;
        }
        return executeDelete(id, redirectAttributes);
    }

    @GetMapping("/categories/Castdelete/{id}")
    public String forceDeleteCategory(@PathVariable int id, RedirectAttributes redirectAttributes) {
        filmService.deleteByCategoryId(id);

        return executeDelete(id, redirectAttributes);
    }

    private String executeDelete(int id, RedirectAttributes redirectAttributes) {
        try {
            categoryService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", SUCCESS_MSG);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur technique : Impossible de supprimer la catégorie (contrainte SQL).");
        }
        return REDIRECT_PATH;
    }
}


