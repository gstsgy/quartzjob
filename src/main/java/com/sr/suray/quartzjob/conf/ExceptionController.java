package com.sr.suray.quartzjob.conf;

import com.sr.suray.quartzjob.bean.ResponseBean;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * @ClassName ExceptionController
 * @Description TODO
 * @Author guyue
 * @Date 2020/9/8 下午3:31
 **/
@RestControllerAdvice
public class ExceptionController {





    // 捕捉其他所有异常
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.OK)
    public ResponseBean globalException(HttpServletRequest request, Throwable ex) {
       // System.out.println("url:"+request.getRequestURI()+";  method:"+request.getMethod());
      //  request.getParameterMap().values().stream().map(JSON::toJSON).forEach(System.out::println);
        ex.printStackTrace();

      //  log.error(ex.getMessage());
        return new ResponseBean(-1, ex.getMessage(), null);
    }

    private HttpStatus getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if (statusCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return HttpStatus.valueOf(statusCode);
    }
}
