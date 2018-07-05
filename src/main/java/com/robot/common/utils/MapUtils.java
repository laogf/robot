package com.robot.common.utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 
 * Map处理工具类
 *
 * @Author 	Laogf
 * @Version 	1.0 
 * @Data 	2018年7月2日
 */
public class MapUtils {

	/**
	 * 取int最大值
	 */
	public static String getKeyByMaxIntValue(Map<String,Integer> map){
		List<Integer> list = new ArrayList<Integer>();
	    String returnKey = "";
        for (String temp : map.keySet()) {
            int value = map.get(temp);
            list.add(value);
        }
        int max = 0;
        for (int i = 0; i < list.size(); i++) {
            int size = list.get(i);
            max = (max>size)?max:size;
        }
        for (String key : map.keySet()) {
            if (max == map.get(key)) {
            	returnKey =  key;
            }
        }
        return returnKey;
	}
	
	
	/**
	 * 取int最大值的所有key-value
	 */
	public static Map<String,Integer> getMapByMaxIntValue(Map<String,Integer> map){
		List<Integer> list = new ArrayList<Integer>();
		Map<String,Integer> returnMap = new HashMap<String,Integer>();
        for (String temp : map.keySet()) {
            int value = map.get(temp);
            list.add(value);
        }
        int max = 0;
        for (int i = 0; i < list.size(); i++) {
            int size = list.get(i);
            max = (max>size)?max:size;
        }
        for (String key : map.keySet()) {
            if (max == map.get(key)) {
            	returnMap.put(key, max);
            }
        }
        return returnMap;
	}
	
	/**
	 * 取double最大值
	 */
	public static String getKeyByMaxDoubleValue(Map<String,Double> map){
		List<Double> list = new ArrayList<Double>();
	    String returnKey = "";
        for (String temp : map.keySet()) {
            double value = map.get(temp);
            list.add(value);
        }
        double max = 0;
        for (int i = 0; i < list.size(); i++) {
            double size = list.get(i);
            max = (max>size)?max:size;
        }
        for (String key : map.keySet()) {
            if (max == map.get(key)) {
            	returnKey =  key;
            }
        }
        return returnKey;
	}
	
	/**
	 * 将 Map 转换为 Stream 降序排序
	 */
	public static Map<String,Double> mapSort(Map<String,Double> map){
		Map<String,Double> result = map.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        return result;
	}
	
}
