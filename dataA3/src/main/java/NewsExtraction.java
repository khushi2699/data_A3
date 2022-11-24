import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class NewsExtraction {
    public static void main(String[] args) throws IOException, InterruptedException {
        String[] searchWords = {"Canada", "Halifax", "hockey", "hurricane", "electricity", "house", "inflation"};
//        for (String searchWord : searchWords) {
//            String URL = "https://newsapi.org/v2/everything?q=" + searchWord + "&apiKey=9af615bf109c4c6bbc53f04144761c5d";
//            HttpClient httpClient = HttpClient.newHttpClient();
//            HttpRequest httpRequest = HttpRequest.newBuilder()
//                    .GET()
//                    .header("accept", "application/json")
//                    .uri(URI.create(URL)).build();
//            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
//            storeInText(response, searchWord);
//        }
        NewsTransformation nt = new NewsTransformation();
        nt.filterNews();
    }

    public static void storeInText(HttpResponse<String> response, String searchWord) throws IOException {
        File file = new File("/Users/khsha/IdeaProjects/dataA3/src/main/java/files/" + searchWord + ".txt");
        FileWriter fw = new FileWriter(file);
        if(response != null) {
            try {
                fw = new FileWriter(file);
                fw.write(response.body());
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        else {
            try {
                fw = new FileWriter(file);
                fw.write(" ");
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
