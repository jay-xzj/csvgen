package com.consume;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Main {
    public static ReentrantLock lock = new ReentrantLock();
    public static Condition empty = lock.newCondition();
    public static Condition full = lock.newCondition();
    public static void main(String[] args) {
        List<List<List<Object>>> queue = new ArrayList<>();
        int length = 2;
        Producer p1 = new Producer(queue,length);
        //Producer p2 = new Producer(queue,length);
        //Producer p3 = new Producer(queue,length);
        Consumer c1 = new Consumer(queue);
        //Consumer c2 = new Consumer(queue);
        //Consumer c3 = new Consumer(queue);
        ExecutorService service = Executors.newCachedThreadPool();
        service.execute(p1);
        //service.execute(p2);
        //service.execute(p3);
        service.execute(c1);
        //service.execute(c2);
        //service.execute(c3);
    }
}