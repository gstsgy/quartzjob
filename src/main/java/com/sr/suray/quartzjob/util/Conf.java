package com.sr.suray.quartzjob.util;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.*;


public class Conf {

    private final static DumperOptions OPTIONS = new DumperOptions();

    private static File file;

    private static InputStream ymlInputSteam;

    private static Object CONFIG_MAP;

    private static Yaml yaml;

    static {
        //将默认读取的方式设置为块状读取
        OPTIONS.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        // 设置配置文件地址
        file = new File(Objects.requireNonNull(Conf.class.getClassLoader().getResource("application.yml")).getFile());
        if (ymlInputSteam == null) {
            try {
                setYmlInputSteam(new FileInputStream(file));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 使用其他方法之前必须调用一次 设置yml的输入流
     *
     * @param inputSteam 输入流
     */
    public static void setYmlInputSteam(InputStream inputSteam) {
        ymlInputSteam = inputSteam;
        yaml = new Yaml(OPTIONS);
        CONFIG_MAP = yaml.load(ymlInputSteam);
    }

    /**
     * 根据键获取值
     *
     * @param key 键
     * @return 查询到的值
     */
    @SuppressWarnings("unchecked")
    public static Object getByKey(String key) {
        if (ymlInputSteam == null) {
            return null;
        }
        String[] keys = key.split("\\.");
        Object configMap = CONFIG_MAP;
        for (String s : keys) {
            if (configMap instanceof Map) {
                configMap = ((Map<String, Object>) configMap).get(s);
            } else {
                break;
            }
        }
        return configMap == null ? "" : configMap;
    }
    public static Object getall(){

        return CONFIG_MAP;
    }

    public static void saveOrUpdateByKey(Map<String,Object> map) {
        map.keySet().forEach(item->{
            Object value = map.get(item);
            KeyAndMap keyAndMap = new KeyAndMap(item).invoke();
            item = keyAndMap.getKey();
            Map<String, Object> map1 = keyAndMap.getMap();
            map1.put(item, value);
        });
        //将数据重新写回文件
        try {
            yaml.dump(CONFIG_MAP, new FileWriter(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void removeByKey(String key) throws Exception {
        KeyAndMap keyAndMap = new KeyAndMap(key).invoke();
        key = keyAndMap.getKey();
        Map<String, Object> map = keyAndMap.getMap();
        Map<String, Object> fatherMap = keyAndMap.getFatherMap();
        map.remove(key);
        if (map.size() == 0) {
            Set<Map.Entry<String, Object>> entries = fatherMap.entrySet();
            for (Map.Entry<String, Object> entry : entries) {
                if (entry.getValue() == map) {
                    fatherMap.remove(entry.getKey());
                }
            }
        }
        yaml.dump(CONFIG_MAP, new FileWriter(file));
    }

   /* public static void main(String[] args) {
        String key = "log.level";
        Integer value = 1;
        saveOrUpdateByKey(key,value);

        System.out.println(getall());
    }*/

    private static class KeyAndMap {
        private String key;
        private Map<String, Object> map;
        private Map<String, Object> fatherMap;

        public KeyAndMap(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }

        public Map<String, Object> getMap() {
            return map;
        }

        public Map<String, Object> getFatherMap() {
            return fatherMap;
        }

        @SuppressWarnings("unchecked")
        public KeyAndMap invoke() {
            if (file == null) {
                System.err.println("请设置文件路径");
            }
            if (null == CONFIG_MAP) {
                CONFIG_MAP = new LinkedHashMap<>();
            }
            String[] keys = key.split("\\.");
            key = keys[keys.length - 1];
            map = (Map<String, Object>) CONFIG_MAP;
            for (int i = 0; i < keys.length - 1; i++) {
                String s = keys[i];
                if (map.get(s) == null || !(map.get(s) instanceof Map)) {
                    map.put(s, new HashMap<>(4));
                }
                fatherMap = map;
                map = (Map<String, Object>) map.get(s);
            }
            return this;
        }
    }
}
