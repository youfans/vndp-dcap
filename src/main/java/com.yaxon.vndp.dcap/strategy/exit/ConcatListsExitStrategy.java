package com.yaxon.vndp.dcap.strategy.exit;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Author: 游锋锋
 * Time: 2016-03-01 15:45
 * Copyright (C) 2016 Xiamen Yaxon Networks CO.,LTD.
 */
public class ConcatListsExitStrategy<T> implements ExitStrategy<T> {

    private final List<T> resultList = Lists.newArrayList();

    @Override
    public synchronized boolean addResult(T result) {
        resultList.add(result);
        return false;
    }

    @Override
    public T compileResults() {
        return null;
    }

    public List<T> compileResultLists(){
        return resultList;
    }
}
