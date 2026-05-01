package tp1.film.Services.interfaces;

import tp1.film.Entity.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryInterfaces {

    Category save(Category category);

    Optional<Category> findById(int id);

    List<Category> findAll();

    Category update(Category category);

    void deleteById(int id);
}
