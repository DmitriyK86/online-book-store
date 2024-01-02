INSERT INTO books (id, title, author, isbn, price)
VALUES (1, 'Harry Potter', 'J Rowling', '123456-890', 23.59);

INSERT INTO categories (id, name)
VALUES (1, 'Fantasy');

INSERT INTO books_categories (book_id, category_id)
VALUES (1, 1);

INSERT INTO users (id, email, password, first_name, last_name, shipping_address, is_deleted)
VALUES (1, 'admin@gmail.com', '123456789', 'Bob', 'Smith', 'SomeAddress', false);

INSERT INTO shopping_carts (user_id, is_deleted)
VALUES (1, false);

INSERT INTO cart_items (id, shopping_cart_id, book_id, quantity)
VALUES (1, 1, 1, 2);