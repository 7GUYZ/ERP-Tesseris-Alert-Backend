package com.msa.alarm_service.context;

/**
 * AppId를 ThreadLocal에 저장하고 관리하는 컨텍스트 클래스
 * 
 * 목적:
 * - HTTP 요청별로 AppId를 안전하게 저장
 * - 스레드 간 격리 보장 (ThreadLocal 사용)
 * - 요청 처리 중 어디서든 AppId 접근 가능
 * 
 * 사용 시나리오:
 * 1. AppIdInterceptor에서 요청 헤더의 AppId를 저장
 * 2. 서비스 레이어에서 AppIdContext.getAppId()로 접근
 * 3. 요청 완료 후 ThreadLocal 정리
 * 
 * @author MSA Team
 * @version 1.0
 */
public class AppIdContext {

    // ThreadLocal을 사용하여 스레드별로 AppId를 독립적으로 저장
    // 각 HTTP 요청은 별도 스레드에서 처리되므로 요청 간 격리 보장
    private static final ThreadLocal<String> appIdThreadLocal = new ThreadLocal<>();

    /**
     * 현재 스레드에 AppId를 저장합니다.
     * @param appId 저장할 AppId 값
     */
    public static void setAppId(String appId) {
        appIdThreadLocal.set(appId);
    }

    /**
     * 현재 스레드에서 AppId를 가져옵니다.
     * return 저장된 AppId 값 (없으면 null)
     */
    public static String getAppId() {
        return appIdThreadLocal.get();
    }

    /**
     * 현재 스레드의 AppId를 제거합니다.
     * 메모리 누수 방지를 위해 요청 완료 후 호출해야 합니다.
     */
    public static void clear() {
        appIdThreadLocal.remove();
    }
}