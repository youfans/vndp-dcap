package com.yaxon.vndp.dcap.strategy.access;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.*;
import com.yaxon.vndp.dcap.Shard;
import com.yaxon.vndp.dcap.ShardOperation;
import com.yaxon.vndp.dcap.exception.MultipleCauseException;
import com.yaxon.vndp.dcap.strategy.exit.ExitStrategy;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

/**
 * Author: 游锋锋
 * Time: 2016-03-01 10:21
 * Copyright (C) 2016 Xiamen Yaxon Networks CO.,LTD.
 */
public class ParallelShardAccessStrategy implements ShardAccessStrategy {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private ListeningExecutorService executorService;

    public ParallelShardAccessStrategy(ExecutorService executorService) {
        Validate.notNull(executorService);
        this.executorService = MoreExecutors.listeningDecorator(executorService);
    }

    @Override
    public <T> T apply(final List<Shard> shards, final ShardOperation<T> operation,final ExitStrategy<T> exitStrategy) {
        final List<Pair<Shard, ListenableFuture<T>>> futureTasks = Lists.newArrayList();

        for (final Shard shard : shards) {
            ListenableFuture<T> f = executorService.submit(new Callable<T>() {
                @Override
                public T call() throws Exception {
                    return operation.execute(shard);
                }
            });
            futureTasks.add(new ImmutablePair<Shard, ListenableFuture<T>>(shard, f));
        }

        final CountDownLatch doneSignal = new CountDownLatch(futureTasks.size());
        final MultipleCauseException exceptions = new MultipleCauseException();
        for (Pair<Shard, ListenableFuture<T>> ft : futureTasks) {
            final Shard shard = ft.getLeft();

            Futures.addCallback(ft.getRight(), new FutureCallback<T>() {
                @Override
                public void onSuccess(T res) {
                    try {
                        if (exitStrategy.addResult(res)) {
                            logger.debug("Short-circuiting execution of {} on other threads after execution against shard {}",
                                    operation.getOperationName(),
                                    shard);
                            cancelFutureTasks();
                        } else {
                            logger.debug("No need to short-cirtcuit execution of {} on other threads after execution against shard {}",
                                    operation.getOperationName(),
                                    shard);
                        }
                    } finally {
                        doneSignal.countDown();
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    try {
                        if (t instanceof CancellationException) {
                            logger.debug("Cancel execution on shard {}", shard);
                        } else {
                            exceptions.add(t);

                            logger.debug("Short-circuiting execution of {} on other threads after failing to execute on shard {}",
                                    operation.getOperationName(),
                                    shard);
                            cancelFutureTasks();
                        }
                    } finally {
                        doneSignal.countDown();
                    }
                }

                private void cancelFutureTasks() {
                    for (Pair<Shard, ListenableFuture<T>> t : futureTasks) {
                        if (!shard.equals(t.getLeft())) {
                            logger.debug("Prepare to cancel future task on shard {}.", t.getLeft());
                            t.getRight().cancel(false);
                        }
                    }
                }
            });
        }

        try {
            logger.debug("Waiting for threads to complete processing before proceeding.");
            //TODO:(maxr) let users customize timeout behavior
              /*
              if(!doneSignal.await(10, TimeUnit.SECONDS)) {
                final String msg = "Parallel operations timed out.";
                logger.error(msg);
                throw new HibernateException(msg);
              }
              */

            // now we wait until all threads finish
            doneSignal.await();
        } catch (InterruptedException e) {
            // not sure why this would happen or what we should do if it does
            logger.error("Received unexpected exception while waiting for done signal.", e);
        }


        if (!exceptions.getCauses().isEmpty()) {
            logger.error("One or more errors when performing data access operations against multiple shards", exceptions);
        } else {
            logger.debug("Compiling results.");
        }
        return exitStrategy.compileResults();
    }
}
