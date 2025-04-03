package tool;

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
 * WordCount2: ToolRunner 기반의 MapReduce 드라이버 클래스
 */
public class WordCount2 extends Configured implements Tool {

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("사용법: WordCount2 <input path> <output path>");
            System.exit(-1);
        }

        int exitCode = ToolRunner.run(new Configuration(), new WordCount2(), args);
        System.exit(exitCode);
    }

    @Override
    public int run(String[] args) throws Exception {
        // 설정 가져오기
        Configuration conf = getConf();
        conf.set("AppName", "ToolRunner Test");

        // 잡 객체 생성
        Job job = Job.getInstance(conf);
        job.setJarByClass(WordCount2.class);
        job.setJobName(conf.get("AppName"));

        // 입력 및 출력 경로 설정
        FileInputFormat.setInputPaths(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        // Mapper 및 Reducer 클래스 설정
        job.setMapperClass(WordCount2Mapper.class);
        job.setReducerClass(WordCount2Reducer.class);

        // 출력 키/값 타입 설정
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        // 잡 실행
        return job.waitForCompletion(true) ? 0 : 1;
    }
}
