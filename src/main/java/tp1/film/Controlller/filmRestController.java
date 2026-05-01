package tp1.film.Controlller;

import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tp1.film.Dto.FilmRequest;
import tp1.film.Dto.FilmUpdateRequest;
import tp1.film.Entity.Film;
import tp1.film.Services.interfaces.FilmInterfaces;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/films")
@AllArgsConstructor
public class filmRestController {
    private final FilmInterfaces filmService;

    @GetMapping
    public List<Film> listFilms() {
        return filmService.findAll();
    }
     @PreAuthorize("hasRole('ADMIN')") // Only users with ADMIN role can access this endpoint
    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> add(
            @RequestParam("titre") String titre,
            @RequestParam("description") String description,
            @RequestParam("annee") int annee,
            @RequestParam("idCategorie") int idCategorie,
            @RequestParam(value = "idActeurs", required = false) List<Integer> idActeurs,
            @RequestParam("image") MultipartFile image) throws IOException {

        if (filmService.getfilmbyTitre(titre)) {
            return ResponseEntity.badRequest().body("Film already exists with title: " + titre);
        }

        FilmRequest filmRequest = new FilmRequest();
        filmRequest.setTitre(titre);
        filmRequest.setDescription(description);
        filmRequest.setAnnee(annee);
        filmRequest.setIdCategorie(idCategorie);
        filmRequest.setIdActeurs(idActeurs != null ? idActeurs : List.of());
        filmRequest.setImage(image);

        return ResponseEntity.ok(filmService.saveWithImage(filmRequest));
    }

//
//    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
//    public Film addJson(@RequestBody FilmRequest filmRequest) {
//        return filmService.saveWithImage(filmRequest);
//    }

    @GetMapping("/{id}")
    public ResponseEntity<Film> getParId(@PathVariable int id) {
        return filmService.findById(id)
                .map(film -> ResponseEntity.ok(film))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    @PreAuthorize("hasRole('ADMIN')") 
    @DeleteMapping("/{id}")
    public String delete(@PathVariable int id) {
        filmService.deleteById(id);
        return "Le film avec l'ID " + id + " a été supprimé avec succès.";
    }

    @PreAuthorize("hasRole('ADMIN')") 
    @PutMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Film update(
            @RequestParam("id") int id,
            @RequestParam("titre") String titre,
            @RequestParam("description") String description,
            @RequestParam("annee") int annee,
            @RequestParam("idCategorie") int idCategorie,
            @RequestParam(value = "idActeurs", required = false) List<Integer> idActeurs,
            @RequestParam(value = "photo", required = false) MultipartFile photo) {

        FilmUpdateRequest film = new FilmUpdateRequest();
        film.setId(id);
        film.setTitre(titre);
        film.setDescription(description);
        film.setAnnee(annee);
        film.setIdCategorie(idCategorie);
        film.setIdActeurs(idActeurs != null ? idActeurs : List.of());
        film.setPhoto(photo);

        return filmService.update(film);
    }


}