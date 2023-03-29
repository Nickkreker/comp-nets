package org.nickkreker.onlineshop.services;

import org.nickkreker.onlineshop.models.Product;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;


@Service
public class ProductService {
    private static final List<Product> products = new ArrayList<>(Arrays.asList(
            new Product(UUID.randomUUID(), "Coca-Cola", "Газированный безалкогольный напиток",  URI.create("Coca-Cola.png")),
            new Product(UUID.randomUUID(), "Snickers", "Шоколадный батончик с жареным арахисом", URI.create("Snickers.png")),
            new Product(UUID.randomUUID(), "JJ CCR", "Дыхательный аппарат", URI.create("Rebreather.png")),
            new Product(UUID.randomUUID(), "Shearwater Teric", "Лучший дайверский компьютер", URI.create("Divecomputer.png"))
    ));

    private static final String MSG_NOT_FOUND = "Продукт с идентификатором {0} не найден";
    private static final String MSG_IDS_NOT_EQUAL = "Идентификатор в URL не соответствует идентификатору объекта";

    public List<Product> getAll() {
        return products;
    }

    public ResponseEntity<Object> get(UUID id) {
        var product = products.stream().filter(p -> p.getId().equals(id)).findFirst();
        return product
                .map(p -> ResponseEntity.status(HttpStatus.OK).body((Object)p))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                                               .body(Map.of("message", MessageFormat.format(MSG_NOT_FOUND, id)))
                );
    }

    public Product create(Product product) {
        product.setId(UUID.randomUUID());
        products.add(product);
        return product;
    }

    public ResponseEntity<Object> update(Product newProduct, UUID id) {
        if (!Objects.equals(id, newProduct.getId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                 .body(Map.of("message", MSG_IDS_NOT_EQUAL));
        }

        boolean found = false;
        for (Product product : products) {
            if (product.getId().equals(newProduct.getId())) {
                product.setName(newProduct.getName());
                product.setDescription(newProduct.getDescription());
                found = true;
            }
        }

        if (!found) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body(Map.of("message", MessageFormat.format(MSG_NOT_FOUND, newProduct.getId())));
        }
        return ResponseEntity.status(HttpStatus.OK).body((Object)newProduct);
    }

    public void delete(UUID id) {
        products.removeIf(p -> Objects.equals(p.getId(), id));
    }
}
