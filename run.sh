#!/bin/bash
exec java -jar \
  -Dspring.profiles.active=prod \
  -Duser.timezone=Asia/Seoul \
  -Xms1g \
  -Xmx8g \
  -server \
  -XX:+UseG1GC \
  /dreamdiary/dreamdiary.jar
