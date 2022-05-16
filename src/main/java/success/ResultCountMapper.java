package success;

import lombok.extern.log4j.Log4j;
import org.apache.hadoop.conf.Configuration;
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
@Log4j
public class ResultCountMapper extends Mapper<LongWritable, Text, Text, IntWritable> {

    // 맵리듀스 잡 이름
    String appName = "";

    // URL 전송 성공 여부 코드값
    // 성공 : 200 / 실패 : 200 아닌 것
    String resultCode = "";

    /**
     * Driver 파일에서 정의한 변수값을 가져와 map함수에 적용하기 위해 setup함수 구현
     */
    @Override
    protected void setup(Mapper<LongWritable, Text, Text, IntWritable>.Context context) throws IOException, InterruptedException {
        super.setup(context);

        // 사용자 정의 정보 가져오기
        Configuration conf = context.getConfiguration();

        // // Driver에서 정의된 맵리듀스 잡 이름 가져오기
        this.appName = conf.get("AppName");

        // Driver에서 정의된 환경설정값 가져오기
        // Driver에서 정의된 환경설정값이 없다면, 200으로 설정함
        this.resultCode = conf.get("resultCode", "200");

        log.info("[" + this.appName + "] 난 map합수 실행하기 전에 1번만 실행되는 setup함수다!");

    }

    @Override
    protected void cleanup(Mapper<LongWritable, Text, Text, IntWritable>.Context context) throws IOException, InterruptedException {
        super.cleanup(context);

        log.info("[" + this.appName + "] 난 에러나도 무조건 실행되는 cleanup함수다!");
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
        String line = value.toString();

        // 단어별로 나누기
        String[] arr = line.split("\\W+");

        // 전송 결과코드가 존재하는 위치
        // 로그의 마지막에서 2번째에 성공코드 값이 존재함
        int pos = arr.length - 2;

        // 전송 결과코드
        String result = arr[pos];

        log.info("[" + this.appName + "] " + result);

        // Driver 파일에서 정의한 코드값과 로그의 코드값이 일치한다면
        if (resultCode.equals(result)) {
            context.write(new Text(result), new IntWritable(1));

        }

    }
}

