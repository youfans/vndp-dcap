package com.yaxon.vndp.dcap.strategy.resolution.xml;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.yaxon.vndp.dcap.ShardId;
import com.yaxon.vndp.dcap.strategy.resolution.ShardResolutionStrategy;
import com.yaxon.vndp.dcap.strategy.resolution.ShardResolutionStrategyData;
import com.yaxon.vndp.dcap.util.ShardIdUtil;
import com.yaxon.vndp.dcap.util.XMLConfigurationEx;
import org.apache.commons.configuration.Configuration;
import org.springframework.beans.factory.InitializingBean;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Author: 游锋锋
 * Time: 2016-03-01 10:28
 * Copyright (C) 2016 Xiamen Yaxon Networks CO.,LTD.
 */
public class XmlShardResolutionStrategy implements ShardResolutionStrategy,InitializingBean {
    private Map<String, Rule> sqlMap2Rule = Maps.newHashMap();
    private String configLocation;
    private Map<String, Object> functions;
    private List<ShardId> EMPTY_SHARD_IDS = Lists.newArrayList();

    public void setConfigLocation(String configLocation) {
        this.configLocation = configLocation;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        loadConfig(configLocation);
    }

    @Override
    public List<ShardId> selectShardIdsFromShardResolutionStrategyData(ShardResolutionStrategyData shardResolutionStrategyData) {
        String statement = shardResolutionStrategyData.getStatement();
        Object parameter = shardResolutionStrategyData.getParameter();

        List<ShardId> shardIds = route(statement, parameter);
        if (shardIds == null || shardIds.isEmpty()) {
            if (statement != null) {
                String namespace = statement.substring(0, statement.lastIndexOf("."));
                shardIds = route(namespace, parameter);
            }
        }

        if (shardIds == null) {
            return EMPTY_SHARD_IDS;
        } else {
            return shardIds;
        }
    }

    private void loadConfig(String configLocation) throws Exception {
        try {
            Configuration config = new XMLConfigurationEx(configLocation);

            String key = null;
            for(int i = 0;;i++) {
                key = "rule("+i+")";
                if (!config.getKeys(key).hasNext()) {
                    break;
                }

                String sqlMap = config.getString(key + "[@redisMap]");
                if (sqlMap2Rule.containsKey(sqlMap)) {
                    throw new Exception("存在重复的sqlMap：" + sqlMap);
                }

                Rule rule = new Rule();
                rule.setSqlMap(sqlMap);

                String[] defaultShards = config.getStringArray(key + "[@defaultShards]");
                if (defaultShards != null && defaultShards.length > 0) {
                    Set<ShardId> shardIdSet = Sets.newHashSet();
                    for (String ds : defaultShards) {
                        shardIdSet.addAll(ShardIdUtil.parseShardIds(ds));
                    }
                    rule.setDefaultShardIds(shardIdSet);
                }

                for (int j = 0;;j++) {
                    key = "rule("+i+").ruleItem(" + j + ")";
                    if (!config.getKeys(key).hasNext()) {
                        break;
                    }

                    String[] shards = config.getStringArray(key + "[@shards]");
                    if (shards == null || shards.length == 0) {
                        throw new Exception("rule.ruleItem[@shards] 不能为空");
                    }

                    Set<ShardId> shardIdSet = Sets.newHashSet();
                    for (String shard : shards) {
                        shardIdSet.addAll(ShardIdUtil.parseShardIds(shard));
                    }

                    String expression = config.getString(key);

                    rule.addRuleItem(new RuleItem(expression, shardIdSet));
                }

                sqlMap2Rule.put(sqlMap, rule);
            }
        } catch (Exception e) {
            throw new Exception("加载redis数据切分配置文件[" + configLocation + "]异常: " + e.getMessage(), e);
        }
    }

    private List<ShardId> route(String statement, Object parameter) {
        if (sqlMap2Rule.containsKey(statement)) {
            Rule rule = sqlMap2Rule.get(statement);
            return rule.route(statement, parameter);
        }
        return null;
    }
}
