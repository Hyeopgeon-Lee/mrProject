# mrProject — Java 17 / Apache Hadoop 3.5 MapReduce 실습

**Java 17 · Apache Hadoop 3.5.0 · Maven** 기반의 MapReduce 교육용 예제 모음입니다.
기존 패키지명과 `hadoop jar` 실행 진입점은 그대로 유지합니다.

## 개발 환경

- JDK **17** (서버와 클라이언트에 사용)
- Apache Hadoop **3.5.0** (HDFS, YARN, MapReduce)
- Maven 3.9 이상 권장
- MongoDB 예제만 별도 MongoDB 서버 필요

```bash
java -version
hadoop version
mvn -B -ntp clean verify
```

Maven 컴파일 옵션은 `--release 17`이며, Java 17 클래스 파일의 major version은 **61**입니다.
GitHub Actions에서도 Java 17로 `mvn clean verify`를 실행합니다.

## 실습 목록

| 실습 | 진입 클래스 |
| --- | --- |
| 기본 WordCount | `wc.WordCount` |
| ToolRunner WordCount | `tool.WordCount2` |
| Distributed Cache | `cache.WordCount3` |
| Mapper-only WordCount | `noreduce.WordCount4` |
| 문자 수 계산 | `cc.CharCount` |
| IP별 횟수 | `ip.IPCount` |
| Combiner | `combiner.IPCount2` |
| 월별 Partitioner | `partition.MonthLog` |
| 시간별 Partitioner | `partition.TimeLog` |
| 이미지 요청 카운터 | `maponly.ImageCount` |
| SequenceFile 생성 | `seq.CreateSequenceFile` |
| 압축 SequenceFile 생성 | `seq.CreateCompressSequenceFile` |
| SequenceFile 읽기 | `seq.ReadCompressSequenceFile` |
| 결과 코드 분석 | `success.ResultCount`, `success.ResultCount2` |
| MongoDB 저장 | `mongo.AccessLog`, `mongo.MonthLog2` |

## HDFS에서 실행

```bash
mvn -B -ntp clean package
hdfs dfs -mkdir -p /user/$USER/mr/input
hdfs dfs -put -f src/main/resources/comedies /user/$USER/mr/input/
hdfs dfs -rm -r -f /user/$USER/mr/output
hadoop jar target/mr-1.0-SNAPSHOT.jar wc.WordCount /user/$USER/mr/input /user/$USER/mr/output
hdfs dfs -cat /user/$USER/mr/output/part-r-00000
```

출력 디렉터리가 이미 존재하면 Hadoop 작업이 실패하므로, 기존 결과를 삭제하거나 다른 경로를 사용하세요.
`hadoop jar`는 클러스터에 설치된 Hadoop 3.5의 라이브러리를 사용합니다.
프로젝트 JAR에는 별도 MongoDB/Jackson 라이브러리를 포함하되 Hadoop 자체 라이브러리는 포함하지 않습니다.

## Java 17 / Hadoop 3.5 마이그레이션 사항

- Hadoop Java 라이브러리 **3.3.6 → 3.5.0**, `maven.compiler.release=17`
- MapReduce 패키지와 진입점 유지; `ToolRunner`의 설정 보존
- 이전 `@Log4j` 대신 SLF4J 로그 사용
- 잘못된 access log 입력 행에 대한 방어 처리
- Partitioner 값 범위 검사 및 리듀서 수에 따른 유효 인덱스 반환
- MongoDB Java 드라이버 교체, 비밀번호 하드코딩 제거, 접속 종료 처리
- 로컬 MapReduce WordCount 및 Partitioner 자동 테스트 추가

## MongoDB 예제 안내

MongoDB 예제는 기존 드라이버 3.12 대신 **MongoDB Java Driver 5.5.1**을 사용합니다.
접속 문자열이나 비밀번호를 코드에 기록하지 않습니다.

- `MONGODB_URI` — 기본값 `mongodb://localhost:27017`
- `MONGODB_DATABASE` — 기본값 `MyDB`

**주의:** YARN 클러스터로 실행하는 경우, MongoDB 접속 설정이 작업을 실행하는 Mapper/Reducer 컨테이너에도 제공되어야 합니다. `localhost`는 각 컨테이너 자신의 주소입니다. 교육용 내부망에서만 기본값을 사용하고, 실습 이외에는 인증과 TLS를 적용하세요.

## 검증 범위

`mvn clean verify`는 소스 컴파일, JUnit 테스트, JAR 패키징을 수행합니다.
WordCount 통합 테스트는 클러스터가 아닌 **로컬 MapReduce 실행**을 검증합니다.
원격 HDFS/YARN 동작, 압축 코덱 설치, MongoDB 서버 연결은 실제 실습 클러스터에서 별도 확인이 필요합니다.

---

한국폴리텍대학 서울강서캠퍼스 빅데이터소프트웨어공학과
