
START TRANSACTION;

-- -------------------------
-- 1) Roles
-- -------------------------
INSERT INTO roles(role) VALUES
                            ('ADMINISTRATOR'),
                            ('LIBRARIAN'),
                            ('STAFF')
    ON DUPLICATE KEY UPDATE id = id;

--- -------------------------
-- Admin user
INSERT INTO system_users
(username, email, full_name, password, enabled, create_at, create_by, update_at, phone, role_id)
VALUES
    ('admin', 'admin@example.com', 'Admin User',
     '$2a$10$7EqJtq98hPqEX7fNZaFWo./Xq5Q6DOW3EtFPXK62o/4pG7Wu3.yGa',  -- "password"
     b'1', NOW(6), 1, NOW(6), '0100000000',
     (SELECT id FROM roles WHERE role = 'ADMINISTRATOR'))
    ON DUPLICATE KEY UPDATE id = id;

-- Librarian
INSERT INTO system_users
(username, email, full_name, password, enabled, create_at, create_by, update_at, phone, role_id)
VALUES
    ('librarian', 'librarian@example.com', 'Lib Rarian',
     '$2a$10$7EqJtq98hPqEX7fNZaFWo./Xq5Q6DOW3EtFPXK62o/4pG7Wu3.yGa',
     b'1', NOW(6), 1, NOW(6), '0100000001',
     (SELECT id FROM roles WHERE role = 'LIBRARIAN'))
    ON DUPLICATE KEY UPDATE id = id;

-- Staff
INSERT INTO system_users
(username, email, full_name, password, enabled, create_at, create_by, update_at, phone, role_id)
VALUES
    ('staff', 'staff@example.com', 'Staff Member',
     '$2a$10$7EqJtq98hPqEX7fNZaFWo./Xq5Q6DOW3EtFPXK62o/4pG7Wu3.yGa',
     b'1', NOW(6), 1, NOW(6), '0100000002',
     (SELECT id FROM roles WHERE role = 'STAFF'))
    ON DUPLICATE KEY UPDATE id = id;


-- -------------------------
-- 3) Lookup tables
-- -------------------------
INSERT INTO book_copy_status(status) VALUES
                                         ('AVAILABLE'), ('LOANED'), ('DAMAGED'), ('LOST')
    ON DUPLICATE KEY UPDATE id = id;

INSERT INTO publisher(name, founder_year) VALUES
                                              ('Pearson', 1844),
                                              ('O''Reilly Media', 1980)
    ON DUPLICATE KEY UPDATE id = id;

INSERT INTO category(name, description) VALUES
                                            ('Technology', 'Technology books'),
                                            ('Programming', 'Programming/software engineering'),
                                            ('Databases', 'Relational and NoSQL databases')
    ON DUPLICATE KEY UPDATE id = id;

INSERT INTO authors(full_name, bio) VALUES
                                        ('Robert C. Martin', 'Uncle Bob, software craftsmanship'),
                                        ('Joshua Bloch', 'Effective Java author')
    ON DUPLICATE KEY UPDATE id = id;

-- -------------------------
-- 4) Members
-- -------------------------
INSERT INTO members(code, full_name, email, phone, active, joined_on,
                    create_at, create_by, update_at)
VALUES
    ('MBR-0001', 'Ali Ahmed', 'ali@example.com', '0101111111', b'1', CURDATE(),
     NOW(6), 1, NOW(6)),
    ('MBR-0002', 'Sara Hassan', 'sara@example.com', '0102222222', b'1', CURDATE(),
     NOW(6), 1, NOW(6))
    ON DUPLICATE KEY UPDATE id = id;

-- -------------------------
-- 5) Books (single author/category/publisher per your schema)
-- -------------------------
INSERT INTO books(title, isbn, publication_year, language, edition, summary,
                  create_at, create_by, update_at, author_id, category_id, publisher_id)
VALUES
    ('Clean Code', '9780132350884', 2008, 'EN', '1',
     'A Handbook of Agile Software Craftsmanship',
     NOW(6), 1, NOW(6),
     (SELECT id FROM authors  WHERE full_name='Robert C. Martin'),
     (SELECT id FROM category WHERE name='Programming'),
     (SELECT id FROM publisher WHERE name='Pearson')),

    ('Effective Java', '9780134685991', 2018, 'EN', '3',
     'Best practices for the Java platform',
     NOW(6), 1, NOW(6),
     (SELECT id FROM authors  WHERE full_name='Joshua Bloch'),
     (SELECT id FROM category WHERE name='Programming'),
     (SELECT id FROM publisher WHERE name='Pearson'))
    ON DUPLICATE KEY UPDATE id = id;

-- -------------------------
-- 6) Book copies (with barcodes)
-- -------------------------
SET @status_available := (SELECT id FROM book_copy_status WHERE status='AVAILABLE');

INSERT INTO book_copies(book_id, barcode, status_id, acquired_at, create_at, create_by, update_at)
VALUES
    ((SELECT id FROM books WHERE isbn='9780132350884'), 'BC-1001', @status_available, NOW(6), NOW(6), 1, NOW(6)),
    ((SELECT id FROM books WHERE isbn='9780132350884'), 'BC-1002', @status_available, NOW(6), NOW(6), 1, NOW(6)),
    ((SELECT id FROM books WHERE isbn='9780134685991'), 'BC-2001', @status_available, NOW(6), NOW(6), 1, NOW(6))
    ON DUPLICATE KEY UPDATE id = id;

-- -------------------------
-- 7) A sample open loan with one item
-- -------------------------
-- Create loan for Ali Ahmed, created by admin, due in 14 days
INSERT INTO loans(create_at, due_at, status, update_at, create_by, member_id)
VALUES
    (NOW(6), NOW(6) + INTERVAL 14 DAY, 0, NOW(6),
     (SELECT id FROM system_users WHERE username='admin'),
     (SELECT id FROM members WHERE code='MBR-0001'));

-- Add one loan item (copy BC-1001)
INSERT INTO loan_items(create_at, create_by, fine_amount, returned_at, update_at, copy_id, loan_id)
SELECT NOW(6), (SELECT id FROM system_users WHERE username='admin'),
       0.0, NULL, NOW(6),
       (SELECT id FROM book_copies WHERE barcode='BC-1001'),
       (SELECT id FROM loans ORDER BY id DESC LIMIT 1);

-- Optionally mark that copy as LOANED
UPDATE book_copies bc
    JOIN book_copy_status s ON s.id = bc.status_id
    SET bc.status_id = (SELECT id FROM book_copy_status WHERE status='LOANED'),
        bc.update_at = NOW(6)
WHERE bc.barcode = 'BC-1001';

COMMIT;

