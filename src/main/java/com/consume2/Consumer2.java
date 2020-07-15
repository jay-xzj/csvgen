package com.consume2;


import com.multithread.CSVUtils;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class Consumer2 implements Runnable{
    static CountDownLatch cdl=new CountDownLatch(10);;
    static AtomicInteger ai=new AtomicInteger(0);

    private List<List<List<Object>>> queue;
    public Consumer2(List<List<List<Object>>> queue){
        this.queue = queue;
    }
    @Override
    public void run() {
        try {
            while (true) {
                if (Thread.currentThread().isInterrupted())
                    break;
                List<List<Object>> data = null;
                Main2.lock.lock();
                if (queue.size() == 0){
                    Main2.full.signalAll();
                    Main2.empty.await();
                }
                Thread.sleep(1000);
                //取到queue里面的一个数据集进行消费，在我们这就是写到csv中啦
                //队列里面的每个数据集的大小是10W,队列长队为10,那么都写到csv文件中就是100W条数据
                data = queue.remove(0);
                //每个数据集是10W条
                Object[] head = {"from","to","groupId","artifactId","version"};
                List<Object> headList = Arrays.asList(head);
                //String downloadFilePath = "/Users/xuzhijie/Documents/small_poms";
                String downloadFilePath = "E:\\";

                // 导出文件名称
                int idx = 1; //这边的index是第几个10，也就是消耗完第几个队列了。

                cdl.countDown();
                idx = idx + ai.getAndIncrement();
                /*if (cdl.getCount() == 0){
                    ai.incrementAndGet();
                    cdl = new CountDownLatch(10);
                }else{
                    idx = idx + ai.get();
                }*/
                String fileName = "depend_"+idx;
                File csvFile = CSVUtils.createCSVFile(headList, data, downloadFilePath, fileName);

                Main2.lock.unlock();
                System.out.println("消费者ID:"+Thread.currentThread().getId()+" 消费到了第"+idx+"批次");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}