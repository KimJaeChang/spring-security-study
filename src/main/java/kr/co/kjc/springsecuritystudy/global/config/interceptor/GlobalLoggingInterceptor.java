package kr.co.kjc.springsecuritystudy.global.config.interceptor;

import static java.nio.charset.StandardCharsets.UTF_8;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class GlobalLoggingInterceptor implements HandlerInterceptor {

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {
    String requestURI = request.getRequestURI();
    String uuid = UUID.randomUUID().toString();
    request.setAttribute("requestId", uuid);

    StringBuilder sb = new StringBuilder();
    sb.append("[REQUEST][").append(uuid).append("][").append(request.getMethod()).append("][")
        .append(requestURI);

    if (StringUtils.hasText(request.getQueryString())) {
      sb.append("?").append(request.getQueryString()).append("]");
    } else {
      sb.append("]");
    }

    try {
      ContentCachingRequestWrapper cachingRequest = new ContentCachingRequestWrapper(request);
      cachingRequest.getContentAsByteArray();

      if (cachingRequest.getContentAsByteArray().length != 0) {
        sb.append("\n").append(new String(cachingRequest.getContentAsByteArray(), UTF_8));
      }
      //cast exception
    } catch (ClassCastException e) {
      // Multipart Request 의 경우 ContentCachingRequestWrapper 로 캐스팅 할 수 없다.
    }

    log.info(sb.toString());
    return true;
  }

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
      Object handler, Exception ex) throws Exception {
    ContentCachingResponseWrapper cachingResponse = new ContentCachingResponseWrapper(response);
    String requestURI = request.getRequestURI();
    String uuid = (String) request.getAttribute("requestId");

    StringBuilder sb = new StringBuilder();

    if (ex != null) {
      sb.append("[EXCEPTION][").append(uuid).append("][").append(requestURI).append("]");
      log.error(sb.toString(), ex);
      return;
    }

    sb.append("[RESPONSE][").append(uuid).append("][").append(requestURI).append("]");

    byte[] responseContentAsByteArray = cachingResponse.getContentAsByteArray();
    if (responseContentAsByteArray.length != 0 && response.getContentType()
        .contains("application/json")) {
      sb.append("\n").append(new String(responseContentAsByteArray, UTF_8));
    }
    log.info(sb.toString());
  }
}
