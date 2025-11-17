package ip;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

/**
 * Mapper 클래스는 로그 파일에서 각 줄마다 호출되어
 * 첫 번째 필드(IP 주소)를 키로, 1을 값으로 출력합니다.
 * 최종적으로 IP별 요청 횟수를 집계하는 데 사용됩니다.
 */
public class IPCountMapper extends Mapper<LongWritable, Text, Text, IntWritable> {

    /**
     * map() 함수는 입력 데이터의 각 레코드(한 줄)에 대해 한 번씩 호출됩니다.
     * 입력 키: LongWritable → 파일의 오프셋 위치 (사용 안 함)
     * 입력 값: Text → 로그 한 줄
     * 출력 키: Text → IP 주소
     * 출력 값: IntWritable → 카운트(항상 1)
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
