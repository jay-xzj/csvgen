package com.consume2;


import com.multithread.CSVUtils;
import com.util.Constant;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class Consumer2 implements Runnable{
    static CountDownLatch cdl=new CountDownLatch(10);;
    static AtomicInteger ai=new AtomicInteger(0);
    //final String downloadFilePath = "E:\\";
    final String downloadFilePath = "/Users/xuzhijie/Documents/small_poms";

    private List<List<List<Object>>> queue;
    //与type有关的有csv的格式，表头，文件名
    private String type;
    private int len;
    private boolean flag = true;

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public Consumer2(List<List<List<Object>>> queue, int len, String type){
        this.queue = queue;
        this.len = len;
        this.type = type;
    }
    @Override
    public void run() {
        try {
            while (flag) {
                if (Thread.currentThread().isInterrupted())
                    break;
                List<List<Object>> data = null;
                Main2.lock.lock();
                if (queue.size() == 0 && !Main2.lastBatch){
                    Main2.full.signalAll();
                    Main2.empty.await();
                }

                Thread.sleep(1000);
                //取到queue里面的一个数据集进行消费，在我们这就是写到csv中啦
                //队列里面的每个数据集的大小是10W,队列长队为10,那么都写到csv文件中就是100W条数据
                if (!queue.isEmpty()) {
                    data = queue.remove(0);
                }else{
                    //第二次校验，queue里面应该有数据集才对，如果没有就是生产者挂了
                    setFlag(false);
                    break;
                }

                // 导出文件名称
                int batchNum = ai.incrementAndGet(); //这边的index是第几个10，也就是消耗完第几个队列了。
                cdl.countDown();
                /*if (cdl.getCount() == 0){
                    idx = idx + ai.incrementAndGet();
                    cdl = new CountDownLatch(10);
                }else{
                    idx = idx + ai.get();
                }*/

                List<Object> headList = null;
                String fileName = null;
                if (Constant.DEPEND_ON_ALL.equalsIgnoreCase(type)){
                    //每个数据集是10W条
                    Object[] head = {"from","to","groupId","artifactId","version"};
                    headList = Arrays.asList(head);
                    fileName = "depend_"+batchNum;
                }else if (Constant.ARTIFACT_ALL.equalsIgnoreCase(type)){
                    //每个数据集是10W条
                    Object[] head = {"from","to","groupId","artifactId","version"};
                    headList = Arrays.asList(head);
                    fileName = "depend_"+batchNum;
                }
                File csvFile = CSVUtils.createCSVFile(headList, data, downloadFilePath, fileName);

                Main2.lock.unlock();
                //System.out.println("消费者ID:"+Thread.currentThread().getId()+" 消费到了第"+(ai.get() * len + (10-cdl.getCount()))+"批次");
                System.out.println("消费者ID:"+Thread.currentThread().getId()+" 消费到了第"+batchNum+"批次");
            }
            /*if (Main2.lastBatch == Boolean.TRUE){
                setFlag(false);
                break;
            }*/
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}