drop table if exists users, categories, locations, events, compilations, requests, event_compilations cascade;

CREATE TABLE IF NOT EXISTS users (
id    INT          GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
name  VARCHAR(255) NOT NULL,
email VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS categories (
id    INT         GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
name  VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS locations (
id  INT    GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
lat FLOAT,
lon FLOAT
);

CREATE TABLE IF NOT EXISTS events (
id                 INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
annotation         VARCHAR(2000) NOT NULL,
category_id        INT,
confirmed_requests INT,
created_on         TIMESTAMP WITHOUT TIME ZONE,
description        VARCHAR(7000) NOT NULL,
event_date         TIMESTAMP WITHOUT TIME ZONE,
initiator_id       INT,
location_id        INT,
paid               BOOLEAN,
participant_limit  INT,
published_on       TIMESTAMP WITHOUT TIME ZONE,
request_moderation BOOLEAN,
state              VARCHAR(120),
title              VARCHAR(120),
views              BIGINT,
FOREIGN KEY (location_id) REFERENCES locations (id),
FOREIGN KEY (category_id) REFERENCES categories (id),
FOREIGN KEY (initiator_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS compilations (
    id     INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    pinned BOOLEAN      NOT NULL,
    title  VARCHAR(120) NOT NULL,
    CONSTRAINT UQ_TITLE_COMPILATION UNIQUE (title)
);

CREATE TABLE IF NOT EXISTS requests (
    id           INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    created      TIMESTAMP WITHOUT TIME ZONE,
    event_id     INT,
    status       VARCHAR(100),
    requester_id INT,
    FOREIGN KEY (event_id) REFERENCES events (id),
    FOREIGN KEY (requester_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS event_compilations (
    compilation_id INT NOT NULL REFERENCES compilations (id) ON DELETE CASCADE,
    event_id       INT NOT NULL REFERENCES events (id) ON DELETE CASCADE
);

