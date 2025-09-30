# ECC Backend Server
서울과학기술대학교 중앙 영어 동아리 ECC를 위한 AI 기반 영어 학습 동아리 관리 플랫폼의 백엔드 서버입니다.

## 🏗️ System Architecture

### 개발 환경
<p align="center">
  <img width="600" alt="ECC 개발환경 drawio" src="https://github.com/user-attachments/assets/a27bcc0c-3156-4a36-8434-c5b30572054f" />
</p>

### 프로덕션 환경
<p align="center">
  <img width="600" alt="ECC 기술 아키텍처 drawio" src="https://github.com/user-attachments/assets/40fb6dbe-5002-4c9e-8265-58875f441351" />
</p>

### 프로덕션 환경 (프리티어 종료 대비 버전)
<p align="center">
  <img width="400" alt="ECC 기술 아키텍처(프리티어 종료버전) drawio" src="https://github.com/user-attachments/assets/6e52f500-8a17-48ce-9190-0ca2396b2f04" />
</p>

## 📊 Data Architecture Diagram
<p align="center">
  <img src="https://github.com/user-attachments/assets/1aafe7ea-06ef-4ee5-a551-316cb3f590ff" alt="Data Architecture" height="350" />
</p>

## 🛠️ 기술 스택
- **Backend**: Spring Boot 3.2.5, Java 17
- **Database**: MariaDB, MongoDB, Redis
- **AI**: Spring AI OpenAI (GPT-3.5)
- **Optimization**: Google OR-Tools
- **Security**: JWT, BCrypt
- **Infrastructure**: AWS (EC2, RDS, ElastiCache), MongoDB Atlas

## 📖 프로젝트 소개
ECC 플랫폼은 대학 영어 동아리의 운영 효율성을 높이고 회원들의 학습 효과를 극대화하기 위해 개발된 종합 관리 시스템입니다.

### ✨ 핵심 특징
- **🤖 AI 기반 학습 지원**: OpenAI GPT-3.5를 활용한 실시간 번역/교정 및 개인 맞춤형 복습 자료 생성
- **🎯 자동 팀 배정**: Google OR-Tools 최적화 알고리즘을 통한 효율적인 스터디 팀 구성
- **💾 하이브리드 데이터베이스**: MariaDB, MongoDB, Redis를 활용한 최적화된 데이터 관리
- **📚 이원화 스터디 시스템**: 정규 스터디와 번개 스터디로 구분된 유연한 학습 환경

## 🎯 주요 기능

### 📝 스터디 관리
- **정규 스터디**: 자동 팀 배정, 주차별 보고서 제출, 우수 조 선정
- **번개 스터디**: 즉석 모집 및 참여, 실시간 참여 관리

### 🤖 AI 학습 지원
- 실시간 번역/교정
- 개인별 맞춤형 복습 자료 자동 생성
- AI 기반 자동 채점
