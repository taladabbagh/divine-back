package divine.repository;

import divine.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    List<Category> findByGender(String gender);

    // Fetch categories where gender is NULL or empty
    @Query("SELECT c FROM Category c WHERE c.gender IS NULL OR c.gender = ''")
    List<Category> findCategoriesWithoutGender();

}
