package com.msa.alarm_service.interceptor;

import com.msa.alarm_service.context.AppIdContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * HTTP 요청에서 AppId 헤더를 추출하여 ThreadLocal에 저장하는 인터셉터
 * 
 * 목적:
 * - 모든 HTTP 요청에서 AppId 헤더를 자동으로 처리
 * - AppId를 ThreadLocal에 저장하여 서비스 레이어에서 접근 가능
 * - MSA 환경에서 서비스 간 식별자 관리
 * 
 * 동작 과정:
 * 1. HTTP 요청이 들어오면 preHandle() 메서드 실행
 * 2. 요청 헤더에서 "AppId" 값을 추출
 * 3. AppIdContext.setAppId()로 ThreadLocal에 저장
 * 4. 이후 컨트롤러/서비스에서 AppIdContext.getAppId()로 접근 가능
 * 
 * 설정 방법:
 * - WebMvcConfigurer에서 addInterceptors()로 등록 필요
 * 
 * @author MSA Team
 * @version 1.0
 */
@Component
public class AppIdInterceptor implements HandlerInterceptor {

    // HTTP 요청 헤더에서 AppId를 가져올 때 사용할 키 이름
    public static final String APP_ID_HEADER = "AppId";

    /**
     * HTTP 요청 처리 전에 실행되는 메서드
     * 
     * @param request HTTP 요청 객체
     * @param response HTTP 응답 객체  
     * @param handler 요청을 처리할 핸들러
     * @return true: 요청 계속 처리, false: 요청 중단
     * @throws Exception 예외 발생 시
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 요청 헤더에서 AppId를 가져옵니다
        String appId = request.getHeader(APP_ID_HEADER);

        if (appId != null) {
            // ThreadLocal에 AppId를 저장하여 나중에 사용 가능하도록 합니다
            // 이렇게 저장된 AppId는 같은 스레드에서 처리되는 모든 코드에서 접근 가능
            AppIdContext.setAppId(appId);
        }

        // true를 반환하여 요청 처리를 계속 진행합니다
        return true;
    }
}
