package maponly;

import lombok.extern.log4j.Log4j;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

/**
 * 맵리듀스를 실행하기 위한 Main 함수가 존재하는 자바 파일
 * 드라이버 파일로 부름
 */
@Log4j
public class ImageCount extends Configuration implements Tool {

    // 맵리듀스 실행 함수
    public static void main(String[] args) throws Exception {

        // 파라미터는 분석 결과가 저장될 파일(폴더)만 받음
        if (args.length != 1) {
            log.info("분석결과가 저장될 폴더를 입력해야 합니다.");
            System.exit(-1);

        }

        int exitCode = ToolRunner.run(new ImageCount(), args);

        System.exit(exitCode);

    }

    @Override
    public void setConf(Configuration configuration) {

        // App 이름 정의
        configuration.set("AppName", "Map Only Test");

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
        String analysisFile = "/access_log";

        Configuration conf = this.getConf();
        String appName = conf.get("AppName");

        System.out.println("appName : " + appName);

        // 맵리듀스 실행을 위한 잡 객체를 가져오기
        // 하둡이 실행되면, 기본적으로 잡 객체를 메모리에 올림
        Job job = Job.getInstance(conf);

        // 호출이 발생하면, 메모리에 저장하여 캐시 처리 수행
        // 하둡분산파일시스템에 저장된 파일만 가능함
        job.addCacheFile(new Path(analysisFile).toUri());

        // 맵리듀스 잡이 시작되는 main함수가 존재하는 파일 설정
        job.setJarByClass(ImageCount.class);

        // 맵리듀스 잡 이름 설정, 리소스 매니저 등 맵리듀스 실행 결과 및 로그 확인할 때 편리함
        job.setJobName(appName);

        // 분석할 폴더(파일) -- 첫번째 파라미터
        FileInputFormat.setInputPaths(job, new Path(analysisFile));

        // 분석 결과가 저장되는 폴더(파일) -- 두번째 파라미터
        FileOutputFormat.setOutputPath(job, new Path(args[0]));

        // 맵리듀스의 맵 역할을 수행하는 Mapper 자바 파일 설정
        job.setMapperClass(ImageCountMapper.class);

        // 리듀서 객체를 생성하지 못하도록 객체의 수를 0으로 정의
        job.setNumReduceTasks(0);

        // 맵리듀스 실행
        boolean success = job.waitForCompletion(true);

        if (success) {

            // 맵리듀스의 Counter는 맵리듀스 실행된 결과에 대한 보고를 위해 활용하는 영역
            // 맵 분석 결과에 대한 결과를 Counter 영역에 저장

            // jpg를 요청한 URL 수
            long jpg = job.getCounters().findCounter("imageCount", "jpg").getValue();

            // gif를 요청한 URL 수
            long gif = job.getCounters().findCounter("imageCount", "gif").getValue();

            // jpg와 gif를 제외한 요청한 URL 수
            long other = job.getCounters().findCounter("imageCount", "other").getValue();

            log.info("jpg : " + jpg);
            log.info("gif : " + gif);
            log.info("other : " + other);

            return 0;

        } else {
            return 1;

        }

    }
}
