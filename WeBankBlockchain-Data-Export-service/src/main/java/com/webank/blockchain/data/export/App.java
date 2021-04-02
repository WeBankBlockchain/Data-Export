package com.webank.blockchain.data.export;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * @author wesleywang
 * @Description:
 * @date 2021/3/30
 */
@SpringBootApplication(exclude= {DataSourceAutoConfiguration.class})
@EnableConfigurationProperties
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

}
