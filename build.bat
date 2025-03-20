@echo off
setlocal enabledelayedexpansion
call ./gradlew --stop

set KEY_ALIAS=ringed-pixel-dungeon
set KEYSTORE_PATH=android\key\key.jks

rem 手动输入 Keystore 密码
set /p PASSWORD=请输入 Keystore 密码：
set /p EXTRACT_PASSWORD=请输入解压密码：

del android\key\key.jks

rem 检查是否存在加密的 Keystore 并解密
if exist "android\key\key.zip" (
    echo [INFO] 正在解密...
    if exist "android\key\7z.exe" (
        "android\key\7z.exe" e "android\key\key.zip" -o"android\key" -p"!EXTRACT_PASSWORD!"
    ) else (
        echo [ERROR] 找不到 7-Zip
        pause
        exit /b
    )

    if not exist "android\key\key.jks" (
        echo [INFO] 解压失败，请重新运行脚本并输入正确密码！
        pause
        exit /b
    )

    echo [INFO] Keystore 解密完成！
)

rem 检查 Keystore 是否存在
if not exist "android\key\key.jks" (
    set /p PASSWORD=请设置 Keystore 密码：
    set /p EXTRACT_PASSWORD=请设置解压密码：

    echo [INFO] Keystore 正在创建...
    
    keytool -genkeypair -v -keystore "android\key\key.jks" -alias "!KEY_ALIAS!" -keyalg RSA -keysize 2048 -validity 10000 ^
        -storepass "!PASSWORD!" -keypass "!PASSWORD!" ^
        -dname "CN=ringed-pixel-dungeon, OU=pixel-dungeon, O=dungeon, L=China, ST=China, C=China"

    if !ERRORLEVEL! NEQ 0 (
        echo [ERROR] Keystore 创建失败！
        pause
        exit /b
    )

    echo [INFO] Keystore 创建成功！正在加密存储...
    "android\key\7z.exe" a -tzip "android\key\key.zip" "android\key\key.jks" -p"!EXTRACT_PASSWORD!" -mem=AES256

    if !ERRORLEVEL! NEQ 0 (
        echo [ERROR] Keystore 加密失败！
        pause
        exit /b
    )

    echo [INFO] Keystore 已加密成功！
)

set KEYSTORE_PATH=key\key.jks
echo [INFO] 开始编译...
call gradlew assembleRelease
if !ERRORLEVEL! NEQ 0 (
    echo [ERROR] 编译失败，请重新运行脚本并输入正确密码！
    call ./gradlew --stop
    pause
    exit /b
)

call ./gradlew --stop

rem 删除明文 Keystore
del "android\key\key.jks"

rem 清除环境变量
set "PASSWORD="
set "EXTRACT_PASSWORD="

endlocal
