package com.market;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 二手物品交易平台 - 应用主入口
 */
@SpringBootApplication
@MapperScan("com.market.modules.**.mapper")
public class MarketApplication {

    public static void main(String[] args) {
        SpringApplication.run(MarketApplication.class, args);
    }
}
