
# STEP 1: Java 17 기반의 런타임 이미지 사용
FROM openjdk:17-jdk-slim

# STEP 2: 인자 (JAR 파일 경로) 정의. 빌드 시 사용할 수 있습니다.
ARG JAR_FILE=build/libs/*.jar

# STEP 3: 빌드된 JAR 파일을 컨테이너 내부로 복사
COPY ${JAR_FILE} app.jar

# STEP 4: 타임존 설정 (선택 사항이지만 권장)
ENV TZ Asia/Seoul
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/local timezone

# STEP 5: 컨테이너가 시작될 때 실행될 명령어 정의
ENTRYPOINT ["java", "-jar", "app.jar"]

# STEP 6: 애플리케이션이 사용할 포트 노출 (SpringBoot 기본 포트 8080)
EXPOSE 8080

