package com.yaxon.vndp.dcap.dao;

import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.listener.PatternTopic;

import java.util.concurrent.TimeUnit;

/**
 * Author: 游锋锋
 * Time: 2016-02-26 10:56
 * Copyright (C) 2016 Xiamen Yaxon Networks CO.,LTD.
 */

public interface BaseRedisDao {
    /**
     * 通过Key获取对象
     * @param key key值
     * @param routObj 路由对象，需要包含路由的字段（可为空，则不进行路由，
     *                会用默认分区，可为Map对象，key为路由键名。或Class对象，字段为路由键名）
     * @return
     */
    String get(String key,Object routObj);

    /**
     *设置缓存（永久性有效值）
     * @param key 缓存键
     * @param value 对应值
     * @param routObj 路由对象，需要包含路由的字段（可为空，则不进行路由，
     *                会用默认分区，可为Map对象，key为路由键名。或Class对象，字段为路由键名）
     */
    int set(String key,String value,Object routObj);

    /**
     * 设置缓存
     * @param key 缓存键
     * @param value 对应值
     * @param time 过期时间数值
     * @param timeUnit 过期时间单位
     * @param routObj 路由对象，需要包含路由的字段（可为空，则不进行路由，
     *                会用默认分区，可为Map对象，key为路由键名。或Class对象，字段为路由键名）
     */
    int set(String key,String value,Long time,TimeUnit timeUnit,Object routObj);

    /**
     * 删除缓存
     * @param key 要删除的目标缓存键
     * @param routObj 路由对象，需要包含路由的字段（可为空，则不进行路由，
     *                会用默认分区，可为Map对象，key为路由键名。或Class对象，字段为路由键名）
     */
    int delete(String key,Object routObj);

    /**
     * 发布信息
     * @param topic  发布的主题（频道）
     * @param value  发布的信息值
     * @param routObj 路由对象，需要包含路由的字段（可为空，则不进行路由，
     *                会用默认分区，可为Map对象，key为路由键名。或Class对象，字段为路由键名）
     */
    int push(String topic,Object value,Object routObj);

    /**
     * 订阅频道
     * @param messageListener  发布的主题（频道）
     * @param patternTopic  发布的信息值
     * @param routKey  路由的字段名
     * @param parameter  路由的参数（可为空，则不进行路由，会用默认分区，一般为车辆id，
     *                  支持批量连续id，如“$1-1000,1004,1006”,订阅车辆id为1-1000的，1004,1006的车所在的分区中的频道）
     * @return
     */
    int addMessageListener(MessageListener messageListener,PatternTopic patternTopic,String routKey,String parameter);

    /**
     * 订阅所有分区的目标频道
     * @param messageListener  目标监听类
     * @param patternTopic  目标频道
     * @return
     */
    int addAllShardMessageListener(MessageListener messageListener,PatternTopic patternTopic);

    /**
     * 移除频道监听
     * @param messageListener  目标监听类（频道）
     * @param routKey 路由的字段名
     * @param parameter  路由对象，需要包含路由的字段（可为空，则不进行路由，会用默认分区，一般为车辆id，
     *                  支持批量连续id，如“$1-1000,1004,1006”,订阅车辆id为1-1000的，1004,1006的车所在的分区中的频道）
     * @return
     */
    int removeMessageListener(MessageListener messageListener,String routKey,String parameter);

    /**
     * 移除频道监听
     * @param messageListener  目标监听类（频道）
     * @param patternTopic  目标频道
     * @param routKey 路由的字段名
     * @param parameter  路由对象，需要包含路由的字段（可为空，则不进行路由，会用默认分区，一般为车辆id，
     *                  支持批量连续id，如“$1-1000,1004,1006”,订阅车辆id为1-1000的，1004,1006的车所在的分区中的频道）
     * @return
     */
    int removeMessageListener(MessageListener messageListener,PatternTopic patternTopic,String routKey,String parameter);

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
     * @param routObj 路由对象，需要包含路由的字段（可为空，则不进行路由，
     *                会用默认分区，可为Map对象，key为路由键名。或Class对象，字段为路由键名）
     * @param key 缓存键
     * @param value 对应值
     */
    int setForAdd(Object routObj,String key,String... value);

    /**
     *设置缓存（永久性有效值）
     * @param routObj 路由对象，需要包含路由的字段（可为空，则不进行路由，
     *                会用默认分区，可为Map对象，key为路由键名。或Class对象，字段为路由键名）
     * @param key 缓存键
     */
    Boolean hasKey(String key,Object routObj);
}
