create sequence if not exists customer_id_seq;

create table IF NOT EXISTS employee
(
    id         integer default nextval('customer_id_seq'::regclass) not null
        constraint employee_pk
            primary key,
    name       varchar                                              not null,
    age        varchar                                              not null,
    address    varchar                                              not null,
    created_at timestamp,
    updated_at timestamp,
    username   varchar
        constraint employee_pk_2
            unique
);

alter table employee
    owner to sampletest1;

create index IF NOT EXISTS employee_username_index
    on employee (username);

INSERT INTO employee (name, age, address, created_at, updated_at, username) VALUES ('Tran Nam Tien', '32', 'Ha Noi', '2026-03-24 09:21:46.983000', '2026-03-24 09:21:46.983000', 'tientn') ON CONFLICT (username) DO NOTHING;;
INSERT INTO employee (name, age, address, created_at, updated_at, username) VALUES ('Tran Minh Hung', '32', 'Ha Noi', '2026-03-24 09:21:46.983000', '2026-03-24 09:21:46.983000', 'hungtm') ON CONFLICT (username) DO NOTHING;;
