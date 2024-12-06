package Webflux.Controllers;

import Webflux.Models.Product;
import Webflux.Services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public Flux<Product> getProducts() {
        return productService.getProducts();
    }

    @GetMapping(value = "/all-reactive", produces = {"text/event-stream"})
    public Flux<Product> getAllReactive() {
        return productService.getAllReactive();
    }

}
