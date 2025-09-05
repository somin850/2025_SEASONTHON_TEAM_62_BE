#!/bin/bash

echo "🚀 SeasonThon 애플리케이션 업데이트 시작..."

# 1. 빌드
echo "📦 Gradle 빌드 중..."
./gradlew build

# 2. Docker 이미지 빌드
echo "🐳 Docker 이미지 빌드 중..."
docker build --platform linux/amd64 -t seasonthon:latest .

# 3. ECR 로그인
echo "🔐 ECR 로그인 중..."
aws ecr get-login-password --region ap-northeast-2 | docker login --username AWS --password-stdin 883105811636.dkr.ecr.ap-northeast-2.amazonaws.com

# 4. 이미지 태그 및 푸시
echo "📤 ECR에 이미지 푸시 중..."
docker tag seasonthon:latest 883105811636.dkr.ecr.ap-northeast-2.amazonaws.com/seasonthon:latest
docker push 883105811636.dkr.ecr.ap-northeast-2.amazonaws.com/seasonthon:latest

# 5. ECS 서비스 업데이트
echo "🔄 ECS 서비스 업데이트 중..."
aws ecs update-service \
  --cluster seasonthon-cluster \
  --service seasonthon-service \
  --force-new-deployment \
  --region ap-northeast-2

echo "✅ 업데이트 완료!"
echo "🌐 접속 URL: http://seasonthon-alb-272154529.ap-northeast-2.elb.amazonaws.com/"
