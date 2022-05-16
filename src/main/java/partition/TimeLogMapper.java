package partition;

import lombok.extern.log4j.Log4j;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * 맵 역할을 수행하기 위해서는 Mapper 자바 파일을 상속받아야 함
 * Mapper 파일의 앞의 2개 데이터 타입(LongWritable, Text)은 분석할 파일의 키과 값의 데이터 타입
 * Mapper 파일의 뒤의 2개 데이터 타입(Text, Text)은 리듀스(Shuffle and Sort)에 보낼 키와 값의 데이터 타입
 */
@Log4j
public class TimeLogMapper extends Mapper<LongWritable, Text, Text, Text> {

    // access_log파일로부터 추출될 시간대 정보가 제대로 수집되었는지 확인하기 위해서 만듬
    List<String> times = null;

    public TimeLogMapper() {

        // 이 변수는 만들지 않아도 되지만, 추출한 값이 정상적으로 들어왔는지 체크할려고 만듬
        // 추출한 값이 times 변수에 존재하는 값이 맞는지 체크
        this.times = Arrays.asList("00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12",
                "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23");

    }

    /**
     * 부모 Mapper 자바 파일에 작성된 map 함수를 덮어쓰기 수행
     * map 함수는 분석할 파일의 레코드 1줄마다 실행됨
     * 파일의 라인수가 100개라면, map함수는 100번 실행됨
     */
    @Override
    public void map(LongWritable key, Text value, Context context)
            throws IOException, InterruptedException {

        // 분석할 파일의 한 줄 값
        String[] fields = value.toString().split(" ");

        if (fields.length > 0) {
            String ip = fields[0];

            String[] dtFields = fields[3].split("/");

            if (dtFields.length > 1) {

                // dtFields[2] 필드 값 : 2009:14:58:59 => 여기에서 시간 부분만 가져오기
                // 인덱스 번호를 5,7 가져오면 됨
                String time = dtFields[2].substring(5, 7);

                if (times.contains(time)) {
                    context.write(new Text(ip), new Text(time));
                }

            }
        }
    }
}
