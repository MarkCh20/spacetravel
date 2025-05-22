package com.spacetravel.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "planet")
public class Planet {

    @Id
    @Column(length = 10)
    private String id;

    @Column(nullable = false, length = 500)
    private String name;

    public Planet() {}

    public Planet(String id, String name) {
        this.id = id;
        this.name = name;
    }

    // Getter-Setter
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
