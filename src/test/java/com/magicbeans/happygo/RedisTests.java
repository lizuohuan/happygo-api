package com.magicbeans.happygo;


import com.alibaba.fastjson.JSON;
import com.magicbeans.happygo.redis.JdkRedisTemplate;
import com.magicbeans.happygo.redis.ObjectRedisTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.*;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Arrays;
import java.util.Random;
import java.util.Timer;

@RunWith(SpringJUnit4ClassRunner.class) // SpringJUnit支持，由此引入Spring-Test框架支持！
@SpringBootTest(classes = {ApiTemplateApplication.class})
@WebAppConfiguration
public class RedisTests {

    @Autowired
    private ObjectRedisTemplate objectRedisTemplate;

    @Autowired
    private JdkRedisTemplate jdkRedisTemplate;


    @Test
    public void redisCacheTests() {

//        //set get
        BoundValueOperations valueOperations = objectRedisTemplate.boundValueOps("valueKey");
        String[] values = new String[]{"111","222","33"};
        valueOperations.set(Arrays.asList(values));
        Object value = valueOperations.get();
        System.out.println(JSON.toJSONString(value));

//
//
//        BoundHashOperations hashOperations = objectRedisTemplate.boundHashOps("hashValue");
//        hashOperations.put("key1","value1");
//        System.out.println( hashOperations.get("key1"));


//        BoundSetOperations setOperations = objectRedisTemplate.boundSetOps("set");
//        for (int i =0;i<10;i++){
//            setOperations.add(new Random().nextInt(2));
//        }
//        System.out.println(JSON.toJSONString(setOperations.members()));
//
//        BoundZSetOperations boundZSetOperations = objectRedisTemplate.boundZSetOps("zset");
//        for (int i =0;i<10;i++){
//            Random random = new Random();
//            boundZSetOperations.add(random.nextInt(1000),random.nextDouble());
//        }
//        System.out.println(JSON.toJSONString(boundZSetOperations.rangeByScore(0,2)));
    }

}
