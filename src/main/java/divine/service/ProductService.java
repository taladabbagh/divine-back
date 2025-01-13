package divine.service;

import divine.model.Product;
import divine.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    private static final String IMAGE_UPLOAD_DIR = "uploads/product-images/";

    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    public Product updateProduct(Integer productId, Product productDetails) {
        Optional<Product> existingProduct = productRepository.findById(productId);
        if (existingProduct.isPresent()) {
            Product product = existingProduct.get();
            product.setName(productDetails.getName());
            product.setDescription(productDetails.getDescription());
            product.setPrice(productDetails.getPrice());
            product.setQuantity(productDetails.getQuantity());
            product.setSubCategory(productDetails.getSubCategory());
            return productRepository.save(product);
        }
        return null;
    }

    public void deleteProduct(Integer productId) {
        productRepository.deleteById(productId);
    }

    public Product getProductById(Integer productId) {
        return productRepository.findById(productId).orElse(null);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public List<Product> getProductsBySubCategory(Integer subCategoryId) {
        return productRepository.findBySubCategoryId(subCategoryId);
    }

    public void uploadProductImage(Integer productId, MultipartFile image) throws IOException {
        Optional<Product> productOptional = productRepository.findById(productId);

        if (productOptional.isPresent()) {
            Product product = productOptional.get();

            // Ensure the directory exists
            File uploadDir = new File(IMAGE_UPLOAD_DIR);
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
}
