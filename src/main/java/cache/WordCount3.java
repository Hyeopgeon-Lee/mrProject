package cache;

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

import java.net.URI;

/**
 * 캐시 파일(/comedies)을 메모리에 올려 필터링하는 MapReduce 드라이버
 */
@Log4j
public class WordCount3 extends Configured implements Tool {

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            log.error("사용법: <출력경로>");
            System.exit(-1);
        }

        long start = System.currentTimeMillis();
        int exitCode = ToolRunner.run(new Configuration(), new WordCount3(), args);
        long end = System.currentTimeMillis();

        log.info("캐시 기반 MapReduce 실행 시간: " + (end - start) + "ms");
        System.exit(exitCode);
    }

    @Override
    public int run(String[] args) throws Exception {
        Configuration conf = getConf();
        conf.set("AppName", "Cache Test");
        String appName = conf.get("AppName");

        log.info("실행 중인 Job 이름: " + appName);

        // 분석할 캐시 파일 (HDFS 경로)
        String cacheFilePath = "/comedies";

        Job job = Job.getInstance(conf);
        job.setJarByClass(WordCount3.class);
        job.setJobName(appName);

        // 캐시 파일 등록 (Distributed Cache 활용)
        job.addCacheFile(new Path(cacheFilePath).toUri());

        // 캐시 등록 확인 (선택 사항)
        for (URI cacheUri : job.getCacheFiles()) {
            log.info("캐시 파일 등록됨: " + cacheUri.getPath());
        }

        // 입력 및 출력 경로 설정
        FileInputFormat.setInputPaths(job, new Path(cacheFilePath));
        FileOutputFormat.setOutputPath(job, new Path(args[0]));

        // Mapper & Reducer 설정
        job.setMapperClass(WordCount3Mapper.class);
        job.setReducerClass(WordCount3Reducer.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        return job.waitForCompletion(true) ? 0 : 1;
    }
}
