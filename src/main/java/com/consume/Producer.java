package com.consume;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 生产者
 * @author ctk
 * 生产者消费者模型
 */

public class Producer  implements Runnable{
    static CountDownLatch cdl=new CountDownLatch(20);;
    static AtomicInteger ai=new AtomicInteger(0);

    private List<List<List<Object>>> queue;
    private int len;
    public Producer(List<List<List<Object>>> queue,int len){
        this.queue = queue;
        this.len = len;
    }
    @Override
    public void run() {
        try{
            while(true){
                if(Thread.currentThread().isInterrupted()) {
                    break;
                }
                List<List<Object>> dataList = new ArrayList<>();
                //data.setData(r.nextInt(500));
                //放入数据到data 中
                for (int i = 0; i < 100; i++) {
                    List<Object> rowList = new ArrayList<Object>();
                    Object[] row = new Object[5];
                    row[0] = "from_"+i;
                    row[1] = "to_"+i;
                    row[2] = "gId_"+i;
                    row[3] = "aId_"+i;
                    row[4] = "s_version_"+i;
                    for(int j=0;j<row.length;j++){
                        rowList.add(row[j]);
                    }
                    dataList.add(rowList);
                }


                Main.lock.lock();
                if(queue.size() >= len) {
                    Main.empty.signalAll();
                    Main.full.await();
                }
                Thread.sleep(1000);
                queue.add(dataList);
                Main.lock.unlock();

                cdl.countDown();
                int i = ai.incrementAndGet();
                if (cdl.getCount()==0){
                    cdl.await();
                }

                System.out.println("生产者ID:"+Thread.currentThread().getId()+" 生产到了第"+i+"批次");
            }
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
