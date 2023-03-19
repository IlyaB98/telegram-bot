-- liquibase formatted sql

-- changeSet ilya:1
CREATE TABLE notification_task
(
    id            SERIAL,
    chat_id       BIGINT    NOT NULL,
    massage       TEXT      NOT NULL,
    dispatch_time TIMESTAMP NOT NULL
);
