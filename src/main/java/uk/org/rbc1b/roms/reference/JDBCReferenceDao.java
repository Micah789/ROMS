/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.org.rbc1b.roms.reference;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/**
 * JBDC implementation of reference data dao.
 *
 * @author oliver.elder.esq
 */
@Repository
public class JDBCReferenceDao implements ReferenceDao {
    private JdbcTemplate jdbcTemplate;

    @Override
    @Cacheable("reference.maritalStatus")
    public List<Pair<Integer, String>> findMaritalStatusValues() {
        return this.jdbcTemplate.query(
                "SELECT MaritalStatusId AS id, Description AS value FROM MaritalStatus",
                new IntegerStringPairRowMapper());
    }

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    private static final class IntegerStringPairRowMapper implements RowMapper<Pair<Integer, String>> {

        @Override
        public Pair<Integer, String> mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new ImmutablePair<Integer, String>(rs.getInt("id"), rs.getString("value"));
        }
    }
}
