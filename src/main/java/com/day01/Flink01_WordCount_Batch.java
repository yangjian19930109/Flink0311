package com.day01;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.operators.*;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;

/**
 * @Description：
 * @Author：YJ
 * @Createtime 2021/5/16 17:31
 */
public class Flink01_WordCount_Batch {
    public static void main(String[] args) throws Exception {
        // 1.获取执行环境
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

        // 2.读取文件数据
        DataSource<String> input = env.readTextFile("input");

        // 3.压平
        FlatMapOperator<String, String> wordDS = input.flatMap(new MyFlatMapFuntion());

        // 4.将单词转换成元组
        /**
        MapOperator<String, Tuple2<String, Integer>> wordToOneDS = wordDS.map((MapFunction<String, Tuple2<String, Integer>>) s -> {
//                return Tuple2.of(s, 1);
            return new Tuple2<>(s, 1);
        });
        */

        MapOperator<String, Tuple2<String, Integer>> wordToOneDS = wordDS.map(new MapFunction<String, Tuple2<String, Integer>>() {
            @Override
            public Tuple2<String, Integer> map(String s) throws Exception {
//                return Tuple2.of(s, 1);
                return new Tuple2<>(s, 1);
            }
        });

        // 5.分组
        UnsortedGrouping<Tuple2<String, Integer>> groupBy = wordToOneDS.groupBy("0");
        /**
        wordToOneDS.groupBy(new KeySelector<Tuple2<String, Integer>, Object>() {
            @Override
            public Object getKey(Tuple2<String, Integer> value) throws Exception {
                return null;
            }
        });
        */

        // 6.聚合
        AggregateOperator<Tuple2<String, Integer>> result = groupBy.sum(1);

        // 7.打印结果
        result.print();
    }

    // 自定义实现压平操作的类
    public static class MyFlatMapFuntion implements FlatMapFunction<String, String> {
        @Override
        public void flatMap(String s, Collector<String> collector) throws Exception {
            // 按照空格切割
            String[] words = s.split(" ");

            // 便利 words，写出一个个的单词
            for (String word : words) {
                collector.collect(word);
            }
        }
    }
}
