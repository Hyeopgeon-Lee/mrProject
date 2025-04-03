package success;

import lombok.extern.log4j.Log4j;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

/**
 * ResultCount: URL 호출 결과 코드 분석용 MapReduce 드라이버
 */
@Log4j
public class ResultCount extends Configured implements Tool {

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            log.error("사용법: ResultCount <input path> <output path>");
            System.exit(-1);
        }

        int exitCode = ToolRunner.run(new Configuration(), new ResultCount(), args);
        System.exit(exitCode);
    }

    @Override
    public int run(String[] args) throws Exception {
        Configuration conf = getConf();

        // App 및 사용자 정의 설정
        conf.set("AppName", "Send Result");
        conf.set("resultCode", "200");

        String appName = conf.get("AppName");
        log.info("실행 중인 Job 이름: " + appName);

        // 잡 객체 생성
        Job job = Job.getInstance(conf);
        job.setJarByClass(ResultCount.class);
        job.setJobName(appName);

        // 입출력 경로 설정
        FileInputFormat.setInputPaths(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        // Mapper / Reducer 설정
        job.setMapperClass(ResultCountMapper.class);
        job.setReducerClass(ResultCountReducer.class);

        // 출력 타입 설정
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        // 잡 실행
        return job.waitForCompletion(true) ? 0 : 1;
    }
}
