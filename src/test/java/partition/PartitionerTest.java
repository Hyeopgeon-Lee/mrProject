package partition;

import mongo.MonthLog2Partitioner;
import org.apache.hadoop.io.Text;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PartitionerTest {
    @Test
    void monthlyPartitionsAreBounded() {
        MonthLogPartitioner partitioner = new MonthLogPartitioner();
        assertEquals(0, partitioner.getPartition(new Text("127.0.0.1"), new Text("Jan"), 12));
        assertEquals(11, partitioner.getPartition(new Text("127.0.0.1"), new Text("Dec"), 12));
        assertEquals(0, partitioner.getPartition(new Text("127.0.0.1"), new Text("Dec"), 1));
        assertEquals(0, partitioner.getPartition(new Text("127.0.0.1"), new Text("Dec"), 0));
        assertThrows(IllegalArgumentException.class,
                () -> partitioner.getPartition(new Text("127.0.0.1"), new Text("???"), 12));
    }

    @Test
    void hourlyPartitionsAreBounded() {
        TimeLogPartitioner partitioner = new TimeLogPartitioner();
        assertEquals(0, partitioner.getPartition(new Text("127.0.0.1"), new Text("00"), 24));
        assertEquals(23, partitioner.getPartition(new Text("127.0.0.1"), new Text("23"), 24));
        assertEquals(3, partitioner.getPartition(new Text("127.0.0.1"), new Text("23"), 10));
        assertThrows(IllegalArgumentException.class,
                () -> partitioner.getPartition(new Text("127.0.0.1"), new Text("24"), 24));
    }

    @Test
    void mongoPartitionerUsesMonthKey() {
        MonthLog2Partitioner partitioner = new MonthLog2Partitioner();
        assertEquals(3, partitioner.getPartition(new Text("Apr"), new Text("{}"), 12));
        assertThrows(IllegalArgumentException.class,
                () -> partitioner.getPartition(new Text("Bogus"), new Text("{}"), 12));
    }
}
