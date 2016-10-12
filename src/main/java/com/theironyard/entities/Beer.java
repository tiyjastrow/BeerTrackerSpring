package com.theironyard.entities;

import javax.persistence.*;

/**
 * Created by zach on 11/10/15.
 */
@Entity
@Table(name = "beers")
public class Beer {
    @Id
    @GeneratedValue
    Integer id;

    @Column(nullable = false)
    public String name;

    public Beer() {
    }

    @Column(nullable = false)
    public String type;

    @Column(nullable = false)
    public Integer calories;

    @ManyToOne
    public User user;

    public Beer(String name, String type, Integer calories, User user) {
        this.name = name;
        this.type = type;
        this.calories = calories;
        this.user = user;
    }
}
