package cache;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;

/**
 * 캐시 파일에서 특정 단어 목록을 메모리에 로드한 후,
 * 입력 파일에서 해당 단어가 나올 경우에만 카운팅하는 Mapper 클래스
 */
public class WordCount3Mapper extends Mapper<LongWritable, Text, Text, IntWritable> {

    // 캐시 파일에서 읽은 단어들을 저장할 Set (중복 방지 & 빠른 조회용)
    private final Set<String> wordSet = new HashSet<>();

    /**
     * 맵리듀스 작업 시작 전 한 번 실행됨 (1회)
     * Distributed Cache에 등록된 파일을 읽어 메모리에 단어 목록으로 저장함
     */
    @Override
    protected void setup(Context context) throws IOException {
        // 등록된 캐시 파일 배열 가져오기
        URI[] cacheFiles = context.getCacheFiles();

        if (cacheFiles != null) {
            for (URI cacheFile : cacheFiles) {
                // HDFS 상 경로에서 파일 이름만 추출 (캐시 파일은 로컬로 복사됨)
                String fileName = new Path(cacheFile.getPath()).getName();

                // 캐시 파일을 한 줄씩 읽어서 단어 리스트 생성
                BufferedReader reader = new BufferedReader(new FileReader(fileName));
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim().toLowerCase();
                    if (!line.isEmpty()) {
                        wordSet.add(line);  // 단어를 Set에 저장
                    }
                }
                reader.close();
            }
        }
    }

    /**
     * 입력 파일에서 한 줄씩 읽어 단어를 분리한 후,
     * 캐시 파일에서 등록된 단어와 일치하는 것만 출력으로 넘김
     */
    @Override
    protected void map(LongWritable key, Text value, Context context)
            throws IOException, InterruptedException {

        // 입력 파일의 한 줄
        String line = value.toString();

        // 공백 또는 특수문자를 기준으로 단어 분리
        for (String word : line.split("\\W+")) {
            if (!word.isEmpty()) {
                // 대소문자 구분 없이 처리할 경우 toLowerCase() 필요
                String lowerWord = word.toLowerCase();

                // 캐시에서 불러온 단어 목록에 포함된 단어만 출력
                if (wordSet.contains(lowerWord)) {
                    context.write(new Text(lowerWord), new IntWritable(1));
                }
            }
        }
    }
}
