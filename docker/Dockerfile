FROM openjdk:11-jdk
ADD ./build/libs/strava-activity-uploader.jar /app.jar
HEALTHCHECK --interval=5s --timeout=10s --retries=3 CMD curl -sS http://localhost:4567/health || exit 1
ENTRYPOINT ["java","-Xmx128m","-Xlog:gc","-jar","/app.jar"]
