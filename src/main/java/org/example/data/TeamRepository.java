package org.example.data;

import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TeamRepository extends JpaRepository<Team, Long> {

    /** Q35 해결 1: fetch join → 한 번의 쿼리로 팀 + 선수 */
    @Query("select distinct t from Team t join fetch t.players")
    List<Team> findAllWithPlayers();

    /** Q35 해결 2: @EntityGraph (내부적으로 left join fetch) */
    @EntityGraph(attributePaths = "players")
    @Query("select t from Team t")
    List<Team> findAllWithEntityGraph();

    // 해결 3: spring.jpa.properties.hibernate.default_batch_fetch_size=100 → IN 절로 묶어 조회
    // 컬렉션 fetch join 은 페이징이 메모리에서 처리되므로, 컬렉션 + 페이징은 batch size 를 쓴다.
}
