@echo off
setlocal enabledelayedexpansion

set KEY_ALIAS=ringed-pixel-dungeon
set KEYSTORE_PATH=android\key\key.jks
set CRED_NAME=ringed-pixel-dungeon
set CRED_NAME_EXTRACT=ringed-pixel-dungeon-extract
del "android\key\key.jks"


rem ����Ƿ���ڼ��ܵ� Keystore ������
if exist "android\key\key.zip" (
    rem ��ȡ�Ѵ洢�� Keystore ����
    for /f "tokens=*" %%A in ('cmdkey /list ^| findstr /I "%CRED_NAME%"') do (
        for /f "tokens=2 delims=:" %%B in ("%%A") do set PASSWORD=%%B
    )

    rem ��ȡ�Ѵ洢�Ľ�ѹ����
    for /f "tokens=*" %%A in ('cmdkey /list ^| findstr /I "%CRED_NAME_EXTRACT%"') do (
        for /f "tokens=2 delims=:" %%B in ("%%A") do set EXTRACT_PASSWORD=%%B
    )

    rem ���û�д洢�� Keystore ���룬����ʾ���벢�洢
    if not defined PASSWORD (
        set /p PASSWORD=������ Keystore ���루�����Զ���ס����
        cmdkey /add:%CRED_NAME% /user:keystore /pass:%PASSWORD%
    )

    rem ���û�д洢�Ľ�ѹ���룬����ʾ���벢�洢
    if not defined EXTRACT_PASSWORD (
        set /p EXTRACT_PASSWORD=�������ѹ���루�����Զ���ס����
        cmdkey /add:%CRED_NAME_EXTRACT% /user:extract /pass:%EXTRACT_PASSWORD%
    )

    echo [INFO] ���ڽ���...

    if exist "android\key\7z.exe" (
        "android\key\7z.exe" e "android\key\key.zip" -o"android\key" -p"!EXTRACT_PASSWORD!"
    ) else (
        echo [ERROR] �Ҳ��� 7-Zip
        pause
        exit /b
    )

    if not exist "android\key\key.jks" (
        cmdkey /delete:%CRED_NAME_EXTRACT%  >nul 2>&1
        echo [INFO] ��ѹʧ�ܣ����������нű���������ȷ���룡
        pause
        exit /b
    )

    echo [INFO] Keystore ������ɣ�
)

rem ��� Keystore �Ƿ����
if not exist "android\key\key.jks" (
    cmdkey /delete:%CRED_NAME_EXTRACT%  >nul 2>&1
    cmdkey /delete:%CRED_NAME% >nul 2>&1
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
    cmdkey /delete:%CRED_NAME% >nul 2>&1
    pause
    exit /b
)

rem ɾ������ Keystore
del "android\key\key.jks"

rem �����������
set "PASSWORD="
set "EXTRACT_PASSWORD="

endlocal
pause
