package ru.yandex.practicum.filmorate.repository;

import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.MpaInstance;
import ru.yandex.practicum.filmorate.storage.MpaRateStorage;

import java.util.List;

@Repository
@AllArgsConstructor
@Component
public class MpaRateRepository implements MpaRateStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<MpaInstance> getAllMpaRates() {
        return jdbcTemplate.query("select * from rates order by rate_id", mapMpaRateInstance());
    }

    @Override
    public MpaInstance createMpaRate(MpaInstance mpaInstasnce) {
        return null;
    }

    @Override
    public MpaInstance updateMpaRate(MpaInstance mpaInstasnce) {
        return null;
    }

    @Override
    public MpaInstance getMpaRateById(Integer mpaId) {
        MpaInstance requestedMPA;
        try {
            requestedMPA = jdbcTemplate.queryForObject("select * from rates where rate_id = ?", mapMpaRateInstance(), mpaId);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Нет такого mpa");
        }
        return requestedMPA;
    }

    private RowMapper<MpaInstance> mapMpaRateInstance() {
        return (rs, rowNum) -> {
            if (!(rs.getString("ratename") == null)) {
                MpaInstance mpaRate = new MpaInstance();
                mpaRate.setId(rs.getInt("rate_id"));
                mpaRate.setName(rs.getString("ratename"));
                return mpaRate;
            }
            return null;
        };
    }
}
