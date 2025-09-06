#!/bin/bash

echo "🔍 AWS 권한 확인 스크립트"
echo "=========================="

# 현재 사용자 정보
echo "📋 현재 사용자 정보:"
aws sts get-caller-identity

echo ""
echo "🔐 권한 테스트:"

# RDS 권한 테스트
echo "1. RDS 권한 테스트..."
aws rds describe-db-instances --region ap-northeast-2 --max-items 1 > /dev/null 2>&1
if [ $? -eq 0 ]; then
    echo "   ✅ RDS 권한 있음"
else
    echo "   ❌ RDS 권한 없음"
fi

# EC2 권한 테스트
echo "2. EC2 권한 테스트..."
aws ec2 describe-vpcs --region ap-northeast-2 --max-items 1 > /dev/null 2>&1
if [ $? -eq 0 ]; then
    echo "   ✅ EC2 권한 있음"
else
    echo "   ❌ EC2 권한 없음"
fi

# ECS 권한 테스트
echo "3. ECS 권한 테스트..."
aws ecs list-clusters --region ap-northeast-2 > /dev/null 2>&1
if [ $? -eq 0 ]; then
    echo "   ✅ ECS 권한 있음"
else
    echo "   ❌ ECS 권한 없음"
fi

# ECR 권한 테스트
echo "4. ECR 권한 테스트..."
aws ecr describe-repositories --region ap-northeast-2 --max-items 1 > /dev/null 2>&1
if [ $? -eq 0 ]; then
    echo "   ✅ ECR 권한 있음"
else
    echo "   ❌ ECR 권한 없음"
fi

echo ""
echo "🎯 모든 권한이 있으면 배포를 진행할 수 있습니다!"


