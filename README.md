# OSHU-BE

유성구 소상공인의 가게 지도, 실시간 홍보 게시물, 타임세일을 제공하는 Spring Boot API 서버입니다.

## 구조

```
auth/             domain, presentation, service
store/            domain, presentation, service, exception, infrastructure
promotion/        domain, presentation, service, exception
timesale/         domain, presentation, service, exception
common/           전역 예외 처리와 공통 페이징·검증
config/           Security, Swagger, 샘플 데이터 설정
```

각 기능 패키지의 `presentation`에는 Controller와 DTO, `domain`에는 Entity와 Repository,
`service`에는 유스케이스와 변환·조회 보조 클래스, `exception`에는 기능 전용 예외를 둡니다.

## 실행

Java 17이 필요합니다.

```bash
export PUBLIC_DATA_SERVICE_KEY='공공데이터포털_일반_인증키'
export OSHU_OWNER_TOKEN='점주용_개발_토큰'
./gradlew bootRun
```

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`
- OpenAPI YAML: `http://localhost:8080/v3/api-docs.yaml`

`PUBLIC_DATA_SERVICE_KEY`가 없거나 공공 API 연결에 실패하면 지도 API는 H2에 넣어 둔 샘플 가게를 반환합니다. 키는 [소상공인시장진흥공단 상가(상권)정보 API](https://www.data.go.kr/data/15012005/openapi.do)에서 발급받습니다.

## 주요 API

| 구분 | Endpoint |
| --- | --- |
| 회원가입/로그인 | `POST /api/v1/auth/signup`, `POST /api/v1/auth/login` |
| 가게/지도 | `GET /api/v1/stores`, `GET /api/v1/stores/map`, `GET /api/v1/stores/{storeId}` |
| 행사 | `GET /api/v1/promotions`, `GET /api/v1/stores/{storeId}/promotions` |
| 점주 관리 | `/api/v1/owner/stores`, `/api/v1/owner/promotions`, `/api/v1/owner/time-sales` |

점주 API는 Swagger의 **Authorize**에 `OSHU_OWNER_TOKEN` 값을 Bearer 토큰으로 넣어 호출합니다. 현재 로그인 토큰은 Swagger 시연을 위한 개발용 고정 토큰이며, 배포 전에는 JWT 발급·검증으로 교체해야 합니다.

## 검증

```bash
./gradlew test
```
