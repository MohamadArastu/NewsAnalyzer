package newsapi.exceptionModel;

public class NewsApiException extends Exception {
    public NewsApiException(String errorMessage) {
        super(errorMessage);
    }
}
