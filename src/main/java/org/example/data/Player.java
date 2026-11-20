package org.example.data;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    /** Q36. @ManyToOne 기본은 EAGER → 원칙적으로 LAZY 로 바꾼다 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    protected Player() {
    }

    public Player(String name, Team team) {
        this.name = name;
        this.team = team;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public Team getTeam() { return team; }
}
