package newsanalyzer.ctrl;

import downloader.ParallelDownloader;
import newsapi.NewsApi;
import newsapi.NewsApiBuilder;
import newsapi.beans.Article;
import newsapi.beans.NewsReponse;
import downloader.SequentialDownloader;
import newsapi.enums.Category;
import newsapi.enums.Country;
import newsapi.enums.Endpoint;
import newsapi.exceptionModel.NewsApiException;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class Controller {

    //https://github.com/MohamadArastu/NewsAnalyzer
    public static final String APIKEY = "2342c4285f08489c9f21902fbb6af30d";
    public static StringBuilder analysedData = new StringBuilder();

    public String process(String theme, Endpoint endpoint, Country country, Category category) throws NewsApiException, IOException, ExecutionException, InterruptedException {
        System.out.println("Start process");

        //TODO implement Error handling

        //TODO load the news based on the parameters

        //TODO implement methods for analysis

        NewsApi newsApi = new NewsApiBuilder()
                .setApiKey(APIKEY)
                .setQ(theme)
                .setEndPoint(Endpoint.EVERYTHING)
                .setPageSize("5")
                .createNewsApi();

        List<Article> articles = getData(newsApi);//getData(theme, endpoint, country, category);

        downloadLastSearch(articles);

//        analysedData.append(getArticleCount(articles));
//        analysedData.append(getShortestAuthorName(articles));
//        analysedData.append(sortForLongestTitle(articles));

        System.out.println("End process");

        return analysedData.toString();
    }

    public static int getArticleCount(List<Article> articles) {
        return (int) articles.stream().count();
    }

    public String getShortestAuthorName(List<Article> articles) {
        articles = articles.stream()
                .filter(a -> a.getAuthor() != null)
                .sorted((a1, a2) -> Integer.compare(a1.getAuthor().length(), a2.getAuthor().length()))
                .collect(Collectors.toList());
        return articles.get(0).getAuthor();
    }

    public List<Article> sortForLongestTitle(List<Article> articles) {
        return articles.stream()
                .filter(a -> a.getTitle() != null)
                .sorted((a1, a2) -> Integer.compare(a2.getTitle().length(), a1.getTitle().length()))
                .collect(Collectors.toList());
    }

//    public List<Article> getData(String theme, Endpoint endpoint, Country country, Category category) throws NewsApiException, IOException {
//
//        NewsApi newsApi = new NewsApiBuilder()
//                .setApiKey(APIKEY)
//                .setQ(theme)
//                .setEndPoint(endpoint)
//                .setSourceCountry(country)
//                .setSourceCategory(category)
//                .setPageSize("5")
//                .createNewsApi();
//
//        NewsReponse newsReponse = newsApi.getNews();
//        if (newsReponse != null)
//            return newsReponse.getArticles();
//        else
//            return null;
//    }

    public List<Article> getData(NewsApi newsApi) throws NewsApiException, IOException {
        NewsReponse newsResponse = newsApi.getNews();
        return newsResponse.getArticles();
    }

    public void downloadLastSearch(List<Article> articles) throws InterruptedException, ExecutionException {
        var seqDownloader = new SequentialDownloader();
        var parlDownloader = new ParallelDownloader();

        long minuteNow = System.currentTimeMillis();
        seqDownloader.process(getURLs(articles));
        long endMinute = System.currentTimeMillis();

        System.out.println("Sequential Execution time: " + (endMinute - minuteNow) + " ms");

        long minuteNow2 = System.currentTimeMillis();
        parlDownloader.process(getURLs(articles));
        long finishedMin = System.currentTimeMillis();

        System.out.println("Parallel Execution time: " + (finishedMin - minuteNow2) + " ms");
    }

    public static List<String> getURLs(List<Article> articles) {
        return articles.stream().map(Article::getUrl).collect(Collectors.toList());
    }
}

