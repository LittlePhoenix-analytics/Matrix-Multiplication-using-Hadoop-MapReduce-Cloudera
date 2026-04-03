import java.io.IOException;
import java.util.*;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class MatrixMultiplication {

    // 🔹 Mapper
    public static class MatrixMapper extends Mapper<LongWritable, Text, Text, Text> {

        public void map(LongWritable key, Text value, Context context)
                throws IOException, InterruptedException {

            String[] parts = value.toString().split(",");

            String matrix = parts[0]; // A or B
            int row = Integer.parseInt(parts[1]);
            int col = Integer.parseInt(parts[2]);
            int val = Integer.parseInt(parts[3]);

            // For A matrix
            if (matrix.equals("A")) {
                for (int k = 0; k < 2; k++) {   // assuming 2x2 matrix
                    context.write(new Text(row + "," + k),
                            new Text("A," + col + "," + val));
                }
            }
            // For B matrix
            else {
                for (int k = 0; k < 2; k++) {
                    context.write(new Text(k + "," + col),
                            new Text("B," + row + "," + val));
                }
            }
        }
    }

    // 🔹 Reducer
    public static class MatrixReducer extends Reducer<Text, Text, Text, IntWritable> {

        public void reduce(Text key, Iterable<Text> values, Context context)
                throws IOException, InterruptedException {

            Map<Integer, Integer> mapA = new HashMap<>();
            Map<Integer, Integer> mapB = new HashMap<>();

            for (Text val : values) {
                String[] parts = val.toString().split(",");

                if (parts[0].equals("A")) {
                    mapA.put(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
                } else {
                    mapB.put(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
                }
            }

            int result = 0;

            // Multiply matching indices
            for (int k : mapA.keySet()) {
                if (mapB.containsKey(k)) {
                    result += mapA.get(k) * mapB.get(k);
                }
            }

            context.write(key, new IntWritable(result));
        }
    }

    // 🔹 Driver
    public static void main(String[] args) throws Exception {

        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "Matrix Multiplication");

        job.setJarByClass(MatrixMultiplication.class);

        job.setMapperClass(MatrixMapper.class);
        job.setReducerClass(MatrixReducer.class);

        // 🔥 FIX (IMPORTANT)
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);

        // Final output
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
