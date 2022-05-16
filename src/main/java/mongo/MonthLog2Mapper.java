package mongo;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j;
import mongo.dto.AccessLogDTO;
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
public class MonthLog2Mapper extends Mapper<LongWritable, Text, Text, Text> {

    // access_log파일로부터 추출될 월 정보가 제대로 수집되었는지 확인하기 위해서 만듬
    List<String> months = null;

    public MonthLog2Mapper() {

        // 이 변수는 만들지 않아도 되지만, 추출한 값이 정상적으로 들어왔는지 체크할려고 만듬
        // 추출한 값이 months 변수에 존재하는 값이 맞는지 체크
        this.months = Arrays.asList("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec");

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

        String ip = fields[0]; // IP 추출
        String reqTime = ""; // 요청일시

        if (fields[3].length() > 2) { // 일부 데이터가 요청일시 값이 누락된 경우가 있어 요청일시 값이 존재하는지 체크
            reqTime = fields[3].substring(1); // 요청일시 추출
        }

        String reqMethod = ""; // GET, POST 요청 방법 추출

        if (fields[5].length() > 2) { // 일부 데이터가 요청일시 값이 누락된 경우가 있어 요청일시 값이 존재하는지 체크
            reqMethod = fields[5].substring(1); // 요청일시 추출
        }

        String reqURI = fields[6]; // 요청 URI 추출

        String reqMonth = ""; // 요청일시에서 월 정보 추출하기

        String[] dtFields = fields[3].split("/");

        if (dtFields.length > 1) {
            reqMonth = dtFields[1];
        }

        log.info("ip : " + ip);
        log.info("reqTime : " + reqTime);
        log.info("reqMethod : " + reqMethod);
        log.info("reqURI : " + reqURI);
        log.info("reqMonth : " + reqMonth);

        // 추출한 정보를 저장하기 위해 pDTO 선언 후, 값 저장
        AccessLogDTO pDTO = new AccessLogDTO();
        pDTO.setIp(ip);
        pDTO.setReqTime(reqTime); // 요청 시간
        pDTO.setReqMethod(reqMethod); // 요청 방법
        pDTO.setReqURI(reqURI); // 요청 URI

        // DTO에 저장된 내용을 JSON 문자열로 변경
        // DTO -> JSON 변경
        String json = new ObjectMapper().writeValueAsString(pDTO);

        // 월정보가 일치하는 체크
        if (months.contains(reqMonth)) {

            // MonthLog2Partitioner 로 보내서, 월별 리듀스 분할하기
            context.write(new Text(reqMonth), new Text(json));
        }
    }
}

