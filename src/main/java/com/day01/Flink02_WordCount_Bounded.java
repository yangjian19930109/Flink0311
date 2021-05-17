package com.day01;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

/**
 * @Description：
 * @Author：YJ
 * @Createtime 2021/5/17 20:29
 */
public class Flink02_WordCount_Bounded {
    public static void main(String[] args) throws Exception {
        // 1.获取执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // 2.读取文件创建流
        DataStreamSource<String> input = env.readTextFile("input");

        // 3.压平并将单词转换为元组
        SingleOutputStreamOperator<Tuple2<String, Integer>> wordToOneDS = input.flatMap(new LineToTupleFlatMapFunction());

        // 4.分组
        KeyedStream<Tuple2<String, Integer>, String> keyedStream = wordToOneDS.keyBy(new KeySelector<Tuple2<String, Integer>, String>() {
            @Override
            public String getKey(Tuple2<String, Integer> value) throws Exception {
                return value.f0;
            }
        });

        // 5.按照 Key 做聚合
        SingleOutputStreamOperator<Tuple2<String, Integer>> result = keyedStream.sum(1);

        // 6.打印结果
        result.print();

        // 7.启动任务
        env.execute();
    }

    public static class LineToTupleFlatMapFunction implements FlatMapFunction<String, Tuple2<String, Integer>> {
        @Override
        public void flatMap(String value, Collector<Tuple2<String, Integer>> out) throws Exception {

            // 按照空格切分数据
            String[] words = value.split(" ");

            // 遍历写出
            for (String word : words) {
                out.collect(new Tuple2<>(word, 1));
            }
        }
    }
}
