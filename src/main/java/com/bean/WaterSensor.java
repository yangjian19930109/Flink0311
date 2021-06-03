package com.bean;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * @Description：水位传感器：用于接收水位数据
 * id:传感器编号
 * ts:时间戳
 * vc:水位
 * @Author：YJ
 * @Createtime 2021/5/30 15:10
 */


@Data
@NoArgsConstructor
@AllArgsConstructor
public class WaterSensor {
    private String id;
    private Long ts;
    private Integer vc;
}