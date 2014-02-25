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
package com.datumbox.opensource.classifiers;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.datumbox.opensource.examples.NaiveBayesExample;

/**
 * Implements a basic form of Multinomial Naive Bayes Text Classifier as
 * described at
 * http://blog.datumbox.com/machine-learning-tutorial-the-naive-bayes-text-classifier/
 *
 * @author Vasilis Vryniotis <bbriniotis at datumbox.com>
 * @see <a
 * href="http://blog.datumbox.com/developing-a-naive-bayes-text-classifier-in-java/">http://blog.datumbox.com/developing-a-naive-bayes-text-classifier-in-java/</a>
 */
public class NaiveBayes {

    public HashMap<String, HashMap<String, Integer>> clusterMap = new HashMap<String, HashMap<String, Integer>>();

    public void createTermFrequenyMap(String clusterName, String content[]) {
        HashMap<String, Integer> map = new HashMap<>();
        map.put(clusterName + "_count", content.length);
        for (String word : content) {
            Integer count = map.get(word);
            if (count == null) {
                map.put(word, 1);
            } else {
                map.put(word, ++count);
            }
        }
        clusterMap.put(clusterName, map);
    }

    public static int findTfForADoc(String wordToBeCounted, String docContent) {
        Pattern regEx = Pattern.compile("(^|\\s)" + wordToBeCounted + "(\\s|$)");
        Matcher matcher = regEx.matcher(docContent);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count;
    }
    public TreeMap<String, BigDecimal> treeMap = new TreeMap<String, BigDecimal>();
    public TreeMap<String, BigDecimal> treeMap1 = new TreeMap<String, BigDecimal>();
    public static BigDecimal calculateVal(double numerator, double denominator, int pow) {
//		MathContext ctxt = new MathContext(2,RoundingMode.HALF_UP);
        BigDecimal num = new BigDecimal(numerator);
        BigDecimal denom = new BigDecimal(denominator);
        BigDecimal res = num.divide(denom, 30, BigDecimal.ROUND_HALF_UP);
        res.setScale(30, BigDecimal.ROUND_HALF_UP);
        BigDecimal mul = new BigDecimal(100);
        res = res.multiply(mul);
        res = res.pow(pow);
        return res;
    }

    public void calculateClusterScoreForADoc(String docContent, int totLength) {
        LinkedHashMap<String, BigDecimal> map = new LinkedHashMap<>();
        for (int i = 0; i < NaiveBayesExample.Files.length; i++) {
            HashMap<String, Integer> dataMap = clusterMap.get(NaiveBayesExample.Files[i]);
            Set<String> docContentSet = new HashSet<>();
            String[] docContentAry = docContent.split(" ");
            Collections.addAll(docContentSet, docContent.split(" "));
//			System.out.println(termMap.keySet());
            int clusterVocabSize = dataMap.get(NaiveBayesExample.Files[i] + "_count");
            BigDecimal soln = new BigDecimal(1.0);
            soln.setScale(2, BigDecimal.ROUND_HALF_UP);
            BigDecimal soln1 = new BigDecimal(0.0);
            soln1.setScale(2, BigDecimal.ROUND_HALF_UP);
            for (String dummy : docContentSet) {
                try {
                    int count = findTfForADoc(dummy, docContent);
                    Integer str = dataMap.get(dummy);
                    int clusterTermCnt = 0;
                    if (str != null) {
                        clusterTermCnt = str;
                    }
//                    soln += (Math.log((double) (clusterTermCnt + 1) / (double) (clusterVocabSize + docContentAry.length)) * count);
                    BigDecimal val = calculateVal((double) (clusterTermCnt + 1), (double) (clusterVocabSize + docContentAry.length), count);
                    soln = soln.multiply(val);
                    if(clusterTermCnt != 0)
                    {BigDecimal val1 = calculateVal((double) (count), (double) (docContentAry.length), 1);
                    val1.setScale(5,RoundingMode.HALF_UP);
                    soln1 = soln1.add(val1,new MathContext(5));
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Exception occured");
                }
            }
//            BigDecimal soln1 = new BigDecimal(Math.pow(Math.E, soln));
////            soln1.setScale(100000, BigDecimal.ROUND_HALF_UP);
//            System.out.println("soln1" + soln1.toString());
            treeMap.put(NaiveBayesExample.Files[i], soln);
            treeMap1.put(NaiveBayesExample.Files[i], soln1);
        }
    }
}
