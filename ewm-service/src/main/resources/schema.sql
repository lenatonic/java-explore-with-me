CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    email VARCHAR(254) UNIQUE,
    name  VARCHAR(250)                            NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS categories
(
    id   BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name VARCHAR(50)                             NOT NULL UNIQUE,
    CONSTRAINT pk_categories PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS locations
(
    id  BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    lon FLOAT                                   NOT NULL,
    lat FLOAT                                   NOT NULL,
    CONSTRAINT pk_locations primary key (id)
);

CREATE TABLE IF NOT EXISTS events
(
    id                 BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    annotation         VARCHAR(2000),
    category_id        BIGINT,
    confirmed_requests BIGINT,
    created_on         TIMESTAMP,
    description        VARCHAR(7000),
    event_date         TIMESTAMP,
    initiator_id       BIGINT,
    location_id        BIGINT,
    paid               BOOLEAN,
    participant_limit  BIGINT,
    published_on       TIMESTAMP,
    request_moderation BOOLEAN,
    state              VARCHAR(20),
    title              VARCHAR(120),
    views              BIGINT,
    CONSTRAINT events_categories_id_fk FOREIGN KEY (category_id) REFERENCES categories (id) ON DELETE CASCADE,
    CONSTRAINT events_locations_id_fk FOREIGN KEY (location_id) REFERENCES locations (id) ON DELETE CASCADE,
    CONSTRAINT events_users_id_fk FOREIGN KEY (initiator_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT pk_event PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS requests
(
    id           BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    created      TIMESTAMP                               NOT NULL,
    event_id     BIGINT                                  NOT NULL,
    requester_id BIGINT                                  NOT NULL,
    status       VARCHAR(100),
    CONSTRAINT pk_requests PRIMARY KEY (id),
    CONSTRAINT fk_event FOREIGN KEY (event_id) REFERENCES events (id) ON DELETE CASCADE,
    CONSTRAINT fk_requester FOREIGN KEY (requester_id) REFERENCES users (id) ON DELETE CASCADE
);