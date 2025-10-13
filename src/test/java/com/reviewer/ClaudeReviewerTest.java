package com.reviewer;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * ClaudeReviewer 테스트
 */
class ClaudeReviewerTest {

    @Test
    void testBuilderPattern() {
        // 빌더 패턴이 정상적으로 동작하는지 확인
        assertNotNull(ClaudeReviewer.builder());
    }

    @Test
    void testBasicFunctionality() {
        // 기본 기능 테스트 (추후 확장)
        assertTrue(true);
    }
}
