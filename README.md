# 💬 실시간 채팅 서비스

WebSocket 기반 실시간 채팅 서비스입니다.

## 🚀 배포 URL
[https://chat-service-production-eea8.up.railway.app]

## 🛠 기술 스택
- **Backend**: Java 17, Spring Boot, Spring Security, Spring WebSocket
- **Database**: MySQL, Redis
- **Frontend**: Thymeleaf, HTML/CSS
- **Deploy**: Railway, GitHub Actions

## ✨ 주요 기능
- 실시간 채팅 (WebSocket + STOMP)
- 채팅방 생성 / 삭제
- 메시지 삭제
- 입장 / 퇴장 알림
- 안읽은 메시지 수 표시
- Redis 메시지 캐싱으로 성능 개선

## 📌 트러블슈팅

**Redis 캐싱 적용**
- 문제: 채팅방 입장 시마다 DB 조회 발생
- 해결: Redis로 최근 메시지 캐싱, 캐시 히트 시 DB 쿼리 없이 응답

**Railway 배포 환경변수 설정**
- 문제: gitignore로 인해 properties 파일이 Railway에서 읽히지 않음
- 해결: Railway Variables에 환경변수 직접 등록

## 🖥 실행 방법
```bash
git clone https://github.com/nuj27301/chat-service.git
cd chat-service
# application.properties에 DB 정보 입력 후
./mvnw spring-boot:run
```