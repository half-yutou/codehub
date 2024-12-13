package cn.xyt.codehub;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("cn.xyt.codehub.mapper")
@SpringBootApplication
public class CodehubApplication {

    public static void main(String[] args) {
        SpringApplication.run(CodehubApplication.class, args);
    }

}
