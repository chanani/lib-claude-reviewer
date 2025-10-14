# Claude PR Reviewer 🤖

Claude AI 자동 코드 리뷰 라이브러리

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

### 1. 라이브러리로 사용

환경 변수 사용 (GitHub Actions에서 자동)
```java
import com.reviewer.ClaudeReviewer;

public class Main {
    public static void main(String[] args) throws Exception {
        ClaudeReviewer.builder()
                .build()
                .executeFullReview();
    }
}
```



### 2. GitHub Actions로 사용 (PR 자동 리뷰)

⚠️ **중요: Workflow 파일 생성은 필수입니다!**

GitHub Actions에서 자동 PR 리뷰를 사용하려면 아래 2단계가 필요합니다:

---

#### 단계 1: GitHub Secrets에 API Key 추가

1. GitHub 저장소 페이지로 이동
2. **Settings** → **Secrets and variables** → **Actions**
3. **"New repository secret"** 클릭
4. 아래 정보 입력:
   - **Name**: `ANTHROPIC_API_KEY`
   - **Secret**: 당신의 Claude API 키 (예: `sk-ant-...`)
5. **Add secret** 클릭

---

#### 단계 2: Workflow 파일 생성 (필수!)

**프로젝트 루트에** 아래 파일을 생성하세요:

**파일 경로**: `.github/workflows/pr-review.yml`

**파일 내용**:
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
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Claude PR Review
        uses: chanani/lib-claude-reviewer@main
        with:
          github_token: ${{ secrets.GITHUB_TOKEN }}
          anthropic_api_key: ${{ secrets.ANTHROPIC_API_KEY }}
```

> 💡 **참고**:
> - `GITHUB_TOKEN`은 GitHub가 자동 제공하므로 Secrets에 추가할 필요 없습니다
> - `language`는 기본값 `ko`(한국어), 영어 원하면 `language: 'en'` 추가
> - `file_extensions`는 기본값 `.java,.kt,.xml,.gradle`, 변경 원하면 추가

---

#### 단계 3: Git에 커밋 & 푸시

```bash
git add .github/workflows/pr-review.yml
git commit -m "Add AI PR review workflow"
git push
```

---

#### 완료! 이제 작동합니다 ✅

- ✅ PR 생성/업데이트 시 **자동으로** Claude 코드 리뷰
- ✅ PR에 **자동으로** 리뷰 댓글 작성
- ✅ `.java`, `.kt`, `.xml`, `.gradle` 파일만 리뷰




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
