CREATE TABLE District (
    id      INTEGER      PRIMARY KEY,
    name    VARCHAR(255) NOT NULL
);

CREATE TABLE Municipality (
    id          INTEGER      PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    email       VARCHAR(255) NOT NULL,
    district    INTEGER,
    FOREIGN KEY (district) REFERENCES District(id)
);

CREATE TABLE Town (
    id              VARCHAR(6)   PRIMARY KEY,
    name            VARCHAR(255) NOT NULL,
    municipality    INTEGER,
    FOREIGN KEY (municipality) REFERENCES Municipality(id)
);