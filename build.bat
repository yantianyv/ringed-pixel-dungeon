@echo off
setlocal enabledelayedexpansion
call ./gradlew --stop

set KEY_ALIAS=ringed-pixel-dungeon
set KEYSTORE_PATH=android\key\key.jks

rem �ֶ����� Keystore ����
set /p PASSWORD=������ Keystore ���룺
set /p EXTRACT_PASSWORD=�������ѹ���룺

del android\key\key.jks

rem ����Ƿ���ڼ��ܵ� Keystore ������
if exist "android\key\key.zip" (
    echo [INFO] ���ڽ���...
    if exist "android\key\7z.exe" (
        "android\key\7z.exe" e "android\key\key.zip" -o"android\key" -p"!EXTRACT_PASSWORD!"
    ) else (
        echo [ERROR] �Ҳ��� 7-Zip
        pause
        exit /b
    )

    if not exist "android\key\key.jks" (
        echo [INFO] ��ѹʧ�ܣ����������нű���������ȷ���룡
        pause
        exit /b
    )

    echo [INFO] Keystore ������ɣ�
)

rem ��� Keystore �Ƿ����
if not exist "android\key\key.jks" (
    set /p PASSWORD=������ Keystore ���룺
    set /p EXTRACT_PASSWORD=�����ý�ѹ���룺

    echo [INFO] Keystore ���ڴ���...
    
    keytool -genkeypair -v -keystore "android\key\key.jks" -alias "!KEY_ALIAS!" -keyalg RSA -keysize 2048 -validity 10000 ^
        -storepass "!PASSWORD!" -keypass "!PASSWORD!" ^
        -dname "CN=ringed-pixel-dungeon, OU=pixel-dungeon, O=dungeon, L=China, ST=China, C=China"

    if !ERRORLEVEL! NEQ 0 (
        echo [ERROR] Keystore ����ʧ�ܣ�
        pause
        exit /b
    )

    echo [INFO] Keystore �����ɹ������ڼ��ܴ洢...
    "android\key\7z.exe" a -tzip "android\key\key.zip" "android\key\key.jks" -p"!EXTRACT_PASSWORD!" -mem=AES256

    if !ERRORLEVEL! NEQ 0 (
        echo [ERROR] Keystore ����ʧ�ܣ�
        pause
        exit /b
    )

    echo [INFO] Keystore �Ѽ��ܳɹ���
)

set KEYSTORE_PATH=key\key.jks
echo [INFO] ��ʼ����...
call gradlew assembleRelease
if !ERRORLEVEL! NEQ 0 (
    echo [ERROR] ����ʧ�ܣ����������нű���������ȷ���룡
    call ./gradlew --stop
    pause
    exit /b
)

call ./gradlew --stop

rem ɾ������ Keystore
del "android\key\key.jks"

rem �����������
set "PASSWORD="
set "EXTRACT_PASSWORD="

endlocal
