CREATE TABLE email_preferences_category (
    email_preferences BIGSERIAL NOT NULL,
    category BIGSERIAL NOT NULL,
    name VARCHAR(255),
    PRIMARY KEY (email_preferences, category)
);

CREATE TABLE email_preferences
(
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email_topics VARCHAR(255)[]
);