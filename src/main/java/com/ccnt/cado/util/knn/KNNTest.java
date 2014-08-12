package com.ccnt.cado.util.knn;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class KNNTest {

    public void read(List<List<Double>> datas, String path){ 
        try { 
            BufferedReader br = new BufferedReader(new FileReader(new File(path))); 
            String data = br.readLine(); 
            List<Double> l = null; 
            while (data != null) { 
                String t[] = data.split(" "); 
                l = new ArrayList<Double>(); 
                for (int i = 0; i < t.length; i++) { 
                    l.add(Double.parseDouble(t[i])); 
                } 
                datas.add(l); 
                data = br.readLine(); 
            }
            br.close();
        } catch (Exception e) { 
            e.printStackTrace(); 
        } 
    } 
     

    public static void main(String[] args) { 
    	KNNTest t = new KNNTest(); 
        String datafile = t.getClass().getClassLoader().getResource("dataFile").toString().substring(6); 
        String testfile = t.getClass().getClassLoader().getResource("testFile").toString().substring(6); 
        try { 
            List<List<Double>> datas = new ArrayList<List<Double>>(); 
            List<List<Double>> testDatas = new ArrayList<List<Double>>(); 
            t.read(datas, datafile); 
            t.read(testDatas, testfile); 
            KNN knn = new KNN(); 
            for (int i = 0; i < testDatas.size(); i++) { 
                List<Double> test = testDatas.get(i); 
                System.out.print("测试元组: "); 
                for (int j = 0; j < test.size(); j++) { 
                    System.out.print(test.get(j) + " "); 
                } 
                System.out.print("类别为: "); 
                System.out.println(Math.round(Float.parseFloat((knn.knn(datas, test, 3))))); 
            } 
        } catch (Exception e) { 
            e.printStackTrace(); 
        } 
    } 
}
