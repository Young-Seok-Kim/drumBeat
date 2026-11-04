# Beat Drum — 초견 강화 / 음성 메트로놈

드럼 초견 연습과 메트로놈, 셀프 비트 측정 기능을 제공하는 Android 앱입니다.
패키지명 `com.youngs.beatdrum`. 광고·결제 없이 무료로 제공합니다.

## 브랜치와 배포 (CI/CD)

작업은 `main_qa` 브랜치에서 하고, `main` 은 배포 트리거입니다. `main` 에 직접 커밋하지 않습니다.

```
main_qa 에 push ──▶ auto-pr.yml 이 main 으로 가는 PR 을 만들거나 갱신
       │
       └─▶ ci.yml 유닛 테스트

PR 머지(main 에 push) ──▶ deploy.yml
       ├─ app/build.gradle 의 versionCode 가 올랐으면
       │    유닛 테스트 → 서명된 AAB 빌드 → Play 프로덕션 업로드
       │    → v{versionName}-b{versionCode} 태그·GitHub 릴리스 자동 생성
       └─ versionCode 가 그대로면 아무것도 올리지 않음
```

### 새 버전 내보내는 순서

1. `main_qa` 에서 코드를 고친다.
2. `app/build.gradle` 의 `versionCode` 를 +1 하고 `versionName` 을 올린다.
3. `CHANGELOG.md` 맨 위에 `## v{versionName} (b{versionCode})` 항목을 추가하고
   `### 스토어 문구` 블록을 쓴다. 이 블록이 Play 의 "이 버전의 새로운 기능"이 된다 (500자 제한).
4. 커밋 → `main_qa` 에 push. 봇이 `main` 으로 가는 PR 을 만든다.
5. PR 을 머지한다. 그 순간 Play 프로덕션에 올라가고, 심사가 끝나면 스토어에 반영된다.

### 워크플로 파일

| 파일 | 역할 |
| --- | --- |
| `.github/workflows/ci.yml` | `main`·`main_qa` push 와 PR 에서 유닛 테스트 |
| `.github/workflows/auto-pr.yml` | `main_qa` push 때 `main` 으로 가는 PR 생성·갱신 |
| `.github/workflows/deploy.yml` | `main` push 때 versionCode 가 올랐으면 AAB 빌드 → Play 업로드 → 태그·릴리스 |
| `.github/workflows/preflight.yml` | 시크릿·권한 점검만. 워크플로 파일이 바뀐 `main_qa` push 와 수동 실행 |
| `.github/workflows/create_apk.yml` | 커밋 메시지에 `create apk` 가 있으면 서명된 APK 를 아티팩트로 생성 (수동 테스트용) |

### 저장소 시크릿 (Settings → Secrets and variables → Actions)

| 이름 | 값 |
| --- | --- |
| `PLAY_SERVICE_ACCOUNT_JSON` | Play Console 에 "출시 관리" 권한으로 초대한 서비스 계정 키 JSON 내용 그대로 |
| `KEYSTORE_BASE64` | 서명 키스토어(.jks)를 base64 로 (`base64 -w0 파일`) |
| `KEYSTORE_PASSWORD` | 키스토어 비밀번호 |
| `KEY_ALIAS` | 키 별칭 |
| `KEY_PASSWORD` | 키 비밀번호 |

- 시크릿을 넣거나 바꾼 뒤에는 Actions 탭에서 **Preflight (배포 점검)** 를 먼저 돌려 통과하는지 확인한 다음 PR 을 머지한다.
- `auto-pr.yml` 이 PR 을 만들려면 저장소 Settings → Actions → General → Workflow permissions 의
  "Allow GitHub Actions to create and approve pull requests" 가 켜져 있어야 한다.
- 같은 `versionCode` 는 Play 에 두 번 올라가지 않는다. 배포가 실패해 다시 올릴 때는 코드를 고치지 않았더라도 `versionCode` 를 올려야 한다.
