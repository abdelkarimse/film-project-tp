package tp1.film.Services.interfaces;

import org.springframework.data.domain.Page;
import tp1.film.Dto.FilmRequest;
import tp1.film.Dto.FilmUpdateRequest;
import tp1.film.Entity.Film;

import java.util.List;
import java.util.Optional;

public interface FilmInterfaces {

    Film save(Film film);

    // Read
    Optional<Film> findById(int id);

    List<Film> findAll();

    boolean existsByCategoryId(int categoryId);

    void deleteByCategoryId(int categoryID);

    Page<Film> findPaginetedFilms(int pageNum, int pageSize, String sortField, String sortDir,
                                  String keyword, Integer categoryId);

    // Update
    Film update(FilmUpdateRequest film);

    // Delete
    void deleteById(int id);

    Film saveWithImage(FilmRequest film);


    boolean getfilmbyTitre(String titre);
}


