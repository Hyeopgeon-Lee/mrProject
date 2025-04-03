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

        // 분석할 파일의 한 줄 값
        String line = value.toString();

        String ip = ""; // 로그부터 추출된 IP
        int forCnt = 0; // 반복 횟수

        // 단어 빈도수 구현은 단어가 아닌 것을 기준으로 단어로 구분함
        // 분석할 한 줄 내용을 단어가 아닌 것으로 나눔
        // word 변수는 단어가 저장됨
        for (String field : line.split("\\W+")) {

            if (!field.isEmpty()) {

                forCnt++;// 반복회수 증가
                ip += (field + "."); // IP값 저장함

                // IP는 196.168.0.127 => 4가지 숫자로 조합되기에 반복문 1번부터 4번까지가 IP 주소임
                if (forCnt == 4) {

                    // ip변수 값은 196.168.0.127. 와 같이 마지막에도 . 붙음
                    // 마지막 .을 제거하기 위해 0부터 마지막 위치에서 -1 값까지 문자열을 짜름
                    ip = ip.substring(0, ip.length()-1);

                    // Suffle and Sort로 데이터를 전달하기
                    // 전달하는 값은 단어와 빈도수(1)를 전달함
                    context.write(new Text(ip), new IntWritable(1));

                }
            }
        }
    }
}
