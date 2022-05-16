package seq;

import lombok.extern.log4j.Log4j;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

/**
 * 맵리듀스를 실행하기 위한 Main 함수가 존재하는 자바 파일
 * 드라이버 파일로 부름
 */
@Log4j
public class ReadCompressSequenceFile extends Configuration implements Tool {

    // 맵리듀스 실행 함수
    public static void main(String[] args) throws Exception {

        // 파라미터는 시퀀스 파일로 압축된 파일(폴더)과 압축해제될 파일(폴더)로 2개를 받음
        if (args.length != 2) {
            log.info("시퀀스 파일로 압축된 파일(폴더)과 압축해제될 파일(폴더)를 입력해야 합니다.");
            System.exit(-1);

        }
        int exitCode = ToolRunner.run(new ReadCompressSequenceFile(), args);

        System.exit(exitCode);

    }

    @Override
    public void setConf(Configuration configuration) {

        // App 이름 정의
        configuration.set("AppName", "Compress SequenceFile Read Test");

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

        Configuration conf = this.getConf();
        String appName = conf.get("AppName");

        log.info("appName : " + appName);

        // 맵리듀스 실행을 위한 잡 객체를 가져오기
        // 하둡이 실행되면, 기본적으로 잡 객체를 메모리에 올림
        Job job = Job.getInstance(conf);

        // 맵리듀스 잡이 시작되는 main함수가 존재하는 파일 설정
        job.setJarByClass(ReadCompressSequenceFile.class);

        // 맵리듀스 잡 이름 설정, 리소스 매니저 등 맵리듀스 실행 결과 및 로그 확인할 때 편리함
        job.setJobName(appName);

        // 압축된 시퀀스 파일 -- 첫번째 파라미터
        FileInputFormat.setInputPaths(job, new Path(args[0]));

        // 압축해제될 시퀀스 파일 폴더(파일) -- 두번째 파라미터
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        // 압축된 시퀀스 파일 읽기 설정
        job.setInputFormatClass(SequenceFileInputFormat.class);

        // 시퀀스 파일 만들기는 리듀서 작업은 필요하지 않음
        job.setNumReduceTasks(0);

        // 맵리듀스 실행
        boolean success = job.waitForCompletion(true);

        return (success ? 0 : 1);
    }
}

