import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class NewsTransformation {
    MongoClient mongoClient = MongoClients.create("mongodb://127.0.0.1:27017");
    MongoDatabase database = mongoClient.getDatabase("bigMongoNews");

    public void filterNews() throws IOException {
        File directoryPath = new File("/Users/khsha/IdeaProjects/Data_Assignment_A3/src/main/java/files");
        String contents[] = directoryPath.list();
        getFilesAndRead(contents);
    }

    public void getFilesAndRead(String[] contents) throws IOException {
        String finalDataInFile = null;
        final String separatorFilter = "},\\{";

        for(int i = 0 ; i < contents.length; i++){
            MongoCollection<Document> collection = database.getCollection(contents[i].substring(0,contents[i].length()-4));
            File file = new File ("/Users/khsha/IdeaProjects/dataA3/src/main/java/files/"+contents[i]);
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line;

            while ((line = br.readLine()) != null){
                finalDataInFile = line;
            }
            br.close();
            fr.close();
            final String emojisFilter = "[^\\p{L}\\p{N}\\p{P}\\p{Z}]";
            final String urlToImageFilter = "(\"urlToImage\":(\"http.*?\"|null|\"null\"|\"\"),)";
            final String urlFilter = "(\"url\":(\"http.*?\"|null|\"null\"|\"\"),)";
            final String authorFilter = "(\"author\":(\"http.*?\"|null|\"null\"|\"\"),)";
            final String idFilter = "(,\"id\":(null|\"null\"|\"\"))";
            final String generalFilter = "(\\\\[ntr])|(NBSP)|(<[^>]*>)";
            final String specialCharacterFilter = "([!@#$%^&])";

            finalDataInFile = finalDataInFile.replaceAll(urlToImageFilter, " ");
            finalDataInFile = finalDataInFile.replaceAll(urlFilter, " ");
            finalDataInFile = finalDataInFile.replaceAll(emojisFilter, " ");
            finalDataInFile = finalDataInFile.replaceAll(authorFilter, " ");
            finalDataInFile = finalDataInFile.replaceAll(idFilter, " ");
            finalDataInFile = finalDataInFile.replaceAll(generalFilter, " ");
            finalDataInFile = finalDataInFile.replaceAll(specialCharacterFilter, "");
            final String[] listOfArticles = finalDataInFile.split(separatorFilter);

            collection.insertMany(filterlistOfArticles(listOfArticles,collection));
        }
    }

    public List<Document> filterlistOfArticles(String[] listOfArticles, MongoCollection<Document> collection){
        final List<Document> mongoNewsDocuments = new ArrayList<>();
        for(int i = 0 ; i < listOfArticles.length; i++){
            String id = getId(listOfArticles[i]);
            String name = getName(listOfArticles[i]);
            String title = getTitle(listOfArticles[i]);
            String description = getDescription(listOfArticles[i]);
            String publishedAt = getPublishAt(listOfArticles[i]);
            String content = getContent(listOfArticles[i]);

            Document document = new Document("id",id).append("name",name).append("title",title)
                    .append("description",description).append("published_at",publishedAt)
                    .append("content",content);
            mongoNewsDocuments.add(document);
        }
        return mongoNewsDocuments;
    }
    public String getId(String article){
        int start = article.indexOf("\"source\":{\"id\":\"") + "\"source\":{\"id\":\"".length();
        int end = article.lastIndexOf(",\"name");
        String id = article.substring(start,end);
        if(id.contains("\""))
        {
            id = article.substring(start, end-1);
        }
        return id;
    }

    public String getName(String article){
        int start = article.indexOf(",\"name\":\"") + ",\"name\":\"".length();
        int end = article.lastIndexOf("\"},");
        return article.substring(start,end);
    }

    public String getTitle(String article){
        int start = article.indexOf("\"title\":\"") + "\"title\":\"".length();
        int end = article.lastIndexOf("\",\"description");
        return article.substring(start, end);
    }

    public String getDescription(String article){
        int start = article.indexOf("\"description\":\"") + "\"description\":\"".length();
        int end = article.lastIndexOf("\",  \"publishedAt");
        return article.substring(start,end);
    }

    public String getPublishAt(String article){
        int start = article.indexOf("\"publishedAt\":\"") + "\"publishedAt\":\"".length();
        int end = article.lastIndexOf("\",\"content");
        return article.substring(start,end);
    }

    public String getContent(String article){
        int start = article.indexOf("\"content\":\"") + "\"content\":\"".length();
        int end = article.lastIndexOf("\"");
        return article.substring(start,end);
    }
}

