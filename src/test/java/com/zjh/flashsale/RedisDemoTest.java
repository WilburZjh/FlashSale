package com.zjh.flashsale;

import com.zjh.flashsale.util.RedisService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class RedisDemoTest {

    @Resource
    private RedisService redisService;

    @Test
    public void setTest(){
        redisService.setValue("age",100L);
    }

    @Test
    public void  getTest(){
        String age =  redisService.getValue("age");
        System.out.println(age);
    }

    @Test
    public void  stockTest(){
        redisService.setValue("stock:28",10L);
    }

    @Test
    public void getStockTest(){
     String stock =  redisService.getValue("stock:28");
     System.out.println(stock);
    }

    @Test
    public void stockDeductValidatorTest(){
      boolean result =  redisService.stockDeductValidator("stock:28");
      System.out.println("result:"+result);
      String stock =  redisService.getValue("stock:28");
      System.out.println("stock:"+stock);
    }

}
