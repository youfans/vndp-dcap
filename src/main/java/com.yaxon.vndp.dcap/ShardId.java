package com.yaxon.vndp.dcap;

/**
 * Author: 游锋锋
 * Time: 2016-02-25 19:49
 * Copyright (C) 2016 Xiamen Yaxon Networks CO.,LTD.
 */

/**
 * 分区id实体类
 */
public class ShardId {
    private final String shardId;

    public ShardId(final String shardId) {
        this.shardId = shardId;
    }

    public String getId() {
        return shardId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ShardId shardId1 = (ShardId) o;

        if (shardId != null ? !shardId.equals(shardId1.shardId) : shardId1.shardId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return shardId != null ? shardId.hashCode() : 0;
    }

    @Override
    public String toString() {
        return shardId;
    }
}
