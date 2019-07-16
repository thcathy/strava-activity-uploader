FROM openjdk:11
ADD ./build/libs/strava-activity-uploader.jar /app.jar
ENTRYPOINT ["java","-jar","/app.jar","-Xmx128m -XX:+UseConcMarkSweepGC -XX:+PrintGCDetails -XX:+PrintHeapAtGC"]