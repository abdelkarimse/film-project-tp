package tp1.film.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tp1.film.Entity.Category;
import tp1.film.Entity.Film;

import java.util.List;

public interface FIlmRepository extends JpaRepository<Film, Integer> {
    List<Film> findByCategory(Category category);

    // Spring Data JPA creates the SQL for you!
    boolean existsByCategoryId(int categoryId);

    Film findByTitre(String titire);

    void deleteByCategoryId(int id);

    @Query("SELECT f FROM Film f WHERE " +
            "(:keyword IS NULL OR :keyword = '' OR LOWER(f.titre) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "(:categoryId IS NULL OR f.category.id = :categoryId)")
    Page<Film> searchFilms(@Param("keyword") String keyword,
                           @Param("categoryId") Integer categoryId,
                           Pageable pageable);
}
