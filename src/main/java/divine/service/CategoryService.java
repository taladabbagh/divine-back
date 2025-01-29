package divine.service;

import divine.dto.CategoryDTO;
import divine.dto.ProductDTO;
import divine.dto.SubCategoryDTO;
import divine.model.Category;
import divine.model.Product;
import divine.model.SubCategory;
import divine.repository.CategoryRepository;
import divine.repository.SubCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final SubCategoryRepository subCategoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository, SubCategoryRepository subCategoryRepository) {
        this.categoryRepository = categoryRepository;
        this.subCategoryRepository = subCategoryRepository;
    }

    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public CategoryDTO getCategoryById(Integer id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        return convertToDTO(category);
    }

    public CategoryDTO saveOrUpdateCategory(CategoryDTO categoryDTO) {
        Category category = convertToEntity(categoryDTO);
        Category savedCategory = categoryRepository.save(category);
        return convertToDTO(savedCategory);
    }

    public void deleteCategory(Integer id) {
        if (!categoryRepository.existsById(id)) {
            throw new RuntimeException("Category not found");
        }
        categoryRepository.deleteById(id);
    }

    private CategoryDTO convertToDTO(Category category) {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setGender(category.getGender());

        // Convert subcategories to DTO if they exist
        if (category.getSubCategories() != null) {
            List<SubCategoryDTO> subCategoryDTOs = category.getSubCategories().stream()
                    .map(this::convertSubCategoryToDTO)
                    .collect(Collectors.toList());
            dto.setSubCategories(subCategoryDTOs);
        }

        return dto;
    }

    private Category convertToEntity(CategoryDTO categoryDTO) {
        Category category = new Category();
        category.setId(categoryDTO.getId());
        category.setName(categoryDTO.getName());

        // Handle optional gender field
        if (categoryDTO.getGender() != null) {
            category.setGender(categoryDTO.getGender());
        }

        // Convert subcategories to entity if provided
        if (categoryDTO.getSubCategories() != null) {
            List<SubCategory> subCategoryEntities = categoryDTO.getSubCategories().stream()
                    .map(this::convertSubCategoryDTOToEntity)
                    .collect(Collectors.toList());
            category.setSubCategories(subCategoryEntities);
        } else {
            category.setSubCategories(null);
        }

        return category;
    }

    private SubCategoryDTO convertSubCategoryToDTO(SubCategory subCategory) {
        SubCategoryDTO subCategoryDTO = new SubCategoryDTO();
        subCategoryDTO.setId(subCategory.getId());
        subCategoryDTO.setName(subCategory.getName());
        return subCategoryDTO;
    }

    private SubCategory convertSubCategoryDTOToEntity(SubCategoryDTO subCategoryDTO) {
        SubCategory subCategory = new SubCategory();
        subCategory.setId(subCategoryDTO.getId());
        subCategory.setName(subCategoryDTO.getName());
        return subCategory;
    }

    public List<ProductDTO> getProductsByCategoryId(Integer id) {
        // Find the category by ID, or throw an exception if not found
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        // Stream through subcategories and collect all products
        return category.getSubCategories().stream()
                .flatMap(subCategory -> subCategory.getProducts().stream()) // Flatten the product streams
                .map(this::convertProductToDTO) // Convert to ProductDTO
                .collect(Collectors.toList());
    }

    private ProductDTO convertProductToDTO(Product product) {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(product.getId());
        productDTO.setName(product.getName());
        productDTO.setDescription(product.getDescription());
        productDTO.setPrice(product.getPrice());
        productDTO.setQuantity(product.getQuantity());
        return productDTO;
    }
}
