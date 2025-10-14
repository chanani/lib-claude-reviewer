# Claude PR Reviewer 🤖

Java + DI 패턴으로 구현한 Claude AI 자동 코드 리뷰 라이브러리

## 특징

- ✅ **Maven Central 배포** - 간단한 의존성 주입으로 사용
- ✅ 완전한 의존성 주입 (DI) 패턴
- ✅ 계층화된 서비스 구조
- ✅ Gradle 빌드 시스템
- ✅ Lombok으로 보일러플레이트 제거
- ✅ 설정 가능한 옵션들
- ✅ Java 21 지원

## 설치

### Gradle
```gradle
dependencies {
    implementation 'io.github.chanani:claude-reviewer:1.0.4'
}
```

### Maven
```xml
<dependency>
    <groupId>io.github.chanani</groupId>
    <artifactId>claude-reviewer</artifactId>
    <version>1.0.4</version>
</dependency>
```

## 사용 방법

### 1. 라이브러리로 사용 (권장)

#### 방법 1: 환경 변수 사용 (GitHub Actions에서 자동)
```java
import com.reviewer.ClaudeReviewer;

public class Main {
    public static void main(String[] args) throws Exception {
        // 환경 변수에서 자동으로 읽음:
        // - GITHUB_TOKEN
        // - ANTHROPIC_API_KEY
        // - PR_NUMBER
        // - REPO_NAME
        ClaudeReviewer.builder()
                .build()
                .executeFullReview();
    }
}
```

#### 방법 2: 코드에서 직접 설정
```java
import com.reviewer.ClaudeReviewer;

public class Main {
    public static void main(String[] args) throws Exception {
        // ClaudeReviewer 인스턴스 생성
        ClaudeReviewer reviewer = ClaudeReviewer.builder()
                .githubToken("ghp_xxxxxxxxxxxx")
                .anthropicApiKey("sk-ant-xxxxxxxxxxxx")
                .repoName("owner/repository")
                .prNumber(123)
                .language("ko")  // 또는 "en"
                .fileExtensions(".java,.kt,.xml,.gradle")
                .build();

        // 방법 1: 리뷰만 받기
        String reviewText = reviewer.reviewPullRequest();
        System.out.println(reviewText);

        // 방법 2: 리뷰하고 자동으로 댓글 작성
        reviewer.executeFullReview();

        // 방법 3: 수동으로 댓글 작성
        String review = reviewer.reviewPullRequest();
        reviewer.postReviewComment(review);

        // 방법 4: 변경된 파일 목록만 조회
        List<FileChange> files = reviewer.getChangedFiles();
        files.forEach(f -> System.out.println(f.getFilename()));
    }
}
```

#### 환경 변수 자동 감지
Builder에서 값을 설정하지 않으면 자동으로 환경 변수에서 읽습니다:

| 환경 변수 | Builder 메서드 | 필수 여부 |
|---------|--------------|---------|
| `GITHUB_TOKEN` | `.githubToken()` | ✅ 필수 |
| `ANTHROPIC_API_KEY` | `.anthropicApiKey()` | ✅ 필수 |
| `PR_NUMBER` | `.prNumber()` | ✅ 필수 |
| `REPO_NAME` | `.repoName()` | ✅ 필수 |

### 2. GitHub Actions로 사용

다른 사용자들이 이 라이브러리를 GitHub Actions에서 사용하려면:

#### 단계 1: Anthropic API Key를 GitHub Secrets에 추가
1. GitHub 저장소 → Settings → Secrets and variables → Actions
2. "New repository secret" 클릭
3. Name: `ANTHROPIC_API_KEY`
4. Secret: 당신의 Claude API 키 입력

#### 단계 2: Workflow 파일 생성
`.github/workflows/pr-review.yml`:
```yaml
name: AI PR Review

on:
  pull_request:
    types: [opened, synchronize, reopened]

jobs:
  review:
    runs-on: ubuntu-latest
    permissions:
      contents: read
      pull-requests: write

    steps:
      - name: Checkout
        uses: actions/checkout@v4

      - name: Claude PR Review
        uses: chanani/lib-claude-reviewer@main  # 또는 @v1.0.4 (Release 생성 후)
        with:
          github_token: ${{ secrets.GITHUB_TOKEN }}
          anthropic_api_key: ${{ secrets.ANTHROPIC_API_KEY }}
          language: 'ko'  # 선택 사항: 'en' 또는 'ko'
          file_extensions: '.java,.kt,.xml,.gradle'  # 선택 사항
```

#### 작동 방식
- PR이 생성되거나 업데이트될 때 자동으로 실행됩니다
- Claude AI가 변경된 코드를 분석합니다
- PR에 자동으로 리뷰 댓글을 작성합니다
- **prNumber와 repoName은 자동으로 감지됩니다!**

## 아키텍처

```
ClaudeReviewerApplication (Main)
    ↓
ServiceFactory (DI Container)
    ↓
ReviewService (Facade)
    ↓
├── GitHubService
└── ClaudeService
```

## API 레퍼런스

### ClaudeReviewer

#### 빌더 메서드
- `githubToken(String)` - GitHub Personal Access Token (필수)
- `anthropicApiKey(String)` - Anthropic API Key (필수)
- `repoName(String)` - 저장소 이름 "owner/repo" (필수)
- `prNumber(int)` - Pull Request 번호 (필수)
- `model(String)` - Claude 모델 (기본값: claude-sonnet-4-5-20250929)
- `language(String)` - 리뷰 언어 "ko" 또는 "en" (기본값: ko)
- `fileExtensions(String)` - 리뷰할 파일 확장자 (기본값: .java,.kt,.xml,.gradle)
- `maxTokens(int)` - 최대 토큰 수 (기본값: 2000)

#### 주요 메서드
- `String reviewPullRequest()` - PR 리뷰 수행 및 결과 반환
- `void postReviewComment(String)` - PR에 댓글 작성
- `List<FileChange> getChangedFiles()` - 변경된 파일 목록 조회
- `void executeFullReview()` - 리뷰 수행 + 자동 댓글 작성

## 프로젝트 구조

```
claude-pr-reviewer/
├── action.yml                  # GitHub Actions 정의
├── build.gradle                # Maven Central 배포 설정
├── gradle.properties           # Gradle 설정
├── PUBLISHING.md              # 배포 가이드
└── src/main/java/com/reviewer/
    ├── ClaudeReviewer.java    # 공개 API (메인 진입점)
    ├── ClaudeReviewerApplication.java  # CLI 실행용
    ├── config/
    │   └── ReviewConfig.java
    ├── model/
    │   └── FileChange.java
    ├── service/
    │   ├── GitHubService.java
    │   ├── ClaudeService.java
    │   └── ReviewService.java
    └── di/
        └── ServiceFactory.java
```

## 배포 가이드

Maven Central에 배포하는 방법은 [PUBLISHING.md](PUBLISHING.md)를 참조하세요.

## 요구사항

- Java 21+
- Gradle 8.x+

## 라이센스

MIT License

## 기여

Pull Request는 언제나 환영합니다!

## 지원

이슈가 있다면 [GitHub Issues](https://github.com/chanani/claude-pr-reviewer/issues)에 등록해주세요.
