create table categories
(
    id               bigserial primary key,
    name             varchar(255) not null,
    thumbnail        varchar(500),
    parent_id        bigserial references categories (id) on delete cascade,
    created_at       timestamp    not null,
    created_by       varchar(255),
    last_modified_at timestamp    not null,
    last_modified_by varchar(255),
    version          int          not null
);

ALTER TABLE categories
    ALTER COLUMN parent_id DROP NOT NULL;

CREATE TABLE books
(
    id                 BIGSERIAL    NOT NULL PRIMARY KEY,
    isbn               varchar(50)  NOT NULL UNIQUE,
    title              varchar(255) NOT NULL UNIQUE,
    publisher          varchar(255) NOT NULL,
    supplier           varchar(255) NOT NULL,
    description        text null,
    language           varchar(255) NOT NULL,
    edition            int          NOT NULL,
    category_id        BIGSERIAL    NOT NULL REFERENCES categories (id) ON DELETE CASCADE,
    category_name      varchar(255) NOT NULL,
    thumbnails         varchar(500)[],
    created_date       timestamp    NOT NULL,
    created_by         varchar(255),
    last_modified_date timestamp    NOT NULL,
    last_modified_by   varchar(255),
    version            int          NOT NULL
);

CREATE TABLE ebooks
(
    id                 BIGSERIAL    NOT NULL PRIMARY KEY,
    isbn               varchar(255) not null references books (isbn) ON DELETE CASCADE,
    file_size          int null,
    format             VARCHAR(255) NULL,
    url                VARCHAR(255) NULL,
    purchases          int          not null,
    number_of_pages    int          not null,
    publication_date   timestamp    not null,
    release_date       timestamp    not null,
    original_price     bigint       not null,
    discounted_price   bigint       not null default 0,
    currency_price     VARCHAR(255) not null,
    created_date       timestamp    NOT NULL,
    created_by         varchar(255),
    last_modified_date timestamp    NOT NULL,
    last_modified_by   varchar(255),
    version            int          NOT NULL
);

create table print_books
(
    id                 BIGSERIAL    NOT NULL PRIMARY KEY,
    isbn               varchar(255) not null references books (isbn) ON DELETE CASCADE,
    purchases          int          not null,
    number_of_pages    int          not null,
    publication_date   timestamp    not null,
    release_date       timestamp    not null,
    original_price     bigint       not null,
    discounted_price   bigint       not null default 0,
    currency_price     VARCHAR(255) not null,
    cover_type         VARCHAR(255) not null,
    weight             float8       NOT NULL,
    width              float8       NOT NULL,
    height             float8       NOT NULL,
    thickness          float8       NOT NULL,
    inventory          int          not null,
    created_date       timestamp    NOT NULL,
    created_by         varchar(255),
    last_modified_date timestamp    NOT NULL,
    last_modified_by   varchar(255),
    version            int          NOT NULL
);

create table authors
(
    id           bigserial    not null primary key,
    job_title    varchar(255) not null,
    first_name   varchar(255) not null,
    last_name    varchar(255) not null,
    email        varchar(255) not null,
    phone        varchar(255) not null,
    country_code int          not null,
    about        text null
);

create table book_author
(
    author      bigserial    not null,
    book        bigserial    not null,
    author_name varchar(255) not null,
    primary key (author, book)
);

create table quotations
(
    id                 BIGSERIAL    NOT NULL PRIMARY KEY,
    isbn               varchar(255) not null references books (isbn) ON DELETE CASCADE,
    author_id          bigserial    not null references authors (id) on delete cascade,
    job_title          varchar(255) not null,
    text               varchar(255) not null,
    created_date       timestamp    NOT NULL,
    created_by         varchar(255),
    last_modified_date timestamp    NOT NULL,
    last_modified_by   varchar(255)
);

create table shopping_carts
(
    id               uuid DEFAULT gen_random_uuid() primary key,
    created_at       timestamp           NOT NULL,
    created_by       varchar(255) unique NOT NULL,
    last_modified_at timestamp           NOT NULL,
    last_modified_by varchar(255)
);

create table cart_items
(
    id       serial       not null primary key,
    cart_id  uuid         not null references shopping_carts (id) ON DELETE CASCADE,
    isbn     varchar(255) not null references books (isbn) ON DELETE CASCADE,
    quantity integer      not null
);

CREATE table orders
(
    id                     uuid DEFAULT gen_random_uuid() primary key,
    total_original_price   bigint       not null,
    total_discounted_price bigint null,
    total_currency_price   VARCHAR(255) not null,
    status                 varchar(255) not null,
    payment_method         varchar(255) not null,
    first_name             varchar(255) not null,
    last_name              varchar(255) not null,
    email                  varchar(255) not null,
    phone                  varchar(255) not null,
    country_code           varchar(255) not null,
    city                   varchar(255) not null,
    zip_code               varchar(255) not null,
    address                varchar(255) not null,
    otp                    bigint null,
    otp_expired_at         timestamp null,
    created_date           timestamp    not null,
    created_by             varchar(255),
    last_modified_date     timestamp    not null,
    last_modified_by       varchar(255),
    version                int          not null
);

CREATE table line_items
(
    id                     bigserial primary key,
    order_id               uuid         not null references orders (id) ON DELETE CASCADE,
    isbn                   VARCHAR(255) not null,
    book_type              VARCHAR(255) not null,
    quantity               int          not null,
    original_price         bigint       not null,
    discounted_price       bigint null,
    currency_price         VARCHAR(255) not null,
    version                int          not null
);

CREATE TABLE email_preferences_category
(
    email_preferences BIGSERIAL NOT NULL,
    category          BIGSERIAL NOT NULL,
    name              VARCHAR(255),
    PRIMARY KEY (email_preferences, category)
);

CREATE TABLE email_preferences
(
    id           BIGSERIAL PRIMARY KEY,
    email        VARCHAR(255) NOT NULL,
    first_name   VARCHAR(255) NOT NULL,
    last_name    VARCHAR(255) NOT NULL,
    email_topics VARCHAR(255)[]
);