package cloud.codecloud.util;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * web应用程序的工具类
 *
 * @author zhaoYoung
 * @date 2021/4/25 22:00
 */
@Slf4j
public class WebUtil extends WebUtils {
    public static final String X_REQUESTED_WIDTH = "X-Requested-With";
    public static final String X_REAL_IP = "X-Real-IP";
    public static final String X_FORWARDED_FOR = "X-Forwarded-For";
    public static final String XML_HTTP_REQUEST = "XMLHttpRequest";
    public static final String UNKNOWN = "unKnown";

    /**
     * 获取http请求中的request对象
     *
     * @return
     */
    public static HttpServletRequest getRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes == null ? null : attributes.getRequest();
    }

    /**
     * 获取http请求中的response对象
     *
     * @return
     */
    public static HttpServletResponse getResponse() {
        ServletRequestAttributes attributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        return attributes == null ? null : attributes.getResponse();
    }

    /**
     * 获取当前请求的真实ip
     *
     * @return
     */
    public static String getIpAddr() {
        HttpServletRequest request = getRequest();
        return (request == null) ? "" : getIpAddr(request);
    }

    /**
     * 获取request请求的真实ip
     *
     * @param request
     * @return
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader(X_FORWARDED_FOR);
        if (StrUtil.isEmpty(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader(X_REAL_IP);
        }

        if (StrUtil.isNotEmpty(ip) && !UNKNOWN.equalsIgnoreCase(ip)) {
            // 多次反向代理后会有多个ip值，第一个ip才是真实ip
            return ip.split(",")[0];
        } else {
            return request.getRemoteAddr();
        }
    }

}
