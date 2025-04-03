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
 * Combiner가 적용된 맵리듀스 실행 시간 측정용 드라이버
 */
@Log4j
public class IPCount2TimeCheck1 extends Configured implements Tool {

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            log.error("사용법: <입력경로> <출력경로>");
            System.exit(-1);
        }

        long start = System.currentTimeMillis();
        int exitCode = ToolRunner.run(new Configuration(), new IPCount2TimeCheck1(), args);
        long end = System.currentTimeMillis();

        log.info("Combiner 적용 MapReduce 작업 실행 시간: " + (end - start) + "ms");

        System.exit(exitCode);
    }

    @Override
    public int run(String[] args) throws Exception {
        Configuration conf = getConf();
        conf.set("AppName", "Combiner Time Test");

        String appName = conf.get("AppName");
        log.info("실행 중인 Job 이름: " + appName);

        Job job = Job.getInstance(conf);
        job.setJarByClass(IPCount2TimeCheck1.class);
        job.setJobName(appName);

        FileInputFormat.setInputPaths(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        job.setMapperClass(IPCount2Mapper.class);
        job.setReducerClass(IPCount2Reducer.class);
        job.setCombinerClass(IPCount2Reducer.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        return job.waitForCompletion(true) ? 0 : 1;
    }
}
