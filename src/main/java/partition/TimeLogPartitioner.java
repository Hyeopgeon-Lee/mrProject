package partition;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Partitioner;

import java.util.HashMap;
import java.util.Map;

/**
 * 맵에서 Suffle and Sort로 데이터를 전달할 때 실행됨
 */
public class TimeLogPartitioner extends Partitioner<Text, Text> {

    // 월과 리듀서를 매칭하기 위한 객체
    // 0시는 0번 리듀스, 23시는 23번 리듀스 .... 이런 형태로 매칭함
    Map<String, Integer> times = new HashMap<>();

    // 생성자에 매칭 정보 저장
    public TimeLogPartitioner() {
        this.times.put("00", 0);
        this.times.put("01", 1);
        this.times.put("02", 2);
        this.times.put("03", 3);
        this.times.put("04", 4);
        this.times.put("05", 5);
        this.times.put("06", 6);
        this.times.put("07", 7);
        this.times.put("08", 8);
        this.times.put("09", 9);
        this.times.put("10", 10);
        this.times.put("11", 11);
        this.times.put("12", 12);
        this.times.put("13", 13);
        this.times.put("14", 14);
        this.times.put("15", 15);
        this.times.put("16", 16);
        this.times.put("17", 17);
        this.times.put("18", 18);
        this.times.put("19", 19);
        this.times.put("20", 20);
        this.times.put("21", 21);
        this.times.put("22", 22);
        this.times.put("23", 23);
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

        // 실행될 리듀스 번호를 시간대에 따라 매핑
        return times.get(value.toString());
    }

}

