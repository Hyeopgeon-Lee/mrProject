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
 * ResultCount2: 전송 결과 코드에 따른 MapReduce 로그 분석 드라이버
 */
@Log4j
public class ResultCount2 extends Configured implements Tool {

    public static void main(String[] args) throws Exception {
        if (args.length != 3) {
            log.error("사용법: <입력경로> <출력경로> <분석할 결과 코드>");
            System.exit(-1);
        }

        int exitCode = ToolRunner.run(new Configuration(), new ResultCount2(), args);
        System.exit(exitCode);
    }

    @Override
    public int run(String[] args) throws Exception {
        Configuration conf = getConf();

        // 사용자 설정 값 등록
        conf.set("AppName", "Send Result2");
        conf.set("resultCode", args[2]);  // 분석할 HTTP 응답 코드 (ex: 200, 404 등)

        String appName = conf.get("AppName");
        log.info("실행 중인 Job 이름: " + appName);
        log.info("분석 대상 코드: " + conf.get("resultCode"));

        // 잡 생성 및 설정
        Job job = Job.getInstance(conf);
        job.setJarByClass(ResultCount2.class);
        job.setJobName(appName);

        FileInputFormat.setInputPaths(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        job.setMapperClass(ResultCountMapper.class);
        job.setReducerClass(ResultCountReducer.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        return job.waitForCompletion(true) ? 0 : 1;
    }
}
