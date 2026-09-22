# server9091

중계 서버 테스트용 외부 API 목 서버입니다. 기본 포트는 `9091`이며, JSON 응답은 `GET /mock/json`으로 확인할 수 있습니다.

## 오프라인 빌드

이 저장소에는 Gradle 플러그인과 라이브러리를 위한 `offline-maven/`, Gradle 배포본, JDK 21 번들이 함께 포함되어 있습니다. 따라서 **한 번 클론한 뒤에는 네트워크나 기존 Gradle 캐시 없이** 빌드할 수 있습니다.

### 사전 조건

- 최초 `git clone`에는 네트워크가 필요합니다.
- macOS ARM64에서는 포함된 Eclipse Temurin JDK 21.0.7을 자동으로 사용합니다.
- 그 밖의 OS/CPU에서는 시스템에 **JDK 21**을 설치한 뒤 실행하세요. `java`와 `javac`가 모두 JDK 21이어야 합니다.

### 새 클론에서 실행

```bash
git clone <repository-url>
cd server9091
./build-offline.sh
```

성공하면 마지막에 `BUILD SUCCESSFUL`이 표시됩니다. 이 명령은 다음을 수행합니다.

1. GitHub 파일 제한 때문에 분할되어 있는 Gradle 배포본과 JDK 모듈 파일을 로컬에서 자동 복원합니다.
2. 프로젝트 내부 `.offline-gradle-home/`만 Gradle 캐시로 사용합니다. 사용자의 `~/.gradle` 캐시는 사용하지 않습니다.
3. `offline-maven/`만 의존성 저장소로 사용하고, Gradle을 `--offline`으로 실행합니다.
4. `build` 태스크를 실행하므로 컴파일과 테스트가 함께 수행됩니다.

복원된 대형 파일과 `.offline-gradle-home/`은 Git에서 무시되므로 빌드 후 변경 사항으로 나타나지 않습니다.

### 오프라인 여부를 직접 확인하는 방법

클론이 끝난 뒤 네트워크를 끊거나 방화벽으로 차단한 상태에서 다음을 실행해도 동일하게 성공해야 합니다.

```bash
./build-offline.sh
```

일반 `./gradlew build`는 온라인 의존성 갱신 용도로 남겨둔 명령입니다. 오프라인 재현성 확인에는 사용하지 말고 항상 `./build-offline.sh`를 사용하세요.

### 의존성을 변경했을 때

의존성이나 플러그인 버전을 바꾼 경우에는 네트워크가 가능한 환경에서 먼저 새 의존성을 받습니다.

```bash
./gradlew build
./gradlew prepareOfflineMaven
./build-offline.sh
```

마지막 명령이 성공한 뒤 `offline-maven/`의 변경 사항을 함께 커밋해야 다음 새 클론도 오프라인 빌드할 수 있습니다. 대형 Gradle/JDK 번들을 갱신했다면 Git 호스팅 파일 제한을 넘지 않도록 기존과 같이 분할 파일로 저장해야 합니다.

추가 설명은 [OFFLINE_BUILD.md](OFFLINE_BUILD.md)를 참고하세요.
