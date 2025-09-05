#!/bin/bash

# AWS RDS MySQL 인스턴스 생성 스크립트
# 사용법: ./create-rds.sh

echo "🚀 AWS RDS MySQL 인스턴스 생성 시작..."

# 기본 설정
DB_INSTANCE_IDENTIFIER="seasonthon-mysql"
DB_NAME="SeasonThon"
DB_USERNAME="admin"
DB_PASSWORD="SeasonThon2024!"
DB_INSTANCE_CLASS="db.t3.micro"
DB_ENGINE="mysql"
DB_ENGINE_VERSION="8.0"
DB_ALLOCATED_STORAGE="20"
DB_STORAGE_TYPE="gp2"
DB_VPC_SECURITY_GROUP_ID=""  # 자동 생성
DB_SUBNET_GROUP_NAME="seasonthon-subnet-group"  # 생성한 서브넷 그룹 사용
DB_REGION="ap-northeast-2"  # 서울 리전

echo "📋 생성할 RDS 인스턴스 정보:"
echo "  - 인스턴스 ID: $DB_INSTANCE_IDENTIFIER"
echo "  - 데이터베이스명: $DB_NAME"
echo "  - 사용자명: $DB_USERNAME"
echo "  - 인스턴스 클래스: $DB_INSTANCE_CLASS"
echo "  - 엔진: $DB_ENGINE $DB_ENGINE_VERSION"
echo "  - 스토리지: ${DB_ALLOCATED_STORAGE}GB $DB_STORAGE_TYPE"
echo "  - 리전: $DB_REGION"
echo ""

# RDS 인스턴스 생성
echo "🔨 RDS MySQL 인스턴스 생성 중..."
aws rds create-db-instance \
    --db-instance-identifier $DB_INSTANCE_IDENTIFIER \
    --db-instance-class $DB_INSTANCE_CLASS \
    --engine $DB_ENGINE \
    --engine-version $DB_ENGINE_VERSION \
    --master-username $DB_USERNAME \
    --master-user-password $DB_PASSWORD \
    --allocated-storage $DB_ALLOCATED_STORAGE \
    --storage-type $DB_STORAGE_TYPE \
    --db-name $DB_NAME \
    --vpc-security-group-ids $DB_VPC_SECURITY_GROUP_ID \
    --db-subnet-group-name $DB_SUBNET_GROUP_NAME \
    --backup-retention-period 7 \
    --multi-az \
    --publicly-accessible \
    --storage-encrypted \
    --region $DB_REGION

if [ $? -eq 0 ]; then
    echo "✅ RDS 인스턴스 생성 요청이 성공적으로 제출되었습니다!"
    echo "⏳ 인스턴스가 생성되는 동안 잠시 기다려주세요..."
    echo ""
    echo "📊 인스턴스 상태 확인:"
    echo "aws rds describe-db-instances --db-instance-identifier $DB_INSTANCE_IDENTIFIER --region $DB_REGION"
    echo ""
    echo "🔗 엔드포인트 확인:"
    echo "aws rds describe-db-instances --db-instance-identifier $DB_INSTANCE_IDENTIFIER --region $DB_REGION --query 'DBInstances[0].Endpoint.Address' --output text"
else
    echo "❌ RDS 인스턴스 생성에 실패했습니다."
    exit 1
fi
