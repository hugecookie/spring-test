package org.example.data;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import org.example.member.MemberRepository;
import org.hibernate.Hibernate;
import org.hibernate.LazyInitializationException;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.transaction.support.TransactionTemplate;

/** 7. 데이터 접근과 JPA (Q32 ~ Q36) */
@SpringBootTest
class DataAccessTest {

    @Autowired MemberJdbcRepository memberJdbcRepository;
    @Autowired MemberRepository memberRepository;
    @Autowired TeamRepository teamRepository;
    @Autowired PlayerRepository playerRepository;
    @Autowired TransactionTemplate tx;
    @Autowired EntityManagerFactory emf;
    @PersistenceContext EntityManager em;

    Statistics stats;

    @BeforeEach
    void setUp() {
        for (int i = 1; i <= 3; i++) {
            Team team = teamRepository.save(new Team("team" + i));
            playerRepository.save(new Player("player" + i + "-a", team));
            playerRepository.save(new Player("player" + i + "-b", team));
        }
        stats = emf.unwrap(SessionFactory.class).getStatistics();
        stats.clear();
    }

    @AfterEach
    void tearDown() {
        playerRepository.deleteAll();
        teamRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("Q32. JdbcTemplate: SQL 과 RowMapper 만 작성")
    void q32_jdbcTemplate() {
        memberJdbcRepository.save("jdbc1");
        memberJdbcRepository.save("jdbc2");
        assertThat(memberJdbcRepository.findAll()).extracting(MemberDto::name).containsExactly("jdbc1", "jdbc2");
    }

    @Test
    @DisplayName("Q33. SQLException 이 Spring 의 DataAccessException 계층으로 변환된다")
    void q33_exceptionTranslation() {
        assertThatThrownBy(() -> memberJdbcRepository.findFromWrongTable())
                .isInstanceOf(BadSqlGrammarException.class)
                .isInstanceOf(DataAccessException.class);
    }

    @Test
    @DisplayName("Q34. 영속성 컨텍스트: 1차 캐시 + 동일성 보장 + 변경 감지")
    void q34_persistenceContext() {
        Long teamId = teamRepository.findAll().get(0).getId();
        stats.clear();

        tx.executeWithoutResult(status -> {
            Team first = em.find(Team.class, teamId);   // SELECT
            Team second = em.find(Team.class, teamId);  // 1차 캐시 → SQL 없음
            assertThat(first).isSameAs(second);         // 동일성 보장
            first.changeName("changed");                // save() 호출 없이
        });                                             // 커밋 시 변경 감지 → UPDATE

        assertThat(stats.getPrepareStatementCount()).isEqualTo(2); // SELECT 1 + UPDATE 1
        assertThat(teamRepository.findById(teamId).orElseThrow().getName()).isEqualTo("changed");
    }

    @Test
    @DisplayName("Q35. N+1: 팀 목록 1번 + 팀마다 선수 조회 N(3)번 = 4번")
    void q35_nPlusOne() {
        tx.executeWithoutResult(status -> {
            List<Team> teams = teamRepository.findAll();
            teams.forEach(team -> team.getPlayers().size()); // 지연 로딩 초기화
        });
        assertThat(stats.getPrepareStatementCount()).isEqualTo(1 + 3);
    }

    @Test
    @DisplayName("Q35. fetch join / @EntityGraph: 쿼리 1번")
    void q35_fetchJoin() {
        tx.executeWithoutResult(status -> {
            List<Team> teams = teamRepository.findAllWithPlayers();
            assertThat(teams).hasSize(3);
            teams.forEach(team -> assertThat(team.getPlayers()).hasSize(2));
        });
        assertThat(stats.getPrepareStatementCount()).isEqualTo(1);

        stats.clear();
        tx.executeWithoutResult(status ->
                teamRepository.findAllWithEntityGraph().forEach(team -> team.getPlayers().size()));
        assertThat(stats.getPrepareStatementCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("Q36. LAZY 는 프록시로 두었다가, 트랜잭션 밖에서 초기화하면 LazyInitializationException")
    void q36_lazyLoading() {
        Long playerId = playerRepository.findAll().get(0).getId();

        Player player = playerRepository.findById(playerId).orElseThrow(); // 여기서 트랜잭션 종료
        assertThat(Hibernate.isInitialized(player.getTeam())).isFalse();   // 아직 프록시
        assertThatThrownBy(() -> player.getTeam().getName())
                .isInstanceOf(LazyInitializationException.class);
    }
}
