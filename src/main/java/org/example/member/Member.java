package org.example.member;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Q31. Spring Boot 3 부터 javax.persistence → jakarta.persistence */
@Entity
@Table(name = "members")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    protected Member() {
    }

    public Member(String name) {
        this.name = name;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
}
