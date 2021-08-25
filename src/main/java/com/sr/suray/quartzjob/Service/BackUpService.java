package com.sr.suray.quartzjob.Service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sr.suray.quartzjob.bean.TaskInfo;
import com.sr.suray.quartzjob.mapper.TaskInfoMapper;
import com.sr.suray.quartzjob.util.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author guyue
 * @version 3.0
 * @description: TODO
 * @date 2021/8/16 上午11:37
 */
@Service
public class BackUpService {

    private String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCWX9hhWbNboFMoEPNKhiCQkII4ggxWBsB+bMc+Rd3jlf+Jov9mM3oGhbNE6yFnxHzZlowPgzayXGNSanNDz3Zu8R02K2zSQdKUNByq5xExtSk9T0+damLiKagikHzTfhcUysTBoE5fztvID1OW0urefoKy+ZJqardj0ezdTPzSHwIDAQAB";

    private String privateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJZf2GFZs1ugUygQ80qGIJCQgjiCDFYGwH5sxz5F3eOV/4mi/2YzegaFs0TrIWfEfNmWjA+DNrJcY1Jqc0PPdm7xHTYrbNJB0pQ0HKrnETG1KT1PT51qYuIpqCKQfNN+FxTKxMGgTl/O28gPU5bS6t5+grL5kmpqt2PR7N1M/NIfAgMBAAECgYAk8sHxEaBPaDJmitusuQ4xV0CX7+RmEtyjc8j1FSezLv0N7Z4Bl65pAdsNmRSpMY9Xy3T4rs32v1qsOnm99LDtqxugdnGnKPWRBUNgX8wcrC9IIMMlnqrqxQx5I2q9jP1MMi4594uWIB+p9hI2CdwJp2QftRmKp1PdY2t+25WJ+QJBAMpsFpUrAvox+WwVMfBo0lzfVao7TDbhNXnJ3YTyU1/Li9sBKhTCVDiKzSv8ed0acDzX8QENzZTHpyMjVagonNMCQQC+LQ1uQbh5CqiA9dkHwX/Ji1m93AR7ZzKwW3Hk5U7zgD+kCWmCVWDh+gS7oAzCZmKYwgfCCGRqvwf4NFeBJfYFAkEAt0HL4BgkheYgbV1y7Le5WyNGJuuwQC77ftrmlandGWjpoMgZFNop7VacESpuGWIKIstNAStB52QpSIKA1dCOyQJAWAAU9O4oZKpP6szYjsQ1U5fOARLsaAYK6JyICXkqnXa8/DT2w76qRCcjRYb8IICsIy+100162gJnt6zQyQw92QJAMiJLYnfCy3MV79cnoA82z50zEzDNrgQPH1HDEIoFw8jzwcCJ+sRxPcIRmzOJGDR/qtePw/3NQo4YzGM6dKB1Ng==";

    @Autowired
    private TaskInfoMapper taskMapper;

    public void down(HttpServletResponse response) {
        response.setContentType("application/x-download");
        String fileName = "cpoy123";

        try {
            fileName = java.net.URLEncoder.encode(fileName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        response.addHeader("Content-Disposition", "attachment;fileName=" + fileName); // 设置文件名

        QueryWrapper query = new QueryWrapper<TaskInfo>();
        List<TaskInfo> infos = taskMapper.selectList(query);

        String fileContent = JSON.toJSONString(infos);
        try {
            response.getOutputStream().write(Objects.requireNonNull(Encrypt.encryptToRSA(fileContent, publicKey)).getBytes());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void upload(MultipartFile file)  {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(file.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        StringBuilder content = new StringBuilder();
        String tmp = null;
        try {
            tmp = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (tmp != null) {
            content.append("\n").append(tmp);
            try {
                tmp = br.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        List<TaskInfo> infos = null;
        try {
            infos = JSON.parseArray(Encrypt.decryptToRSA(content.toString(),privateKey), TaskInfo.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        infos.forEach(taskMapper::insert);
    }
}
