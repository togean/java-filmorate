package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.MpaInstance;
import ru.yandex.practicum.filmorate.service.MpaRateService;

import java.util.Collection;
import java.util.Map;

@RestController
@RequestMapping
@AllArgsConstructor
public class MpaRateController {
    private MpaRateService mpaRateService;

    @GetMapping("/mpa")
    public Collection<MpaInstance> getAllMpaRates() {
        return mpaRateService.getAllMpaRates();
    }

    @GetMapping("/mpa/{rateId}")
    public MpaInstance getMpaRateById(@PathVariable("rateId") String rateId) {
        return mpaRateService.getMpaRateById(Integer.valueOf(rateId));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFoundException(final NotFoundException e) {
        return Map.of("Ошибка", e.getMessage());
    }
}
