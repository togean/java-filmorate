package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class Genre implements Comparable<Genre> {
    Integer id;
    String name;

    @Override
    public int compareTo(Genre o) {
        return this.id.compareTo(o.id);
    }
}
