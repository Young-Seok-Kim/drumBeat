# 변경 이력

버전을 올릴 때 이 파일 맨 위에 새 항목을 추가한다.
`app/build.gradle` 에는 `versionCode` / `versionName` 만 두고, 상세 내역은 여기에 적는다.

각 버전은 두 부분으로 적는다.

- **스토어 문구** — Play Console "이 버전의 새로운 기능"에 그대로 붙여넣는 칸.
  일반 텍스트만 쓴다 (마크다운·굵게 안 먹힘). **500자 제한**. 사용자가 체감하는 것만.
- **상세 내역** — 개발용 기록. 스토어에는 올리지 않는다.

---

## v1.6.1 (b9) — 2026-11-05 화면 구조 정리(하단 탭·상단바 제거), 청록색 테마, 광고 즉시 표시

### 스토어 문구

화면을 새로 정리했습니다. 상단바와 메뉴 서랍을 없애고 아래쪽 탭으로 초견과 BPM 측정을 바로 오갑니다.
색을 청록색 계열로 바꿨습니다. BPM을 잰 뒤 "메트로놈에 적용"을 누르면 메트로놈 템포가 바로 맞춰집니다.
광고가 뜨는 데 걸리던 시간을 줄였고, 화면을 옮겨도 다시 뜨기를 기다리지 않습니다.

### 상세 내역

- 구조: Toolbar·DrawerLayout·NavigationView·메인 메뉴 화면(MainFragment/MainViewModel/MenuItem) 삭제.
  BottomNavigationView(초견·BPM 측정) + NavigationUI.setupWithNavController. 시작 화면은 초견.
- 색: bd_* 팔레트를 보라에서 청록(#0F766E)/호박색 강조로 교체. 상태 표시줄은 배경색에 녹이고 아이콘 어둡게.
- 레이아웃: 초견·BPM 화면을 카드형(bd_card)으로 재구성, 화면 제목을 각 화면 안에 둠. 메트로놈 바는 가로 꽉 채움.
- BPM 측정: "메트로놈에 적용" 버튼(KEY_BPM 저장 → MetronomeFragment 가 pref 리스너로 반영).
- 광고: SDK 초기화를 Application.onCreate 백그라운드로(BeatDrumApp.kt), 화면마다 있던 AdView 3개를
  activity_main.xml 상단 배너 하나로 합침. resume/pause/destroy 를 액티비티 생명주기에 연결.

## v1.6 (b8) — 2026-11-05 메트로놈 박자표·정확한 타이밍·클릭음, 설정 기억

### 스토어 문구

메트로놈에 박자표(2/4·3/4·4/4)를 추가했고, 오래 틀어도 박자가 밀리지 않게 고쳤습니다.
클릭 소리를 또렷한 샘플로 바꿨습니다. BPM, 음표, 박자표, 초견 간격을 마지막 값으로 기억합니다.
가로로 돌렸을 때 초견 악보 비율이 어긋나던 문제와 뒤로가기가 꼬이던 문제를 고쳤습니다.

### 상세 내역

- 메트로놈: 박자표 스피너 추가(초견 화면과 동기화), 시작 시각 기준 절대 예약으로 드리프트 제거, SoundPool 클릭 샘플(res/raw)
- BPM 측정: 음표 배수 수식 오류 수정(8분음표면 절반이어야 하는데 2배였음), 빈 tickRunnable 제거, 3초 쉬면 새로 측정
- 뒤로가기: MainActivity 콜백 하나로 통합(MainFragment 콜백·onBackPressed 재정의 삭제)
- 내비게이션: popUpTo(main)+launchSingleTop 으로 백스택 누적 제거
- 초견: onConfigurationChanged 에서 스케일 타입 재적용, getIdentifier → drawable id 배열, ViewModel 패키지 선언 추가
- 설정 기억: SharedPreferences(Settings.kt) 로 BPM·음표·박자표·초견 간격 저장
- 코드 문자열을 리소스로 옮기고 영어 번역 추가
- 쓰지 않는 Compose 의존성 제거

## v1.5 (b7) — 2026-07-22 초견 음표 크기 수정

이 파일을 만들기 전에 이미 배포된 버전. 기록 시작점으로만 둔다.

### 스토어 문구

초견 화면에서 음표 크기가 맞지 않던 문제를 고쳤습니다.

### 상세 내역

- 초견 음표 크기 안 맞는 현상 수정
- GitHub Actions 배포 구성 추가 (main_qa → main 머지 시 versionCode 가 오르면 Play 프로덕션 자동 업로드)
