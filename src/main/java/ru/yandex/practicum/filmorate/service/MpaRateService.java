package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.MpaInstance;

import java.util.List;

public interface MpaRateService {
    List<MpaInstance> getAllMpaRates();

    MpaInstance getMpaRateById(Integer rateId);

    MpaInstance createMpaRate(MpaInstance mpaInstasnce);

    MpaInstance updateMpaRate(MpaInstance mpaInstasnce);
}
