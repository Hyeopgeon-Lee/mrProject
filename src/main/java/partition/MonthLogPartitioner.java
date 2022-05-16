package partition;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Partitioner;

import java.util.HashMap;
import java.util.Map;

/**
 * 맵에서 Suffle and Sort로 데이터를 전달할 때 실행됨
 */
public class MonthLogPartitioner extends Partitioner<Text, Text> {

    // 월과 리듀서를 매칭하기 위한 객체
    // 1월은 0번 리듀스, 2월은 2번 리듀스 .... 이런 형태로 매칭함
    Map<String, Integer> months = new HashMap<>();

    // 생성자에 매칭 정보 저장
    public MonthLogPartitioner() {
        this.months.put("Jan", 0);
        this.months.put("Feb", 1);
        this.months.put("Mar", 2);
        this.months.put("Apr", 3);
        this.months.put("May", 4);
        this.months.put("Jun", 5);
        this.months.put("Jul", 6);
        this.months.put("Aug", 7);
        this.months.put("Sep", 8);
        this.months.put("Oct", 9);
        this.months.put("Nov", 10);
        this.months.put("Dec", 11);
    }

    /**
     * Partitioner 객체에 정의된 함수를 오버라이드
     *
     * @param key            맵에서 Suffle and Sort로 전달한 키(IP 값이 들어옴)
     * @param value          맵에서 Suffle and Sort로 전달한 값(월 값이 들어옴)
     * @param numReduceTasks 리듀서 객체 번호(0부터 시작됨)
     */
    @Override
    public int getPartition(Text key, Text value, int numReduceTasks) {

        // 실행될 리듀스 번호를 월에 따라 매핑
        return months.get(value.toString());
    }

}

