package ru.yandex.practicum.filmorate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FilmorateApplication {
	public static final Logger log = LoggerFactory.getLogger(FilmorateApplication.class);

	public static void main(String[] args) {
		((ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME))
				.setLevel(ch.qos.logback.classic.Level.convertAnSLF4JLevel(Level.INFO));
		SpringApplication.run(FilmorateApplication.class, args);
	}
}
