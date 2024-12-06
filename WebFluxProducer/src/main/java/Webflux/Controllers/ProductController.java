package Webflux.Controllers;
import Webflux.DTO.ProductEvent;
import Webflux.Models.Product;
import Webflux.Services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ResponseEntity<Void>> createProducts(@RequestBody Flux<ProductEvent> productEvents) {
        return productService.createProducts(productEvents)
                .then(Mono.just(ResponseEntity.status(HttpStatus.CREATED).build()));
    }

    @PutMapping("/{id}")
    public Mono<Product> updateProduct(@PathVariable String id, @RequestBody ProductEvent productEvent) {
        return productService.updateProduct(id, productEvent);
    }
}