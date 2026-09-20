package com.edu.analysis;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 教育大数据分析与可视化系统 - 启动类
 */
@SpringBootApplication
@MapperScan("com.edu.analysis.mapper")
public class EduAnalysisApplication {

    public static void main(String[] args) {
        SpringApplication.run(EduAnalysisApplication.class, args);
        System.out.println("================================================");
        System.out.println("  教育大数据分析与可视化系统 后端启动成功!");
        System.out.println("  API 地址: http://localhost:8080/api");
        System.out.println("================================================");
    }
}
