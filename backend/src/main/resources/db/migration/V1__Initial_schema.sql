CREATE TABLE books
(
    id               BIGSERIAL    NOT NULL PRIMARY KEY,
    isbn             varchar(255) NOT NULL UNIQUE,
    title            varchar(255) NOT NULL,
    author           varchar(255) NOT NULL,
    publisher        varchar(255) NOT NULL,
    supplier         varchar(255) NOT NULL,
    description      text,
    price            bigint       NOT NULL,
    inventory        int          NOT NULL,
    language         varchar(255) NOT NULL,
    weight           float8       NOT NULL,
    cover_type       varchar(255) NOT NULL,
    number_of_pages  int          NOT NULL,
    width            float8       NOT NULL,
    height           float8       NOT NULL,
    thickness        float8       NOT NULL,
    photos           varchar(255)[],
    purchases        int          NOT NULL,
    created_at       timestamp    NOT NULL,
    created_by       varchar(255),
    last_modified_at timestamp    NOT NULL,
    last_modified_by varchar(255),
    version          int          NOT NULL
);

create table shopping_carts
(
    id uuid DEFAULT gen_random_uuid() primary key,
    created_at       timestamp    NOT NULL,
    created_by       varchar(255) unique NOT NULL,
    last_modified_at timestamp    NOT NULL,
    last_modified_by varchar(255)
);

create table cart_items
(
    id       serial       not null primary key,
    cart_id  uuid         not null references shopping_carts (id),
    isbn     varchar(255) not null references books(isbn),
    title    varchar(255) NOT NULL,
    photo    varchar(255),
    price    bigint       NOT NULL,
    quantity integer      not null
);
