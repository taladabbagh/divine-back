package divine.service;

import divine.dto.ProductDTO;
import divine.model.Product;
import divine.model.SubCategory;
import divine.repository.ProductRepository;
import divine.repository.SubCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final SubCategoryRepository subCategoryRepository;

    @Autowired
    public ProductService(ProductRepository productRepository, SubCategoryRepository subCategoryRepository) {
        this.productRepository = productRepository;
        this.subCategoryRepository = subCategoryRepository;
    }

    // Fetch all products
    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Fetch a product by ID
    public ProductDTO getProductById(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return convertToDTO(product);
    }

    // Fetch products by SubCategory ID
    public List<ProductDTO> getProductsBySubCategory(Integer subCategoryId) {
        List<Product> products = productRepository.findBySubCategoryId(subCategoryId);

        // Convert products to ProductDTOs and return
        return products.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Create or update a product
    public ProductDTO saveOrUpdateProduct(ProductDTO productDTO) {
        SubCategory subCategory = subCategoryRepository.findById(productDTO.getSubCategoryId())
                .orElseThrow(() -> new RuntimeException("SubCategory not found"));
        Product product = convertToEntity(productDTO, subCategory);
        Product savedProduct = productRepository.save(product);
        return convertToDTO(savedProduct);
    }

    // Delete a product by ID
    public void deleteProduct(Integer id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found");
        }
        productRepository.deleteById(id);
    }

    // Upload product image
    public void uploadProductImage(Integer productId, MultipartFile image) throws IOException {
        Optional<Product> productOptional = productRepository.findById(productId);

        if (productOptional.isPresent()) {
            Product product = productOptional.get();

            // Ensure the directory exists
            File uploadDir = new File("uploads/product-images/");
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // Save the file
            String fileName = "product-" + productId + "-" + image.getOriginalFilename();
            File file = new File(uploadDir, fileName);
            image.transferTo(file);

            // Update the product with the image URL
            String imageUrl = "/api/products/images/" + fileName; // Example URL
            product.setImageUrl(imageUrl);
            productRepository.save(product);
        } else {
            throw new IllegalArgumentException("Product not found with ID: " + productId);
        }
    }

    // Convert Product entity to ProductDTO
    private ProductDTO convertToDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setQuantity(product.getQuantity());
        dto.setSubCategoryId(product.getSubCategory().getId());
        dto.setImageUrl(product.getImageUrl());
        return dto;
    }

    // Convert ProductDTO to Product entity
    private Product convertToEntity(ProductDTO productDTO, SubCategory subCategory) {
        Product product = new Product();
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setQuantity(productDTO.getQuantity());
        product.setSubCategory(subCategory);
        return product;
    }
}
