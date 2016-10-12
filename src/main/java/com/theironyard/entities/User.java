package com.theironyard.entities;

import javax.persistence.*;

/**
 * Created by zach on 11/11/15.
 */
@Entity
@Table(name = "users")
public class User {
    public User() {
    }

    @Id
    @GeneratedValue
    Integer id;

    @Column(nullable = false)
    public String name;

    @Column(nullable = false)
    public String password;

    public User(String name, String password) {
        this.name = name;
        this.password = password;
    }
}
