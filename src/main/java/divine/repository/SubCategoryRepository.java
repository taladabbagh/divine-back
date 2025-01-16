package divine.repository;

import divine.model.SubCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface SubCategoryRepository extends JpaRepository<SubCategory, Integer> {
    List<SubCategory> findByCategoryId(Integer categoryId);
}
