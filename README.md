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
export OSHU_JWT_SECRET='충분히_긴_JWT_서명_시크릿'
./gradlew bootRun
```

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`
- OpenAPI YAML: `http://localhost:8080/v3/api-docs.yaml`
- 정적 OpenAPI YAML: [openapi.yaml](openapi.yaml)

`PUBLIC_DATA_SERVICE_KEY`가 없거나 공공 API 연결에 실패하면 지도 API는 H2에 넣어 둔 샘플 가게를 반환합니다. 키는 [소상공인시장진흥공단 상가(상권)정보 API](https://www.data.go.kr/data/15012005/openapi.do)에서 발급받습니다.

## 주요 API

| 구분 | Endpoint |
| --- | --- |
| 회원가입/로그인 | `POST /auth/signup`, `POST /auth/login` |
| 가게/지도 | `GET /stores`, `GET /stores/map`, `GET /stores/{storeId}` |
| 행사 | `GET /promotions`, `GET /stores/{storeId}/promotions` |
| 점주 관리 | `/owner/stores`, `/owner/promotions`, `/owner/time-sales` |

점주 API는 `POST /auth/login`으로 받은 `accessToken`을 Swagger의 **Authorize** 또는 `Authorization: Bearer {token}` 헤더에 넣어 호출합니다. 회원가입 계정은 기본적으로 `OWNER` 권한으로 저장되며, 로그인 토큰에는 `role=OWNER`와 `authorities=["ROLE_OWNER"]`가 포함됩니다.

## Google 로그인

클라이언트는 `GET /oauth2/authorization/google`로 **페이지 이동**을 시작해야 합니다. `fetch`나 Axios 호출로 시작하면 최종 `oshu://` 딥링크를 앱으로 열 수 없습니다. Google Cloud Console의 승인된 리디렉션 URI에는 배포 API의 `https://<api-domain>/login/oauth2/code/google`을 등록하세요. `GOOGLE_REDIRECT_URI`를 비워 두면 요청의 공개 도메인으로 이 주소를 자동 생성합니다.

## Claude 할인 시간대 추천

점주는 하루 단위로 시간대별 주문 건수를 저장하고, 해당 하루 데이터 기준의 할인 시간·할인율·설명을 받습니다. `ANTHROPIC_API_KEY`는 서버 환경변수에만 설정합니다.

```http
POST /owner/stores/{storeId}/order-statistics
Authorization: Bearer {accessToken}

{"orderDate":"2026-07-15","hourlyOrderCounts":[{"hour":12,"orderCount":8},{"hour":15,"orderCount":1}]}
```

```http
GET /owner/stores/{storeId}/discount-recommendations?orderDate=2026-07-15
Authorization: Bearer {accessToken}
```

`orderDate`를 생략하면 오늘(한국 시간) 데이터를 분석합니다.

## 검증

```bash
./gradlew test
```

## Docker 배포

Docker Compose는 애플리케이션과 MySQL을 함께 실행합니다. MySQL 데이터는 `mysql-data` 볼륨에 유지됩니다.

```bash
cp .env.example .env
# .env에서 MYSQL_PASSWORD, MYSQL_ROOT_PASSWORD, PUBLIC_DATA_SERVICE_KEY, OSHU_JWT_SECRET을 설정
docker compose up -d --build
docker compose logs -f app
```

실행 후 Swagger UI는 `http://서버_IP:8080/swagger-ui.html`에서 확인할 수 있습니다. 운영 환경에서는 Nginx와 HTTPS를 앞에 두고 8080 포트를 외부에 공개하지 않습니다.
