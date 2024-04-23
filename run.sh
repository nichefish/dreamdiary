#!/bin/bash
exec java -jar -Dspring.profiles.active=prod -Duser.timezone=Asia/Seoul -Xms1g -Xmx8g -server /dreamdiary.jar
