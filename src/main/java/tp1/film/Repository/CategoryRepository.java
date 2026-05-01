package tp1.film.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tp1.film.Entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
}
