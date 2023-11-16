INSERT INTO books (id, title, author, isbn, price)
VALUES (1, 'Harry Potter', 'J Rowling', '123456-890', 23.59);

INSERT INTO categories (id, name)
VALUES (1, 'Fantasy');

INSERT INTO books_categories (book_id, category_id)
VALUES (1, 1);
