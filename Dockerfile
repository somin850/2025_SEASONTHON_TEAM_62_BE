FROM openjdk:21-jdk-slim

WORKDIR /app

COPY build/libs/seasonthon-0.0.1-SNAPSHOT.jar app.jar

# 환경 변수 설정
ENV SPRING_PROFILES_ACTIVE=prod
ENV SPRING_DATASOURCE_URL=jdbc:mysql://seasonthon-mysql.ch2my6wuyfnt.ap-northeast-2.rds.amazonaws.com:3306/SeasonThon?useSSL=false&characterEncoding=UTF-8&serverTimezone=UTC
ENV SPRING_DATASOURCE_USERNAME=admin
ENV SPRING_DATASOURCE_PASSWORD=SeasonThon2024!
ENV JWT_SECRET="394kr6dvlZiSi+kUIDqy4Q/ho/Dt3pqcMNXCS6kruCjdGBHGfBcaNzjLUqGnS0JFueBgm/QrKskwf5mQIBgLtw=="
ENV GOOGLE_CLIENT_ID="${GOOGLE_CLIENT_ID}"
ENV GOOGLE_CLIENT_SECRET="${GOOGLE_CLIENT_SECRET}"
ENV NAVER_CLIENT_ID="zKU2JcvL2aGeiLG2xpWI"
ENV NAVER_CLIENT_SECRET="LvDiJCIhid"
ENV KAKAO_CLIENT_ID="bbe833ae2a34f50da00470f4df648388"
ENV KAKAO_CLIENT_SECRET="RJhuZVwhlo42B7tQx0ZyBZiVqyGbVDcf"
ENV BASE_URL="https://your-domain.com/"

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]