package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.MpaInstance;

import java.util.List;

public interface MpaRateStorage {
    List<MpaInstance> getAllMpaRates();

    MpaInstance createMpaRate(MpaInstance mpaInstasnce);

    MpaInstance updateMpaRate(MpaInstance mpaInstasnce);

    MpaInstance getMpaRateById(Integer mpaId);
}
