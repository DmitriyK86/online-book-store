package bookstore.dto;

public record BookSearchParameters(String[] titles, String[] authors, String[] isbns, String[] prices, String[] descriptions) {
}
