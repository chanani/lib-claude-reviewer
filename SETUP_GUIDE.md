# 깃허브 오픈소스 설정 가이드

## 1. GitHub 저장소 생성

### 1.1 새 저장소 생성
1. GitHub에 로그인
2. 우측 상단 `+` → `New repository`
3. 저장소 정보 입력:
   ```
   Repository name: claude-pr-reviewer
   Description: A library for automated code review using Claude AI
   Public (중요!)
   ☑ Add a README file (체크 해제)
   ☑ Add .gitignore (체크 해제)
   ☑ Choose a license: MIT License
   ```

### 1.2 저장소 설정
1. `Settings` → `General`
2. Features:
   - ☑ Issues
   - ☑ Discussions (권장)
   - ☑ Projects
3. `Settings` → `Actions` → `General`
   - Actions permissions: `Allow all actions`

## 2. 로컬에서 Git 초기화

```bash
cd /Users/ichanhan/Desktop/projectfile/claude-pr-reviewer

# Git 초기화
git init

# .gitignore 확인 (이미 생성됨)
cat .gitignore

# 모든 파일 추가
git add .

# 첫 커밋
git commit -m "feat: initial commit - Claude PR Reviewer library

- Java 21 기반 Maven Central 라이브러리
- DI 패턴 기반 서비스 아키텍처
- GitHub API + Claude AI 통합
- 완전한 문서화 및 예제 포함"

# 원격 저장소 연결 (YOUR_USERNAME을 실제 사용자명으로 변경)
git remote add origin https://github.com/YOUR_USERNAME/claude-pr-reviewer.git

# 브랜치 이름 확인 및 변경
git branch -M main

# 푸시
git push -u origin main
```

## 3. GitHub Secrets 설정 (Maven Central 배포용)

### 3.1 필요한 Secrets
`Settings` → `Secrets and variables` → `Actions` → `New repository secret`

1. **OSSRH_USERNAME**
   - Sonatype 사용자명

2. **OSSRH_PASSWORD**
   - Sonatype 비밀번호

3. **SIGNING_KEY_ID**
   - GPG 키 ID (예: `12345678`)
   ```bash
   gpg --list-keys
   # pub   rsa3072/12345678 2025-01-01 [SC]
   #                ^^^^^^^^ 이 부분
   ```

4. **SIGNING_PASSWORD**
   - GPG 키 비밀번호

5. **GPG_KEY_BASE64**
   - GPG 비밀키를 Base64로 인코딩
   ```bash
   gpg --export-secret-keys YOUR_KEY_ID | base64 | pbcopy
   ```

## 4. Topics 설정 (저장소 검색 최적화)

`About` (저장소 상단) → `⚙️` 아이콘 클릭:
```
Topics: java, maven-central, claude-ai, code-review, github-actions,
        pr-review, ai-code-review, dependency-injection, gradle
```

## 5. README 뱃지 추가

README.md 상단에 추가:
```markdown
# Claude PR Reviewer 🤖

[![CI](https://github.com/YOUR_USERNAME/claude-pr-reviewer/workflows/CI/badge.svg)](https://github.com/YOUR_USERNAME/claude-pr-reviewer/actions)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.chanani/claude-reviewer.svg)](https://search.maven.org/artifact/io.github.chanani/claude-reviewer)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-21+-orange.svg)](https://www.oracle.com/java/)
```

## 6. GitHub Releases 만들기

### 첫 릴리즈
1. `Releases` → `Create a new release`
2. Tag 생성: `v1.0.0`
3. Release title: `v1.0.0 - Initial Release`
4. Release notes:
   ```markdown
   ## 🎉 첫 번째 릴리즈!

   ### ✨ Features
   - Claude AI 기반 자동 코드 리뷰
   - Maven Central 배포
   - 다양한 사용 방법 지원 (라이브러리, GitHub Actions, CLI)
   - 완전한 DI 패턴 아키텍처

   ### 📦 Installation

   #### Gradle
   \`\`\`gradle
   dependencies {
       implementation 'io.github.chanani:claude-reviewer:1.0.0'
   }
   \`\`\`

   #### Maven
   \`\`\`xml
   <dependency>
       <groupId>io.github.chanani</groupId>
       <artifactId>claude-reviewer</artifactId>
       <version>1.0.0</version>
   </dependency>
   \`\`\`

   ### 📖 Documentation
   - [README](https://github.com/YOUR_USERNAME/claude-pr-reviewer)
   - [Examples](https://github.com/YOUR_USERNAME/claude-pr-reviewer/blob/main/EXAMPLE.md)
   - [Contributing](https://github.com/YOUR_USERNAME/claude-pr-reviewer/blob/main/CONTRIBUTING.md)
   ```
5. `Publish release` 클릭

## 7. Maven Central 배포

### 7.1 Sonatype 계정 생성
1. https://issues.sonatype.org 에서 계정 생성
2. New Issue 생성:
   ```
   Project: Community Support - Open Source Project Repository Hosting (OSSRH)
   Issue Type: New Project
   Group Id: io.github.YOUR_USERNAME
   Project URL: https://github.com/YOUR_USERNAME/claude-pr-reviewer
   SCM url: https://github.com/YOUR_USERNAME/claude-pr-reviewer.git
   ```
3. 승인 대기 (1-2일)

### 7.2 GPG 키 생성
```bash
# GPG 키 생성
gpg --gen-key
# 이름, 이메일, 비밀번호 입력

# 키 확인
gpg --list-keys

# 공개 키 서버에 업로드
gpg --keyserver keyserver.ubuntu.com --send-keys YOUR_KEY_ID
```

### 7.3 배포
```bash
# 로컬에서 테스트
./gradlew publishToMavenLocal

# Maven Central에 배포 (Sonatype 승인 후)
./gradlew publish
```

또는 GitHub Release 생성 시 자동 배포 (`.github/workflows/publish.yml`)

## 8. GitHub Actions 확인

1. `Actions` 탭 확인
2. CI 워크플로우가 자동으로 실행됨
3. 빌드 성공 확인

## 9. Community 파일 확인

다음 파일들이 GitHub에서 자동 인식됨:
- ✅ README.md
- ✅ LICENSE
- ✅ CONTRIBUTING.md
- ✅ CODE_OF_CONDUCT.md (선택)
- ✅ Issue Templates
- ✅ PR Template

## 10. 브랜치 보호 규칙 (선택)

`Settings` → `Branches` → `Add rule`:
```
Branch name pattern: main

☑ Require a pull request before merging
☑ Require status checks to pass before merging
  - CI
☑ Require conversation resolution before merging
```

## 11. GitHub Pages (문서 호스팅, 선택)

Javadoc 호스팅:
```bash
./gradlew javadoc
```

`Settings` → `Pages`:
- Source: `Deploy from a branch`
- Branch: `gh-pages` (별도 생성 필요)

## 체크리스트

- [ ] GitHub 저장소 생성
- [ ] 로컬에서 Git 초기화 및 푸시
- [ ] GitHub Secrets 설정
- [ ] Topics 추가
- [ ] README 뱃지 추가
- [ ] 첫 릴리즈 생성
- [ ] Sonatype 계정 및 승인
- [ ] GPG 키 생성 및 업로드
- [ ] Maven Central 배포
- [ ] GitHub Actions 확인
- [ ] 브랜치 보호 규칙 설정

## 다음 단계

1. **홍보**:
   - Reddit (r/java)
   - Twitter/X
   - Dev.to
   - Korean developer communities

2. **문서 개선**:
   - Wiki 추가
   - 블로그 포스트 작성
   - 유튜브 튜토리얼

3. **기능 추가**:
   - 더 많은 AI 모델 지원
   - 커스터마이징 옵션
   - 플러그인 시스템

축하합니다! 🎉 이제 오픈소스 프로젝트가 준비되었습니다!
