package com.example.asm1.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "Users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "first_name")
    private String firstName;

    private String surname;

    @Column(name = "shopping_preference")
    private String shoppingPreference;

    @Column(name = "dob_day")
    private Integer dobDay;

    @Column(name = "dob_month")
    private Integer dobMonth;

    @Column(name = "dob_year")
    private Integer dobYear;

    @Column(name = "email_signup")
    private boolean emailSignup;

    @Column(name = "agree_terms")
    private boolean agreeTerms;

    @Column(name = "role_id", nullable = false)
    private Integer roleId;
}
