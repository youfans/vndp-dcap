package com.yaxon.vndp.dcap.strategy.resolution.xml;

import com.google.common.collect.ImmutableList;
import com.yaxon.vndp.dcap.ShardId;
import org.mvel2.MVEL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * Author: 游锋锋
 * Time: 2016-03-01 10:25
 * Copyright (C) 2016 Xiamen Yaxon Networks CO.,LTD.
 */
public class RuleItem {
    private final Logger logger = LoggerFactory.getLogger(RuleItem.class);


    private Serializable expression;
    private List<ShardId> shardIds;

    public RuleItem(String expression) {
        this(expression, null);
    }

    public RuleItem(String expression, Collection<ShardId> shardIds) {
        this.expression = MVEL.compileExpression(expression);

        if (shardIds == null || shardIds.isEmpty()) {
            throw new RuntimeException("RuleItem 中的 shardIds 不能为空");
        }

        this.shardIds = ImmutableList.copyOf(shardIds);
    }

    public List<ShardId> getShardIds() {
        return shardIds;
    }

    public boolean apply(String statement, Object parameter) {
        try {
            return (Boolean)MVEL.executeExpression(expression, parameter);
        } catch (Exception e) {
            logger.debug("Fail to eval sharding expression: {}/{}", statement, parameter);
            return false;
        }
    }
}
