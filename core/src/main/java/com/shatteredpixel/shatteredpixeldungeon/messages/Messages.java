/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2025 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.messages;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IllegalFormatException;
import java.util.Locale;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.I18NBundle;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.utils.TextObfuscator;

public class Messages {

    private static ArrayList<I18NBundle> bundles;
    private static ArrayList<I18NBundle> bundlesZh;
    private static ArrayList<I18NBundle> bundlesEn;

    private static Languages lang;
    private static Locale locale;

    public static final String NO_TEXT_FOUND = "!!!NO TEXT FOUND!!!";

    // 序列化标志：禁用文本混淆以确保存档key一致性
    private static final ThreadLocal<Boolean> isSerializing = ThreadLocal.withInitial(() -> false);

    public static Languages lang() {
        return lang;
    }

    public static Locale locale() {
        return locale;
    }

    // 设置序列化状态
    public static void setSerializing(boolean serializing) {
        isSerializing.set(serializing);
    }

    // 获取序列化状态
    public static boolean isSerializing() {
        return isSerializing.get();
    }

    private static String[] prop_files = new String[]{
        Assets.Messages.ACTORS,
        Assets.Messages.ITEMS,
        Assets.Messages.JOURNAL,
        Assets.Messages.LEVELS,
        Assets.Messages.MISC,
        Assets.Messages.PLANTS,
        Assets.Messages.SCENES,
        Assets.Messages.UI,
        Assets.Messages.WINDOWS
    };

    static {
        formatters = new HashMap<>();
        setup(SPDSettings.language());
    }

    public static void setup(Languages lang) {
        I18NBundle.setExceptionOnMissingKey(false);

        Messages.lang = lang;
        Locale userLocale = (lang == Languages.ENGLISH) ? Locale.ROOT : new Locale(lang.code());
        locale = (lang == Languages.ENGLISH) ? Locale.ENGLISH : userLocale;

        formatters.clear();

        // 加载用户语言
        bundles = new ArrayList<>();
        for (String file : prop_files) {
            if (locale.getLanguage().equals("id")) {
                // This is a really silly hack to fix some platforms using "id" for indonesian
                // and some using "in" (Android 14- mostly).
                // So if we detect "id" then we treat "###_in" as the base bundle so that it
                // gets loaded instead of English.
                bundles.add(I18NBundle.createBundle(Gdx.files.internal(file + "_in"), userLocale));
            } else {
                bundles.add(I18NBundle.createBundle(Gdx.files.internal(file), userLocale));
            }
        }

        // 加载中文语言包
        bundlesZh = new ArrayList<>();
        for (String file : prop_files) {
            bundlesZh.add(I18NBundle.createBundle(Gdx.files.internal(file), new Locale("zh")));
        }

        // 加载英文语言包（默认）
        bundlesEn = new ArrayList<>();
        for (String file : prop_files) {
            bundlesEn.add(I18NBundle.createBundle(Gdx.files.internal(file), Locale.ROOT));
        }
    }

    public static String get(String key, Object... args) {
        return get(null, key, args);
    }

    public static String get(Object o, String k, Object... args) {
        return get(o.getClass(), k, args);
    }

    public static String get(Class c, String k, Object... args) {
        String key;
        if (c != null) {
            key = c.getName().replace("com.shatteredpixel.shatteredpixeldungeon.", "");
            key += "." + k;
        } else {
            key = k;
        }

        String value = getFromBundle(key.toLowerCase(Locale.ENGLISH));
        if (value != null) {
            return (args.length > 0) ? format(value, args) : value;
        } else if (c != null && c.getSuperclass() != null) {
            return get(c.getSuperclass(), k, args);
        } else {
            return NO_TEXT_FOUND;
        }
    }

    private static String getFromBundle(String key) {
        String result;

        // 尝试用户语言
        for (I18NBundle b : bundles) {
            result = b.get(key);
            if (!isMissing(result, key)) {
                return applyIlliteracyChallenge(result);
            }
        }

        // 尝试中文
        for (I18NBundle b : bundlesZh) {
            result = b.get(key);
            if (!isMissing(result, key)) {
                return applyIlliteracyChallenge(result);
            }
        }

        // 尝试英文
        for (I18NBundle b : bundlesEn) {
            result = b.get(key);
            if (!isMissing(result, key)) {
                return applyIlliteracyChallenge(result);
            }
        }

        return null;
    }

    /**
     * 应用文盲挑战处理
     * 只在游戏进行中生效（主菜单、英雄选择等界面不受影响）
     */
    private static String applyIlliteracyChallenge(String text) {
        // 检查是否启用了文盲挑战，并且是否在游戏进行中
        // Dungeon.hero 只在实际游戏中存在，主菜单和英雄选择页面为 null
        if (text != null && Dungeon.hero != null && Dungeon.isChallenged(Challenges.ILLITERACY)) {
            return TextObfuscator.processText(text);
        }
        return text;
    }

    // 判断是否为未找到的字符串
    private static boolean isMissing(String result, String key) {
        return result.length() == key.length() + 6 && result.contains(key);
    }

    public static String format(String format, Object... args) {
        try {
            String result = String.format(locale(), format, args);

            // 检查是否正在序列化，如果是则跳过文本混淆
            if (isSerializing.get()) {
                return result;
            }

            // 文盲挑战：只在游戏进行中将数值替换为十六进制
            // 必须同时满足：hero存在 + 启用了文盲挑战 + 当前场景是GameScene
            boolean isInGame = Dungeon.hero != null &&
                com.watabou.noosa.Game.scene() instanceof com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;

            if (isInGame && Dungeon.isChallenged(Challenges.ILLITERACY)) {
                result = TextObfuscator.convertNumbersInText(result);
            }

            return result;
        } catch (IllegalFormatException e) {
            ShatteredPixelDungeon.reportException(new Exception("formatting error for the string: " + format, e));
            return format;
        }
    }

    private static HashMap<String, DecimalFormat> formatters;

    public static String decimalFormat(String format, double number) {
        if (!formatters.containsKey(format)) {
            formatters.put(format, new DecimalFormat(format, DecimalFormatSymbols.getInstance(locale())));
        }
        String result = formatters.get(format).format(number);

        // 检查是否正在序列化，如果是则跳过文本混淆
        if (isSerializing.get()) {
            return result;
        }

        // 文盲挑战：只在游戏进行中返回十六进制
        // 必须同时满足：hero存在 + 启用了文盲挑战 + 当前场景是GameScene
        boolean isInGame = Dungeon.hero != null &&
            com.watabou.noosa.Game.scene() instanceof com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;

        if (isInGame && Dungeon.isChallenged(Challenges.ILLITERACY)) {
            return TextObfuscator.convertNumbersInText(result);
        }

        return result;
    }

    public static String capitalize(String str) {
        if (str.length() == 0) {
            return str;
        }
        return str.substring(0, 1).toUpperCase(locale) + str.substring(1);
    }

    private static final HashSet<String> noCaps = new HashSet<>(
            Arrays.asList("a", "an", "and", "of", "by", "to", "the", "x", "for")
    );

    public static String titleCase(String str) {
        if (lang == Languages.ENGLISH) {
            String result = "";
            for (String word : str.split("(?<=\\p{Zs})")) {
                if (noCaps.contains(word.trim().toLowerCase(Locale.ENGLISH).replaceAll(":|[0-9]", ""))) {
                    result += word;
                } else {
                    result += capitalize(word);
                }
            }
            return capitalize(result);
        }
        return capitalize(str);
    }

    public static String upperCase(String str) {
        return str.toUpperCase(locale);
    }

    public static String lowerCase(String str) {
        return str.toLowerCase(locale);
    }
}
