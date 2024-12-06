package Webflux.Services;

import Webflux.DTO.ProductEvent;
import Webflux.Models.Product;
import Webflux.Repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ReactiveKafkaProducerTemplate<String, Object> kafkaTemplate;

    public ProductService(ProductRepository productRepository, ReactiveKafkaProducerTemplate<String, Object> kafkaTemplate) {
        this.productRepository = productRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    public Flux<Product> createProducts(Flux<ProductEvent> productEvents) {
        return productEvents
                .flatMap(productEvent -> productRepository.save(productEvent.getProduct())
                        .flatMap(savedProduct -> {
                            ProductEvent event = new ProductEvent("CreateProduct", savedProduct);
                            return kafkaTemplate.send("product-event-1", event).thenReturn(savedProduct);
                        }))
                .thenMany(productRepository.findAll());
    }


    public Mono<Product> updateProduct(String id, ProductEvent productEvent) {
        return productRepository.findById(id)
                .flatMap(existingProduct -> {
                    Product newProduct = productEvent.getProduct();
                    existingProduct.setName(newProduct.getName());
                    existingProduct.setPrice(newProduct.getPrice());
                    existingProduct.setDescription(newProduct.getDescription());
                    return productRepository.save(existingProduct)
                            .flatMap(productDO -> {
                                ProductEvent event = new ProductEvent("UpdateProduct", productDO);
                                return kafkaTemplate.send("product-event-1", event).then(Mono.just(productDO));
                            });
                });
    }
}
