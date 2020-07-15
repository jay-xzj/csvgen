package com.consume2;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Main2 {
    public static ReentrantLock lock = new ReentrantLock();
    public static Condition empty = lock.newCondition();
    public static Condition full = lock.newCondition();
    public static void main(String[] args) throws InterruptedException {
        Thread.sleep(20000);
        /*List<List<List<Object>>> queue = new ArrayList<>();
        int length = 1;
        Producer2 p1 = new Producer2(queue,length);
        Consumer2 c1 = new Consumer2(queue);
        ExecutorService service = Executors.newCachedThreadPool();
        service.execute(p1);
        service.execute(c1);*/
        System.out.println(System.lineSeparator());
        //System.out.println(File.separator());
    }
}