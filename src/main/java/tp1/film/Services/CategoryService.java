package tp1.film.Services;

import org.springframework.stereotype.Service;
import tp1.film.Entity.Category;
import tp1.film.Repository.CategoryRepository;
import tp1.film.Services.interfaces.CategoryInterfaces;

import java.util.List;
import java.util.Optional;

@Service
class CategoryService implements CategoryInterfaces {
    private CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Category save(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public Optional<Category> findById(int id) {
        return categoryRepository.findById(id);
    }

    @Override
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Override
    public Category update(Category category) {
        if (category.getId() < 0 || !categoryRepository.existsById(category.getId())) {
            throw new RuntimeException("Category not found");
        }
        return categoryRepository.save(category);
    }

    @Override
    public void deleteById(int id) {
        categoryRepository.deleteById(id);
    }
}
