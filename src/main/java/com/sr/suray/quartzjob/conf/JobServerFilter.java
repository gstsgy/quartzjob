package com.sr.suray.quartzjob.conf;

import com.alibaba.fastjson.JSON;
import com.sr.suray.quartzjob.bean.ResponseBean;
import com.sr.suray.quartzjob.util.JWTUtil;


import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @ClassName NotSameCommitFilter
 * @Description TODO
 * @Author guyue
 * @Date 2020/9/15 上午11:51
 **/
@WebFilter(urlPatterns = {"/job/*"}, filterName = "jobfilter")
public class JobServerFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String[] skipAuthUrls = new String[]{".*/login"};

        String url =((HttpServletRequest)request).getRequestURI();

        List<Pattern> rules = Arrays.stream(skipAuthUrls).map(Pattern::compile).collect(Collectors.toList());
        for (Pattern p : rules) {
            Matcher m = p.matcher(url);
            if (m.matches()) {
                chain.doFilter(request, response);
            }
        }


        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setHeader("Access-control-Allow-Origin", req.getHeader("Origin"));
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE");
        httpServletResponse.setHeader("Access-Control-Allow-Headers", req.getHeader("Access-Control-Request-Headers"));

        String authorization = req.getHeader("token");
        if(authorization==null||"".equals(authorization)){
            ResponseBean result = new ResponseBean();
            result = result.toLogin();
            response.getWriter().println(JSON.toJSONString(result));
            response.flushBuffer();
            //exception.printStackTrace();
            return;
        }else{
            if(!JWTUtil.verify(authorization)){
                ResponseBean result = new ResponseBean();
                result = result.toLogin();
                response.getWriter().println(JSON.toJSONString(result));
                response.flushBuffer();
                //exception.printStackTrace();
                return;
            }
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {

    }
}
