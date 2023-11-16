INSERT INTO books (id, title, author, isbn, price)
VALUES (1, 'Harry Potter', 'J Rowling', '123456-890', 23.59);

INSERT INTO books (id, title, author, isbn, price)
VALUES (2, 'Harry Potter 2', 'J Rowling', '123456-899', 25.59);

INSERT INTO books (id, title, author, isbn, price)
VALUES (3, 'Kobzar', 'T Shevchenko', '123456-999', 27.59);

INSERT INTO categories (id, name)
VALUES (1, 'Fantasy');

INSERT INTO categories (id, name)
VALUES (2, 'Poetry');

INSERT INTO books_categories (book_id, category_id)
VALUES (1, 1);

INSERT INTO books_categories (book_id, category_id)
VALUES (2, 1);

INSERT INTO books_categories (book_id, category_id)
VALUES (3, 2);