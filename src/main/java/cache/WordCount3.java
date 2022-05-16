package cache;

import lombok.extern.log4j.Log4j;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import java.net.URI;

/**
 * 맵리듀스를 실행하기 위한 Main 함수가 존재하는 자바 파일
 * 드라이버 파일로 부름
 */
@Log4j
public class WordCount3 extends Configuration implements Tool {

    // 맵리듀스 실행 함수
    public static void main(String[] args) throws Exception {

        // 파라미터는 분석 결과가 저장될 파일(폴더)만 받음
        if (args.length != 1) {
            log.info("분석결과가 저장될 폴더를 입력해야 합니다.");
            System.exit(-1);

        }

        // Cache로 로그파일을 올리고, 맵리듀스 실행 전 시간
        long startTime = System.nanoTime();

        int exitCode = ToolRunner.run(new WordCount3(), args);

        // Cache로 로그파일을 올리고, 맵리듀스 실행 완료 시간
        long endTime = System.nanoTime();

        log.info("Cache Time : " + (endTime - startTime) + "ns");

        System.exit(exitCode);

    }

    @Override
    public void setConf(Configuration configuration) {

        // App 이름 정의
        configuration.set("AppName", "Cache Test");

    }

    @Override
    public Configuration getConf() {

        // 맵리듀스 전체에 적용될 변수를 정의할 때 사용
        Configuration conf = new Configuration();

        // 변수 정의
        this.setConf(conf);

        return conf;
    }

    @Override
    public int run(String[] args) throws Exception {

        // 캐시메모리에 올릴 분석 파일
        String analysisFile = "/comedies";

        Configuration conf = this.getConf();
        String appName = conf.get("AppName");

        log.info("appName : "+ appName);

        // 맵리듀스 실행을 위한 잡 객체를 가져오기
        // 하둡이 실행되면, 기본적으로 잡 객체를 메모리에 올림
        Job job = Job.getInstance(conf);

        // 맵리듀스 잡이 시작되는 main함수가 존재하는 파일 설정
        job.setJarByClass(WordCount3.class);

        // 맵리듀스 잡 이름 설정, 리소스 매니저 등 맵리듀스 실행 결과 및 로그 확인할 때 편리함
        job.setJobName(appName);

        // 호출이 발생하면, 메모리에 저장하여 캐시 처리 수행
        // 하둡분산파일시스템에 저장된 파일만 가능함
        // /comedies 파일을 메모리에 올리기
        job.addCacheFile(new Path(analysisFile).toUri());

        // 캐시 메모리에 저장된 파일 정보들 가져오기
        URI[] cacheFiles = job.getCacheFiles();

        // 파일 이름 가져오기
        for (URI cacheFile : cacheFiles) {
            Path uploadFile = new Path(cacheFile.getPath());

            // 캐시 메모리에 업로드된 파일명 확인
            log.info("Uploaded CacheFile : " + uploadFile);

        }

        // 분석할 폴더(파일) -- 메모리에 올라간 /comedies 파일명 작성함
        // 메모리에 존재하면, 그 파일을 먼저 받음
        FileInputFormat.setInputPaths(job, analysisFile);

        // 분석 결과가 저장되는 폴더(파일) -- 두번째 파라미터
        FileOutputFormat.setOutputPath(job, new Path(args[0]));

        // 맵리듀스의 맵 역할을 수행하는 Mapper 자바 파일 설정
        job.setMapperClass(WordCount3Mapper.class);

        // 맵리듀스의 리듀스 역할을 수행하는 Reducer 자바 파일 설정
        job.setReducerClass(WordCount3Reducer.class);

        // 분석 결과가 저장될때 사용될 키의 데이터 타입
        job.setOutputKeyClass(Text.class);

        // 분석 결과가 저장될때 사용될 값의 데이터 타입
        job.setOutputValueClass(IntWritable.class);

        // 맵리듀스 실행
        boolean success = job.waitForCompletion(true);

        return (success ? 0 : 1);
    }


}
