@echo off
chcp 65001
setlocal enabledelayedexpansion

rem 设置Android SDK路径
set ANDROID_HOME=./asdk/
echo sdk.dir=%ANDROID_HOME%>local.properties

@REM rem 接受SDK许可证并安装必要组件
@REM if exist "%ANDROID_HOME%\cmdline-tools\latest\bin\sdkmanager.bat" (
@REM     call "%ANDROID_HOME%\cmdline-tools\latest\bin\sdkmanager.bat" --licenses
@REM     call "%ANDROID_HOME%\cmdline-tools\latest\bin\sdkmanager.bat" "platform-tools" "build-tools;35.0.0" "platforms;android-35"
@REM )

call ./gradlew clean
call ./gradlew --stop

set KEY_ALIAS=ringed-pixel-dungeon
set KEYSTORE_PATH=android\key\key.jks

cls

rem 手动输入 Keystore 密码
set /p PASSWORD=请输入 Keystore 密码（留空则编译debug版本）：
cls

rem 检测是否需要编译debug版本
if "!PASSWORD!"=="" (
    call ./gradlew assembleDebug
    explorer android\build\outputs\apk\debug
    if !ERRORLEVEL! NEQ 0 (
        pause
    )
    exit /b
)
set /p EXTRACT_PASSWORD=请输入解压密码：
cls

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
@REM call gradlew assembleRelease
call gradlew build
if !ERRORLEVEL! NEQ 0 (
    echo [ERROR] 编译失败，请重新运行脚本并输入正确密码！
    call ./gradlew --stop
    pause
    exit /b
)
explorer android\build\outputs\apk\release

call ./gradlew --stop

rem 删除明文 Keystore
del "android\key\key.jks"

rem 清除环境变量
set "PASSWORD="
set "EXTRACT_PASSWORD="

if !ERRORLEVEL! NEQ 0 (
    pause
)

endlocal
