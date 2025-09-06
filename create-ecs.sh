#!/bin/bash

# ECS 클러스어 생성
aws ecs create-cluster --cluster-name seasonthon-cluster --region ap-northeast-2

# 태스크 정의 등록
aws ecs register-task-definition --cli-input-json file://task-definition.json --region ap-northeast-2

# 서비스 생성
aws ecs create-service \
  --cluster seasonthon-cluster \
  --service-name seasonthon-service \
  --task-definition seasonthon-task \
  --desired-count 1 \
  --launch-type FARGATE \
  --network-configuration "awsvpcConfiguration={subnets=[subnet-0a1b2c3d4e5f67890],securityGroups=[sg-0c8880cb357bf0665],assignPublicIp=ENABLED}" \
  --region ap-northeast-2

echo "ECS 서비스가 생성되었습니다."