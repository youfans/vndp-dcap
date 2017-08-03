package com.yaxon.vndp.dcap.test;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yaxon.vndp.dcap.Shard;

import com.yaxon.vndp.dcap.ShardedRedisTemplate;
import com.yaxon.vndp.dcap.dao.RedisDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;

/**
 * Author: 游锋锋
 * Copyright (C) 2016 Xiamen Yaxon Networ
 * Time: 2016-03-02 10:45ks CO.,LTD.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring.xml"})
public class RedisTest {
    @Autowired
    private RedisDao redisDao;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private StringRedisSerializer stringRedisSerializer;
    @Autowired
    private RedisMessageListenerContainer messageListenerContainer;
    @Autowired
    private ShardedRedisTemplate shardedRedisTemplate;

    @Test
    public void test(){
        long time=System.currentTimeMillis();
        redisDao.set("yff", "youfengfeng", null);
        long time1=System.currentTimeMillis();
        System.out.println("总共耗时1："+(time1-time));

        long timea=System.currentTimeMillis();
        redisDao.set("yff", "youfengfeng", null);
        long timeb=System.currentTimeMillis();
        System.out.println("总共耗时2："+(timeb-timea));

        long timec=System.currentTimeMillis();
        redisDao.set("yff", "youfengfeng", null);
        long timed=System.currentTimeMillis();
        System.out.println("总共耗时3："+(timed-timec));
    }

    @Test
    public void test1(){
//        Entity e1=new Entity();
//        e1.setVid(222222);
//        long time=System.currentTimeMillis();
//        baseRedisStringDao.set("yff","1317`",e1);
//        long time1=System.currentTimeMillis();
//        System.out.println("总共耗时："+(time1-time));

        Map<String,Object> values=Maps.newHashMap();
        values.put("vid",1);
        long timea=System.currentTimeMillis();
        redisDao.set("gh","h000",values);
        long timeb=System.currentTimeMillis();
        System.out.println("总共耗时："+(timeb-timea));

        Entity e=new Entity();
        e.setVid(2000);
        long timec=System.currentTimeMillis();
        redisDao.set("gh111","h666",e);
        long timed=System.currentTimeMillis();
        System.out.println("总共耗时："+(timed-timec));

        Entity e1=new Entity();
        e1.setVid(1);
        long timec1=System.currentTimeMillis();
        redisDao.set("gh111","h1111",e1);
        long timed1=System.currentTimeMillis();
        System.out.println("总共耗时："+(timed1-timec1));

        Entity e2=new Entity();
        e2.setVid(1);
        long timec2=System.currentTimeMillis();
        redisDao.set("gh111","h5555",e2);
        long timed2=System.currentTimeMillis();
        System.out.println("总共耗时："+(timed2-timec2));
    }

    @Test
    public void test2(){
        long time=System.currentTimeMillis();
        stringRedisTemplate.opsForValue().set("yff", "youfengfeng444");
        long time1=System.currentTimeMillis();
        System.out.println("总共耗时1："+(time1-time));


        long timea=System.currentTimeMillis();
        stringRedisTemplate.opsForValue().set("yff", "youfengfeng444");
        long timeb=System.currentTimeMillis();
        System.out.println("总共耗时2："+(timeb-timea));

        long timec=System.currentTimeMillis();
        stringRedisTemplate.opsForValue().set("yff", "youfengfeng444");
        long timed=System.currentTimeMillis();
        System.out.println("总共耗时3："+(timed-timec));
    }


    @Test
    public void test3(){
        long time=System.currentTimeMillis();
        Entity e1=new Entity();
        e1.setVid(1);
        redisDao.push("tb", "zzzzzzzz",e1);
        long time1=System.currentTimeMillis();
        System.out.println("总共耗时1："+(time1-time));


        long timea=System.currentTimeMillis();
        Entity e2=new Entity();
        e2.setVid(2200);
        redisDao.push("tb", "xxxxxxxxx",e2);
        long timeb=System.currentTimeMillis();
        System.out.println("总共耗时2："+(timeb-timea));

        long timec=System.currentTimeMillis();
        Entity e3=new Entity();
        e3.setVid(3);
        redisDao.push("tb", "vvvvvvvvv",e3);
        long timed=System.currentTimeMillis();
        System.out.println("总共耗时3："+(timed-timec));
    }

    @Test
    public void test4(){
        long time=System.currentTimeMillis();
        stringRedisTemplate.convertAndSend("tv", "youfengfeng1");
        long time1=System.currentTimeMillis();
        System.out.println("总共耗时1："+(time1-time));


        long timea=System.currentTimeMillis();
        stringRedisTemplate.convertAndSend("tv", "youfengfeng2");
        long timeb=System.currentTimeMillis();
        System.out.println("总共耗时2："+(timeb-timea));

        long timec=System.currentTimeMillis();
        stringRedisTemplate.convertAndSend("tv", "youfengfeng3");
        long timed=System.currentTimeMillis();
        System.out.println("总共耗时3："+(timed-timec));
    }

    @Test
    public void test5() throws InterruptedException {

    }

    @Test
    public void TestClass() throws Exception {
        long a=System.currentTimeMillis();
        Map<String,Object> map= Maps.newConcurrentMap();
        List<Shard> shardIdSet= Lists.newArrayList();
        for (int i=1;i<=30000;i++){
            map.put("vid",i);
            shardIdSet.addAll(shardedRedisTemplate.calShards("com.yaxon.vndp.dcap.dao.BaseRedisStringDao", map));
            /*System.out.println("当前："+i);*/
        }
        long b=System.currentTimeMillis();
        System.out.println("用时："+(b-a)+"    总共："+shardIdSet.size());
        for (Shard shard:shardIdSet){
            System.out.println(shard.toString());
        }
    }

    @Test
    public void TestListener() throws InterruptedException {
//        String s="$3-5,20,30";
//        Set<String> set= ShardIdUtil.parseStrs(s);
//        for (String st:set){
//            System.out.println(st);
//        }
        MessageListener messageListener=new MessageListener() {
            @Override
            public void onMessage(Message message, byte[] pattern) {
                    System.out.println("收到订阅信息："+stringRedisSerializer.deserialize(message.getBody()));
            }
        };
        redisDao.addMessageListener(messageListener,new PatternTopic("tb*"),"vid","$1-20000");
        Map<String,Object> values=Maps.newHashMap();
        values.put("vid",800);
        Thread.sleep(3000);
        redisDao.push("tb","ddddddd",values);
        redisDao.removeMessageListener(messageListener,"vid","$1-20000");
        values.put("vid",3000);
        redisDao.push("tb2","fffffffffff",values);
        Thread.sleep(3000000);
    }

    @Test
    public void TestListener2() throws InterruptedException{

        long s=System.currentTimeMillis();
        messageListenerContainer.addMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message, byte[] pattern) {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        },new PatternTopic("tb"));
        long e=System.currentTimeMillis();
        System.out.println("普通的订阅共耗时："+(e-s)+"毫秒");


        long start=System.currentTimeMillis();
        redisDao.addMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message, byte[] pattern) {
                System.out.println("收到订阅信息："+stringRedisSerializer.deserialize(message.getBody()));
            }
        },new PatternTopic("tb"),"vid","$1-2000");
        long end=System.currentTimeMillis();
        System.out.println("分布式的订阅共耗时："+(end-start)+"毫秒");

        long start1=System.currentTimeMillis();
        redisDao.addMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message, byte[] pattern) {
                System.out.println("收到订阅信息："+stringRedisSerializer.deserialize(message.getBody()));
            }
        },new PatternTopic("tb"),"vid","$1-2100");
        long end1=System.currentTimeMillis();
        System.out.println("分布式的2订阅共耗时："+(end1-start1)+"毫秒");

        long start2=System.currentTimeMillis();
        redisDao.addMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message, byte[] pattern) {
                System.out.println("收到订阅信息："+stringRedisSerializer.deserialize(message.getBody()));
            }
        },new PatternTopic("tb2"),"vid","$1-2500");
        long end2=System.currentTimeMillis();
        System.out.println("分布式的3订阅共耗时："+(end2-start2)+"毫秒");

        long start3=System.currentTimeMillis();
        redisDao.addMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message, byte[] pattern) {
                System.out.println("收到订阅信息："+stringRedisSerializer.deserialize(message.getBody()));
            }
        },new PatternTopic("tb1"),"vid","$80-3500");
        long end3=System.currentTimeMillis();
        System.out.println("分布式的4订阅共耗时："+(end3-start3)+"毫秒");
    }

    @Test
    public void testHasKey(){
        System.out.println(redisDao.hasKey("r",null));
    }

    @Test
    public void testSetForAdd(){
        Map<String,Object> values=Maps.newHashMap();
        values.put("vid",11);
        long s=System.currentTimeMillis();
        redisDao.setForAdd(values,"myset2",new String("11"));
        long e=System.currentTimeMillis();
        System.out.println("耗时1："+(e-s)+"毫秒");

        Map<String,Object> values0=Maps.newHashMap();
        values0.put("vid",11);
        long s1=System.currentTimeMillis();
        redisDao.setForAdd(values0,"myset2",new String("33"));
        long e1=System.currentTimeMillis();
        System.out.println("耗时1："+(e1-s1)+"毫秒");

        Map<String,Object> values1=Maps.newHashMap();
        values1.put("vid",123456);
        long start=System.currentTimeMillis();
        redisDao.setForAdd(values1,"myset2",new String("22"));
        long end=System.currentTimeMillis();
        System.out.println("耗时2："+(end-start)+"毫秒");

        Map<String,Object> values2=Maps.newHashMap();
        values2.put("vid",123456);
        long start1=System.currentTimeMillis();
        redisDao.setForAdd(values2,"myset2",new String("44"));
        long end1=System.currentTimeMillis();
        System.out.println("耗时2："+(end1-start1)+"毫秒");

    }
}
