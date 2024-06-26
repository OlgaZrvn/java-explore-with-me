drop table if exists hits;

CREATE TABLE IF NOT EXISTS hits (
id        BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
app       VARCHAR(255) NOT NULL,
uri       VARCHAR(255) NOT NULL,
ip        VARCHAR(255) NOT NULL,
timestamp TIMESTAMP WITHOUT TIME ZONE NOT NULL
);