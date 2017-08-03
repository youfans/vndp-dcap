package com.yaxon.vndp.dcap.strategy.resolution;

/**
 * Author: 游锋锋
 * Time: 2016-03-01 10:31
 * Copyright (C) 2016 Xiamen Yaxon Networks CO.,LTD.
 */
public class ShardResolutionStrategyData {
    /* 命名空间（或+执行命令变量）  */
    private final String statement;
    /* 执行参数 */
    private final Object parameter;

    public ShardResolutionStrategyData(String statement, Object parameter) {
        this.statement = statement;
        this.parameter = parameter;
    }

    public String getStatement() {
        return statement;
    }

    public Object getParameter() {
        return parameter;
    }
}
