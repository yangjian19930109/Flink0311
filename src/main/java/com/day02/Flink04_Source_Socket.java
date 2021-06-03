package com.day02;

import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * @Description：
 * @Author：YJ
 * @Createtime 2021/5/30 16:19
 */
public class Flink04_Source_Socket {
    public static void main(String[] args) throws Exception {
        // 1.创建执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        // 2.从端口获取数据
        DataStreamSource<String> socketStream = env.socketTextStream("hadoop106", 8888);

        // 3.打印
        socketStream.print();

        // 4.执行
        env.execute();
    }
}
