package com.hhplush.eCommerce.domain.event;

public enum ProcessState {
    PENDING,        // 이벤트 발행전
    PUBLISHED,      // 이벤트 발행완료
    PUBLISHED_FAILED,         // 이벤트 발행실패
    FAILED,         // 처리 실패
    PROCESSED       // 처리 완료
}
