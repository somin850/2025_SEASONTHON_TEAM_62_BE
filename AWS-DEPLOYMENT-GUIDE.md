# 🚀 SeasonThon AWS 배포 가이드

## 📋 개요
이 가이드는 SeasonThon Spring Boot 애플리케이션을 AWS ECS + RDS MySQL로 배포하는 방법을 설명합니다.

## 🛠️ 사전 준비사항

### 1. AWS 계정 설정
```bash
# AWS CLI 설정
aws configure
# Access Key ID: [입력]
# Secret Access Key: [입력]
# Default region name: ap-northeast-2
# Default output format: json
```

### 2. Docker 설치 확인
```bash
docker --version
```

## 🗄️ 1단계: RDS MySQL 인스턴스 생성

### RDS 인스턴스 생성
```bash
./create-rds.sh
```

### RDS 엔드포인트 확인
```bash
aws rds describe-db-instances \
  --db-instance-identifier seasonthon-mysql \
  --region ap-northeast-2 \
  --query 'DBInstances[0].Endpoint.Address' \
  --output text
```

### Dockerfile 업데이트
RDS 엔드포인트를 확인한 후, Dockerfile의 `SPRING_DATASOURCE_URL`을 업데이트하세요:
```dockerfile
ENV SPRING_DATASOURCE_URL=jdbc:mysql://[RDS-ENDPOINT]:3306/SeasonThon?useSSL=false&characterEncoding=UTF-8&serverTimezone=UTC
```

## 🐳 2단계: Docker 이미지 빌드 및 ECR 푸시

### Docker 이미지 재빌드
```bash
docker build -t seasonthon:latest .
```

### ECR 리포지토리 생성 및 이미지 푸시
```bash
./create-ecr.sh
```

## ☁️ 3단계: ECS 클러스터 및 서비스 생성

### ECS 클러스터 생성
```bash
./create-ecs.sh
```

### VPC 및 보안 그룹 설정
```bash
# 기본 VPC 정보 확인
aws ec2 describe-vpcs --filters "Name=is-default,Values=true" --region ap-northeast-2

# 기본 서브넷 정보 확인
aws ec2 describe-subnets --filters "Name=vpc-id,Values=[VPC-ID]" --region ap-northeast-2
```

## 🔧 4단계: 추가 설정

### 보안 그룹 생성
```bash
# ECS용 보안 그룹 생성
aws ec2 create-security-group \
  --group-name seasonthon-ecs-sg \
  --description "Security group for SeasonThon ECS" \
  --vpc-id [VPC-ID] \
  --region ap-northeast-2

# HTTP 트래픽 허용
aws ec2 authorize-security-group-ingress \
  --group-id [SECURITY-GROUP-ID] \
  --protocol tcp \
  --port 8080 \
  --cidr 0.0.0.0/0 \
  --region ap-northeast-2
```

### RDS 보안 그룹 설정
```bash
# RDS용 보안 그룹 생성
aws ec2 create-security-group \
  --group-name seasonthon-rds-sg \
  --description "Security group for SeasonThon RDS" \
  --vpc-id [VPC-ID] \
  --region ap-northeast-2

# MySQL 포트 허용 (ECS에서만)
aws ec2 authorize-security-group-ingress \
  --group-id [RDS-SECURITY-GROUP-ID] \
  --protocol tcp \
  --port 3306 \
  --source-group [ECS-SECURITY-GROUP-ID] \
  --region ap-northeast-2
```

## 🌐 5단계: Application Load Balancer 설정

### ALB 생성
```bash
# ALB 생성
aws elbv2 create-load-balancer \
  --name seasonthon-alb \
  --subnets [SUBNET-ID-1] [SUBNET-ID-2] \
  --security-groups [ALB-SECURITY-GROUP-ID] \
  --region ap-northeast-2
```

### 타겟 그룹 생성
```bash
# 타겟 그룹 생성
aws elbv2 create-target-group \
  --name seasonthon-targets \
  --protocol HTTP \
  --port 8080 \
  --vpc-id [VPC-ID] \
  --target-type ip \
  --region ap-northeast-2
```

## 📊 6단계: 모니터링 설정

### CloudWatch 대시보드 생성
```bash
# CloudWatch 대시보드 생성
aws cloudwatch put-dashboard \
  --dashboard-name "SeasonThon-Dashboard" \
  --dashboard-body '{
    "widgets": [
      {
        "type": "metric",
        "properties": {
          "metrics": [
            ["AWS/ECS", "CPUUtilization", "ServiceName", "seasonthon-service", "ClusterName", "seasonthon-cluster"],
            ["AWS/ECS", "MemoryUtilization", "ServiceName", "seasonthon-service", "ClusterName", "seasonthon-cluster"]
          ],
          "period": 300,
          "stat": "Average",
          "region": "ap-northeast-2",
          "title": "ECS Service Metrics"
        }
      }
    ]
  }' \
  --region ap-northeast-2
```

## 🔍 7단계: 배포 확인

### 서비스 상태 확인
```bash
# ECS 서비스 상태 확인
aws ecs describe-services \
  --cluster seasonthon-cluster \
  --services seasonthon-service \
  --region ap-northeast-2

# 태스크 상태 확인
aws ecs list-tasks \
  --cluster seasonthon-cluster \
  --service-name seasonthon-service \
  --region ap-northeast-2
```

### 로그 확인
```bash
# CloudWatch 로그 확인
aws logs describe-log-streams \
  --log-group-name "/ecs/seasonthon-task" \
  --region ap-northeast-2
```

## 🚨 문제 해결

### 일반적인 문제들

1. **RDS 연결 실패**
   - 보안 그룹 설정 확인
   - RDS 엔드포인트 확인
   - 데이터베이스 사용자 권한 확인

2. **ECS 태스크 시작 실패**
   - 태스크 정의 확인
   - IAM 역할 확인
   - 서브넷 및 보안 그룹 확인

3. **ALB 헬스체크 실패**
   - 애플리케이션 포트 확인
   - 헬스체크 경로 설정 확인

## 📞 지원

문제가 발생하면 다음을 확인하세요:
- AWS CloudWatch 로그
- ECS 태스크 이벤트
- RDS 연결 로그

## 🔗 유용한 링크

- [AWS ECS 콘솔](https://ap-northeast-2.console.aws.amazon.com/ecs/v2/clusters)
- [AWS RDS 콘솔](https://ap-northeast-2.console.aws.amazon.com/rds/home)
- [AWS CloudWatch 콘솔](https://ap-northeast-2.console.aws.amazon.com/cloudwatch/home)


