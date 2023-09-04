drop table IF EXISTS USERS cascade;
drop table IF EXISTS FRIENDS cascade;
drop table IF EXISTS GENRES cascade;
drop table IF EXISTS FILMS_GENRE cascade;
drop table IF EXISTS FILMS_RATING cascade;
drop table IF EXISTS FILMS cascade;

CREATE TABLE IF NOT EXISTS GENRES
(
    GENRE_ID   LONG auto_increment,
    GENRE_NAME CHARACTER VARYING(30) not null,
    constraint GENRES_PK
        primary key (GENRE_ID)
);

CREATE TABLE IF NOT EXISTS FILMS_RATING
(
    RATING_ID   LONG auto_increment,
    RATING_NAME CHARACTER VARYING(10) not null,
    constraint FILMS_RATING_PK
        primary key (RATING_ID)
);

CREATE TABLE IF NOT EXISTS FILMS
(
    FILM_ID          LONG auto_increment,
    FILM_NAME        CHARACTER VARYING(100) not null,
    FILM_DESCRIPTION CHARACTER VARYING(200) not null,
    RELEASE_DATE     DATE                   not null,
    DURATION         REAL                   not null,
    RATE             INTEGER,
    RATING_ID        LONG,
    constraint FILMS_PK
        primary key (FILM_ID),
    constraint FILMS_FILMS_RATING_RATING_ID_FK
        foreign key (RATING_ID) references FILMS_RATING
);

create table FILMS_GENRE
(
    MATCH_ID int auto_increment,
    FILM_ID  LONG not null,
    GENRE_ID LONG not null,
    constraint FILMS_GENRE_PK
        primary key (MATCH_ID),
    constraint FILMS_GENRE_FILMS_FILM_ID_FK
        foreign key (FILM_ID) references FILMS,
    constraint FILMS_GENRE_GENRES_GENRE_ID_FK
        foreign key (GENRE_ID) references GENRES
);

CREATE TABLE IF NOT EXISTS USERS
(
    USER_ID   LONG auto_increment,
    EMAIL     CHARACTER VARYING(50) not null,
    LOGIN     CHARACTER VARYING(50) not null,
    USER_NAME CHARACTER VARYING(50) not null,
    BIRTHDAY  DATE                  not null,
    constraint USERS_PK
        primary key (USER_ID)
);

CREATE TABLE IF NOT EXISTS FRIENDS
(
    FRIENDSHIP_ID INTEGER auto_increment,
    USER_ID       LONG not null,
    FRIEND_ID     LONG not null,
    constraint FRIENDS_PK
        primary key (FRIENDSHIP_ID),
    constraint FRIENDS_USERS_USER_ID_FK
        foreign key (FRIEND_ID) references USERS,
    constraint FRIENDS_USERS_USER_ID_FK_2
        foreign key (USER_ID) references USERS
);