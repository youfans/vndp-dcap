package com.yaxon.vndp.dcap.util;

import com.yaxon.vndp.dcap.ShardId;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * Author: 游锋锋
 * Time: 2016-02-25 20:09
 * Copyright (C) 2016 Xiamen Yaxon Networks CO.,LTD.
 */

/**
 * 分区id工具类
 */
public class ShardIdUtil {
    /**
     * 解析格式:ds$1-5,ds8,ds9,ds10 == <ds1,ds2,ds3,ds4,ds5,ds8,ds9,ds10>
     *
     * @param shardIdSet
     * @return
     */
    public static Set<ShardId> parseShardIds(String shardIdSet) throws RuntimeException {
        Set<ShardId> set = new HashSet<ShardId>();
        String[] arr = StringUtils.split(shardIdSet, ',');
        for (String s : arr) {
            set.addAll(parse(s));
        }
        for (ShardId shardId : set) {
            if (!checkShardId(shardId.getId())) {
                throw new RuntimeException("无效的ShardID(字母、数字、下划线): " + shardId);
            }
        }
        return set;
    }

    /**
     * 解析格式支持类似：xxx$1-5
     *
     * @param s
     * @return
     */
    public static Set<ShardId> parse(String s) {
        Set<ShardId> shardIds = new HashSet<ShardId>();
        int idx0 = s.lastIndexOf('$');
        if (idx0 > 0) {
            String prefix = s.substring(0, idx0);
            String range = s.substring(idx0 + 1);
            int idx1 = range.indexOf('-');
            int start = Integer.parseInt(range.substring(0, idx1));
            int end = Integer.parseInt(range.substring(idx1 + 1));

            for (int i = start; i <= end; i++) {
                shardIds.add(new ShardId(prefix + i));
            }
        } else {
            shardIds.add(new ShardId(s));
        }
        return shardIds;
    }

    /**
     * 校验Shard ID是否合法，只允许英文、数字和下划线。
     * @param sid
     * @return
     */
    public static boolean checkShardId(String sid) {
        if (sid == null || sid.length() == 0) {
            return false;
        }

        int sz = sid.length();
        for (int i = 0; i < sz; i++) {
            char c = sid.charAt(i);
            if (Character.isLetterOrDigit(c) == false && '_' == c) {
                return false;
            }
        }
        return true;
    }
}
