# Maven Central 배포 가이드

## 1. 사전 준비

### 1.1 Sonatype 계정 생성
1. https://central.sonatype.com 에서 계정 생성
2. 새 이슈 생성: "New Project"
3. Group Id: `io.github.chanani` 입력
4. GitHub 저장소 URL 제공
5. 승인 대기 (보통 1-2일)

### 1.2 GPG 키 생성
```bash
# GPG 키 생성
gpg --gen-key

# 키 확인
gpg --list-keys

# 공개 키 업로드
gpg --keyserver keyserver.ubuntu.com --send-keys YOUR_KEY_ID
```

### 1.3 자격증명 설정
`~/.gradle/gradle.properties` 파일 생성:
```properties
ossrhUsername=your-sonatype-username
ossrhPassword=your-sonatype-password

signing.keyId=YOUR_KEY_ID
signing.password=your-gpg-password
signing.secretKeyRingFile=/Users/username/.gnupg/secring.gpg
```

## 2. 배포 방법

### 2.1 빌드 및 검증
```bash
# 빌드
./gradlew clean build

# 생성된 파일 확인
./gradlew publishToMavenLocal
```

### 2.2 Maven Central 배포
```bash
# Staging 저장소에 업로드
./gradlew publishMavenJavaPublicationToOSSRHRepository

# 또는 전체 퍼블리싱 프로세스
./gradlew publish
```

### 2.3 Sonatype에서 Release
1. https://s01.oss.sonatype.org 로그인
2. "Staging Repositories" 클릭
3. 업로드된 저장소 선택
4. "Close" 버튼 클릭 (검증 시작)
5. 검증 완료 후 "Release" 버튼 클릭
6. 약 2-4시간 후 Maven Central에 동기화됨

## 3. 버전 관리

### 3.1 버전 업데이트
`build.gradle`에서:
```gradle
version = '1.0.1'  // 버전 변경
```

### 3.2 SNAPSHOT 배포 (테스트용)
```gradle
version = '1.0.1-SNAPSHOT'
```

## 4. 배포 후 사용

### 4.1 Gradle
```gradle
dependencies {
    implementation 'io.github.chanani:claude-reviewer:1.0.0'
}
```

### 4.2 Maven
```xml
<dependency>
    <groupId>io.github.chanani</groupId>
    <artifactId>claude-reviewer</artifactId>
    <version>1.0.0</version>
</dependency>
```

## 5. 자동화 (GitHub Actions)

`.github/workflows/publish.yml`:
```yaml
name: Publish to Maven Central

on:
  release:
    types: [created]

jobs:
  publish:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4

      - name: Set up JDK 21
        uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'

      - name: Publish to Maven Central
        env:
          OSSRH_USERNAME: ${{ secrets.OSSRH_USERNAME }}
          OSSRH_PASSWORD: ${{ secrets.OSSRH_PASSWORD }}
          SIGNING_KEY_ID: ${{ secrets.SIGNING_KEY_ID }}
          SIGNING_PASSWORD: ${{ secrets.SIGNING_PASSWORD }}
          SIGNING_KEY: ${{ secrets.SIGNING_KEY }}
        run: |
          echo "$SIGNING_KEY" | base64 -d > secring.gpg
          ./gradlew publish \
            -Psigning.keyId=$SIGNING_KEY_ID \
            -Psigning.password=$SIGNING_PASSWORD \
            -Psigning.secretKeyRingFile=secring.gpg
```

## 참고 자료
- https://central.sonatype.org/publish/
- https://docs.gradle.org/current/userguide/publishing_maven.html
