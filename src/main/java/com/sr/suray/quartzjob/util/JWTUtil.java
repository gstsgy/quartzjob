package com.sr.suray.quartzjob.util;

import com.alibaba.fastjson.JSON;
import com.sr.suray.quartzjob.bean.JobToken;

public class JWTUtil {
    private static String publicKey;
    private static String priavteKey;

    // 过期时间30分钟
    private static final long EXPIRE_TIME = 30 * 60 * 1000;


    static {
        publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC2MLcrSoBiHvi2NbDP5ruMymmBOVbY0OlcGEo8szy2QIEFiNLNeCcOggy+M9A6zjy6bdT/HUHz2FfHKG4wU8F88wishFe3ThFu079z6r8Bkde9nngexV3I6SrryKJaDNWXLrWPizLRDXLNZadT0Xm7vBGF19Y6sOv+/iLkeoVyXQIDAQAB";
        priavteKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBALYwtytKgGIe+LY1sM/mu4zKaYE5VtjQ6VwYSjyzPLZAgQWI0s14Jw6CDL4z0DrOPLpt1P8dQfPYV8cobjBTwXzzCKyEV7dOEW7Tv3PqvwGR172eeB7FXcjpKuvIoloM1ZcutY+LMtENcs1lp1PRebu8EYXX1jqw6/7+IuR6hXJdAgMBAAECgYBCxOO8F9epHhSkIlUkPDkrxdqYozzyxM1hFP3P9Rgg6s7eCmDHMGuP45Vr920uv/p9kFbpD/3lsbmpoWWQjIFx/NHI1kBUj9ZKI6qhMgNgwmNxabFG1NwX/jzAWjRtdw8e2D78y5Rq7LBcGgDGQwZXhSMKgbz5CPF5Dig9LQwneQJBAOPVT+kFFPxhIIK7ilpvlIh3fDJsQ6Jls31iJzWt2P8cZDMs2/37yRwEoVyfG7VQd7xt79WjYJk6Yqyf/jW5X1sCQQDMttnNmRRHo59SpgWGdJ3QLe1ra7bqvtDyOY05WZgwOt+jc9eHfS5YW65zcF92xljqnTdEGJoevpJTEzQmCxqnAkEAt5CPkNyCWxHagtqdj13lW8qBa5LlL3wnkLc7hjlq46i5Zq4XFdz+3S8x35FmXsDVqCEfoo6+7tYm2JUT2a9gjwJBAL3uF/2MvSgKQtNnfVDV+WUgd1pmwXriNvHFO6Wt3mlA9iSsYZcvKUSJKz2nUKreM7jYzGG9gaBsp020T3nMa5kCQAGlxVrq1AiLkMd9s9elPqRt+Xqeq/zYubstPPb26uNbDulDUtUB1SVdXlA98ex6JpB5jJnV+8e7txrHTIBg+Zs=";
    }

    public static void main(String[] args) {
        String str = "123";
        try {
            System.out.println(Encrypt.decryptToRSA(Encrypt.encryptToRSA(str, publicKey), priavteKey));
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public static String getToken(JobToken jobToken) {
        if (jobToken == null) {
            jobToken = new JobToken("admin", System.currentTimeMillis());
        }
        return Encrypt.encryptToRSA(JSON.toJSONString(jobToken), publicKey);
    }

    public static boolean verify(String token) {

        try {
            //System.out.println(Encrypt.decryptToRSA(token, priavteKey));
            JobToken jobToken =  JSON.parseObject(Encrypt.decryptToRSA(token, priavteKey),JobToken.class);
            System.out.println(jobToken);
            if (jobToken != null && "admin".equals(jobToken.getUserName())) {
                if (System.currentTimeMillis() - jobToken.getMsgTime() < EXPIRE_TIME) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
