-- auto-generated definition
create table authors
(
    id        int auto_increment
        primary key,
    bio       longtext     null,
    full_name varchar(120) not null
);

-- auto-generated definition
create table book_copies
(
    id          int auto_increment
        primary key,
    acquired_at datetime(6) not null,
    barcode     varchar(64) not null,
    create_at   datetime(6) not null,
    create_by   int         not null,
    update_at   datetime(6) not null,
    book_id     int         not null,
    status_id   int         not null,
    constraint FKhlawea8y2e2dv0ta58vc6f5nr
        foreign key (book_id) references books (id),
    constraint FKssluh7ce4ptq1gkymsuascqur
        foreign key (status_id) references book_copy_status (id)
);

-- auto-generated definition
create table book_copy_status
(
    id        int auto_increment
        primary key,
    status    varchar(30)                              not null,
    create_at datetime(6) default CURRENT_TIMESTAMP(6) not null,
    update_at datetime(6) default CURRENT_TIMESTAMP(6) not null on update CURRENT_TIMESTAMP(6),
    constraint UKhgtc3mkt0gg7gtqb9613gron2
        unique (status)
);

-- auto-generated definition
create table books
(
    id               int auto_increment
        primary key,
    create_at        datetime(6)  not null,
    create_by        int          not null,
    edition          varchar(40)  null,
    isbn             varchar(20)  not null,
    language         varchar(40)  null,
    publication_year int          null,
    summary          longtext     null,
    title            varchar(240) not null,
    update_at        datetime(6)  not null,
    author_id        int          null,
    category_id      int          null,
    publisher_id     int          null,
    constraint uk_books_isbn
        unique (isbn),
    constraint FK1eujqvebj0cej9mcivv49grwi
        foreign key (publisher_id) references publisher (id),
    constraint FK8el3ddb59ciucupyc17vu7835
        foreign key (category_id) references category (id),
    constraint FKfjixh2vym2cvfj3ufxj91jem7
        foreign key (author_id) references authors (id)
);

-- auto-generated definition
create table category
(
    id          int auto_increment
        primary key,
    description longtext     null,
    name        varchar(120) not null
);


-- auto-generated definition
create table loan_items
(
    id          int auto_increment
        primary key,
    create_at   datetime(6) not null,
    create_by   int         not null,
    fine_amount double      null,
    returned_at datetime(6) null,
    update_at   datetime(6) not null,
    copy_id     int         not null,
    loan_id     int         not null,
    constraint FKb1hb4c28uds2onfikt2gi5lbr
        foreign key (loan_id) references loans (id),
    constraint FKhys79rgv69qi6oqge59yhtv8r
        foreign key (copy_id) references book_copies (id)
);

-- auto-generated definition
create table loans
(
    id        int auto_increment
        primary key,
    create_at datetime(6) not null,
    due_at    datetime(6) not null,
    status    int         null,
    update_at datetime(6) not null,
    create_by int         not null,
    member_id int         not null,
    constraint FKcx90n1minpb22v3jw4ojinqm
        foreign key (member_id) references members (id),
    constraint FKsl83pxpdxvyhfefugveckmm61
        foreign key (create_by) references system_users (id)
);

-- auto-generated definition
create table members
(
    id        int auto_increment
        primary key,
    active    bit          not null,
    code      varchar(255) not null,
    create_at datetime(6)  not null,
    create_by bigint       not null,
    email     varchar(120) not null,
    full_name varchar(120) not null,
    joined_on date         not null,
    phone     varchar(40)  null,
    update_at datetime(6)  not null
);

-- auto-generated definition
create table publisher
(
    id           int auto_increment
        primary key,
    founder_year int          null,
    name         varchar(120) not null
);

-- auto-generated definition
create table roles
(
    id   int auto_increment
        primary key,
    role varchar(64) not null,
    constraint UKg50w4r0ru3g9uf6i6fr4kpro8
        unique (role)
);

-- auto-generated definition
create table system_users
(
    id        int auto_increment
        primary key,
    create_at datetime(6)  not null,
    create_by int          not null,
    email     varchar(120) not null,
    enabled   bit          not null,
    full_name varchar(120) not null,
    password  varchar(255) not null,
    phone     varchar(255) null,
    update_at datetime(6)  not null,
    username  varchar(64)  not null,
    role_id   int          not null,
    constraint UKdxy6tf9nvg7o3kd7yfd5j7qiu
        unique (email),
    constraint UKtr0kj1o2dqfwm13a6fvwrg867
        unique (username),
    constraint FKfkicymc5odo1idcdy8eo464p4
        foreign key (role_id) references roles (id)
);

