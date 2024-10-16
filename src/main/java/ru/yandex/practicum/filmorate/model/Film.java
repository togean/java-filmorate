package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Set;
import java.util.SortedSet;

/**
 * Film.
 */
@Data
@Component
public class Film implements Cloneable {
    private Integer id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;
    private Set<Integer> usersWhoSetLikes;
    private SortedSet<Genre> genres;
    private MpaInstance mpa;

    public Film clone() throws CloneNotSupportedException {
        return (Film) super.clone();
    }
}
