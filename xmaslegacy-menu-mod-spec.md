# XmasLegacy 커스텀 메인메뉴 모드 — 구현 계획서

> 이 문서는 나중에 그대로 복붙해서 이어서 요청할 수 있도록 정리한 스펙입니다.
> 목표: 마인크래프트 게임 자체의 타이틀 화면(메인 메뉴)을 하늘색+흰눈 테마로 완전히 개조하는 **자바 Fabric 모드**를 처음부터 만든다. (페더클/던이 쓰는 방식과 동일한 접근)

---

## 1. 한 줄 목표
바닐라 마인크래프트 타이틀 화면을 가로채서(Mixin), 하늘색+흰눈 테마의 배경/버튼/로고로 완전히 새로 그리고, 눈 내리는 파티클 애니메이션을 추가한다. 이 모드는 런처(XmasLegacy Launcher)가 새 인스턴스를 만들 때 **자동으로 mods 폴더에 심어주는 방식**으로 배포한다.

---

## 2. 버전 확정
| 항목 | 값 | 이유 |
|---|---|---|
| 마인크래프트 버전 | **1.21.1** | 현재 가장 널리 쓰이는 안정 버전, Fabric 생태계 지원 제일 탄탄함 |
| 모드로더 | **Fabric** | Forge보다 가볍고, Mixin 작업이 상대적으로 접근성 좋음. FancyMenu 등 참고할 오픈소스 사례도 Fabric이 제일 많음 |
| Java 버전 | **Java 21** | 1.21.1이 요구하는 최소 버전 |
| 빌드 도구 | **Gradle + Fabric Loom** | Fabric 모드 개발 표준 툴체인 |

> ⚠️ 다른 마인크래프트 버전을 나중에 추가하려면, 이 프로젝트를 그 버전에 맞게 **별도로 포팅**해야 함 (마인크래프트 내부 클래스/메서드 이름이 버전마다 바뀌기 때문). 지금은 1.21.1 하나에 집중하고, 안정화되면 다른 버전(1.20.1 등)으로 확장 여부를 다시 논의.

---

## 3. 기술 스택 상세

| 구성요소 | 사용 기술 |
|---|---|
| 모드로더 API | Fabric Loader + Fabric API |
| 화면 가로채기 | **Mixin** — `net.minecraft.client.gui.screen.TitleScreen` 클래스에 주입 |
| 렌더링 | 마인크래프트 자체 렌더 파이프라인(`DrawContext`) 사용 — 별도 렌더엔진 안 씀 |
| 리소스 | 커스텀 텍스처(배경, 버튼, 로고), 눈 파티클 스프라이트 |
| 설정/레이아웃 | 처음엔 하드코딩 → 추후 JSON 설정 파일로 분리해 유연하게 바꿀 수 있게 (FancyMenu처럼) |
| 빌드/검증 환경 | **로컬 자바 개발 환경이 없는 상태로 진행** → GitHub Actions에서 Gradle 빌드 돌리며 에러 잡는 방식 (런처 만들 때와 동일한 패턴) |

---

## 4. 기능 범위 (라운드별로 나눠서 진행)

### 라운드 1 — 기반 다지기 (제일 먼저)
- [ ] Fabric 모드 프로젝트 뼈대 생성 (`fabric.mod.json`, `build.gradle`, `mixins.json`)
- [ ] `TitleScreen`에 Mixin 주입해서 **배경만** 하늘색+흰눈 그라데이션으로 교체 (기존 바닐라 버튼 위치는 그대로 유지)
- [ ] GitHub Actions로 빌드되는지 확인 (여기가 첫 번째 관문)

### 라운드 2 — 비주얼 추가
- [ ] 로고를 XmasLegacy 눈꽃 로고로 교체
- [ ] 버튼 텍스처를 하늘색 톤 커스텀 디자인으로 교체
- [ ] 눈 내리는 파티클 애니메이션 추가 (화면 위에서 아래로 떨어지는 흰 점, 속도/크기 랜덤)

### 라운드 3 — 마감/디테일
- [ ] 버튼 hover 시 반짝이는 효과
- [ ] 배경에 은은한 오로라/반짝임 효과 (선택)
- [ ] 성능 체크 (파티클 개수 너무 많으면 저사양에서 버벅일 수 있어서 조절)

### 나중에 (여유 생기면)
- [ ] 완전히 새로운 레이아웃 (탭 구조, 코스메틱 메뉴 등 페더클 수준)
- [ ] 설정 파일로 레이아웃 커스터마이징 가능하게 (FancyMenu처럼)

---

## 5. 프로젝트 구조 (예상)
```
xmaslegacy-menu-mod/
├── build.gradle
├── settings.gradle
├── gradle.properties          ← MC 버전, Fabric API 버전, Loom 버전 등 명시
├── src/main/java/com/xmaslegacy/menu/
│   ├── XmasLegacyMenuMod.java         ← 모드 진입점
│   ├── mixin/
│   │   └── TitleScreenMixin.java      ← 타이틀 화면 가로채는 Mixin
│   ├── render/
│   │   ├── SnowParticleRenderer.java  ← 눈 내리는 효과
│   │   └── ThemeRenderer.java         ← 배경/버튼 그리기
│   └── config/
│       └── ThemeConfig.java           ← 색상/레이아웃 설정값
├── src/main/resources/
│   ├── fabric.mod.json                ← 모드 메타데이터
│   ├── xmaslegacymenu.mixins.json     ← Mixin 등록
│   └── assets/xmaslegacymenu/
│       ├── textures/gui/
│       │   ├── background.png
│       │   ├── button_normal.png
│       │   ├── button_hover.png
│       │   └── logo.png
│       └── icon.png                   ← 모드 아이콘
└── .github/workflows/
    └── build.yml                      ← GitHub Actions로 Gradle 빌드
```

---

## 6. 색상 팔레트 (기존 런처 테마와 통일)
| 용도 | 색상 |
|---|---|
| 배경 그라데이션 상단 | `#2B79CC` (하늘색) |
| 배경 그라데이션 하단 | `#0E4C5C` (짙은 바다색, 야간 하늘 느낌) |
| 버튼 기본 | `#5196DF` |
| 버튼 hover | `#8EBAEB` |
| 눈 파티클 | `#FFFFFF` (약간의 투명도) |
| 로고 포인트 | `#2B79CC` |
| 텍스트 | `#EAF6F6` |

---

## 7. 배포/통합 방식
1. GitHub Actions에서 빌드된 `.jar` 파일을 **GitHub Releases**에 올림
2. 런처(XmasLegacy Launcher) 쪽 코드에서, **새 인스턴스 생성 시(1.21.1 + Fabric 조합일 때) 이 jar를 자동으로 mods 폴더에 다운로드/복사**하는 로직 추가
   - 이 부분은 이전에 논의한 "바닐라 숨기고 Fabric+모드팩 고정" 작업이랑 같이 진행
3. 필요한 의존 모드(Fabric API)는 모드린스 API에서 자동으로 같이 받아오게 처리

---

## 8. 검증 방법 (로컬 개발환경 없이 진행하는 현실적 방법)
- 저(Claude)는 여기서 자바/그레들 컴파일 환경에 직접 접근이 안 되기 때문에, **매 라운드마다 코드를 짜서 드리면 → GitHub Actions로 빌드 → 에러 로그 다시 붙여넣기 → 수정** 순서로 반복
- 런처 만들 때와 완전히 같은 패턴이라 이미 익숙한 흐름

---

## 9. 지금 당장 할 일
- [ ] **라운드 1** 착수 — 모드 프로젝트 뼈대 + 배경색만 바뀐 최소 버전부터 시작
- [ ] 새 GitHub 저장소 하나 파기 (예: `xmaslegacy-menu-mod`) — 런처 저장소랑 분리하는 걸 추천 (관리 편함)

---

### 메모
(작업하면서 생각나는 것 여기 계속 추가)
