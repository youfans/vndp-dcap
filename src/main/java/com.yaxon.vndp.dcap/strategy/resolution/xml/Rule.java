package com.yaxon.vndp.dcap.strategy.resolution.xml;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.yaxon.vndp.dcap.ShardId;

import java.util.Collection;
import java.util.List;

/**
 * Author: 游锋锋
 * Time: 2016-03-01 10:24
 * Copyright (C) 2016 Xiamen Yaxon Networks CO.,LTD.
 */
public class Rule {
    private String sqlMap;
    private List<ShardId> defaultShardIds;
    private List<RuleItem> ruleItems = Lists.newArrayList();

    public void setSqlMap(String sqlMap) {
        this.sqlMap = sqlMap;
    }

    public void setDefaultShardIds(Collection<ShardId> shardIds) {
        this.defaultShardIds = ImmutableList.copyOf(shardIds);
    }

    public List<ShardId> getDefaultShardIds() {
        return defaultShardIds;
    }

    public void addRuleItem(RuleItem ruleItem) {
        ruleItems.add(ruleItem);
    }

    public List<ShardId> route(String statement, Object parameter) {
        for (RuleItem ruleItem : ruleItems) {
            if (ruleItem.apply(statement, parameter)) {
                return ruleItem.getShardIds();
            }
        }
        return defaultShardIds;
    }
}
