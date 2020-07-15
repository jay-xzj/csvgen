package com.consume2;

import com.sun.org.apache.xalan.internal.xsltc.dom.SortingIterator;
import com.util.Constant;
import com.util.DiveDeeperUtil;
import com.util.PomUtil;

import java.io.File;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Main2 {
    public static ReentrantLock lock = new ReentrantLock();
    public static Condition empty = lock.newCondition();
    public static Condition full = lock.newCondition();
    public static Boolean lastBatch = Boolean.FALSE;

    public static void main(String[] args) throws InterruptedException {
        //final List<File> files = DiveDeeperUtil.getDeeper3LevelFiles("E:\\poms");
        final List<File> files = DiveDeeperUtil.getDeeper3LevelFiles("/Users/xuzhijie/Documents/small_poms");
        List<File> allPomFiles = new ArrayList<>();
        for (File file : files) {
            List<File> pomFiles = new ArrayList<>();
            PomUtil.recur(file,pomFiles);
            allPomFiles.addAll(pomFiles);
        }
        System.out.println("allPomFiles.size = "+allPomFiles.size());

        //Thread.sleep(2000);
        List<List<List<Object>>> queue = new ArrayList<>();
        int length = 1;
        Producer2 p1 = new Producer2(queue,length, Constant.DEPEND_ON_ALL,allPomFiles);
        Consumer2 c1 = new Consumer2(queue,length,Constant.DEPEND_ON_ALL);
        //Thread pt1 = new Thread(p1,"P1");
        //Thread ct1 = new Thread(c1, "C1");
        //ExecutorService service = Executors.newCachedThreadPool();
        ExecutorService service = Executors.newFixedThreadPool(2);
        service.execute(p1);
        Thread.sleep(100);
        service.execute(c1);
        //service.shutdown();
        if (!service.awaitTermination(30, TimeUnit.SECONDS)) {
            service.shutdownNow();
            if (!service.awaitTermination(20,TimeUnit.SECONDS))
                service.shutdownNow();
        }
    }
}