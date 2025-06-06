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
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;

public class Messages {

    private static ArrayList<I18NBundle> bundles;
    private static ArrayList<I18NBundle> bundlesZh;
    private static ArrayList<I18NBundle> bundlesEn;

    private static Languages lang;
    private static Locale locale;

    public static final String NO_TEXT_FOUND = "!!!NO TEXT FOUND!!!";

    public static Languages lang() {
        return lang;
    }

    public static Locale locale() {
        return locale;
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
            bundles.add(I18NBundle.createBundle(Gdx.files.internal(file), userLocale));
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
                return result;
            }
        }

        // 尝试中文
        for (I18NBundle b : bundlesZh) {
            result = b.get(key);
            if (!isMissing(result, key)) {
                return result;
            }
        }

        // 尝试英文
        for (I18NBundle b : bundlesEn) {
            result = b.get(key);
            if (!isMissing(result, key)) {
                return result;
            }
        }

        return null;
    }

    // 判断是否为未找到的字符串
    private static boolean isMissing(String result, String key) {
        return result.length() == key.length() + 6 && result.contains(key);
    }

    public static String format(String format, Object... args) {
        try {
            return String.format(locale(), format, args);
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
        return formatters.get(format).format(number);
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
