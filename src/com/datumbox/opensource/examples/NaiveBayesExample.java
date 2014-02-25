/* 
 * Copyright (C) 2014 Vasilis Vryniotis <bbriniotis at datumbox.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.datumbox.opensource.examples;

import com.datumbox.opensource.classifiers.NaiveBayes;
import com.datumbox.opensource.dataobjects.NaiveBayesKnowledgeBase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author Vasilis Vryniotis <bbriniotis at datumbox.com>
 * @see <a
 * href="http://blog.datumbox.com/developing-a-naive-bayes-text-classifier-in-java/">http://blog.datumbox.com/developing-a-naive-bayes-text-classifier-in-java/</a>
 */
public class NaiveBayesExample {
    public static String Files[] = new String[]{"UI", "Network", "ImageProcessing", "Embedded", "Database"};
    /**
     * Reads the all lines from a file and places it a String array. In each
     * record in the String array we store a training example text.
     *
     * @param url
     * @return
     * @throws IOException
     */
    public static String[] readLines(String url) throws IOException {

        BufferedReader bufferedReader = new BufferedReader(new FileReader(url));
        List<String> lines = null;
        try {
            lines = new ArrayList();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                lines.add(line);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return lines.toArray(new String[lines.size()]);

    }

    public static String readString(String url) throws IOException {
        List<String> lines;
        StringBuilder builder = null;
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(url));
            lines = new ArrayList();
            builder = new StringBuilder();
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                builder.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

    /**
     * Main method
     *
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static NaiveBayes nb = new NaiveBayes();
    public static HashMap<String, BigDecimal> maxProbClust = new HashMap<String, BigDecimal>();
    public static HashMap<String, BigDecimal> finalMaxProbClust = new HashMap<String, BigDecimal>();
    public static String doPreprocessing(String content) {
        content = content.toLowerCase().replaceAll("[^A-Za-z ]", "");
        content = content.trim().replaceAll(" +", " ");
//        for (String str : Configuration.stopWords) {
//            content = content.replaceAll("(^|\\s)" + str + "(\\s|$)", " ");
//        }
        content = content.trim();
        return content;
    }
    public void init() throws IOException {
        Map<String, String> trainingFiles = new HashMap();
        trainingFiles.put("Embedded", "D:\\datasets\\embedded.txt");
        trainingFiles.put("Database", "D:\\datasets\\database_latest.txt");
        trainingFiles.put("UI", "D:\\datasets\\data of UI.txt");
        trainingFiles.put("Network", "D:\\datasets\\Data of Network.txt");
        trainingFiles.put("ImageProcessing", "D:\\datasets\\data of Image.txt");
        //loading examples in memory
        int totLength = 0;
        Map<String, String[]> trainingExamples = new HashMap();
        for (Map.Entry<String, String> entry : trainingFiles.entrySet()) {
            String ary[] = doPreprocessing(readString(entry.getValue())).split(" ");
            totLength += ary.length;
            nb.createTermFrequenyMap(entry.getKey(), ary);
        }

        //train classifier
//        nb.setChisquareCriticalValue(6.63); //0.01 pvalue
//        nb.train(trainingExamples);
//        //get trained classifier knowledgeBase
//        NaiveBayesKnowledgeBase knowledgeBase = nb.getKnowledgeBase();
////        System.out.println(knowledgeBase);
//        nb = null;
//        trainingExamples = null;
//        nb = new NaiveBayes(knowledgeBase);

        String exampleEn = "";
        double maxProb = 0;

        for (int i = 0; i < Files.length; i++) {
//            for (int j = 1; j < 6; j++) {
                exampleEn = "";
                BufferedReader br = new BufferedReader(new FileReader("D:\\datasets\\Data\\" + Files[i] + "\\" + 1 + ".txt"));
                String temp = "";
                while ((temp = br.readLine()) != null) {
                    exampleEn = exampleEn + temp;

                }
                exampleEn = doPreprocessing(exampleEn);
                nb.calculateClusterScoreForADoc(exampleEn, totLength);
                LinkedList<Map.Entry<String, BigDecimal>> list = new LinkedList<Map.Entry<String, BigDecimal>>(nb.treeMap1.entrySet());
                Collections.sort(list, new Comparator<Map.Entry<String, BigDecimal>>() {
                    @Override
                    public int compare(Map.Entry<String, BigDecimal> t, Map.Entry<String, BigDecimal> t1) {
                        return t1.getValue().compareTo(t.getValue());
                    }

                });
                Map.Entry<String, BigDecimal> uselssMap = list.pollFirst();
                finalMaxProbClust.put(uselssMap.getKey(), uselssMap.getValue());
//                String[] strAry = exampleEn.split(" ");
//                double compare = Double.valueOf(outputEn.substring(outputEn.indexOf(',') + 1));
//                if (j == 1) {
//                    maxProb = compare;
//                } else {
//                    if (compare > maxProb) {
//                        maxProb = compare / strAry.length;
//                    }
//                }

//            }
        }
//        System.out.println("Maxprobcluse : " + maxProbClust);
//        maxProbClust = new HashMap<>();
    }

    public static void main(String[] args) throws IOException {

        NaiveBayesExample ex = new NaiveBayesExample();
        ex.init();
        ex.doCalculate("D:\\datasets\\Data\\Database\\test.txt");
//        ex.doCalculate();
//        //map of dataset files
//        //Use classifier
//        NaiveBayesExample ex = new NaiveBayesExample();
//        ex.init();
////        System.out.println(nb.getKnowledgeBase());
//        String exampleEn =  readString("test.txt");
//        String outputEn = nb.predict(exampleEn,false);
//        LinkedList<Map.Entry<String,Double>> list = new LinkedList<Map.Entry<String,Double>>(nb.rankingMap.entrySet());
//                    Collections.sort(list, new Comparator<Map.Entry<String,Double>>() {
//                        @Override
//                        public int compare(Map.Entry<String, Double> t, Map.Entry<String, Double> t1) {
//                            return t1.getValue().compareTo(t.getValue());
//                        }
//
//                    });
//        System.out.println("Final list "+list);
//        TreeMap<String, Double> finalPercMap = new TreeMap<String, Double>();
//        for(Map.Entry<String,Double> temp : list)
//        {
//            System.out.println("Key : "+temp.getKey());
//            double perc = ex.calculateProbability(temp.getValue(),ex.maxProbClust.get(temp.getKey()));
//            System.out.println("Percentage : "+perc);
//            finalPercMap.put(temp.getKey(), perc);
//        }
//        LinkedList<Map.Entry<String,Double>> list1 = new LinkedList<Map.Entry<String,Double>>(finalPercMap.entrySet());
//                    Collections.sort(list, new Comparator<Map.Entry<String,Double>>() {
//                        @Override
//                        public int compare(Map.Entry<String, Double> t, Map.Entry<String, Double> t1) {
//                            return t.getValue().compareTo(t1.getValue());
//                        }
//
//                    });
//        String json = "{";
//        if(!list.getFirst().getKey().equalsIgnoreCase(list1.getFirst().getKey()))
//        {
//            list1.removeFirst();
//            for(Map.Entry<String,Double> entry : list1)
//            {
//                if(json.length() == 1)
//                    json = json +entry.getKey()+":"+entry.getValue();
//                else
//                    json += ","+entry.getKey()+":"+entry.getValue();
//            }
//           json += "}";
//        }
//        else
//        {
//            Map.Entry<String,Double> dummy = list1.removeFirst();
//            json += dummy.getKey()+":"+dummy.getValue()+"}";
//        }
//        return json;
    }

    public String doCalculate(String filePath) throws IOException {
        //map of dataset files
        //Use classifier
        NaiveBayesExample ex = new NaiveBayesExample();
//        ex.init();
//        System.out.println(nb.getKnowledgeBase());
        String exampleEn = readString(filePath);
        exampleEn = doPreprocessing(exampleEn);
        nb.calculateClusterScoreForADoc(exampleEn, 0);
        LinkedList<Map.Entry<String, BigDecimal>> list = new LinkedList<Map.Entry<String, BigDecimal>>(nb.treeMap.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, BigDecimal>>() {
            @Override
            public int compare(Map.Entry<String, BigDecimal> t, Map.Entry<String, BigDecimal> t1) {
                return t1.getValue().compareTo(t.getValue());
            }

        });
        TreeMap<String, BigDecimal> finalPercMap = new TreeMap<String, BigDecimal>();
        for (Map.Entry<String, BigDecimal> temp : list) {
            System.out.println("Key : " + temp.getKey());
            BigDecimal perc = ex.calculateProbability(nb.treeMap1.get(temp.getKey()), ex.finalMaxProbClust.get(temp.getKey()));
            System.out.println("Percentage : " + perc);
            finalPercMap.put(temp.getKey(), perc);
        }
        
        LinkedList<Map.Entry<String, BigDecimal>> list1 = new LinkedList<Map.Entry<String, BigDecimal>>(finalPercMap.entrySet());
        Collections.sort(list1, new Comparator<Map.Entry<String, BigDecimal>>() {
            @Override
            public int compare(Map.Entry<String, BigDecimal> t, Map.Entry<String, BigDecimal> t1) {
                return t1.getValue().compareTo(t.getValue());
            }

        });
        String json = "{";
//        if (!list.getFirst().getKey().equalsIgnoreCase(list1.getFirst().getKey())) {
//            list1.removeFirst();
            for (Map.Entry<String, BigDecimal> entry : list1) {
                if (json.length() == 1) {
                    json = json + "'"+entry.getKey() + "'"+":" + entry.getValue();
                } else {
                    json += "," + "'"+entry.getKey() +"'"+ ":" + entry.getValue();
                }
            }
            json += "}";
//        } else {
//            Map.Entry<String, BigDecimal> dummy = list1.removeFirst();
//            json += "'"+dummy.getKey() + "'"+" :"+dummy.getValue() + "}";
//        }
        System.out.println("json; " + json);
        return json;
    }

    BigDecimal calculateProbability(BigDecimal compareProb, BigDecimal maxProb) {
    	System.out.println(compareProb);
    	System.out.println(maxProb);
    	 BigDecimal res = compareProb.divide(maxProb, 2, BigDecimal.ROUND_HALF_UP);
         res.setScale(2, BigDecimal.ROUND_HALF_UP);
         return res.multiply(new BigDecimal(100));
//           return ((compareProb - maxProb)/-maxProb)*100;
//        if (Math.abs(compareProb) > Math.abs(maxProb)) {
//            Double temp = compareProb;
//            compareProb = maxProb;
//            maxProb = temp;
//        }
//        return ((compareProb * 100) / maxProb);
    }
}
