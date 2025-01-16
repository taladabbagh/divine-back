package divine.controller;

import divine.dto.SubCategoryDTO;
import divine.service.SubCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/subcategories")
public class SubCategoryController {

    private final SubCategoryService subCategoryService;

    @Autowired
    public SubCategoryController(SubCategoryService subCategoryService) {
        this.subCategoryService = subCategoryService;
    }

    @GetMapping
    public ResponseEntity<List<SubCategoryDTO>> getAllSubCategories() {
        List<SubCategoryDTO> subCategories = subCategoryService.getAllSubCategories();
        return ResponseEntity.ok(subCategories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubCategoryDTO> getSubCategoryById(@PathVariable Integer id) {
        SubCategoryDTO subCategory = subCategoryService.getSubCategoryById(id);
        return ResponseEntity.ok(subCategory);
    }

    @PostMapping
    public ResponseEntity<SubCategoryDTO> createSubCategory(@RequestBody SubCategoryDTO subCategoryDTO) {
        SubCategoryDTO createdSubCategory = subCategoryService.saveOrUpdateSubCategory(subCategoryDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSubCategory);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SubCategoryDTO> updateSubCategory(@PathVariable Integer id, @RequestBody SubCategoryDTO subCategoryDTO) {
        subCategoryDTO.setId(id);
        SubCategoryDTO updatedSubCategory = subCategoryService.saveOrUpdateSubCategory(subCategoryDTO);
        return ResponseEntity.ok(updatedSubCategory);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubCategory(@PathVariable Integer id) {
        subCategoryService.deleteSubCategory(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<SubCategoryDTO>> getSubCategoriesByCategoryId(@PathVariable Integer categoryId) {
        List<SubCategoryDTO> subCategories = subCategoryService.getSubCategoriesByCategoryId(categoryId);
        return ResponseEntity.ok(subCategories);
    }
}
