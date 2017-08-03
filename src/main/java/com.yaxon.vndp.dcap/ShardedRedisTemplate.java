package com.yaxon.vndp.dcap;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.yaxon.vndp.dcap.connection.ShardedRedisConnectionFactory;
import com.yaxon.vndp.dcap.strategy.ShardStrategy;
import com.yaxon.vndp.dcap.strategy.ShardStrategyFactory;
import com.yaxon.vndp.dcap.strategy.access.ShardAccessStrategy;
import com.yaxon.vndp.dcap.strategy.exit.ExitStrategy;
import com.yaxon.vndp.dcap.strategy.exit.FirstNonNullResultExitStrategy;
import com.yaxon.vndp.dcap.strategy.exit.IntegerSumExitStrategy;
import com.yaxon.vndp.dcap.strategy.resolution.ShardResolutionStrategy;
import com.yaxon.vndp.dcap.strategy.resolution.ShardResolutionStrategyData;
import com.yaxon.vndp.dcap.util.ShardIdUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Author: 游锋锋
 * Time: 2016-02-26 17:34
 * Copyright (C) 2016 Xiamen Yaxon Networks CO.,LTD.
 */
public class ShardedRedisTemplate implements RedisOperations,InitializingBean{
    private ShardedRedisConnectionFactory shardedRedisConnectionFactory;
    private ShardStrategyFactory shardStrategyFactory;
    private ShardAccessStrategy shardAccessStrategy;
    private ShardResolutionStrategy shardResolutionStrategy;

    private final Integer RESULT_SUCESS=1;
    private final Integer RESULT_FAIL=0;

    @Override
    public void afterPropertiesSet() throws Exception {
        Validate.notNull(shardedRedisConnectionFactory, "Property 'shardedRedisConnectionFactory' is required");
        Validate.notNull(shardStrategyFactory, "Property 'shardStrategyFactory' is required");

        ShardStrategy shardStrategy = shardStrategyFactory.newShardStrategy();
        shardAccessStrategy = shardStrategy.getShardAccessStrategy();
        shardResolutionStrategy = shardStrategy.getShardResolutionStrategy();
    }

    private <T> T execute(List<Shard> shards,
                          ShardOperation<T> shardOperation, ExitStrategy<T> exitStrategy) {
        Validate.notEmpty(shards, "There is no shards to execute shard operation: {}", shardOperation.getOperationName());

        return shardAccessStrategy.apply(shards, shardOperation, exitStrategy);
    }

    /**
     * 根据执行的命名空间+执行的参数进行选择分区
     * @param statement
     * @param parameter
     * @return
     */
    public List<Shard> calShards(String statement, Object parameter) {
        List<ShardId> shardIds = shardResolutionStrategy.selectShardIdsFromShardResolutionStrategyData(
                new ShardResolutionStrategyData(statement, parameter));
        Set<Shard> shards = shardedRedisConnectionFactory.getShardsByShardIds(shardIds);
        return Lists.newArrayList(shards);
    }

     /**
     * 根据执行的命名空间+执行的参数集合进行分区选择。
     * @param statement
     * @param parameter  参数支持范围条件和单个条件的匹配
     * （如$1-3000,5006,5007这样表示对1-3000范围所对应的分区，和5006,5007多对应的分区进行排重选择）
     * @return
     */
    public List<Shard> calShardsComplex(String statement,String routKey, String parameter) {
        if(StringUtils.isBlank(routKey)||StringUtils.isEmpty(routKey)
                ||StringUtils.isBlank(parameter)||StringUtils.isEmpty(parameter)){
            List<ShardId> shardIds = shardResolutionStrategy.selectShardIdsFromShardResolutionStrategyData(
                    new ShardResolutionStrategyData(statement, parameter));
            return Lists.newArrayList(shardedRedisConnectionFactory.getShardsByShardIds(shardIds));
        }
        //TODO:考虑利用多线程并发的方式进行分区选择
        Set<ShardId> shardIdSet= Sets.newHashSet();
        Map<String,Object> values= Maps.newHashMap();
        String[] arr = StringUtils.split(parameter, ',');
        for (String s : arr) {
            int idx0 = s.lastIndexOf('$');
            if (idx0 == 0) {
                String range = s.substring(idx0 + 1);
                int idx1 = range.indexOf('-');
                long start = Long.parseLong(range.substring(0, idx1));
                long end = Long.parseLong(range.substring(idx1 + 1));

                //开始二分双头查找法
                shardIdSet.addAll(fetShardIdByRange(statement,routKey,start,end));
            }else{
                values.put(routKey,s);
                List<ShardId> shardIds = shardResolutionStrategy.selectShardIdsFromShardResolutionStrategyData(
                        new ShardResolutionStrategyData(statement, values));
                shardIdSet.addAll(shardIds);
            }
        }
        return Lists.newArrayList(shardedRedisConnectionFactory.getShardsByShardIds(Lists.newArrayList(shardIdSet)));
    }

    /**
     * 直接根据分区id进行选择分区（目前暂不考虑这种类型）
     * @param shardIds
     * @return
     */
    private List<Shard> calShards(String shardIds) {
        if(StringUtils.isNotEmpty(shardIds)||StringUtils.isBlank(shardIds)){
           return null;
        }
        Set<ShardId> shardIdSet = ShardIdUtil.parseShardIds(shardIds);;
        if(shardIdSet==null||shardIdSet.size()<=0){
            return null;
        }
        Set<Shard> shards = shardedRedisConnectionFactory.getShardsByShardIds(Lists.newArrayList(shardIdSet));
        return Lists.newArrayList(shards);
    }

    /**
     * 根据条件范围来进行递归匹配分区（二分双头查找法）
     * @param statement
     * @param routKey
     * @param start
     * @param end
     * @return
     */
    private List<ShardId> fetShardIdByRange(String statement,String routKey,long start,long end){
        Map<String,Object> values=Maps.newHashMap();
        values.put(routKey,start);
        List<ShardId> startShardIdList=shardResolutionStrategy.selectShardIdsFromShardResolutionStrategyData(
                new ShardResolutionStrategyData(statement, values));
        values.put(routKey,end);
        List<ShardId> endShardIdList=shardResolutionStrategy.selectShardIdsFromShardResolutionStrategyData(
                new ShardResolutionStrategyData(statement, values));
        if(startShardIdList.equals(endShardIdList)){
            return startShardIdList;
        }else{
            Set<ShardId> resultSet=Sets.newHashSet();
            long average=(start+end)/2;
            if(start==average||end==average){
                //start和averge在的平均数和其中一个相当，说明二者值相差1,
                //直接添加二者的分区id集合，避免进入递归死循环
                resultSet.addAll(startShardIdList);
                resultSet.addAll(endShardIdList);
                return Lists.newArrayList(resultSet);
            }
            values.put(routKey,average);
            List<ShardId> averageShardIdList=shardResolutionStrategy.selectShardIdsFromShardResolutionStrategyData(
                    new ShardResolutionStrategyData(statement, values));
            if(startShardIdList.equals(averageShardIdList)){
                resultSet.addAll(startShardIdList);
                resultSet.addAll(fetShardIdByRange(statement,routKey,average,end));
            }else if(endShardIdList.equals(averageShardIdList)){
                resultSet.addAll(endShardIdList);
                resultSet.addAll(fetShardIdByRange(statement,routKey,start,average));
            }else {
                resultSet.addAll(fetShardIdByRange(statement,routKey,start,average));
                resultSet.addAll(fetShardIdByRange(statement,routKey,average,end));
            }
            return Lists.newArrayList(resultSet);
        }
    }


    @Override
    public String get(final String statement, final Object routObj, final String key) {
        ShardOperation<String> shardOperation = new ShardOperation<String>() {
            public String execute(Shard shard) {
                return shard.getRedisTemplate().opsForValue().get(key);
            }

            public String getOperationName() {
                return "getCacheContent<" + statement + ">";
            }
        };

        return execute(calShards(statement, routObj), shardOperation,new FirstNonNullResultExitStrategy<String>());
    }

    @Override
    public int set(final String statement,final Object routObj,final String key,final String value) {
        ShardOperation<Integer> shardOperation = new ShardOperation<Integer>() {
            public Integer execute(Shard shard) {
                shard.getRedisTemplate().opsForValue().set(key,value);
                return RESULT_SUCESS;
            }

            public String getOperationName() {
                return "setCacheContent<" + statement + ">";
            }
        };

       return execute(calShards(statement, routObj), shardOperation,new IntegerSumExitStrategy());
    }

    @Override
    public int set(final String statement,final Object routObj,final String key,final String value,final Long time,final TimeUnit timeUnit) {
        ShardOperation<Integer> shardOperation = new ShardOperation<Integer>() {
            public Integer execute(Shard shard) {
                shard.getRedisTemplate().opsForValue().set(key,value,time,timeUnit);
                return RESULT_SUCESS;
            }

            public String getOperationName() {
                return "setCacheContentWithTime<" + statement + ">";
            }
        };

        return execute(calShards(statement, routObj), shardOperation,new IntegerSumExitStrategy());
    }

    @Override
    public int delete(final String statement,final Object routObj,final String key) {
        ShardOperation<Integer> shardOperation = new ShardOperation<Integer>() {
            public Integer execute(Shard shard) {
                shard.getRedisTemplate().delete(key);
                return RESULT_SUCESS;
            }

            public String getOperationName() {
                return "deleteCacheContent<" + statement + ">";
            }
        };

        return execute(calShards(statement, routObj), shardOperation,new IntegerSumExitStrategy());
    }

    @Override
    public int push(final String statement,final Object routObj,final String topic,final Object value) {
        ShardOperation<Integer> shardOperation = new ShardOperation<Integer>() {
            public Integer execute(Shard shard) {
                shard.getRedisTemplate().convertAndSend(topic, value);
                return RESULT_SUCESS;
            }

            public String getOperationName() {
                return "pushMessage<" + statement + ">";
            }
        };

        return execute(calShards(statement, routObj), shardOperation,new IntegerSumExitStrategy());
    }

    @Override
    public int addMessageListener(final String statement,final String routKey,final String parameter,final MessageListener messageListener,final PatternTopic patternTopic) {
        ShardOperation<Integer> shardOperation = new ShardOperation<Integer>() {
            public Integer execute(Shard shard) {
                shard.getRedisMessageListenerContainer().addMessageListener(messageListener, patternTopic);
                return RESULT_SUCESS;
            }

            public String getOperationName() {
                return "addShardMessageListener";
            }
        };

        return execute(calShardsComplex(statement,routKey,parameter), shardOperation,new IntegerSumExitStrategy());
    }

    @Override
    public int addAllShardMessageListener(final MessageListener messageListener,final PatternTopic patternTopic) {
        ShardOperation<Integer> shardOperation = new ShardOperation<Integer>() {
            public Integer execute(Shard shard) {
                shard.getRedisMessageListenerContainer().addMessageListener(messageListener, patternTopic);
                return RESULT_SUCESS;
            }

            public String getOperationName() {
                return "addAllShardMessageListener";
            }
        };

        return execute(shardedRedisConnectionFactory.getAllShards(), shardOperation,new IntegerSumExitStrategy());
    }

    @Override
    public int removeMessageListener(final String statement,final String routKey,final String parameter,final MessageListener messageListener) {
        ShardOperation<Integer> shardOperation = new ShardOperation<Integer>() {
            public Integer execute(Shard shard) {
                shard.getRedisMessageListenerContainer().removeMessageListener(messageListener);
                return RESULT_SUCESS;
            }

            public String getOperationName() {
                return "removeMessageListener";
            }
        };

        return execute(calShardsComplex(statement,routKey,parameter), shardOperation,new IntegerSumExitStrategy());
    }

    @Override
    public int removeMessageListener(final String statement,final String routKey,final String parameter,final MessageListener messageListener,final PatternTopic patternTopic) {
        ShardOperation<Integer> shardOperation = new ShardOperation<Integer>() {
            public Integer execute(Shard shard) {
                shard.getRedisMessageListenerContainer().removeMessageListener(messageListener,patternTopic);
                return RESULT_SUCESS;
            }

            public String getOperationName() {
                return "removeMessageListener2";
            }
        };

        return execute(calShardsComplex(statement,routKey,parameter), shardOperation,new IntegerSumExitStrategy());
    }

    @Override
    public int removeAllShardMessageListener(final MessageListener messageListener) {
        ShardOperation<Integer> shardOperation = new ShardOperation<Integer>() {
            public Integer execute(Shard shard) {
                shard.getRedisMessageListenerContainer().removeMessageListener(messageListener);
                return RESULT_SUCESS;
            }

            public String getOperationName() {
                return "removeAllShardMessageListener";
            }
        };

        return execute(shardedRedisConnectionFactory.getAllShards(), shardOperation,new IntegerSumExitStrategy());
    }

    @Override
    public int removeAllShardMessageListener(final MessageListener messageListener,final PatternTopic patternTopic) {
        ShardOperation<Integer> shardOperation = new ShardOperation<Integer>() {
            public Integer execute(Shard shard) {
                shard.getRedisMessageListenerContainer().removeMessageListener(messageListener,patternTopic);
                return RESULT_SUCESS;
            }

            public String getOperationName() {
                return "removeAllShardMessageListener2";
            }
        };

        return execute(shardedRedisConnectionFactory.getAllShards(), shardOperation,new IntegerSumExitStrategy());
    }

    @Override
    public int setForAdd(final String statement,final Object routObj,final String key,final String... value) {
        ShardOperation<Integer> shardOperation = new ShardOperation<Integer>() {
            public Integer execute(Shard shard) {
                shard.getRedisTemplate().opsForSet().add(key,value);
                return RESULT_SUCESS;
            }

            public String getOperationName() {
                return "addCacheContentForSet<" + statement + ">";
            }
        };

        return execute(calShards(statement, routObj), shardOperation,new IntegerSumExitStrategy());
    }

    @Override
    public Boolean hasKey(final String statement,final Object routObj,final String key) {
        ShardOperation<Boolean> shardOperation = new ShardOperation<Boolean>() {
            public Boolean execute(Shard shard) {
                return shard.getRedisTemplate().hasKey(key);
            }

            public String getOperationName() {
                return "addCacheContentForSet<" + statement + ">";
            }
        };

        return execute(calShards(statement, routObj), shardOperation,new FirstNonNullResultExitStrategy<Boolean>());
    }


    public ShardedRedisConnectionFactory getShardedRedisConnectionFactory() {
        return shardedRedisConnectionFactory;
    }

    public void setShardedRedisConnectionFactory(ShardedRedisConnectionFactory shardedRedisConnectionFactory) {
        this.shardedRedisConnectionFactory = shardedRedisConnectionFactory;
    }

    public ShardStrategyFactory getShardStrategyFactory() {
        return shardStrategyFactory;
    }

    public void setShardStrategyFactory(ShardStrategyFactory shardStrategyFactory) {
        this.shardStrategyFactory = shardStrategyFactory;
    }
}
