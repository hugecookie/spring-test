package org.example.data;

import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/**
 * Q32. JdbcTemplate: 커넥션 획득·반납, Statement 생성, 예외 처리를 대신해 준다.
 *      개발자는 SQL 과 RowMapper 만 작성 (템플릿 메서드·콜백 패턴).
 * Q33. @Repository + JdbcTemplate → SQLException 이 DataAccessException 계층으로 변환된다.
 */
@Repository
public class MemberJdbcRepository {

    private static final RowMapper<MemberDto> ROW_MAPPER =
            (rs, rowNum) -> new MemberDto(rs.getLong("id"), rs.getString("name"));

    private final JdbcTemplate jdbcTemplate;

    public MemberJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(String name) {
        jdbcTemplate.update("insert into members(name) values (?)", name);
    }

    public List<MemberDto> findAll() {
        return jdbcTemplate.query("select id, name from members order by id", ROW_MAPPER);
    }

    /** 존재하지 않는 테이블 → SQLException → BadSqlGrammarException(DataAccessException) */
    public List<MemberDto> findFromWrongTable() {
        return jdbcTemplate.query("select id, name from no_such_table", ROW_MAPPER);
    }
}
