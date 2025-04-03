package combiner;

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
 * IP Count with Combiner 적용 드라이버 클래스
 */
@Log4j
public class IPCount2 extends Configured implements Tool {

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            log.error("사용법: <입력경로> <출력경로>");
            System.exit(-1);
        }

        int exitCode = ToolRunner.run(new Configuration(), new IPCount2(), args);
        System.exit(exitCode);
    }

    @Override
    public int run(String[] args) throws Exception {
        Configuration conf = getConf();

        // 사용자 정의 설정
        conf.set("AppName", "Combiner Test");

        String appName = conf.get("AppName");
        log.info("실행 중인 Job 이름: " + appName);

        // 잡 생성 및 설정
        Job job = Job.getInstance(conf);
        job.setJarByClass(IPCount2.class);
        job.setJobName(appName);

        // 입출력 경로 설정
        FileInputFormat.setInputPaths(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        // Mapper / Reducer / Combiner 설정
        job.setMapperClass(IPCount2Mapper.class);
        job.setReducerClass(IPCount2Reducer.class);
        job.setCombinerClass(IPCount2Reducer.class);  // Reducer = Combiner 사용 가능

        // 출력 키/값 타입 설정
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        // 잡 실행
        return job.waitForCompletion(true) ? 0 : 1;
    }
}
