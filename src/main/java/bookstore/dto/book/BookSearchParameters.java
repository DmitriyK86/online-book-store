package bookstore.dto.book;

public record BookSearchParameters(String[] titles,
                                   String[] authors,
                                   String[] isbns,
                                   String[] prices,
                                   String[] descriptions) {
}
