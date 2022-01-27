package kim.kin.kklog;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author choky
 */
@Component
@Aspect
public class LogKimAspect {
    private static final String UNKNOWN = "unknown";
    ThreadLocal<Long> currentTime = new ThreadLocal<>();
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private final Logger log = LoggerFactory.getLogger(LogKimAspect.class);
    @Autowired
    ObjectMapper objectMapper;

    /**
     * 配置切入点
     */
    @Pointcut("@annotation(kim.kin.kklog.LogKimAnnotation)")
    public void logPointcut() {
    }

    /**
     * @param joinPoint join point for advice
     */
    @Around("logPointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result;
        long startTime = System.currentTimeMillis();
        currentTime.set(startTime);
        result = joinPoint.proceed();
        currentTime.remove();
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        List<Object> collect = Arrays.stream(joinPoint.getArgs()).toList();
        Enumeration<String> headerNames = request.getHeaderNames();
        log.debug("header:------------------------------------------------");
        headerNames.asIterator().forEachRemaining(s -> {
            String header = request.getHeader(s);
            log.debug(s + " :" + header);
        });
        log.debug("header:------------------------------------------------");
        log.info("ip:" + acquireIp(request) + " args:" + collect + " joinPoint:" + joinPoint);
        long callMillis = System.currentTimeMillis() - startTime;
        printLog(acquireIp(request), joinPoint, result, callMillis);
        return result;
    }

    /**
     * @param joinPoint join point for advice
     * @param e         exception
     */
    @AfterThrowing(pointcut = "logPointcut()", throwing = "e")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
        log.error(e.toString());
        currentTime.remove();
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        log.error("ip:" + acquireIp(request) + " joinPoint:" + joinPoint);
    }

    /**
     * acquire request ip
     */
    public static String acquireIp(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        String comma = ",";
        List<String> localIps = Arrays.asList("127.0.0.1", "0:0:0:0:0:0:0:1", "localhost", "localhost.localdomain", "localhost4", "localhost4.localdomain4", "::1", "localhost6", "localhost6.localdomain6");
        if (ip.contains(comma)) {
            ip = ip.split(",")[0];
        }
        if (localIps.contains(ip)) {
            try {
                ip = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException e) {
                e.printStackTrace();
                ip = "UNKNOWN_IP";
            }
        }
        return ip;
    }

    public void printLog(String ip, ProceedingJoinPoint joinPoint, Object result, long callMillis) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        kim.kin.kklog.LogKimAnnotation logKimAnnotation = method.getAnnotation(kim.kin.kklog.LogKimAnnotation.class);
        StringBuilder stringBuilder = new StringBuilder(LINE_SEPARATOR);
        String methodName = joinPoint.getTarget().getClass().getName() + "." + signature.getName() + "()";

        Object[] argValues = joinPoint.getArgs();
        String[] argNames = ((MethodSignature) joinPoint.getSignature()).getParameterNames();
        StringBuilder paramIn = new StringBuilder();
        if (argValues != null) {
            for (int i = 0; i < argValues.length; i++) {
                paramIn.append(" ").append(argNames[i]).append(": ").append(argValues[i]);
            }
        }
        stringBuilder.append(methodName).append(logKimAnnotation.value()).append(" invoked  ");
        stringBuilder.append("Duration[").append(callMillis).append("]ms");
        stringBuilder.append("Client Ip=").append(ip);
//        stringBuilder.append(" ,Client Address=").append(StringUtils.getCityInfo(ip));
        stringBuilder.append(", Input Parameter {").append(LINE_SEPARATOR);
        stringBuilder.append(paramIn);
        stringBuilder.append(LINE_SEPARATOR).append("}");
        stringBuilder.append("Output Parameter:[").append(LINE_SEPARATOR);
        try {
            stringBuilder.append(objectMapper.writeValueAsString(result));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        stringBuilder.append("]").append(LINE_SEPARATOR);
        log.info(stringBuilder.toString());
    }
}
