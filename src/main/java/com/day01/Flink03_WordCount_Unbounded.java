package com.day01;

import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * @Description：
 * @Author：YJ
 * @Createtime 2021/5/17 21:18
 */

/**
 *
 */
public class Flink03_WordCount_Unbounded {
    public static void main(String[] args) throws Exception {

        // 1.获取执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // 2.读取端口数据创建流
        DataStreamSource<String> socketTextStream = env.socketTextStream("hadoop106", 9999);

        // 3.将每行数据压平并转换为元组
        SingleOutputStreamOperator<Tuple2<String, Integer>> wordToOneDS = socketTextStream.flatMap(new Flink02_WordCount_Bounded.LineToTupleFlatMapFunction());

        // 4.分组
        KeyedStream<Tuple2<String, Integer>, String> keydeStream = wordToOneDS.keyBy(new KeySelector<Tuple2<String, Integer>, String>() {
            @Override
            public String getKey(Tuple2<String, Integer> value) throws Exception {
                return value.f0;
            }
        });

        // 5.聚合结果
        SingleOutputStreamOperator<Tuple2<String, Integer>> result = keydeStream.sum(1);

        // 6.打印结果
//        socketTextStream.print("Line");
//        wordToOneDS.print("WordToDS");
        result.print("Result");

        // 7.启动任务
        env.execute();
    }
}
