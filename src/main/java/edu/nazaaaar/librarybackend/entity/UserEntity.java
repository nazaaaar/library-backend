package edu.nazaaaar.librarybackend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.Set;

@Entity
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String pib;
    private String password;
    private String address;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<UserBookReading> userBookReadings;
    // Constructors, getters, and setters

    public UserEntity() {
    }

    public UserEntity(Long id, String username, String pib, String password, String address) {
        this.id = id;
        this.username = username;
        this.pib = pib;
        this.password = password;
        this.address = address;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public String getPib() {
        return pib;
    }

    public void setPib(String PIB) {
        this.pib = PIB;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String adress) {
        this.address = adress;
    }

    public Set<UserBookReading> getUserBookReadings() {
        return userBookReadings;
    }

    public void setUserBookReadings(Set<UserBookReading> userBookReadings) {
        this.userBookReadings = userBookReadings;
    }
}
