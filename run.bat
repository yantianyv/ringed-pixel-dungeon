@echo off
chcp 65001

call ./gradlew --stop
./gradlew debug


