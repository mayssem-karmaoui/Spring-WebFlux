package Webflux.Services;

import Webflux.DTO.ProductEvent;
import Webflux.Models.Product;
import Webflux.Repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    private final Flux<Product> productFlux;
    private FluxSink<Product> productSink;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
        this.productFlux = Flux.<Product>create(sink -> this.productSink = sink).share();
    }

    public Flux<Product> getProducts() {
        return productRepository.findAll();
    }

    public Flux<Product> getAllReactive() {
        return this.productFlux.delayElements(Duration.ofMillis(500));
    }

    @KafkaListener(topics = "product-event-1", groupId = "product-group-1")
    public void processProductEvents(ProductEvent productEvent) {
        Product product = productEvent.getProduct();
        if ("CreateProduct".equals(productEvent.getEventType())) {
            productRepository.save(product)
                    .doOnNext(savedProduct -> {
                        initializeProductSink();
                        productSink.next(savedProduct);
                    })
                    .subscribe();
        }
        if ("UpdateProduct".equals(productEvent.getEventType())) {
            productRepository.findById(product.getId())
                    .flatMap(existingProduct -> {
                        existingProduct.setName(product.getName());
                        existingProduct.setPrice(product.getPrice());
                        existingProduct.setDescription(product.getDescription());
                        return productRepository.save(existingProduct);
                    })
                    .doOnNext(updatedProduct -> {
                        initializeProductSink();
                        productSink.next(updatedProduct);
                    })
                    .subscribe();
        }
    }

    private synchronized void initializeProductSink() {
        if (productSink == null) {
            this.productFlux.subscribe(sink -> this.productSink = (FluxSink<Product>) sink);
        }
    }
}
