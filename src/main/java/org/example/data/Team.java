package org.example.data;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    /** Q36. @OneToMany 기본 fetch = LAZY */
    @OneToMany(mappedBy = "team")
    private List<Player> players = new ArrayList<>();

    protected Team() {
    }

    public Team(String name) {
        this.name = name;
    }

    /** Q34. 변경 감지: 필드만 바꾸면 커밋 시 UPDATE 가 자동 실행된다 (save 호출 불필요) */
    public void changeName(String name) {
        this.name = name;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public List<Player> getPlayers() { return players; }
}
