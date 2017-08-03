package com.yaxon.vndp.dcap.jedis;

import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.util.Pool;

/**
 * Author: 游锋锋
 * Time: 2016-05-23 20:42
 * Copyright (C) 2016 Xiamen Yaxon Networks CO.,LTD.
 */
public class JedisConnectionFactoryImp extends JedisConnectionFactory{

    public JedisConnectionFactoryImp() {}

    public JedisConnectionFactoryImp(RedisSentinelConfiguration sentinelConfig) {
        super(sentinelConfig, null);
    }

    @Override
    public void setSentinelConfig(RedisSentinelConfiguration sentinelConfig) {
        super.setSentinelConfig(sentinelConfig);
        if (getShardInfo() != null) {
            //保证不是第一次加载，而是之后修改了配置文件后触发以下动作
            inSteadOldPool();
        }
    }

    @Override
    public void setHostName(String hostName) {
        super.setHostName(hostName);
        if (getShardInfo() != null) {
            //保证不是第一次加载，而是之后修改了配置文件后触发以下动作
            setShardInfo(new JedisShardInfo(hostName, getPort()));
            inSteadOldPool();
        }

    }

    @Override
    public void setPort(int port) {
        super.setPort(port);
        if (getShardInfo() != null) {
            //保证不是第一次加载，而是之后修改了配置文件后触发以下动作
            setShardInfo(new JedisShardInfo(getHostName(), port));
            inSteadOldPool();
        }
    }

    /**
     * 新旧连接池平滑过度
     */
    public void inSteadOldPool(){
        Pool<Jedis> pool=getPool();
        if(pool!=null){
            //确定之前有用了连接池
            pool.destroy();
        }
        if (getUsePool()) {
            //确定最新配置是否用了连接池
            setPool(super.externalCreatePool());
        }
    }

    /**这里最database就不做修改，因为database改后会直接更新JedisConnectionFactory的database属性
    而每次getConnection的时候都是直接从JedisConnectionFactory的database属性来动态设置database值的**/

}
