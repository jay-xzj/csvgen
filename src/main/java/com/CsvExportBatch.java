package com;

import com.multithread.CSVUtils;
import com.multithread.ZipUtil;
import com.util.Constant;
import com.util.DiveDeeperUtil;
import com.util.PomUtil;

import java.io.File;
import java.io.IOException;
import java.util.*;
/**
 * CSV导出之大量数据-导出压缩包
 *
 */
public class CsvExportBatch{

    public static void main(String[] args) throws IOException {

        //String s = "/Users/xuzhijie/Documents/small_poms/club/minnced/opus-java-api/1.0.4/opus-java-api-1.0.4.pom";
        //下面三个方法参数都是 Object类型

        //计算指定对象及其引用树上的所有对象的综合大小，单位字节
        /*long a =  RamUsageEstimator.sizeOf(s);
        System.out.println(a);//232

        File file = new File(s);
        long a1 = RamUsageEstimator.sizeOf(file);
        System.out.println(a1);//264

        //计算指定对象本身在堆空间的大小，单位字节
        long b = RamUsageEstimator.shallowSizeOf(s);
        System.out.println(b);

        //计算指定对象及其引用树上的所有对象的综合大小，返回可读的结果，如：2KB
        String c = RamUsageEstimator.humanSizeOf(s);
        System.out.println(c);*/


        long startTime = System.currentTimeMillis();
        // 设置表格头
        Object[] head = {"from","to","groupId","artifactId","version"};
        List<Object> headList = Arrays.asList(head);

        System.out.println("-------------- START TO GET DEPEND_ON DATA --------------");
        //List<List<Object>> dataList = generateDependency("/Users/xuzhijie/Documents/small_poms" , Constant.DEPEND_ON_ALL);
        List<List<Object>> dataList = generateDependency("E:\\poms" , Constant.DEPEND_ON_ALL);
        System.out.println("-------------- END TO  GET  DEPEND_ON DATA --------------");

        /*System.out.println("-------------- START TO GET ARTIFACT DATA --------------");
        List<List<Object>> dataList = getNovel(startCount, pageSize, "E:\\poms" );
        System.out.println("-------------- END TO GET ARTIFACT DATA --------------");*/


        // 设置数据
        int listCount =dataList.size();
        //导出6万以上数据。。。
        int pageSize= 1_000_000;//设置每一个excel文件导出的数量
        //int pageSize= 10;//设置每一个excel文件导出的数量
        int batchNum = listCount / pageSize + (listCount % pageSize == 0?0:1);//共有多少个批次

        System.out.println("++++++++  batchNum= "+batchNum+"  +++++++++");
        System.out.println("----------START assembly batches------------");
        List<List<List<Object>>> batches = new ArrayList<>();
        List<List<Object>> ds = new ArrayList<>();
        for(int i = 0; i < listCount; i++){
            List<Object> list = dataList.get(i);
            ds.add(list);
            if ((i+1) % pageSize == 0) {
                List<List<Object>> newds = new ArrayList<>(ds);
                batches.add(newds);
                ds.clear();
            }else if ((i+1) == pageSize) {
                List<List<Object>> newds = new ArrayList<>(ds);
                batches.add(newds);
                ds.clear();
            }
        }

        //要压缩的csv文件
        List<File> srcfile=new ArrayList<File>();

        for(int i=0;i<batchNum;i++){
            List<List<Object>> lists = batches.get(i);
            // 导出文件路径
            //String downloadFilePath = "D:" + File.separator + "daily" + File.separator;
            String downloadFilePath = "D:\\dev\\neo4j_data\\neo4jDatabases\\database-7665b2bb-35ee-4797-8011-6068d7c8edd4\\installation-3.5.14\\import\\";
            //String downloadFilePath = "/Users/xuzhijie/Documents/small_poms";
            // 导出文件名称
            String fileName = "depend_"+i;
            // 导出CSV文件
            File csvFile = CSVUtils.createCSVFile(headList, lists, downloadFilePath, fileName);
            srcfile.add(csvFile);
        }
        ZipUtil.zipFiles(srcfile, new File("D:\\daily\\download.zip"));
        //ZipUtil.zipFiles(srcfile, new File("/Users/xuzhijie/Documents/download.zip"));
        //ZipUtil.dropFolderOrFile(new File("D:\\daily\\download"));
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
}