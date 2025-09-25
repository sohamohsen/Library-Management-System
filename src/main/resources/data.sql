START TRANSACTION;

-- 1) Roles
INSERT INTO roles(role)
SELECT 'ADMINISTRATOR' WHERE NOT EXISTS (SELECT 1 FROM roles WHERE role='ADMINISTRATOR');
INSERT INTO roles(role)
SELECT 'LIBRARIAN'     WHERE NOT EXISTS (SELECT 1 FROM roles WHERE role='LIBRARIAN');
INSERT INTO roles(role)
SELECT 'STAFF'         WHERE NOT EXISTS (SELECT 1 FROM roles WHERE role='STAFF');

-- 2) Users (guard by username or email)
INSERT INTO system_users (username, email, full_name, password, enabled, create_at, create_by, update_at, phone, role_id)
SELECT 'admin', 'admin@example.com', 'Admin User',
       '$2a$10$7EqJtq98hPqEX7fNZaFWo./Xq5Q6DOW3EtFPXK62o/4pG7Wu3.yGa',  -- "password"
       b'1', NOW(6), 1, NOW(6), '0100000000',
       (SELECT id FROM roles WHERE role='ADMINISTRATOR')
WHERE NOT EXISTS (SELECT 1 FROM system_users WHERE username='admin' OR email='admin@example.com');

INSERT INTO system_users (username, email, full_name, password, enabled, create_at, create_by, update_at, phone, role_id)
SELECT 'librarian', 'librarian@example.com', 'Lib Rarian',
       '$2a$10$7EqJtq98hPqEX7fNZaFWo./Xq5Q6DOW3EtFPXK62o/4pG7Wu3.yGa',
       b'1', NOW(6), 1, NOW(6), '0100000001',
       (SELECT id FROM roles WHERE role='LIBRARIAN')
WHERE NOT EXISTS (SELECT 1 FROM system_users WHERE username='librarian' OR email='librarian@example.com');

INSERT INTO system_users (username, email, full_name, password, enabled, create_at, create_by, update_at, phone, role_id)
SELECT 'staff', 'staff@example.com', 'Staff Member',
       '$2a$10$7EqJtq98hPqEX7fNZaFWo./Xq5Q6DOW3EtFPXK62o/4pG7Wu3.yGa',
       b'1', NOW(6), 1, NOW(6), '0100000002',
       (SELECT id FROM roles WHERE role='STAFF')
WHERE NOT EXISTS (SELECT 1 FROM system_users WHERE username='staff' OR email='staff@example.com');

-- 3) Lookup tables
INSERT INTO book_copy_status(status)
SELECT 'AVAILABLE' WHERE NOT EXISTS (SELECT 1 FROM book_copy_status WHERE status='AVAILABLE');
INSERT INTO book_copy_status(status)
SELECT 'LOANED'    WHERE NOT EXISTS (SELECT 1 FROM book_copy_status WHERE status='LOANED');
INSERT INTO book_copy_status(status)
SELECT 'DAMAGED'   WHERE NOT EXISTS (SELECT 1 FROM book_copy_status WHERE status='DAMAGED');
INSERT INTO book_copy_status(status)
SELECT 'LOST'      WHERE NOT EXISTS (SELECT 1 FROM book_copy_status WHERE status='LOST');

INSERT INTO publisher(name, founder_year)
SELECT 'Pearson', 1844 WHERE NOT EXISTS (SELECT 1 FROM publisher WHERE name='Pearson');

INSERT INTO publisher(name, founder_year)
SELECT 'O''Reilly Media', 1980 WHERE NOT EXISTS (SELECT 1 FROM publisher WHERE name='O''Reilly Media');

INSERT INTO category(name, description)
SELECT 'Technology',  'Technology books'
WHERE NOT EXISTS (SELECT 1 FROM category WHERE name='Technology');

INSERT INTO category(name, description)
SELECT 'Programming', 'Programming/software engineering'
WHERE NOT EXISTS (SELECT 1 FROM category WHERE name='Programming');

INSERT INTO category(name, description)
SELECT 'Databases',   'Relational and NoSQL databases'
WHERE NOT EXISTS (SELECT 1 FROM category WHERE name='Databases');

INSERT INTO authors(full_name, bio)
SELECT 'Robert C. Martin', 'Uncle Bob, software craftsmanship'
WHERE NOT EXISTS (SELECT 1 FROM authors WHERE full_name='Robert C. Martin');

INSERT INTO authors(full_name, bio)
SELECT 'Joshua Bloch', 'Effective Java author'
WHERE NOT EXISTS (SELECT 1 FROM authors WHERE full_name='Joshua Bloch');

-- 4) Members (guard by code or email)
INSERT INTO members(code, full_name, email, phone, active, joined_on, create_at, create_by, update_at)
SELECT 'MBR-0001', 'Ali Ahmed',  'ali@example.com',  '0101111111', b'1', CURDATE(), NOW(6), 1, NOW(6)
WHERE NOT EXISTS (SELECT 1 FROM members WHERE code='MBR-0001' OR email='ali@example.com');

INSERT INTO members(code, full_name, email, phone, active, joined_on, create_at, create_by, update_at)
SELECT 'MBR-0002', 'Sara Hassan','sara@example.com', '0102222222', b'1', CURDATE(), NOW(6), 1, NOW(6)
WHERE NOT EXISTS (SELECT 1 FROM members WHERE code='MBR-0002' OR email='sara@example.com');

-- 5) Books (isbn is already UNIQUE in your schema)
INSERT INTO books(title, isbn, publication_year, language, edition, summary,
                  create_at, create_by, update_at, author_id, category_id, publisher_id)
SELECT 'Clean Code', '9780132350884', 2008, 'EN', '1',
       'A Handbook of Agile Software Craftsmanship',
       NOW(6), 1, NOW(6),
       (SELECT id FROM authors  WHERE full_name='Robert C. Martin'),
       (SELECT id FROM category WHERE name='Programming'),
       (SELECT id FROM publisher WHERE name='Pearson')
WHERE NOT EXISTS (SELECT 1 FROM books WHERE isbn='9780132350884');

INSERT INTO books(title, isbn, publication_year, language, edition, summary,
                  create_at, create_by, update_at, author_id, category_id, publisher_id)
SELECT 'Effective Java', '9780134685991', 2018, 'EN', '3',
       'Best practices for the Java platform',
       NOW(6), 1, NOW(6),
       (SELECT id FROM authors  WHERE full_name='Joshua Bloch'),
       (SELECT id FROM category WHERE name='Programming'),
       (SELECT id FROM publisher WHERE name='Pearson')
WHERE NOT EXISTS (SELECT 1 FROM books WHERE isbn='9780134685991');

-- 6) Book copies (guard by barcode)
SET @status_available := (SELECT id FROM book_copy_status WHERE status='AVAILABLE');

INSERT INTO book_copies(book_id, barcode, status_id, acquired_at, create_at, create_by, update_at)
SELECT (SELECT id FROM books WHERE isbn='9780132350884'), 'BC-1001', @status_available, NOW(6), NOW(6), 1, NOW(6)
WHERE NOT EXISTS (SELECT 1 FROM book_copies WHERE barcode='BC-1001');

INSERT INTO book_copies(book_id, barcode, status_id, acquired_at, create_at, create_by, update_at)
SELECT (SELECT id FROM books WHERE isbn='9780132350884'), 'BC-1002', @status_available, NOW(6), NOW(6), 1, NOW(6)
WHERE NOT EXISTS (SELECT 1 FROM book_copies WHERE barcode='BC-1002');

INSERT INTO book_copies(book_id, barcode, status_id, acquired_at, create_at, create_by, update_at)
SELECT (SELECT id FROM books WHERE isbn='9780134685991'), 'BC-2001', @status_available, NOW(6), NOW(6), 1, NOW(6)
WHERE NOT EXISTS (SELECT 1 FROM book_copies WHERE barcode='BC-2001');

-- 7) A sample open loan with one item (only create if not already created for that member/copy)
-- Create loan for Ali Ahmed due in 14 days if no open loan for that member exists today
INSERT INTO loans(create_at, due_at, status, update_at, create_by, member_id)
SELECT NOW(6), NOW(6) + INTERVAL 14 DAY, 0, NOW(6),
       (SELECT id FROM system_users WHERE username='admin'),
       (SELECT id FROM members WHERE code='MBR-0001')
WHERE NOT EXISTS (
    SELECT 1 FROM loans
    WHERE member_id = (SELECT id FROM members WHERE code='MBR-0001')
      AND DATE(create_at) = CURDATE()
);

-- Add one loan item for copy BC-1001 if no loan item exists today for that copy
INSERT INTO loan_items(create_at, create_by, fine_amount, returned_at, update_at, copy_id, loan_id)
SELECT NOW(6),
       (SELECT id FROM system_users WHERE username='admin'),
       0.0, NULL, NOW(6),
       (SELECT id FROM book_copies WHERE barcode='BC-1001'),
       (SELECT id FROM loans WHERE member_id=(SELECT id FROM members WHERE code='MBR-0001') ORDER BY id DESC LIMIT 1)
WHERE NOT EXISTS (
    SELECT 1 FROM loan_items
    WHERE copy_id = (SELECT id FROM book_copies WHERE barcode='BC-1001')
      AND DATE(create_at) = CURDATE()
);

-- Optionally mark that copy as LOANED (only if it's still AVAILABLE)
UPDATE book_copies bc
    JOIN book_copy_status sAvail ON sAvail.id = bc.status_id AND sAvail.status='AVAILABLE'
SET bc.status_id = (SELECT id FROM book_copy_status WHERE status='LOANED'),
    bc.update_at = NOW(6)
WHERE bc.barcode = 'BC-1001';

COMMIT;
