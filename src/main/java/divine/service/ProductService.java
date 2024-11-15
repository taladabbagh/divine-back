package divine.service;

import divine.model.Product;
import divine.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    public Product updateProduct(Integer productId, Product productDetails) {
        Optional<Product> existingProduct = productRepository.findById(productId); // No need to cast to Long
        if (existingProduct.isPresent()) {
            Product product = existingProduct.get();
            product.setName(productDetails.getName());
            product.setDescription(productDetails.getDescription());
            product.setPrice(productDetails.getPrice());
            product.setQuantity(productDetails.getQuantity());
            product.setSubCategory(productDetails.getSubCategory());
            return productRepository.save(product);
        }
        return null;  // Consider throwing an exception or using Optional
    }

    public void deleteProduct(Integer productId) {
        productRepository.deleteById(productId);  // No need to cast to Long
    }

    public Product getProductById(Integer productId) {
        return productRepository.findById(productId).orElse(null);  // Consider throwing an exception or using Optional
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public List<Product> getProductsBySubCategory(Integer subCategoryId) {
        return productRepository.findBySubCategoryId(subCategoryId);
    }
}
