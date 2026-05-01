package tp1.film.Services;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import tp1.film.Dto.FilmRequest;
import tp1.film.Dto.FilmUpdateRequest;
import tp1.film.Entity.Acteur;
import tp1.film.Entity.Category;
import tp1.film.Entity.Film;
import tp1.film.Repository.ActeurRepository;
import tp1.film.Repository.FIlmRepository;
import tp1.film.Services.interfaces.CategoryInterfaces;
import tp1.film.Services.interfaces.FilmInterfaces;
import tp1.film.utils.ImageUtils;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
class FilmService implements FilmInterfaces {
    private final FIlmRepository filmRepository;
    private final CategoryInterfaces categoryInterfaces;
    private final ActeurRepository acteurRepository;
    private final String uploadDirectory = System.getProperty("user.dir") + "/src/main/resources/static/photos";

    public FilmService(FIlmRepository filmRepository, CategoryInterfaces categoryInterfaces, ActeurRepository acteurRepository) {
        this.filmRepository = filmRepository;
        this.categoryInterfaces = categoryInterfaces;
        this.acteurRepository = acteurRepository;
    }

    @Override
    public Film save(Film film) {
        if (filmRepository.findByTitre(film.getTitre()) != null) {
            throw new RuntimeException("Film already exists with title: " + film.getTitre());
        }

        return filmRepository.save(film);
    }

    @Override
    public Optional<Film> findById(int id) {
        return filmRepository.findById(id);
    }

    @Override
    public List<Film> findAll() {
        return filmRepository.findAll();
    }


    @Override
    public boolean existsByCategoryId(int id) {
        return filmRepository.existsByCategoryId(id);
    }

    @Transactional
    @Override
    public void deleteByCategoryId(int id) {
        filmRepository.deleteByCategoryId(id);
    }

    @Override
    public Film update(FilmUpdateRequest film) {
        if (film.getId() < 0) {
            throw new RuntimeException("Film not found");
        }

        Film existingFilm = filmRepository.findById(film.getId())
                .orElseThrow(() -> new RuntimeException("Film not found"));


        existingFilm.setTitre(film.getTitre());
        existingFilm.setDescription(film.getDescription());
        existingFilm.setAnnepartution(film.getAnnee());

        Category category = categoryInterfaces.findById(film.getIdCategorie())
                .orElseThrow(() -> new RuntimeException("Category not found: " + film.getIdCategorie()));
        existingFilm.setCategory(category);

        List<Integer> acteurIds = film.getIdActeurs() != null ? film.getIdActeurs() : List.of();
        List<Acteur> acteurs = acteurRepository.findAllById(acteurIds);
        existingFilm.setActeurs(acteurs);

        if (film.getPhoto() != null && !film.getPhoto().isEmpty()) {
            try {
                String fileName = ImageUtils.saveImage(uploadDirectory, film.getPhoto());
                existingFilm.setPhoto(fileName);
            } catch (IOException e) {
                throw new RuntimeException("Unable to save image", e);
            }
        }

        return filmRepository.save(existingFilm);
    }


    @Override
    public boolean getfilmbyTitre(String titre) {
        return filmRepository.findByTitre(titre) != null;
    }

    @Override
    public void deleteById(int id) {
        filmRepository.deleteById(id);
    }

    @Override
    public Film saveWithImage(FilmRequest film) {
        Film entity = new Film();
        entity.setTitre(film.getTitre());
        entity.setDescription(film.getDescription());
        entity.setAnnepartution(film.getAnnee());


        Category category = categoryInterfaces.findById(film.getIdCategorie())
                .orElseThrow(() -> new RuntimeException("Category not found: " + film.getIdCategorie()));
        entity.setCategory(category);

        if (film.getIdActeurs() != null && !film.getIdActeurs().isEmpty()) {
            List<Acteur> acteurs = acteurRepository.findAllById(film.getIdActeurs());
            entity.setActeurs(acteurs);
        }

        if (film.getImage() != null && !film.getImage().isEmpty()) {
            try {
                String fileName = ImageUtils.saveImage(uploadDirectory, film.getImage());
                entity.setPhoto(fileName);
            } catch (IOException e) {
                throw new RuntimeException("Unable to save image", e);
            }
        }

        return filmRepository.save(entity);
    }


    @Override
    public Page<Film> findPaginetedFilms(int pageNum, int pageSize, String sortField, String sortDir,
                                         String keyword, Integer categoryId) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, sort);

        String kw = (keyword == null || keyword.isBlank()) ? null : keyword.trim();
        Integer catId = (categoryId != null && categoryId == 0) ? null : categoryId;

        return filmRepository.searchFilms(kw, catId, pageable);
    }
}
