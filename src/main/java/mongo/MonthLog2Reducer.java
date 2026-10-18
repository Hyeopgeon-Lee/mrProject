package mongo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.extern.slf4j.Slf4j;
import mongo.conn.MongoDBConnection;
import mongo.dto.AccessLogDTO;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.bson.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 리듀스 역할을 수행하기 위해서는 Reducer 자바 파일을 상속받아야 함
 * Reducer 파일의 앞의 2개 데이터 타입(Text, Text)은 Suffle and Sort에 보낸 데이터의 키과 값의 데이터 타입
 * 보통 Mapper에서 보낸 데이터타입과 동일함
 * Reducer 파일의 뒤의 2개 데이터 타입(Text, IntWritable)은 결과 파일 생성에 사용될 키와 값
 */
@Slf4j
public class MonthLog2Reducer extends Reducer<Text, Text, Text, IntWritable> {

    // MongoDB 객체
    private MongoDatabase mongodb;
    private MongoDBConnection connection;

    // 월과 리듀서 번호와 매핑할 객체
    // 키 : 월 (Jan...Dec)| 값 : 컬렉션명(LOG_01...)
    private Map<String, String> months = new HashMap<>();

    public MonthLog2Reducer() {

        // 생성자에 매칭 정보 저장
        this.months.put("Jan", "LOG_01");
        this.months.put("Feb", "LOG_02");
        this.months.put("Mar", "LOG_03");
        this.months.put("Apr", "LOG_04");
        this.months.put("May", "LOG_05");
        this.months.put("Jun", "LOG_06");
        this.months.put("Jul", "LOG_07");
        this.months.put("Aug", "LOG_08");
        this.months.put("Sep", "LOG_09");
        this.months.put("Oct", "LOG_10");
        this.months.put("Nov", "LOG_11");
        this.months.put("Dec", "LOG_12");
    }

    @Override
    protected void setup(Reducer<Text, Text, Text, IntWritable>.Context context)
            throws IOException, InterruptedException {

        //MongoDB 객체 생성을 통해 접속
        this.connection = new MongoDBConnection();
        this.mongodb = connection.getMongoDB();

        // MongoDB automatically creates missing collections during insertion.
        // Explicit parallel check-then-create is racy across reducers.
    }

    /**
     * 부모 Reducer 자바 파일에 작성된 reduce 함수를 덮어쓰기 수행
     * reduce 함수는 Suffle and Sort로 처리된 데이터마다 실행됨
     * 처리된 데이터의 수가 500개라면, reduce 함수는  500번 실행됨
     * <p>
     * Reducer 객체는 기본값이 1개로 1개의 쓰레드로 처리함
     */
    @Override
    public void reduce(Text key, Iterable<Text> values, Context context)
            throws IOException, InterruptedException {

        String collectionName = months.get(key.toString());
        if (collectionName == null) {
            throw new IOException("Unknown month: " + key);
        }
        MongoCollection<Document> collection = mongodb.getCollection(collectionName);
        ObjectMapper mapper = new ObjectMapper();

        // Bound each MongoDB insert batch in memory rather than accumulating
        // every record in the reducer before inserting.
        final int batchSize = 1000;
        List<Document> batch = new ArrayList<>(batchSize);
        for (Text value : values) {
            AccessLogDTO dto = mapper.readValue(value.toString(), AccessLogDTO.class);
            batch.add(new Document(mapper.convertValue(dto, Map.class)));
            if (batch.size() >= batchSize) {
                collection.insertMany(batch);
                batch.clear();
            }
        }
        if (!batch.isEmpty()) {
            collection.insertMany(batch);
        }
        log.info("MongoDB collection {} updated", collectionName);
    }

    @Override
    protected void cleanup(Context context)
            throws IOException, InterruptedException {

        // MongoDB 접속 해제
        if (connection != null) {
            connection.close();
        }
        this.mongodb = null;
    }
}

