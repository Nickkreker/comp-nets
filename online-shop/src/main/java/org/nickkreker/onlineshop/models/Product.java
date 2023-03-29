package org.nickkreker.onlineshop.models;

import java.net.URI;
import java.net.URL;
import java.util.UUID;

public class Product {
    private UUID id;

    private String name;

    private String description;

    private URI image;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public URI getImage() {
        return image;
    }

    public void setImage(URI image) {
        this.image = image;
    }

    public Product(UUID id, String name, String description, URI image) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.image = image;
    }
}
