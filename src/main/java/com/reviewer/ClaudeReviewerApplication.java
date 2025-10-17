package com.reviewer;

import com.reviewer.config.ReviewConfig;
import com.reviewer.di.ServiceFactory;
import com.reviewer.service.ReviewService;

/**
 * 애플리케이션 진입점
 */
public class ClaudeReviewerApplication {
    public static void main(String[] args) {
        try {
            // 1. 설정 로드
            ReviewConfig config = ReviewConfig.fromEnvironment();

            // 2. DI Container로 서비스 생성
            ServiceFactory factory = new ServiceFactory(config);
            ReviewService reviewService = factory.createReviewService();

            // 3. 리뷰 실행
            reviewService.executeReview();
        } catch (Exception e) {
            System.err.println("❌ 에러 발생: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}