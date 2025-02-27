# 1.Java 17 기반 OpenJDK 이미지 사용
FROM eclipse-temurin:17-jdk  AS build

# 2. 작업 디렉토리 설정
WORKDIR /app

# 3. 프로젝트의 모든 소스 코드 복사
COPY . .
RUN chmod +x ./entrypoint.sh


# 4. Gradle 빌드 수행 (테스트 제외)
RUN ./gradlew clean build -x test

# 5. JDK 이미지 사용
FROM eclipse-temurin:17-jdk

# 6. 작업 디렉토리 설정
WORKDIR /app

# 7. 빌드된 JAR 파일 복사
COPY --from=build /app/build/libs/*.jar app.jar

# 8. 컨테이너에서 실행될 기본 명령어 설정
CMD ["java", "-jar", "app.jar"]