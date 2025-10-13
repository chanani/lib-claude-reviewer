# 사용 예제

## 1. 기본 사용법

```java
import com.reviewer.ClaudeReviewer;

public class BasicExample {
    public static void main(String[] args) throws Exception {
        ClaudeReviewer reviewer = ClaudeReviewer.builder()
                .githubToken("ghp_xxxxxxxxxxxx")
                .anthropicApiKey("sk-ant-xxxxxxxxxxxx")
                .repoName("chanani/my-project")
                .prNumber(42)
                .build();

        // 전체 리뷰 프로세스 실행
        reviewer.executeFullReview();
    }
}
```

## 2. 리뷰 결과만 받기

```java
import com.reviewer.ClaudeReviewer;

public class ReviewOnlyExample {
    public static void main(String[] args) throws Exception {
        ClaudeReviewer reviewer = ClaudeReviewer.builder()
                .githubToken("ghp_xxxxxxxxxxxx")
                .anthropicApiKey("sk-ant-xxxxxxxxxxxx")
                .repoName("chanani/my-project")
                .prNumber(42)
                .language("en")  // 영어 리뷰
                .build();

        // 리뷰만 수행 (댓글 작성 안 함)
        String reviewText = reviewer.reviewPullRequest();
        System.out.println("Review Result:");
        System.out.println(reviewText);

        // 다른 처리 (예: 슬랙 전송, 이메일 등)
        sendToSlack(reviewText);
    }

    private static void sendToSlack(String message) {
        // 슬랙 전송 로직
    }
}
```

## 3. 커스텀 설정

```java
import com.reviewer.ClaudeReviewer;

public class CustomConfigExample {
    public static void main(String[] args) throws Exception {
        ClaudeReviewer reviewer = ClaudeReviewer.builder()
                .githubToken("ghp_xxxxxxxxxxxx")
                .anthropicApiKey("sk-ant-xxxxxxxxxxxx")
                .repoName("chanani/my-project")
                .prNumber(42)
                .model("claude-sonnet-4-5-20250929")  // 모델 선택
                .language("ko")  // 한국어
                .fileExtensions(".java,.kt,.xml")  // Kotlin 프로젝트
                .maxTokens(4000)  // 토큰 수 증가
                .build();

        reviewer.executeFullReview();
    }
}
```

## 4. 변경된 파일 분석

```java
import com.reviewer.ClaudeReviewer;
import com.reviewer.model.FileChange;
import java.util.List;

public class FileAnalysisExample {
    public static void main(String[] args) throws Exception {
        ClaudeReviewer reviewer = ClaudeReviewer.builder()
                .githubToken("ghp_xxxxxxxxxxxx")
                .anthropicApiKey("sk-ant-xxxxxxxxxxxx")
                .repoName("chanani/my-project")
                .prNumber(42)
                .build();

        // 변경된 파일 목록 조회
        List<FileChange> files = reviewer.getChangedFiles();

        System.out.println("변경된 파일 목록:");
        for (FileChange file : files) {
            System.out.println("- " + file.getFilename() + " (" + file.getStatus() + ")");
        }

        // 조건부 리뷰
        if (files.size() > 10) {
            System.out.println("변경된 파일이 너무 많습니다. 리뷰를 생략합니다.");
        } else {
            reviewer.executeFullReview();
        }
    }
}
```

## 5. Spring Boot 통합

```java
import com.reviewer.ClaudeReviewer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CodeReviewService {

    @Value("${github.token}")
    private String githubToken;

    @Value("${anthropic.api-key}")
    private String anthropicApiKey;

    public void reviewPullRequest(String repoName, int prNumber) throws Exception {
        ClaudeReviewer reviewer = ClaudeReviewer.builder()
                .githubToken(githubToken)
                .anthropicApiKey(anthropicApiKey)
                .repoName(repoName)
                .prNumber(prNumber)
                .language("ko")
                .build();

        reviewer.executeFullReview();
    }

    public String getReviewOnly(String repoName, int prNumber) throws Exception {
        ClaudeReviewer reviewer = ClaudeReviewer.builder()
                .githubToken(githubToken)
                .anthropicApiKey(anthropicApiKey)
                .repoName(repoName)
                .prNumber(prNumber)
                .build();

        return reviewer.reviewPullRequest();
    }
}
```

```java
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/review")
public class ReviewController {

    private final CodeReviewService reviewService;

    public ReviewController(CodeReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/pr")
    public String reviewPR(@RequestParam String repo, @RequestParam int prNumber) {
        try {
            reviewService.reviewPullRequest(repo, prNumber);
            return "리뷰가 완료되었습니다.";
        } catch (Exception e) {
            return "에러: " + e.getMessage();
        }
    }

    @GetMapping("/pr")
    public String getReview(@RequestParam String repo, @RequestParam int prNumber) {
        try {
            return reviewService.getReviewOnly(repo, prNumber);
        } catch (Exception e) {
            return "에러: " + e.getMessage();
        }
    }
}
```

## 6. GitHub Webhook 처리

```java
import com.reviewer.ClaudeReviewer;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webhook")
public class GitHubWebhookController {

    @Value("${github.token}")
    private String githubToken;

    @Value("${anthropic.api-key}")
    private String anthropicApiKey;

    @PostMapping("/github")
    public String handleWebhook(@RequestBody Map<String, Object> payload) {
        try {
            // PR 이벤트인지 확인
            String action = (String) payload.get("action");
            if (!"opened".equals(action) && !"synchronize".equals(action)) {
                return "이벤트 무시";
            }

            // PR 정보 추출
            Map<String, Object> pr = (Map<String, Object>) payload.get("pull_request");
            int prNumber = (int) pr.get("number");

            Map<String, Object> repo = (Map<String, Object>) payload.get("repository");
            String repoName = (String) repo.get("full_name");

            // 리뷰 수행
            ClaudeReviewer reviewer = ClaudeReviewer.builder()
                    .githubToken(githubToken)
                    .anthropicApiKey(anthropicApiKey)
                    .repoName(repoName)
                    .prNumber(prNumber)
                    .build();

            reviewer.executeFullReview();

            return "리뷰 완료";
        } catch (Exception e) {
            return "에러: " + e.getMessage();
        }
    }
}
```

## 7. 환경 변수 사용

```java
import com.reviewer.ClaudeReviewer;

public class EnvVarExample {
    public static void main(String[] args) throws Exception {
        // 환경 변수에서 읽기
        String githubToken = System.getenv("GITHUB_TOKEN");
        String anthropicApiKey = System.getenv("ANTHROPIC_API_KEY");
        String repoName = System.getenv("REPO_NAME");
        int prNumber = Integer.parseInt(System.getenv("PR_NUMBER"));

        ClaudeReviewer reviewer = ClaudeReviewer.builder()
                .githubToken(githubToken)
                .anthropicApiKey(anthropicApiKey)
                .repoName(repoName)
                .prNumber(prNumber)
                .build();

        reviewer.executeFullReview();
    }
}
```

실행:
```bash
export GITHUB_TOKEN="ghp_xxxx"
export ANTHROPIC_API_KEY="sk-ant-xxxx"
export REPO_NAME="chanani/my-project"
export PR_NUMBER="42"

java -jar my-app.jar
```

## 8. 에러 핸들링

```java
import com.reviewer.ClaudeReviewer;
import java.io.IOException;

public class ErrorHandlingExample {
    public static void main(String[] args) {
        try {
            ClaudeReviewer reviewer = ClaudeReviewer.builder()
                    .githubToken("ghp_xxxxxxxxxxxx")
                    .anthropicApiKey("sk-ant-xxxxxxxxxxxx")
                    .repoName("chanani/my-project")
                    .prNumber(42)
                    .build();

            reviewer.executeFullReview();

        } catch (IOException e) {
            System.err.println("API 호출 실패: " + e.getMessage());
            // 재시도 로직 또는 알림
        } catch (IllegalStateException e) {
            System.err.println("설정 오류: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("알 수 없는 오류: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
```
