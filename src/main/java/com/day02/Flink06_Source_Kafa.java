package com.day02;

import com.bean.WaterSensor;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;

import javax.sound.sampled.Line;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * @Description：
 * @Author：YJ
 * @Createtime 2021/6/3 22:01
 */
public class Flink06_Source_Kafa {
    public static void main(String[] args) throws Exception {
        // 1.获取执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        // 2.自定义的数据源中加载数据
        DataStreamSource<WaterSensor> dataStreamSource = env.addSource(new MySource("hadoop106", 8888));

        // 3.打印结果数据
        dataStreamSource.print();

        // 4.执行任务
        env.execute();

    }

    // 自定义从端口读取数据的 Source
    public static class MySource implements SourceFunction<WaterSensor> {

        // 定义属性信息，主机 & 端口号
        private String host;
        private Integer port;
        Socket socket = null;
        BufferedReader reader = null;

        private Boolean running = true;

        public MySource() {

        }

        public MySource(String host, Integer port) {
            this.host = host;
            this.port = port;
        }

        @Override
        public void run(SourceContext<WaterSensor> ctx) throws Exception {

            // 创建输入流
            socket = new Socket(host, port);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));


            while (running) {
                String line = reader.readLine();
                // 接收数据并发送至 Flink 系统
                String[] split = line.split(",");
                WaterSensor waterSensor = new WaterSensor(split[0], Long.parseLong(split[1]), Integer.parseInt(split[2]));

                ctx.collect(waterSensor);
                line = reader.readLine();
            }
        }

        @Override
        public void cancel() {
            running = false;
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
