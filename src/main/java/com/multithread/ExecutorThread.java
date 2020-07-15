package com.multithread;

import com.util.Constant;
import com.util.DiveDeeperUtil;
import com.util.PomUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


/*
*线程类实现Runnable接口
*/
public class ExecutorThread implements Runnable {
    private List<Object> obj;
    private int delay;

    public ExecutorThread(List<Object> obj, int delay) {
        this.obj = obj;
        this.delay = delay;
    }
    long startTime = System.currentTimeMillis();
    public void run() {
          /*
       *这里就是线程需要处理的业务了，可以换成自己的业务
       */
        try {
            if(obj != null) {
                // 设置表格头
                //Object[] head = {"序号","小说名称","作者","出版日期"};
                Object[] head = {"from","to","groupId","artifactId","version"};
                List<Object> headList = Arrays.asList(head);

                System.out.println("-------------- START TO GET DEPEND_ON DATA --------------");
                //List<List<Object>> dataList = generateDependency("/Users/xuzhijie/Documents/small_poms" , Constant.DEPEND_ON_ALL);
                List<List<Object>> dataList = generateDependency("E:\\poms" , Constant.DEPEND_ON_ALL);
                System.out.println("-------------- END TO  GET  DEPEND_ON DATA --------------");
                //List<List<Object>> dataList = getNovel(Integer.parseInt(obj.get(1).toString()),Integer.parseInt(obj.get(2).toString()));

                // 导出文件路径
                String downloadFilePath = "D:" + File.separator + "daily" + File.separator;
                // 导出文件名称
                System.out.println("运行的线程数"+Thread.activeCount()+"----------------"+obj.get(0).toString()+"-----------"+obj.get(2).toString());
                //String fileName = "depend_"+i;
                String  fileName = obj.get(0).toString();
                // 导出CSV文件
                File csvFile = CSVUtils.createCSVFile(headList, dataList, downloadFilePath, fileName);
                long endTime = System.currentTimeMillis();
            }
            Thread.sleep(delay);
        } catch (InterruptedException ignored) {
        }
        long endTime = System.currentTimeMillis();
        System.out.println("分批CSV导出"+(endTime-startTime));
    }

    private static List<List<Object>> generateDependency(String filePath,String type) {
        System.out.println("===========generateDependency==========");

        //需要组装得到这样形式的数据
        /*
        from to to{groupId artifactId version}
         */
        List<List<Object>> dataList = new ArrayList<List<Object>>();

        //get 3 levels deeper folders
        final List<File> files = DiveDeeperUtil.getDeeper3LevelFiles(filePath);

        List<File> allPomFiles = new ArrayList<>();
        for (File file : files) {
            List<File> pomFiles = new ArrayList<>();
            PomUtil.recur(file,pomFiles);
            allPomFiles.addAll(pomFiles);
        }
        System.out.println("allPomFiles.size = "+allPomFiles.size());

        for (File file : allPomFiles) {
            List<List<Object>> subDataList = PomUtil.extractData(file,type);
            dataList.addAll(subDataList);
        }


        /*List<Object> rowList = null;
        for (int i = 0; i < pagesize; i++) {
            rowList = new ArrayList<Object>();
            Object[] row = new Object[4];
            int endCount = startCount+i;
            row[0] = endCount;
            row[1] = "风云第一刀"+endCount+"";
            row[2] = "古龙"+endCount+"";
            row[3] = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            for(int j=0;j<row.length;j++){
                rowList.add(row[j]);
            }
            dataList.add(rowList);
        }*/
        return dataList;
    }

    /*private static List<List<Object>> getNovel(int startCount,int pagesize) {
        List<List<Object>> dataList = new ArrayList<List<Object>>();
        List<Object> rowList = null;
        for (int i = 0; i < pagesize; i++) {
            rowList = new ArrayList<Object>();
            Object[] row = new Object[4];
            int endCount = startCount+i;
            row[0] = endCount;
            row[1] = "风云第一刀"+endCount+"";
            row[2] = "古龙"+endCount+"";
            row[3] = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            for(int j=0;j<row.length;j++){
                rowList.add(row[j]);
            }
            dataList.add(rowList);
        }
        return dataList;
    }*/
}
