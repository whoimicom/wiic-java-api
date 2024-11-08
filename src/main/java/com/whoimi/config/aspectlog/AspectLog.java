package com.whoimi.config.aspectlog;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Aspect
@Component
public class AspectLog {
    private static final Logger log = LoggerFactory.getLogger(AspectLog.class);
    private final ThreadLocal<AspectLogRunner> SN_CONTEXT = new ThreadLocal<>();

    @Resource
    ObjectMapper objectMapper;

    /**
     * packageCut
     */
    @Pointcut("within(com.whoimi..*)")
    public void packageCut() {
    }

    /**
     * restCut
     */
    @Pointcut("@within(org.springframework.web.bind.annotation.RestController)")
    public void restCut() {
    }


    //    @Before("restCut() && packageCut()")
    @Before("restCut()")
    public void before(JoinPoint joinPoint) {
        try {
            printReq(joinPoint);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }


    @AfterReturning(returning = "ret", pointcut = "restCut() && packageCut()")
    public void after(JoinPoint joinPoint, Object ret) {
        try {
            responseLog(ret, joinPoint);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * printReq
     *
     * @param joinPoint JoinPoint
     */
    public void printReq(JoinPoint joinPoint) {
        AspectLogReq aspectLogReq = new AspectLogReq();
        //设置开始时间
        aspectLogReq.setRequestTime(genNow());
        //获取一个sn，并对TL中的执行情况对象做相应设置
        aspectLogReq.setSn(getAndSetupSn());
        // 设定方法路径
        aspectLogReq.setMethod(getMethod(joinPoint));
        // 取配置
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        AspectLogAnnotation aspectLogAnnotation = methodSignature.getMethod().getAnnotation(AspectLogAnnotation.class);
        AspectLogConfig aspectLogConfig = getAspectLogConfig(aspectLogAnnotation);
        aspectLogReq.setKeyword(aspectLogConfig.getKeyword());
        //设置url
        aspectLogReq.setUrl(getUrl());
        //设置Authorization
        aspectLogReq.setAuthorization(getAuthorization());
        //设置IpAddr
        aspectLogReq.setIpaddr(getIpAddr());
        //设置请求参数
        fillRequestInfo(joinPoint, aspectLogReq, aspectLogConfig.printReqRunner);
        //打印请求参数
        if (aspectLogConfig.isPrintReqRunner()) {
            String valueAsString = "";
            if (aspectLogConfig.isPretty()) {
                try {
                    valueAsString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(aspectLogReq);
                } catch (JsonProcessingException e) {
                    log.error(e.getMessage(), e);
                }
            } else {
                valueAsString = aspectLogReq.toString();
            }
            log.info("aopReq: {}", valueAsString);
        }
    }

    /**
     * getAspectLogConfig
     *
     * @param aspectLogAnnotation AspectLogAnnotation
     * @return AspectLogConfig
     */
    private AspectLogConfig getAspectLogConfig(AspectLogAnnotation aspectLogAnnotation) {
        AspectLogConfig aspectLogConfig = new AspectLogConfig();
        if (aspectLogAnnotation != null) {
            aspectLogConfig.setKeyword(aspectLogAnnotation.keyword());
            aspectLogConfig.setPrintReqRunner(aspectLogAnnotation.printReqRunner());
            aspectLogConfig.setPrintResRunner(aspectLogAnnotation.printResRunner());
            aspectLogConfig.setPretty(aspectLogAnnotation.pretty());
        } else {
//            aspectLogConfig.setKeyword("");
            aspectLogConfig.setPrintReqRunner(true);
            aspectLogConfig.setPrintResRunner(true);
            aspectLogConfig.setPretty(true);
        }
        return aspectLogConfig;
    }

    /**
     * responseLog
     *
     * @param object    Object
     * @param joinPoint JoinPoint
     */
    public void responseLog(Object object, JoinPoint joinPoint) {
        AspectLogRes aspectLogRes = new AspectLogRes();
        //设置开始时间
        aspectLogRes.setResponseTime(genNow());
        //获取一个sn，并对TL中的执行情况对象做相应设置
        aspectLogRes.setSn(getAndSetupSn());
        //设置rt
        aspectLogRes.setRt(getRt());
        //清理TL
        cleanTL();
        // 设定方法路径
        aspectLogRes.setMethod(getMethod(joinPoint));
        // 取配置
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        AspectLogAnnotation aspectLogAnnotation = methodSignature.getMethod().getAnnotation(AspectLogAnnotation.class);
        AspectLogConfig aspectLogConfig = getAspectLogConfig(aspectLogAnnotation);
        aspectLogRes.setKeyword(aspectLogConfig.getKeyword());
        //设置url
        aspectLogRes.setUrl(getUrl());
//        aspectLogRes.setAuthorization(getAuthorization());
//        aspectLogRes.setIpaddr(getIpAddr());
        //设置返回参数
        fillResponseInfo(object, aspectLogRes, aspectLogConfig.printResRunner);
        //打印返回结果
        if (aspectLogConfig.isPrintResRunner()) {
            String valueAsString = "";
            if (aspectLogConfig.isPretty()) {
                try {
                    valueAsString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(aspectLogRes);
                } catch (JsonProcessingException e) {
                    log.error(e.getMessage(), e);
                }
            } else {
                valueAsString = aspectLogRes.toString();
            }
            log.info("aopRes: {}", valueAsString);
        }
    }

    /**
     * @param joinPoint    JoinPoint
     * @param aspectLogReq AspectLogReq
     * @param printReq     boolean
     */
    private void fillRequestInfo(JoinPoint joinPoint, AspectLogReq aspectLogReq, boolean printReq) {
        Object[] args = joinPoint.getArgs();
        if (args != null && args.length > 0) {
            List<Object> objects = Arrays.stream(args).filter(s -> !isFile(s)).collect(Collectors.toList());
            if (!objects.isEmpty() && printReq) {
                try {
                    aspectLogReq.setRequest(args);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }

    /**
     * @param object       Object
     * @param aspectLogRes AspectLogRes
     * @param printRes     boolean
     */
    private void fillResponseInfo(Object object, AspectLogRes aspectLogRes, boolean printRes) {
        if (object != null && printRes) {
            try {
                aspectLogRes.setResponse(object);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    private boolean isFile(Object obj) {
        return obj instanceof MultipartFile;
    }


    /**
     * 获取一个sn，并对TL中的执行情况对象做相应设置
     * 当第二次执行TL中已经有相应信息
     * 此sn不能保证唯一，为了对应打印日志的请求和响应
     *
     * @return sn
     */
    private String getAndSetupSn() {
        if (SN_CONTEXT.get() != null && !ObjectUtils.isEmpty(SN_CONTEXT.get().getSn())) {
            SN_CONTEXT.get().setEnd(System.currentTimeMillis());
            SN_CONTEXT.get().setRt(SN_CONTEXT.get().getEnd() - SN_CONTEXT.get().getStart());
            return SN_CONTEXT.get().getSn();
        } else {
            String sn = System.currentTimeMillis() + "_" + new Random().nextInt(100);
            AspectLogRunner aspectLogRunner = new AspectLogRunner();
            aspectLogRunner.setSn(sn);
            aspectLogRunner.setStart(System.currentTimeMillis());
            SN_CONTEXT.set(aspectLogRunner);
            return sn;
        }
    }

    /**
     * return 0L if not found
     */
    private Long getRt() {
        if (SN_CONTEXT.get() != null) {
            return SN_CONTEXT.get().getRt();
        } else {
            return 0L;
        }
    }

    /**
     * cleanTL
     */
    private void cleanTL() {
        SN_CONTEXT.remove();
    }

    private String genNow() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now());
    }

    /**
     * @return requestURI
     */
    private String getUrl() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert requestAttributes != null;
        HttpServletRequest request = requestAttributes.getRequest();
        return request.getRequestURI();
    }

    /**
     * @return Authorization
     */
    private String getAuthorization() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert requestAttributes != null;
        HttpServletRequest request = requestAttributes.getRequest();
        return request.getHeader("Authorization");
    }

    /**
     * @return localAddr + ":" + localPort;
     */
    private String getIpAddr() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert requestAttributes != null;
        HttpServletRequest request = requestAttributes.getRequest();
/*        String localAddr = request.getLocalAddr();
        int localPort = request.getLocalPort();
        return localAddr + ":" + localPort;*/
        List<String> list = Arrays.asList("X-Forwarded-For",  "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR", "X-Real-IP");
        String ip = request.getHeader("x-forwarded-for");
        for (String string : list) {
            if (null == ip || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader(string);
            } else {
                break;
            }
        }
        if (null == ip || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (null != ip && ip.length() > 15) {
            if (ip.indexOf(",") > 0) {
                ip = ip.substring(0, ip.indexOf(","));
            }
        }
        return ip;
    }

    /**
     * @param joinPoint JoinPoint
     * @return getMethod
     */
    private String getMethod(JoinPoint joinPoint) {
        String method = "";
        try {
            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            method = methodSignature.getDeclaringTypeName();
            if (methodSignature.getMethod() != null) {
                method += "." + methodSignature.getMethod().getName();
            }
            return method;
        } catch (Exception e) {
            return method;
        }
    }


}