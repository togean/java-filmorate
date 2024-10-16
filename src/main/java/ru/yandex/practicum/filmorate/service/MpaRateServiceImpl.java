package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.MpaInstance;
import ru.yandex.practicum.filmorate.storage.MpaRateStorage;

import java.util.List;

@Service
@AllArgsConstructor
public class MpaRateServiceImpl implements MpaRateService {
    @Autowired
    private final MpaRateStorage mpaRateStorage;

    @Override
    public List<MpaInstance> getAllMpaRates() {
        return mpaRateStorage.getAllMpaRates();
    }

    @Override
    public MpaInstance getMpaRateById(Integer rateId) {
        return mpaRateStorage.getMpaRateById(rateId);
    }

    @Override
    public MpaInstance createMpaRate(MpaInstance mpaInstasnce) {
        return mpaRateStorage.createMpaRate(mpaInstasnce);
    }

    @Override
    public MpaInstance updateMpaRate(MpaInstance mpaInstasnce) {
        return mpaRateStorage.updateMpaRate(mpaInstasnce);
    }
}
