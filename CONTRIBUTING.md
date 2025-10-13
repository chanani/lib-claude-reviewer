# Contributing to Claude PR Reviewer

Claude PR Reviewer에 기여해주셔서 감사합니다! 🎉

## 기여 방법

### 1. 이슈 생성
버그 리포트나 기능 제안을 하려면:
1. [Issues](https://github.com/chanani/claude-pr-reviewer/issues)로 이동
2. 새 이슈 생성
3. 명확한 제목과 설명 작성

### 2. Fork & Clone
```bash
# 저장소 Fork 후
git clone https://github.com/YOUR_USERNAME/claude-pr-reviewer.git
cd claude-pr-reviewer
```

### 3. 브랜치 생성
```bash
git checkout -b feature/amazing-feature
# 또는
git checkout -b fix/bug-fix
```

### 4. 개발 환경 설정
```bash
# 의존성 다운로드
./gradlew build

# 테스트 실행
./gradlew test
```

### 5. 코드 작성
- 코드 스타일: Java 표준 컨벤션 준수
- 주석: 공개 API는 Javadoc 작성
- 테스트: 새로운 기능은 테스트 코드 포함

### 6. 커밋
```bash
git add .
git commit -m "feat: 새로운 기능 추가"
```

#### 커밋 메시지 규칙
```
feat: 새로운 기능
fix: 버그 수정
docs: 문서 수정
style: 코드 포맷팅
refactor: 리팩토링
test: 테스트 추가/수정
chore: 빌드 설정 등
```

### 7. Push & Pull Request
```bash
git push origin feature/amazing-feature
```

GitHub에서 Pull Request 생성:
- 명확한 제목과 설명
- 관련 이슈 번호 포함 (예: `Closes #123`)
- 변경사항 요약

## 코드 스타일

### Java 코딩 컨벤션
```java
// 클래스: PascalCase
public class ClaudeReviewer { }

// 메서드/변수: camelCase
public void reviewPullRequest() { }
private String githubToken;

// 상수: UPPER_SNAKE_CASE
private static final String API_URL = "https://api.anthropic.com";

// 패키지: 소문자
package com.reviewer.service;
```

### Javadoc
```java
/**
 * PR 리뷰를 수행하고 결과를 반환
 *
 * @return 리뷰 결과 텍스트
 * @throws IOException API 호출 실패 시
 */
public String reviewPullRequest() throws IOException {
    // ...
}
```

## 테스트

```bash
# 모든 테스트 실행
./gradlew test

# 특정 테스트만 실행
./gradlew test --tests ClaudeServiceTest
```

## 문서화

- `README.md`: 사용자 가이드
- `EXAMPLE.md`: 사용 예제
- `PUBLISHING.md`: 배포 가이드
- Javadoc: 공개 API 문서

## Pull Request 체크리스트

- [ ] 코드가 정상적으로 빌드됨
- [ ] 모든 테스트 통과
- [ ] 새로운 기능은 테스트 포함
- [ ] 문서 업데이트 (필요시)
- [ ] 커밋 메시지가 규칙을 따름
- [ ] 코드 리뷰 준비 완료

## 질문이나 도움이 필요하신가요?

- [GitHub Discussions](https://github.com/chanani/claude-pr-reviewer/discussions)
- [Issues](https://github.com/chanani/claude-pr-reviewer/issues)

## 행동 강령

- 존중하고 건설적인 피드백
- 다양성과 포용성 존중
- 협력적인 분위기 조성

모든 기여자에게 감사드립니다! 🙏
