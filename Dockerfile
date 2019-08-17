FROM openjdk:11
ADD ./build/libs/strava-activity-uploader.jar /app.jar
HEALTHCHECK --interval=5s --timeout=10s --retries=3 CMD curl -sS http://localhost:4567/health || exit 1
ENTRYPOINT ["java","-jar","/app.jar","-Xmx128m -XX:+UseConcMarkSweepGC -XX:+PrintGCDetails -XX:+PrintHeapAtGC"]