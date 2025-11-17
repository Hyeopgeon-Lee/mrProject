# 🌱 mrProject

**Java 8+ · Hadoop MapReduce** 기반의 분산 프로그래밍 실습 프로젝트입니다.  
다양한 MapReduce 패턴(WordCount, Combiner, Partitioner, MapOnly, SequenceFile 등)을 학습하고,  
MongoDB 연동 예제까지 포함된 **빅데이터 프로그래밍 교육용 코드**입니다.  

<p align="left">
  <img alt="java" src="https://img.shields.io/badge/Java-8+-007396?logo=openjdk&logoColor=white">
  <img alt="hadoop" src="https://img.shields.io/badge/Hadoop-MapReduce-FFCC00?logo=apachehadoop&logoColor=black">
  <img alt="build" src="https://img.shields.io/badge/Build-Maven-C71A36?logo=apachemaven&logoColor=white">
</p>

---

## ✨ 주요 기능/실습 예제
- **WordCount**
  - 기본 단어 빈도수 계산 (`wc.WordCount`)
  - 개선형 (`tool.WordCount2`, `cache.WordCount3`, `noreduce.WordCount4`)
- **문자/로그 처리**
  - `cc.CharCount` : 문자 수 세기
  - `ip.IPCount` : IP 접속 횟수 계산
  - `combiner.IPCount2` : Combiner 적용 IP 카운트
- **Partitioning**
  - `partition.MonthLog`, `TimeLog` → 월별/시간대별 로그 분리 처리
- **MapOnly**
  - `maponly.ImageCount` → Reduce 없는 단순 맵 처리
- **SequenceFile**
  - `seq.CreateSequenceFile`, `ReadCompressSequenceFile` 등
- **MongoDB 연동**
  - `mongo.MonthLog2`, `AccessLog` → Mongo Mapper/Reducer, 파티셔너 적용
- **성공/실패 로그 분석**
  - `success.ResultCount`, `ResultCount2`

> ⚠️ 본 프로젝트는 Hadoop MapReduce **실습용 예제 코드**이며,  
> 대규모 데이터 처리 시에는 **YARN/클러스터 환경, HDFS 설정, 최적화**가 필요합니다.

---

## 🧱 기술 스택
- **Java 8+**
- **Hadoop MapReduce API**
- **Maven**
- **MongoDB (Mongo-Hadoop Connector 예제 포함)**

---

## 📁 프로젝트 구조(요약)
```
mrProject/
├─ src/main/java/
│  ├─ wc/           # 기본 WordCount
│  ├─ tool/         # 개선된 WordCount (Tool API)
│  ├─ cache/        # Distributed Cache 활용 WordCount
│  ├─ noreduce/     # Mapper-only WordCount
│  ├─ cc/           # CharCount
│  ├─ ip/           # IP Count
│  ├─ combiner/     # Combiner 적용 IP Count
│  ├─ partition/    # MonthLog, TimeLog Partitioner
│  ├─ seq/          # SequenceFile 입출력
│  ├─ mongo/        # MongoDB 연동 MapReduce
│  ├─ success/      # 성공/실패 로그 카운트
│  └─ maponly/      # Map-only 예제 (ImageCount)
├─ src/main/resources/
│  └─ comedies      # 테스트 입력 데이터
├─ pom.xml
├─ target/mr-1.0-SNAPSHOT.jar
```

---

## ⚙️ 빠른 시작
### 1) 필수 요건
- **JDK 8+**
- **Hadoop 3.x**
- (선택) **MongoDB 6.x**

### 2) 빌드
```bash
mvn clean package
```

### 3) 실행 예시
- **WordCount**
  ```bash
  hadoop jar target/mr-1.0-SNAPSHOT.jar wc.WordCount input output
  ```
- **CharCount**
  ```bash
  hadoop jar target/mr-1.0-SNAPSHOT.jar cc.CharCount input output
  ```
- **IPCount (Combiner 적용)**
  ```bash
  hadoop jar target/mr-1.0-SNAPSHOT.jar combiner.IPCount2 input output
  ```
- **MongoDB 로그 분석**
  ```bash
  hadoop jar target/mr-1.0-SNAPSHOT.jar mongo.MonthLog2 input output
  ```

---

## 🧪 테스트 데이터
- `src/main/resources/comedies` → 샘플 텍스트 파일 제공  
- WordCount, CharCount 등 예제 실행 시 기본 입력으로 사용 가능

---

## 📜 라이선스
- 본 저장소는 **Apache-2.0** 라이선스를 따릅니다.  

---

## 🙋‍♀️ 문의
- **한국폴리텍대학 서울강서캠퍼스 빅데이터소프트웨어과**  
- **이협건 교수** · hglee67@kopo.ac.kr  
- 입학 상담 오픈채팅방: <https://open.kakao.com/o/gEd0JIad>
