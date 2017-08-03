package com.yaxon.vndp.dcap;

import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.listener.PatternTopic;

import java.util.concurrent.TimeUnit;

/**
 * Author: 游锋锋
 * Time: 2016-03-01 09:45
 * Copyright (C) 2016 Xiamen Yaxon Networks CO.,LTD.
 */
public interface RedisOperations {
    /**
     * 通过Key获取对象
     * @param statement 命名空间（或+命令变量）
     * @param routObj 路由对象，需要包含路由的字段（可为空，则不进行路由，
     *                会用默认分区，可为Map对象，key为路由键名。或Class对象，字段为路由键名）
     * @param key key值
     * @return
     */
    <T> T get(String statement,Object routObj,String key);

    /**
     *设置缓存（永久性有效值）
     * @param statement 命名空间（或+命令变量）
     * @param routObj 路由对象，需要包含路由的字段（可为空，则不进行路由，
     *                会用默认分区，可为Map对象，key为路由键名。或Class对象，字段为路由键名）
     * @param key 缓存键
     * @param value 对应值
     */
    int set(String statement,Object routObj,String key,String value);

    /**
     * 设置缓存
     * @param statement 命名空间（或+命令变量）
     * @param routObj 路由对象，需要包含路由的字段（可为空，则不进行路由，
     *                会用默认分区，可为Map对象，key为路由键名。或Class对象，字段为路由键名）
     * @param key 缓存键
     * @param value 对应值
     * @param time 过期时间数值
     * @param timeUnit 过期时间单位
     */
    int set(String statement,Object routObj,String key,String value,Long time,TimeUnit timeUnit);

    /**
     * 删除缓存
     * @param statement 命名空间（或+命令变量）
     * @param routObj 路由对象，需要包含路由的字段（可为空，则不进行路由，会用默认分区，
     *                可为Map对象，key为路由键名。或Class对象，字段为路由键名）
     * @param key 要删除的目标缓存键
     */
    int delete(String statement,Object routObj,String key);

    /**
     * 发布信息
     * @param statement 命名空间（或+命令变量）
     * @param routObj  路由对象，需要包含路由的字段（可为空，则不进行路由，会用默认分区，
     *                 可为Map对象，key为路由键名。或Class对象，字段为路由键名）
     * @param topic  发布的主题（频道）
     * @param value  发布的信息值
     */
    int push(String statement,Object routObj,String topic,Object value);

    /**
     * 订阅频道
     * @param statement 命名空间（或+命令变量）
     * @param routKey  路由对象，需要包含路由的字段（可为空，则不进行路由，会用默认分区）
     * @param parameter 路由参数值。（可为空，则不进行路由，会用默认分区，一般为车辆id，
     *                  支持批量连续id，如“$1-1000,1004,1006”,订阅车辆id为1-1000的，1004,1006的车所在的分区中的频道）
     * @param messageListener  目标监听类（频道）
     * @param patternTopic  目标频道
     * @return
     */
    int addMessageListener(String statement,String routKey,String parameter,MessageListener messageListener,PatternTopic patternTopic);

    /**
     * 订阅所有分区的目标频道
     * @param messageListener  目标监听类
     * @param patternTopic  目标频道
     * @return
     */
    int addAllShardMessageListener(MessageListener messageListener,PatternTopic patternTopic);


    /**
     * 移除频道
     * @param statement 命名空间（或+命令变量）
     * @param routKey  路由对象，需要包含路由的字段（可为空，则不进行路由，会用默认分区）
     * @param parameter 路由参数值。（可为空，则不进行路由，会用默认分区，一般为车辆id，
     *                  支持批量连续id，如“$1-1000,1004,1006”,订阅车辆id为1-1000的，1004,1006的车所在的分区中的频道）
     * @param messageListener  目标监听类（频道）
     * @return
     */
    int removeMessageListener(String statement,String routKey,String parameter,MessageListener messageListener);

    /**
     * 订阅频道
     * @param statement 命名空间（或+命令变量）
     * @param routKey  路由对象，需要包含路由的字段（可为空，则不进行路由，会用默认分区）
     * @param parameter 路由参数值。（可为空，则不进行路由，会用默认分区，一般为车辆id，
     *                  支持批量连续id，如“$1-1000,1004,1006”,订阅车辆id为1-1000的，1004,1006的车所在的分区中的频道）
     * @param messageListener  目标监听类（频道）
     * @param patternTopic  目标频道
     * @return
     */
    int removeMessageListener(String statement,String routKey,String parameter,MessageListener messageListener,PatternTopic patternTopic);

    /**
     * 移除所有分区的目标频道
     * @param messageListener  目标监听类
     * @return
     */
    int removeAllShardMessageListener(MessageListener messageListener);

    /**
     * 移除所有分区的目标频道
     * @param messageListener  目标监听类
     * @param patternTopic  目标频道
     * @return
     */
    int removeAllShardMessageListener(MessageListener messageListener,PatternTopic patternTopic);

    /**
     *设置缓存（永久性有效值）
     * @param statement 命名空间（或+命令变量）
     * @param routObj 路由对象，需要包含路由的字段（可为空，则不进行路由，
     *                会用默认分区，可为Map对象，key为路由键名。或Class对象，字段为路由键名）
     * @param key 缓存键
     * @param value 对应值
     */
    int setForAdd(String statement,Object routObj,String key,String... value);

    /**
     *设置缓存（永久性有效值）
     * @param statement 命名空间（或+命令变量）
     * @param routObj 路由对象，需要包含路由的字段（可为空，则不进行路由，
     *                会用默认分区，可为Map对象，key为路由键名。或Class对象，字段为路由键名）
     * @param key 缓存键
     */
    Boolean hasKey(String statement,Object routObj,String key);
}
