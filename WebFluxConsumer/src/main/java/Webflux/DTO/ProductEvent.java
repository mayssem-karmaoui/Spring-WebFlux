package Webflux.DTO;

import Webflux.Models.Product;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "events")
@Data
public class ProductEvent {
    private String eventType;
    private Product product;

    public ProductEvent(String eventType, Product product) {
        this.eventType = eventType;
        this.product = product;
    }

    public ProductEvent() {
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
