package wc;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.util.ToolRunner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import tool.WordCount2;
import static org.junit.jupiter.api.Assertions.*;

class WordCountIntegrationTest {

    @Test
    void wordCountRunsWithLocalHadoop35(@TempDir Path temp) throws Exception {
        Path input = temp.resolve("input.txt");
        Path output = temp.resolve("output");
        Files.writeString(input, "apple banana apple\nbanana carrot\n", StandardCharsets.UTF_8);

        Configuration conf = new Configuration();
        conf.set("fs.defaultFS", "file:///");
        conf.set("mapreduce.framework.name", "local");

        int code = ToolRunner.run(conf, new WordCount2(),
                new String[]{input.toString(), output.toString()});
        assertEquals(0, code);

        String result = Files.readString(output.resolve("part-r-00000"), StandardCharsets.UTF_8);
        assertTrue(result.contains("apple\t2"), result);
        assertTrue(result.contains("banana\t2"), result);
        assertTrue(result.contains("carrot\t1"), result);
    }
}
