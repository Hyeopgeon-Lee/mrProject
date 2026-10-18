package seq;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.util.ToolRunner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

class SequenceFileIntegrationTest {
    @Test
    void sequenceFileWriteAndReadRoundTrip(@TempDir Path temp) throws Exception {
        Path source = temp.resolve("lines.txt");
        Path sequenceOutput = temp.resolve("sequence");
        Path textOutput = temp.resolve("text");
        Files.writeString(source, "first line\nsecond line\n", StandardCharsets.UTF_8);

        Configuration conf = new Configuration();
        conf.set("fs.defaultFS", "file:///");
        conf.set("mapreduce.framework.name", "local");

        assertEquals(0, ToolRunner.run(conf, new CreateSequenceFile(),
                new String[]{source.toString(), sequenceOutput.toString()}));
        assertEquals(0, ToolRunner.run(conf, new ReadCompressSequenceFile(),
                new String[]{sequenceOutput.toString(), textOutput.toString()}));

        String restored = Files.readString(textOutput.resolve("part-m-00000"),
                StandardCharsets.UTF_8);
        assertTrue(restored.contains("first line"), restored);
        assertTrue(restored.contains("second line"), restored);
    }
}
