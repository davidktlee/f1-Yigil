# 이길로그

## 프로젝트 정보

- [배포 사이트](https://yigil.co.kr)

- [Notion](https://www.notion.so/c8649ee9c28b4e06b76df4fb75afebb4?pvs=4)
- [Figma](https://www.figma.com/file/Ka7DHbg9d0n535k6AiDbzs/%EC%9D%B4%EA%B8%B8-%EC%96%B4%EB%95%8C?type=design&node-id=0%3A1&mode=design&t=JDe415NH7dswuMPV-1)

## 기술 스택 & 팀원 역할

### 기술 스택

|    분류    |                   기술 이름                   |
| :--------: | :-------------------------------------------: |
|    언어    | [TypeScript](https://www.typescriptlang.org/) |
| 프레임워크 | [Next.js 14(App Router)](https://nextjs.org/) |

| 스타일링 | [Tailwind CSS](https://tailwindcss.com/) |
| 지도 | [Naver Maps API](https://www.ncloud.com/product/applicationService/maps) |
| 스키마 검증 | [Zod](https://zod.dev/) |

### 맡은 역할

1. 로그인 기능 중 네이버, 구글 OAuth 로그인
2. 마이페이지 기능
   - 내가 작성한 장소와 코스 수정 / 삭제 / 잠금
   - 팔로우 팔로잉 기능
   - 내 정보 수정 및 선호 지역 설정
3. 주변 지도 기능
   - Geolocation을 이용한 현재 위치 표시
   - 마커를 이용한 내가 작성한 장소 및 현재 위치의 주변 장소 표시
4. `Server Sent Event`를 이용한 알림 기능
5. 장소 상세 / 수정 페이지
6. 코스 수정 시 지도에 이동 가능한 경로 표시
7. `MSW(MockServiceWorker)` 환경설정 및 세팅 [\*]
8. 메타 데이터 및 og, twitter 설정

## 트러블슈팅

1. Next.js의 `next/image`를 통해 SVG 파일을 불러오니 `fill`, `stroke` 등의 속성을 사용할 수 없었던 문제가 있어 SVGR 라이브러리를 도입하여 해결함 [\*](https://github.com/Kernel360/f1-Yigil/pull/111)
2. 테스트 라이브러리 `Jest`와 `MSW` 의 호환성 문제로 `Vitest` 로 변경 [\*](https://davidktlee.notion.site/MSW-1323429b4b8a49f9a94f09be36893a4b)
3. **쿠키 에러 해결**

- 서버와 브라우저의 도메인이 달라 `samesite` 를 맞출 수 없어 인증에 실패
- 도메인을 맞추면 되는 간단한 부분이었지만 공부하다보니 사용하고 있는 NextJS의 장점인 `Server Action`을 통해 해결할 수 있을거라 생각해 적용
- 수치로는 측정할 수 없었으나 페이지 간 이동시 데이터의 빠른 시각화가 개선됨

4. **Next14 Cache 기능으로 불필요한 api 요청 제한 및 재검증**

- 기존에 캐싱 및 새로운 요청에 대한 재검증을 axios 및 tanstack-query 이용
- next14의 fetch의 cache 기능을 이용해 캐싱 기능 사용 및 revalidate 기능을 이용해 새로운 데이터 가져오도록 기능 구현
- 외부 라이브러리 사용 감소로 의존성을 줄여 유지보수의 용이성을 높이고 배포 용량을 줄임

5. **Api 응답 검증 라이브러리로 DX 개선**

- 타입스크립트만을 이용해 API 응답 검증을 했을 때 백엔드에서 응답으로 오는 에러들이 정확한 오류 메시지를 가지지 않은 경우가 많았고 응답 객체도 바뀌는 경우가 많아 디버깅하는데 어려움을 겪음.
- Zod 라이브러리를 이용해 응답 스키마를 백엔드와 함께 정의한 값으로 설정함
- 백엔드 응답 유효성 검증을 통해 에러의 위치와 원인을 정확하게 알려주어 DX 개선
