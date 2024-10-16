insert into genres (genrename) values ('Комедия'),('Драма'),('Мультфильм'),('Триллер'),('Документальный'),('Боевик')

insert into rates (ratename) values ('G'),('PG'),('PG-13'),('R'),('NC-17')

--insert into films (name, film_releasedate, description, duration, rateid) values ('Приключения Петрова и Васечкина','1990-10-05','Приключения',120,1),('Каникулы Петрова и Васечкина','1991-10-05','Приключения',130,1)

--insert into users (name, birthday, email, login) values ('Пользователь1','2011-01-29','user1@mail','user1'),('Пользователь2','2010-05-17','user2@mail','user2')

insert into statuses (statusname) values ('неподтверждённая'), ('подтверждённая ')

--insert into films_users (film_id, user_id) values (1,1)

--insert into films_genres (film_id, genre_id) values (1,1),(2,1)

--insert into users_friends (user_id, friend_id, status_id) values (1,2,2)