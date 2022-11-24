import org.apache.commons.io.FileUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Int;
import scala.Tuple2;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class WordCounter {
    public static void main(String[] args) {
        String[] searchWords = {"Canada", "Halifax", "hockey", "hurricane", "electricity", "house", "inflation"};
        SparkConf sparkConf = new SparkConf().setMaster("local").setAppName("Word Counter");
        JavaSparkContext javaSparkContext = new JavaSparkContext(sparkConf);
        for (int i = 0 ; i < searchWords.length; i++){
            JavaRDD<String> file = javaSparkContext.textFile("file:///home//khshah2699//"+searchWords[i]+".txt");
            JavaRDD<String> numberOfWords = file.flatMap(line -> Arrays.asList(line.split(" ")).iterator());
            JavaPairRDD<String, Integer> wordCount = numberOfWords.mapToPair(t -> new Tuple2<>(t, 1)).reduceByKey((x, y) -> x + (int) y);
            Map<String,Integer> map = new HashMap<String, Integer>();
            map = wordCount.collectAsMap();
            if(map.containsKey(searchWords[i])){
                System.out.println(searchWords[i] + ": "+ map.get(searchWords[i]));
            }
        }
    }
}