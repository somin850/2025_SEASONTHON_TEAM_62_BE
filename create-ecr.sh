#!/bin/bash

# AWS ECR 리포지토리 생성 및 이미지 푸시 스크립트
# 사용법: ./create-ecr.sh

echo "🚀 AWS ECR 리포지토리 생성 및 이미지 푸시 시작..."

# 기본 설정
ECR_REPOSITORY_NAME="seasonthon"
AWS_REGION="ap-northeast-2"  # 서울 리전
AWS_ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)

echo "📋 ECR 설정 정보:"
echo "  - 리포지토리명: $ECR_REPOSITORY_NAME"
echo "  - AWS 계정 ID: $AWS_ACCOUNT_ID"
echo "  - 리전: $AWS_REGION"
echo ""

# ECR 리포지토리 생성
echo "🔨 ECR 리포지토리 생성 중..."
aws ecr create-repository \
    --repository-name $ECR_REPOSITORY_NAME \
    --region $AWS_REGION \
    --image-scanning-configuration scanOnPush=true

if [ $? -eq 0 ]; then
    echo "✅ ECR 리포지토리가 성공적으로 생성되었습니다!"
else
    echo "⚠️  ECR 리포지토리가 이미 존재하거나 생성에 실패했습니다."
fi

# ECR 로그인
echo "🔐 ECR에 로그인 중..."
aws ecr get-login-password --region $AWS_REGION | docker login --username AWS --password-stdin $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com

if [ $? -eq 0 ]; then
    echo "✅ ECR 로그인이 성공했습니다!"
else
    echo "❌ ECR 로그인에 실패했습니다."
    exit 1
fi

# Docker 이미지 태그
ECR_URI="$AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/$ECR_REPOSITORY_NAME"
echo "🏷️  Docker 이미지에 ECR 태그 추가 중..."
docker tag seasonthon:latest $ECR_URI:latest

# Docker 이미지 푸시
echo "📤 Docker 이미지를 ECR에 푸시 중..."
docker push $ECR_URI:latest

if [ $? -eq 0 ]; then
    echo "✅ Docker 이미지가 성공적으로 ECR에 푸시되었습니다!"
    echo ""
    echo "📊 ECR 이미지 정보:"
    echo "  - 이미지 URI: $ECR_URI:latest"
    echo "  - 리포지토리 URL: https://$AWS_REGION.console.aws.amazon.com/ecr/repositories/private/$AWS_ACCOUNT_ID/$ECR_REPOSITORY_NAME"
    echo ""
    echo "🔗 ECS 태스크 정의에서 사용할 이미지 URI:"
    echo "$ECR_URI:latest"
else
    echo "❌ Docker 이미지 푸시에 실패했습니다."
    exit 1
fi


