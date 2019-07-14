package com.honeybee;

import java.util.HashMap;
import java.util.Map;

/**
 * 将敏感词在校验语句中完成替换 如 nice 替换成XXXX sun替换成XXX
 * list4提供键值对形式的替换
 */
public class EvilCorp {

    // 敏感词
    static String[] list1 = {"nice"};
    static String[] list2 = {"nice", "sun", "happy"};
    static String[] list3 = {"friend"};
    static HashMap<String, String> list4 = new HashMap<>();
    static String[] list5 = {"happy"};

    // 校验语句
    static String text1 = "You are a nice person";
    static String text2 = "Such a nice day with a bright sun,makes me happy";
    static String text3 = "You are so friendly!";
    static String text4 = "Objection is bad, a better thing to do, is to agree";
    static String text5 = "Are you unhappy today? ";

    public String test(String list[], String text, HashMap list4) {
        //模糊查询字段
        String[] keyLike = list;

        //
        HashMap<String, String> map = new HashMap<>();
        String[] split = text.split("\\s");
        // 分割字符串，按单词分割
        for (int i = 0; i < split.length; i++) {
            //如果有逗号，则再次分割
            if (split[i].indexOf(",") > -1) {
                String[] split1 = split[i].split(",");
                for (int j = 0; j < split1.length; j++) {

//                System.out.println("-----"+split1[1] +"---" +split1[0]);
                    map.put(split1[j], split1[j]);
                }
            } else {
                map.put(split[i], split[i]);
            }
//            System.out.println(keyLike);
//            System.out.println(split[i]);
        }
        //要替换的字段
        String replace = "X";
        //正则
        String regExp = "\\w";
        // 执行替换
        for (Map.Entry<String, String> entity : map.entrySet()) {
            if(list4!=null){
                replace = (String) list4.get(entity.getKey());
                regExp = entity.getKey();
            }
            for (int i = 0; i < keyLike.length; i++) {
                if(keyLike[i] == null){
                    break;
                }
                if (entity.getKey().indexOf(keyLike[i]) > -1) {
                    String x = entity.getKey().replaceAll(regExp, replace);
                    text = text.replaceAll(entity.getKey(), x);
//                    System.out.println(keyLike[i]);
//                    System.out.println(entity.getKey());
//                System.out.println(x);
//                System.out.println(result);
                }
            }

        }
        return text;
    }

    public static void main(String[] args) {
        list4.put("bad", "ungood");
        list4.put("better", "gooder");
        list4.put("agree", "crimestop");
        list4.put("Objection", "thoughtcrime");

        EvilCorp e = new EvilCorp();
        System.out.println("第1小题答案："+e.test(list1, text1,null));
        System.out.println("第2小题答案："+e.test(list2, text2,null));
        System.out.println("第3小题答案："+e.test(list3, text3,null));
        System.out.println("第4小题答案："+e.test(list4.keySet().toArray(new String[20] ),text4,list4));
        System.out.println("第5小题答案："+e.test(list5, text5,null));
    }
}
