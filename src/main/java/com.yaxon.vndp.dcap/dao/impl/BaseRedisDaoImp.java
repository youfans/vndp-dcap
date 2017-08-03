package com.yaxon.vndp.dcap.dao.impl;

import com.yaxon.vndp.dcap.ShardedRedisTemplate;
import com.yaxon.vndp.dcap.dao.BaseRedisDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.listener.PatternTopic;

import java.util.concurrent.TimeUnit;

/**
 * Author: 游锋锋
 * Time: 2016-03-02 20:11
 * Copyright (C) 2016 Xiamen Yaxon Networks CO.,LTD.
 */
public class BaseRedisDaoImp implements BaseRedisDao{
    private static final Logger logger = LoggerFactory.getLogger(BaseRedisDaoImp.class);

    /**
     * 定义redis常用的几个操作命令
     */
    protected static final String GET = "get";
    protected static final String SET = "set";
    protected static final String SET_WITH_TIME = "setWithTime";
    protected static final String DELETE = "delete";
    protected static final String PUSH = "push";
    protected static final String ADD_MESSAGE_LISTENER = "add_message_listener";
    protected static final String REMOVE_MESSAGE_LISTENER="remove_message_listener";
    protected static final String SET_FOR_ADD="set_for_add";
    protected static final String HAS_KEY="has_key";

    @Autowired
    protected ShardedRedisTemplate shardedRedisTemplate;

    protected Class daoClass;
    protected String daoName;

    protected BaseRedisDaoImp() {
        try {
            daoClass = getClass().getInterfaces()[0];
            daoName = daoClass.getName();
        } catch (Exception e) {
            logger.error("初始化DAO失败。 ", e);
        }
    }

    public ShardedRedisTemplate getShardedRedisTemplate() {
        return shardedRedisTemplate;
    }
    /**
     * 获取statement全称
     *
     * @param statementId
     * @return
     */
    protected String getStatement(String statementId) {
        return this.daoName + "." + statementId;
    }

    @Override
    public String get(String key, Object routObj) {
        return getShardedRedisTemplate().get(getStatement(GET),routObj,key);
    }

    @Override
    public int set(String key, String value,Object routObj) {
        return getShardedRedisTemplate().set(getStatement(SET),routObj,key,value);
    }

    @Override
    public int set(String key, String value, Long time, TimeUnit timeUnit,Object routObj) {
        return getShardedRedisTemplate().set(getStatement(SET_WITH_TIME),routObj,key,value,time,timeUnit);
    }

    @Override
    public int delete(String key,Object routObj) {
        return getShardedRedisTemplate().delete(getStatement(DELETE),routObj,key);
    }

    @Override
    public int push(String topic, Object value,Object routObj) {
        return getShardedRedisTemplate().push(getStatement(PUSH),routObj,topic,value);
    }

    @Override
    public int addMessageListener(MessageListener messageListener, PatternTopic patternTopic,String routKey, String parameter) {
        return getShardedRedisTemplate().addMessageListener(getStatement(ADD_MESSAGE_LISTENER),routKey,parameter,messageListener,patternTopic);
    }

    @Override
    public int addAllShardMessageListener(MessageListener messageListener, PatternTopic patternTopic) {
        return getShardedRedisTemplate().addAllShardMessageListener(messageListener, patternTopic);
    }

    @Override
    public int removeMessageListener(MessageListener messageListener, String routKey, String parameter) {
        return getShardedRedisTemplate().removeMessageListener(getStatement(REMOVE_MESSAGE_LISTENER),routKey,parameter,messageListener);
    }

    @Override
    public int removeMessageListener(MessageListener messageListener, PatternTopic patternTopic,String routKey, String parameter) {
        return getShardedRedisTemplate().removeMessageListener(getStatement(REMOVE_MESSAGE_LISTENER),routKey,parameter,messageListener,patternTopic);
    }

    @Override
    public int removeAllShardMessageListener(MessageListener messageListener) {
        return getShardedRedisTemplate().removeAllShardMessageListener(messageListener);
    }

    @Override
    public int removeAllShardMessageListener(MessageListener messageListener, PatternTopic patternTopic) {
        return getShardedRedisTemplate().removeAllShardMessageListener(messageListener,patternTopic);
    }

    @Override
    public int setForAdd(Object routObj,String key, String... value) {
        return getShardedRedisTemplate().setForAdd(getStatement(SET_FOR_ADD),routObj,key,value);
    }

    @Override
    public Boolean hasKey(String key, Object routObj) {
        return getShardedRedisTemplate().hasKey(getStatement(HAS_KEY),routObj,key);
    }


}
