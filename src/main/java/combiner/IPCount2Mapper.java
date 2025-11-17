package combiner;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

/**
 * 맵 역할을 수행하기 위해서는 Mapper 자바 파일을 상속받아야 함
 * Mapper 파일의 앞의 2개 데이터 타입(LongWritable, Text)은 분석할 파일의 키과 값의 데이터 타입
 * Mapper 파일의 뒤의 2개 데이터 타입(Text, IntWritable)은 리듀스에 보낼 키와 값의 데이터 타입
 */
public class IPCount2Mapper extends Mapper<LongWritable, Text, Text, IntWritable> {

    /**
     * 부모 Mapper 자바 파일에 작성된 map 함수를 덮어쓰기 수행
     * map 함수는 분석할 파일의 레코드 1줄마다 실행됨
     * 파일의 라인수가 100개라면, map함수는 100번 실행됨
     */
    @Override
    public void map(LongWritable key, Text value, Context context)
            throws IOException, InterruptedException {

        // 분석할 로그 한 줄 가져오기
        String line = value.toString().trim();

        // 로그 한 줄을 공백 기준으로 나눔
        String[] data = line.split(" ");

        // 로그가 유효하고, 첫 번째 토큰(IP 주소)이 존재하는 경우
        if (data.length > 3 && data[0].matches("\\d+\\.\\d+\\.\\d+\\.\\d+")) {

            // 첫 번째 요소는 IP 주소
            String ip = data[0];

            // IP 주소를 키로, 1을 값으로 출력
            context.write(new Text(ip), new IntWritable(1));
        }
    }
}
