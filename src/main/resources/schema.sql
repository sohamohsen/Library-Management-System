-- ===============================
-- Create Tables
-- ===============================

-- Authors Table
CREATE TABLE IF NOT EXISTS authors (
                                       id INT AUTO_INCREMENT PRIMARY KEY,
                                       bio LONGTEXT NULL,
                                       full_name VARCHAR(120) NOT NULL
    );

-- Book Copy Status Table
CREATE TABLE IF NOT EXISTS book_copy_status (
                                                id INT AUTO_INCREMENT PRIMARY KEY,
                                                status VARCHAR(30) NOT NULL,
    create_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) NOT NULL,
    update_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) NOT NULL ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT UKhgtc3mkt0gg7gtqb9613gron2 UNIQUE (status)
    );

-- Category Table
CREATE TABLE IF NOT EXISTS category (
                                        id INT AUTO_INCREMENT PRIMARY KEY,
                                        description LONGTEXT NULL,
                                        name VARCHAR(120) NOT NULL
    );

-- Publisher Table
CREATE TABLE IF NOT EXISTS publisher (
                                         id INT AUTO_INCREMENT PRIMARY KEY,
                                         founder_year INT NULL,
                                         name VARCHAR(120) NOT NULL
    );

-- Books Table
CREATE TABLE IF NOT EXISTS books (
                                     id INT AUTO_INCREMENT PRIMARY KEY,
                                     create_at DATETIME(6) NOT NULL,
    create_by INT NOT NULL,
    edition VARCHAR(40) NULL,
    isbn VARCHAR(20) NOT NULL,
    language VARCHAR(40) NULL,
    publication_year INT NULL,
    summary LONGTEXT NULL,
    title VARCHAR(240) NOT NULL,
    update_at DATETIME(6) NOT NULL,
    author_id INT NULL,
    category_id INT NULL,
    publisher_id INT NULL,
    CONSTRAINT uk_books_isbn UNIQUE (isbn),
    CONSTRAINT FKfjixh2vym2cvfj3ufxj91jem7 FOREIGN KEY (author_id) REFERENCES authors (id),
    CONSTRAINT FK8el3ddb59ciucupyc17vu7835 FOREIGN KEY (category_id) REFERENCES category (id),
    CONSTRAINT FK1eujqvebj0cej9mcivv49grwi FOREIGN KEY (publisher_id) REFERENCES publisher (id)
    );

-- Book Copies Table
CREATE TABLE IF NOT EXISTS book_copies (
                                           id INT AUTO_INCREMENT PRIMARY KEY,
                                           acquired_at DATETIME(6) NOT NULL,
    barcode VARCHAR(64) NOT NULL,
    create_at DATETIME(6) NOT NULL,
    create_by INT NOT NULL,
    update_at DATETIME(6) NOT NULL,
    book_id INT NOT NULL,
    status_id INT NOT NULL,
    CONSTRAINT FKhlawea8y2e2dv0ta58vc6f5nr FOREIGN KEY (book_id) REFERENCES books (id),
    CONSTRAINT FKssluh7ce4ptq1gkymsuascqur FOREIGN KEY (status_id) REFERENCES book_copy_status (id)
    );

-- Roles Table
CREATE TABLE IF NOT EXISTS roles (
                                     id INT AUTO_INCREMENT PRIMARY KEY,
                                     role VARCHAR(64) NOT NULL,
    CONSTRAINT UKg50w4r0ru3g9uf6i6fr4kpro8 UNIQUE (role)
    );

-- System Users Table
CREATE TABLE IF NOT EXISTS system_users (
                                            id INT AUTO_INCREMENT PRIMARY KEY,
                                            create_at DATETIME(6) NOT NULL,
    create_by INT NOT NULL,
    email VARCHAR(120) NOT NULL,
    enabled BIT NOT NULL,
    full_name VARCHAR(120) NOT NULL,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(255) NULL,
    update_at DATETIME(6) NOT NULL,
    username VARCHAR(64) NOT NULL,
    role_id INT NOT NULL,
    CONSTRAINT UKdxy6tf9nvg7o3kd7yfd5j7qiu UNIQUE (email),
    CONSTRAINT UKtr0kj1o2dqfwm13a6fvwrg867 UNIQUE (username),
    CONSTRAINT FKfkicymc5odo1idcdy8eo464p4 FOREIGN KEY (role_id) REFERENCES roles (id)
    );

-- Members Table
CREATE TABLE IF NOT EXISTS members (
                                       id INT AUTO_INCREMENT PRIMARY KEY,
                                       active BIT NOT NULL,
                                       code VARCHAR(255) NOT NULL,
    create_at DATETIME(6) NOT NULL,
    create_by BIGINT NOT NULL,
    email VARCHAR(120) NOT NULL,
    full_name VARCHAR(120) NOT NULL,
    joined_on DATE NOT NULL,
    phone VARCHAR(40) NULL,
    update_at DATETIME(6) NOT NULL
    );

-- Loans Table
CREATE TABLE IF NOT EXISTS loans (
                                     id INT AUTO_INCREMENT PRIMARY KEY,
                                     create_at DATETIME(6) NOT NULL,
    due_at DATETIME(6) NOT NULL,
    status INT NULL,
    update_at DATETIME(6) NOT NULL,
    create_by INT NOT NULL,
    member_id INT NOT NULL,
    CONSTRAINT FKsl83pxpdxvyhfefugveckmm61 FOREIGN KEY (create_by) REFERENCES system_users (id),
    CONSTRAINT FKcx90n1minpb22v3jw4ojinqm FOREIGN KEY (member_id) REFERENCES members (id)
    );

-- Loan Items Table
CREATE TABLE IF NOT EXISTS loan_items (
                                          id INT AUTO_INCREMENT PRIMARY KEY,
                                          create_at DATETIME(6) NOT NULL,
    create_by INT NOT NULL,
    fine_amount DOUBLE NULL,
    returned_at DATETIME(6) NULL,
    update_at DATETIME(6) NOT NULL,
    copy_id INT NOT NULL,
    loan_id INT NOT NULL,
    CONSTRAINT FKhys79rgv69qi6oqge59yhtv8r FOREIGN KEY (copy_id) REFERENCES book_copies (id),
    CONSTRAINT FKb1hb4c28uds2onfikt2gi5lbr FOREIGN KEY (loan_id) REFERENCES loans (id)
    );
