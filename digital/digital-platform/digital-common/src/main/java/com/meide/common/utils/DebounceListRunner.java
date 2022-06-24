package com.meide.common.utils;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 可以频繁加入数据，然后优化防抖和数据合并，以较低频率对数据列表进行任务操作。
 * 如：设置延迟1000ms执行且超100条时立刻执行，假设1000ms内涌入1010条数据，实际仅立刻执行10次函数，间隔1s后再执行1次，共计11次。
 * @author jiay
 */
public class DebounceListRunner<T> {
    //加入的数据
    private List<T> data = Collections.synchronizedList(new ArrayList<T>());
    private DebounceUtil debounceUtil = new DebounceUtil();//防抖 降频 延迟一定时间操作
    private ExecutorService executorService = null;//用于在达到最大数据量时操作。
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    //操作data时，默认加锁
    private ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();
//    private ReentrantReadWriteLock.ReadLock readLock = lock.readLock();

    private int interval = 1000;
    private int maxSize = 10;
    private int threadPoolSize = 10;
    private CallBack<T> callBack;

    /**
     * @param threadPoolSize 线程池大小，线程为线程池+防抖timer线程  n+1个
     * @param interval       加入数据后回调的调用间隔 单位ms
     * @param maxSize        集合数据达到一定数量时调用，如果采用addAll()，则不能保证最终调用数量
     * @param callBack       加入数据后一定间隔内，调用回调。参数是一定间隔内的数据列表。
     */
    public DebounceListRunner(int threadPoolSize, int interval, int maxSize, CallBack<T> callBack) {
        this.threadPoolSize = threadPoolSize;
        this.interval = interval;
        this.maxSize = maxSize;
        this.callBack = callBack;
    }

    public void add(T item) {
        writeLock.lock();
        data.add(item);
        checkToDo();
        writeLock.unlock();
    }

    /**
     * 使用该方法，可能导致最大maxSize失效。
     *
     * @param list
     */
    @Deprecated
    public void addAll(List<T> list) {
        writeLock.lock();
        data.addAll(list);
        checkToDo();
        writeLock.unlock();
    }

    //in lock.thread safe
    private void checkToDo() {
        int size = data.size();
        if (size >= maxSize) {
            List<T> currList = data;
            DebounceListRunner.this.data = Collections.synchronizedList(new ArrayList<T>());
            realDo(currList);
        } else {
            doTask();
        }
    }

    public void doTask() {
        if (null == callBack) {
            return;
        }
        debounceUtil.start(new TimerTask() {
            @Override
            public void run() {
                writeLock.lock();
                List<T> currList = data;
                DebounceListRunner.this.data = Collections.synchronizedList(new ArrayList<T>());
                realDo(currList);
                writeLock.unlock();
            }
        }, interval);
    }

    //实际执行任务 在线程池中执行。thread safe
    private void realDo(List<T> currList) {
        if (null == executorService) {
            executorService = Executors.newFixedThreadPool(threadPoolSize);
        }
        executorService.submit(() -> {
            //in thread pool
            if (!currList.isEmpty()) {
                callBack.run(currList);
            }
        });
    }

    /**
     * 销毁时调用。
     * 但如果销毁后，再继续添加数据，会自动开启。
     */
    private void cancel() {
        writeLock.lock();
        debounceUtil.cancel();
        if (null != executorService) {
            executorService.shutdownNow();
        }
        executorService = null;
        writeLock.unlock();
    }

    public interface CallBack<T> {
        void run(List<T> data);
    }

    public static void main(String[] args) {
//      测试，数量不足 n 时，时间间隔 ms执行，达到 n 条记录立刻执行。
        Long[] st = new Long[]{System.currentTimeMillis(), 0L};
        DebounceListRunner debounceListRunner = new DebounceListRunner(4, 1000, 333, new CallBack() {
            @Override
            public void run(List data) {
                synchronized (st) {
                    st[1]++;
                    System.out.println("call times:" + st[1]
                            + ",data size:" + data.size()
                            + "," + Thread.currentThread()
                            + ",complete at (ms):" + (System.currentTimeMillis() - st[0]));
                }
            }
        });
        //第1-2s内插入2000条
        for (int i = 0; i < 2000; i++) {
            final int cTime = (int) (i * Math.random());
            final int index = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(cTime);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    String d = "str" + index + ",delay:" + (System.currentTimeMillis() - st[0]);
//                    System.out.println(d);
                    debounceListRunner.add(d);
                }
            }).start();
        }

        // 第3-4s内插入 n条
        for (int i = 0; i < 1000; i++) {
            final int cTime = (int) (i * Math.random()) + 3000;
            final int index = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(cTime);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    String d = "str_2_" + index;
                    debounceListRunner.add(d);
                }
            }).start();
        }

        SimpleTimer.setTimeout(() -> {
            debounceListRunner.cancel();
            System.out.println("cancel!");
        }, 7000);

    }

    /**
     * 简单计时器
     */
    public static class SimpleTimer {
        public static void setTimeout(Runnable runnable, int delayMs) {
            if (null == runnable) {
                return;
            }
            Timer tt = new Timer();
            tt.schedule(new TimerTask() {
                @Override
                public void run() {
                    tt.cancel();
                    runnable.run();
                }
            }, delayMs);
        }
    }

    /**
     * 降频操作
     */
    public static class DebounceUtil {

        private Timer timer = null;
        private TimerTask lastTask;

        public synchronized void start(TimerTask runnable, int delayMillSecond) {
            if (null == timer) {
                timer = new Timer();
            }
            if (lastTask != null) {
                lastTask.cancel();
                lastTask = null;
            }
            if (null == runnable) {
                return;
            }
            timer.schedule(runnable, delayMillSecond);
            lastTask = runnable;
        }

        public synchronized void cancel() {
            if (null != timer) {
                timer.cancel();
                timer = null;
            }
        }

        public static void main(String[] args) {
            DebounceUtil debounceUtil = new DebounceUtil();
            for (int i = 0; i < 100000; i++) {
                debounceUtil.start(new TimerTask() {
                    @Override
                    public void run() {
                        System.out.println("running!");
                    }
                }, 500);
            }

            SimpleTimer.setTimeout(new Runnable() {
                @Override
                public void run() {
                    debounceUtil.start(new TimerTask() {
                        @Override
                        public void run() {
                            System.out.println("running!--2");
                        }
                    }, 500);
                }
            }, 550);

            SimpleTimer.setTimeout(new Runnable() {
                @Override
                public void run() {
                    debounceUtil.cancel();
                }
            }, 2000);

        }
    }


}
