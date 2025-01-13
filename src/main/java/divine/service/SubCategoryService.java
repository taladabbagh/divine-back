package divine.service;

import divine.dto.SubCategoryDTO;
import divine.model.Category;
import divine.model.SubCategory;
import divine.repository.CategoryRepository;
import divine.repository.SubCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubCategoryService {

    private final SubCategoryRepository subCategoryRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public SubCategoryService(SubCategoryRepository subCategoryRepository, CategoryRepository categoryRepository) {
        this.subCategoryRepository = subCategoryRepository;
        this.categoryRepository = categoryRepository;
    }

    // Fetch all subcategories
    public List<SubCategoryDTO> getAllSubCategories() {
        return subCategoryRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Fetch a subcategory by ID
    public SubCategoryDTO getSubCategoryById(Integer id) {
        SubCategory subCategory = subCategoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("SubCategory not found"));
        return convertToDTO(subCategory);
    }

    // Create or update a subcategory
    public SubCategoryDTO saveOrUpdateSubCategory(SubCategoryDTO subCategoryDTO) {
        SubCategory subCategory = convertToEntity(subCategoryDTO);
        SubCategory savedSubCategory = subCategoryRepository.save(subCategory);
        return convertToDTO(savedSubCategory);
    }

    // Delete a subcategory by ID
    public void deleteSubCategory(Integer id) {
        if (!subCategoryRepository.existsById(id)) {
            throw new RuntimeException("SubCategory not found");
        }
        subCategoryRepository.deleteById(id);
    }

    // Convert SubCategory entity to SubCategoryDTO
    private SubCategoryDTO convertToDTO(SubCategory subCategory) {
        SubCategoryDTO dto = new SubCategoryDTO();
        dto.setId(subCategory.getId());
        dto.setName(subCategory.getName());
        dto.setCategoryId(subCategory.getCategory().getId());
        return dto;
    }

    // Convert SubCategoryDTO to SubCategory entity
    private SubCategory convertToEntity(SubCategoryDTO subCategoryDTO) {
        SubCategory subCategory = new SubCategory();
        subCategory.setId(subCategoryDTO.getId());
        subCategory.setName(subCategoryDTO.getName());

        // Set the category if categoryId is provided
        if (subCategoryDTO.getCategoryId() != null) {
            Category category = categoryRepository.findById(subCategoryDTO.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found for ID: " + subCategoryDTO.getCategoryId()));
            subCategory.setCategory(category);
        }

        return subCategory;
    }
}
