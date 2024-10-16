package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.MpaInstance;

import java.util.List;

@Component
public interface MpaRateService {
    List<MpaInstance> getAllMpaRates();

    MpaInstance getMpaRateById(Integer rateId);

    MpaInstance createMpaRate(MpaInstance mpaInstasnce);

    MpaInstance updateMpaRate(MpaInstance mpaInstasnce);
}
