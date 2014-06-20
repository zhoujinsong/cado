package com.ccnt.cado.util.knn;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class KNN {

    private Comparator<KNNNode> comparator = new Comparator<KNNNode>() { 
        public int compare(KNNNode o1, KNNNode o2) { 
            if (o1.getDistance() >= o2.getDistance()) { 
                return 1; 
            } else { 
                return 0; 
            } 
        } 
    }; 

    public List<Integer> getRandKNum(int k, int max) { 
        List<Integer> rand = new ArrayList<Integer>(k); 
        for (int i = 0; i < k; i++) { 
            int temp = (int) (Math.random() * max); 
            if (!rand.contains(temp)) { 
                rand.add(temp); 
            } else { 
                i--; 
            } 
        } 
        return rand; 
    } 

    public double calDistance(List<Double> d1, List<Double> d2) { 
        double distance = 0.00; 
        for (int i = 0; i < d1.size(); i++) { 
            distance += (d1.get(i) - d2.get(i)) * (d1.get(i) - d2.get(i)); 
        } 
        return distance; 
    } 

    public String knn(List<List<Double>> datas, List<Double> testData, int k) { 
        PriorityQueue<KNNNode> pq = new PriorityQueue<KNNNode>(k, comparator); 
        List<Integer> randNum = getRandKNum(k, datas.size()); 
        for (int i = 0; i < k; i++) { 
            int index = randNum.get(i); 
            List<Double> currData = datas.get(index); 
            String c = currData.get(currData.size() - 1).toString(); 
            KNNNode node = new KNNNode(index, calDistance(testData, currData), c); 
            pq.add(node); 
        } 
        for (int i = 0; i < datas.size(); i++) { 
            List<Double> t = datas.get(i); 
            double distance = calDistance(testData, t); 
            KNNNode top = pq.peek(); 
            if (top.getDistance() > distance) { 
                pq.remove(); 
                pq.add(new KNNNode(i, distance, t.get(t.size() - 1).toString())); 
            } 
        } 
         
        return getMostClass(pq); 
    } 

    private String getMostClass(PriorityQueue<KNNNode> pq) { 
        Map<String, Integer> classCount = new HashMap<String, Integer>(); 
        for (int i = 0; i < pq.size(); i++) { 
            KNNNode node = pq.remove(); 
            String c = node.getC(); 
            if (classCount.containsKey(c)) { 
                classCount.put(c, classCount.get(c) + 1); 
            } else { 
                classCount.put(c, 1); 
            } 
        } 
        int maxIndex = -1; 
        int maxCount = 0; 
        Object[] classes = classCount.keySet().toArray(); 
        for (int i = 0; i < classes.length; i++) { 
            if (classCount.get(classes[i]) > maxCount) { 
                maxIndex = i; 
                maxCount = classCount.get(classes[i]); 
            } 
        } 
        return classes[maxIndex].toString(); 
    } 
}
