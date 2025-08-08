package com.shatteredpixel.shatteredpixeldungeon.android;

import android.os.Build;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Belongings;
import com.watabou.noosa.Game;

import java.io.PrintWriter;
import java.io.StringWriter;

public class CrashLogger {
    
    public static String generateCrashReport(Throwable throwable) {
        StringBuilder report = new StringBuilder();
        // 设备信息
        report.append("=== 设备信息 ===\n");
        report.append("品牌: ").append(Build.MANUFACTURER).append("\n");
        report.append("型号: ").append(Build.MODEL).append("\n");
        report.append("Android版本: ").append(Build.VERSION.RELEASE).append("\n");
        report.append("SDK版本: ").append(Build.VERSION.SDK_INT).append("\n\n");
        
        // 游戏信息
        report.append("=== 游戏信息 ===\n");
        report.append("版本: ").append(Game.version).append("\n");
        report.append("版本号: ").append(Game.versionCode).append("\n");
        report.append("横屏模式: ").append(SPDSettings.landscape()).append("\n\n");
        
        // 玩家装备信息
            if (Dungeon.hero != null) {
                report.append("=== 玩家装备 ===\n");
                Belongings belongings = Dungeon.hero.belongings;
                for(Item item : belongings) {
                    if (item != null) {
                        report.append("- ").append(item.getClass().getSimpleName())
                             .append(": ").append(item.name()).append("\n");
                    }
                }
                report.append("\n");
            }
        
        // 异常信息
        report.append("=== 异常详情 ===\n");
        report.append("异常类型: ").append(throwable.getClass().getName()).append("\n");
        report.append("异常消息: ").append(throwable.getMessage()).append("\n\n");
        
        // 详细调用栈
        report.append("=== 调用栈追踪 ===\n");
        StackTraceElement[] stackTrace = throwable.getStackTrace();
        for (int i = 0; i < stackTrace.length; i++) {
            StackTraceElement element = stackTrace[i];
            report.append(i == 0 ? "➤ " : "   ")  // 标记崩溃根源位置
                 .append(element.getClassName())
                 .append(".")
                 .append(element.getMethodName())
                 .append("(")
                 .append(element.getFileName())
                 .append(":")
                 .append(element.getLineNumber())
                 .append(")\n");
        }
        
        // 如果有嵌套异常
        if (throwable.getCause() != null) {
            report.append("\n=== 嵌套异常 ===\n");
            report.append(generateCrashReport(throwable.getCause()));
        }
        
        return report.toString();
    }
}
