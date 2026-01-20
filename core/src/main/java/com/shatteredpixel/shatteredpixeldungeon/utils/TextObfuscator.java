/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2025 Evan Debenham
 *
 * Ringed Pixel Dungeon
 * Copyright (C) 2025-2025 yantianyv
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

package com.shatteredpixel.shatteredpixeldungeon.utils;

import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.messages.Languages;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import com.watabou.utils.Random;
import com.watabou.noosa.Game;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;

public class TextObfuscator {

    // 标志：是否正在处理文本（防止无限递归）
    private static boolean isProcessing = false;

    // LRU缓存，避免重复处理相同文本
    private static final Map<String, String> cache = new LinkedHashMap<String, String>(1000, 0.75f, true) {
        protected boolean removeEldestEntry(Map.Entry eldest) {
            return size() > 1000;
        }
    };

    // 常用中文字符集（约500-1000字，基于GB2312一级字和常用字频率）
    private static final String COMMON_CHINESE_CHARS =
        "的一是在不了有和人这中大为上个国我以要他时来用们生到作地于出就分对成会可主发年动同工也能下过子说产种面而方后多定行学法所民得经十三之进着等部度家电力里如水化高自二理起小物现实量都两体制机当使点从业本去把性好应开它合还因由其些然前外天政四日那社义事平形相全表间样与关各重新线内数正心反你明看原又么利比或但质气第向道命此变条只没结解问意建月公无系军很情者最立代想已通并提直题党程展五果料象员革位入常文总次品式活设及管特件长求老头基资边流路级少图山统接知较将组见计别她手角期根论运农指几九区强放决西被干做必战先回则任取据处队南给色光门即保治北造百规热领七海口东导器压志世金增争济阶油思术极交受联什认六共权收证改清己美再采转更单风切打白教速花带安场身车例真务具万每目至达走积示议声报斗完类八离华名确才科张信马节话米整空元况今集温传土许步群广石记需段研界拉林律叫且究观越织装影算低持音众书布复容儿须际商非验连断深难近矿千周委素技备半办青省列习响约支般史感劳便团往酸历市克何除消构府称太准精值号率族维划选标写存候毛亲快效斯院查江型眼王按格养易置派层片始却专状育厂京识适属圆包火住调满县局照参红细引听铁价严";

    // 生僻字库（500+生僻字、古字、异体字、罕见字）
    private static final String RARE_CHINESE_CHARS =
        // 叠字类（多个相同部件）
        "龘靐驫鱻灥爨籱籮齉纛虋鬱鸍鸘鸙鸜鸝鸛鸖鸗鸕鸔鸓鸑鸏鸒鸐鸎麤龗灪龖厵龻爨癵驫麣纞虋讟钃鞻韤齾齉虋靐飍孮灪麷灥爨籱纞麣" +
        "贔赑朤燨蟲馫靉靆鋱鈾鑻飍掱叒叕嚞朤屾畾厽惢惸歖瞐贔皕皛猋馫驫麤骉鱻羴掱毳纞赑叒叕朤畾厽厵尞" +
        "嚞羴猋骉鱻贔赑朤燨蟲馫靉靆鋱鈾鑻飍掱叒叕嚞朤屾畾厽惢惸歖瞐贔皕皛猋馫驫麤骉鱻羴掱毳纞" +

        // 古汉语用字
        "旮旯旯旮旯氼仨仚仜伮伜伬伭伫伱伳伵伷伹伻伾伿佀佁佂佄佅但佇佈佉佊佋佌佒佔佖佗佘佝佞佟" +
        "佡佢佤佥佦佧佨佩佪佫佬佭佮佯佱佲併佷佸佹佺佻佽佾侀侁侂侅侇侊侌侎侐侒侓侔侕侖侘侙侚侜侞侟" +
        "侠侢侤侫侭侰侱侲侳侴侵侶侷侸侹侺侻侼侽侾俀俁係俅俆俇俈俉俋俌俍俎俐俒俓俔俕俖俙俛俜保俞俟俠俢俣" +
        "俤俥俦俧俫俬俰俲俳俴俵俶俷俸俹俺俻俼俽俿倀倁倂倃倄倅倆倇倈倉倛倞倠倡倢倣値倥倦倧倨倫倯倰倱倲倳倴" +

        // 罕见的自然、动植物用字
        "鼾鼽鼿齁齃齄齅齆齇齈齉齊齋齌齍齎齏齐齑齒齓齔齕齖齗齘齙齚齛齜齝齞齟齠齡齢齣齤齥齦齧齨齩齪齫齬齭齮齯齰齱" +
        "齲齳齴齵齶齷齸齹齺齻齼齽齾齿龁龂龃龄龅龆龇龈龉龊龋龌龍龎龏龐龑龒龓龔龕龖龗龘龙龚龛龜龝龞龡龢龣龤龥" +
        "鼔鼕鼖鼗鼘鼙鼚鼛鼜鼝鼞鼟鼠鼡鼢鼤鼥鼦鼧鼨鼩鼪鼫鼬鼭鼮鼯鼰鼱鼲鼳鼴鼵鼶鼷鼸鼹鼺鼻鼼鼽鼿齁齃齄齅齆齇齈齉齊齋" +
        "魖魗魘魙魚魛魜魝魞魟魠魡魢魣魤魥魦魧魨魩魪魫魬魭魮魯魰魱魲魳魴魵魶魷魸魹魺魻魼魽魾魿鮀鮁鮂鮃鮄鮅鮆鮇鮈" +
        "鮉鮊鮋鮌鮍鮎鮏鮐鮑鮒鮓鮔鮕鮖鮗鮘鮙鮚鮛鮜鮝鮞鮟鮠鮡鮢鮣鮤鮥鮦鮧鮨鮩鮪鮫鮬鮭鮮鮯鮰鮱鮲鮳鮴鮵鮶鮷鮸鮹鮺鮻鮼鮽鮾鮿鯀鯁" +

        // 古代器物、官职用字
        "卺卼卽卾卿厀厁厃厈厎厐厑厒厓厔厖厗厍厎厐厒厔厖厘厙厛厜厝厞厠厡厣厤厧厪厫厬厭厯厰厱厳厴厵厷厸厹厺厽厾叀" +
        "叁参叄叅叆叇叏叐叒叓叕叚叜叝叞叟叠叡叢叧叴叵叶司叹叺叻叼叽叾叿咍咑咓咘咜咞咟咠咡咢咣咤咥咦咧咨咩咫咬咭咮咰咲咳咴" +
        "咵咶咷咸咹咺咻咼咽咿哂哃哅哆哇哈哊哋哌响哎哏哐哑哒哓哔哕哖哘哙哚哛哜哝哞哠哢哣哤哥哧哨哩哪哫哬哯哰哱哴哵哶哷" +
        "哸哹哻哾哿唀唁唂唃唄唅唆唈唉唊唋唌唍唎唏唑唒唓唔唕唖唗唘唙唚唛唜唝唞唟唠唡唢唣唤唥唦唧唨唩唪唫唬唭售唰唲唳唴" +

        // 生僻的形容词、动词
        "妸妺妼妽妿姀姁姂姃姄姅姇姈姉姌姍姎姏姕姛姞姟姠姡姢姣姤姥姦姧姩姪姫姭姮姯姰姱姲姳姴姵姶姷姸姹姺姻姼姽姾娀娂" +
        "娊娋娏娐娒娔娕娖娗娙娚娛娝娞娠娡娢娤娦娧娨娪娫娬娭娮娯娰娳娵娷娸娹娺娻娼娽娾娿婀婁婂婃婄婅婇婈婋婌婍婎婏婐" +
        "婑婒婓婔婖婗婘婙婛婜婝婞婟婠婡婢婣婤婥婦婨婩婫婬婭婮婯婰婱婲婳婸婹婻婼婽婾媀媁媂媃媄媅媆媇媈媉媊媋媏媐" +
        "媑媓媔媕媖媗媘媙媜媝媞媟媠媡媢媣媤媥媦媧媨媩媫媬媭媮媯媰媱媴媶媷媹媺媻媼媽媿嫀嫃嫄嫅嫆嫇嫈嫊嫊嫋嫍嫎嫏嫐嫑嫒嫓嫔" +

        // 罕见的地理、建筑用字
        "廗廘廙廚廜廝廞廠廡廢廤廥廦廧廨廩廪廫廬廭廮廯廰廱廲廳廵廸廹廻廽廾廿忀忁忂忄忇忈忉忊忋忎忏忐忑忒忓忔忕忖忚忛忞忟" +
        "忠忡忢忣忥忦忨忩忪忬忭忮忯忰忱忲忳忴念忶忷忸忹忺忼忽忾忿怂怃怄怅怆怇怈怉怊怋怌怍怏怐怑怒怓怔怕怖怗怘怙怚怛怞怟怡怢" +
        "怣怤怦怩怫怬怭怮怯怰怲怳怴怵怶怷怸怹恀恁恂恃恄恅恆恇恈恉恊恌恎恏恑恔恖恗恘恙恚恛恜恝恞恠恡恦恮恱恴恷恾恿" +
        "悀悁悂悃悄悅悆悇悈悊悋悌悐悑悓悕悗悘悙悜悞悡悢悤悥悧悩悮悰悳悴悷悸悺悻悼悽悾惀惁惂惃惄情惆惇惈惉惌惍惎惏惐惑惒惓惔惖" +

        // 古代音律、历法用字
        "宮宮商徴羽角黻黼黽黾鼂鼃鼄鼅鼆鼇鼈鼉鼊鼌鼍鼏鼐鼑鼒鼔鼕鼖鼘鼙鼚鼛鼜鼝鼞鼟鼡鼣鼤鼥鼦鼧鼨鼩鼪鼫鼭鼮鼯鼰鼱" +
        "龠龡龢龣龤龥龦龧龨龩龪龫龬龭龮龯龰龱龲龳龴龵龶龷龸龹龺龻龼龽龾龿" +
        "仩仫仯仱仴仸仹仺任仼仾伀伂伃伄伅伆伇伈伋伌伎伐伒伓伔伕伖伜伝伡伣伨伩伬伭伮估伳伵伷伹伻伾伿佀佁佂佄佅" +

        // 罕见的心理、情感用字
        "愂愃愄愅愆愇愈愉愊愋愌愍愎愐愑愒愓愔愕愖愗愘愙愚愛愜愝愞愡愢愣愤愥愦愨愩愪愫愬愭愮愯愰愱愲愳愴愵愶愷愸愹愺愻愼愽" +
        "愾愿戂戃戄戅戆戇戉戇戉戋戍戎戏戓戔戕戜戝戞戞戠戣戤戥戧戩戫戬戭戮戯戰戱戲戳戶戶戹戺戻戼戽戾房所扂扃扄扅扆扊扏扐扒扖扗托扙扚扜扝扞扟" +
        "扠扡扢扤扥扨扪扫扬扭扮扯扰扱扲扳扴扵扷扸扺扻扽抁抂抃抅抆抇抈抋抌抍抎抏抐抑抔抙択抣抦抧抩抪抭抮抯抰抲抳抴抉抶抷抺抾拀拁拃拋拏" +

        // 更多罕见字
        "擘抻拏拑拕拗拚拝拞拠拡拤拪拫拰拲拵拸拹拺拻拼拾拁挃挄挅挆指挈挊挋挌挍挎挏挐挒挓挔挕挗挘挙挜挦挧挩挬挭挮挰挱挳挴挵挶挷挸挹挻挼挾挿捀捁捂捄捇捈捊捑捒捓捔捖捗捘捙捚捛捜捝捠捤捥捦捨捪捫捬捯捰捲捳捴捵捸捹捺捻捼捽捾捿掁掃掄掅掆掇授掋掍掎掏掐掑掓掔掕掗掙掚掛掜掝掞掟採掤掦掫掶掹掻掽掾掿拾掑排掓掔掕掖掗掘掙掚掛掜掝掞掟掠採掤掦掫掬掮掯掰掱掳掴掵掶掷掸掹掺掻掼掽掾掿";

    // 英文常用词列表（Dolch sight words + Top 500-800常用词）
    private static final Set<String> COMMON_ENGLISH_WORDS = new HashSet<>(Arrays.asList(
        // Dolch sight words (220个)
        "the", "be", "to", "of", "and", "a", "in", "that", "have", "i",
        "it", "for", "not", "on", "with", "he", "as", "you", "do", "at",
        "this", "but", "his", "by", "from", "they", "we", "say", "her", "she",
        "or", "an", "will", "my", "one", "all", "would", "there", "their", "what",
        "so", "up", "out", "if", "about", "who", "get", "which", "go", "me",
        "when", "make", "can", "like", "time", "no", "just", "him", "know", "take",
        "people", "into", "year", "your", "good", "some", "could", "them", "see", "other",
        "than", "then", "now", "look", "only", "come", "its", "over", "think", "also",
        "back", "after", "use", "two", "how", "our", "work", "first", "well", "way",
        "even", "new", "want", "because", "any", "these", "give", "day", "most", "us",

        // Top 500常用词扩展
        "is", "are", "was", "were", "been", "being", "has", "had", "having", "does",
        "did", "doing", "should", "would", "could", "ought", "i'm", "you're", "he's", "she's",
        "it's", "we're", "they're", "i've", "you've", "we've", "they've", "i'd", "you'd", "he'd",
        "she'd", "we'd", "they'd", "i'll", "you'll", "he'll", "she'll", "we'll", "they'll", "isn't",
        "aren't", "wasn't", "weren't", "hasn't", "haven't", "hadn't", "doesn't", "don't", "didn't",
        "won't", "wouldn't", "shan't", "shouldn't", "can't", "cannot", "couldn't", "mustn't", "let's",
        "that's", "who's", "what's", "here's", "there's", "when's", "where's", "why's", "how's",
        "a", "an", "the", "and", "but", "if", "or", "because", "as", "until",
        "while", "of", "at", "by", "for", "with", "about", "against", "between", "into",
        "through", "during", "before", "after", "above", "below", "to", "from", "up", "down",
        "in", "out", "on", "off", "over", "under", "again", "further", "then", "once",
        "here", "there", "when", "where", "why", "how", "all", "any", "both", "each",
        "few", "more", "most", "other", "some", "such", "no", "nor", "not", "only",
        "own", "same", "so", "than", "too", "very", "s", "t", "can", "will",
        "just", "don", "should", "now", "d", "ll", "m", "o", "re", "ve",
        "y", "ain", "aren", "couldn", "didn", "doesn", "hadn", "hasn", "haven", "isn",
        "ma", "mightn", "mustn", "needn", "shan", "shouldn", "wasn", "weren", "won", "wouldn",

        // 额外常用词
        "found", "great", "where", "right", "too", "did", "come", "only", "through", "just",
        "much", "before", "line", "must", "right", "also", "small", "say", "still", "set",
        "old", "both", "between", "last", "never", "same", "another", "while", "large", "turn",
        "here", "against", "things", "place", "world", "very", "through", "after", "think", "were"
    ));

    // 游戏术语保护（这些词即使在英文环境中也不替换）
    private static final Set<String> PROTECTED_GAME_TERMS = new HashSet<>(Arrays.asList(
        "hp", "gold", "damage", "defense", "strength", "xp", "level", "dungeon",
        "hero", "enemy", "boss", "item", "weapon", "armor", "potion", "scroll",
        "wand", "ring", "bag", "food", "seed", "herb", "artifact", "charm",
        "blessing", "curse", "enchantment", "upgrade", "curse", "identify", "unidentify",
        "dew", "drop", "pickup", "throw", "equip", "unequip", "use", "cancel",
        "yes", "no", "ok", "exit", "return", "back", "next", "prev", "previous",
        "inventory", "gold", "energy", "hunger", "starvation", "visible", "invisible",
        "poison", "burn", "frost", "slow", "haste", "blind", "weak", "fragile",
        "charm", "dodge", "parry", "block", "reflect", "thorns", "kinetic", "momentum"
    ));

    /**
     * 处理文本的主入口方法
     */
    public static String processText(String original) {
        if (original == null || original.isEmpty()) {
            return original;
        }

        // 防止无限递归：如果正在处理中，直接返回原文本
        if (isProcessing) {
            return original;
        }

        // 重要：在检查缓存之前，先验证当前是否应该混淆
        // 必须同时满足：
        // 1. Dungeon.hero 不为 null（游戏正在进行中）
        // 2. 启用了文盲挑战
        // 3. 当前场景是 GameScene（不在主菜单等界面）
        boolean isInGame = Dungeon.hero != null &&
            Game.scene() instanceof GameScene;

        boolean shouldObfuscate = isInGame && Dungeon.isChallenged(Challenges.ILLITERACY);

        if (!shouldObfuscate) {
            // 当前不应该混淆，直接返回原文本
            return original;
        }

        // 检查缓存（只在确定要混淆时才使用缓存）
        if (cache.containsKey(original)) {
            return cache.get(original);
        }

        // 设置处理标志
        isProcessing = true;

        try {
            String result;
            Languages lang = Messages.lang();

            if (!isLanguageSupported()) {
                // 不支持的语言，显示提示信息
                // 直接返回硬编码的文本，避免再次调用 Messages.get() 导致递归
                result = "Language not supported / 暂不支持您的语言";
            } else if (lang == Languages.CHI_SMPL || lang == Languages.CHI_TRAD) {
                // 中文混淆
                result = obfuscateChinese(original);
            } else if (lang == Languages.ENGLISH) {
                // 英文混淆
                result = obfuscateEnglish(original);
            } else {
                result = original;
            }

            // 存入缓存
            cache.put(original, result);
            return result;
        } finally {
            // 清除处理标志
            isProcessing = false;
        }
    }

    /**
     * 检查当前语言是否支持
     */
    public static boolean isLanguageSupported() {
        Languages lang = Messages.lang();
        return lang == Languages.CHI_SMPL ||
               lang == Languages.CHI_TRAD ||
               lang == Languages.ENGLISH;
    }

    /**
     * 中文混淆：常用字保留，非常用字替换为生僻字
     */
    private static String obfuscateChinese(String text) {
        StringBuilder result = new StringBuilder(text.length());

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            // 检查是否为常用中文字符
            if (isCommonChineseCharacter(c)) {
                result.append(c);
            } else if (isChineseCharacter(c)) {
                // 是汉字但不是常用字，替换为生僻字
                result.append(getRandomRareCharacter());
            } else {
                // 非中文字符（数字、标点、英文等）保留
                result.append(c);
            }
        }

        return result.toString();
    }

    /**
     * 英文混淆：常用词保留，非常用词替换为随机字母组合
     */
    private static String obfuscateEnglish(String text) {
        // 保留格式化标记（斜体、粗体、颜色等）
        // 游戏使用 _ 表示斜体，** 表示粗体，@+颜色名称 表示颜色
        String[] parts = text.split("(?=[_@])|(?<=[_@])");

        StringBuilder result = new StringBuilder();

        for (String part : parts) {
            if (part.isEmpty()) {
                continue;
            }

            // 保留格式化标记
            if (part.startsWith("_") || part.startsWith("@")) {
                result.append(part);
            } else {
                // 处理普通文本
                result.append(obfuscateEnglishText(part));
            }
        }

        return result.toString();
    }

    /**
     * 对英文文本进行单词级别的混淆
     */
    private static String obfuscateEnglishText(String text) {
        // 使用正则表达式分割文本，保留标点符号和空格
        Pattern pattern = Pattern.compile("([a-zA-Z]+|[^a-zA-Z]+)");
        Matcher matcher = pattern.matcher(text);

        StringBuilder result = new StringBuilder();

        while (matcher.find()) {
            String token = matcher.group(1);

            if (token == null) {
                continue;
            }

            // 检查是否为单词
            if (token.matches("[a-zA-Z]+")) {
                String wordLower = token.toLowerCase();

                // 如果是常用词或受保护的术语，保留
                if (COMMON_ENGLISH_WORDS.contains(wordLower) ||
                    PROTECTED_GAME_TERMS.contains(wordLower)) {
                    result.append(token);
                } else {
                    // 替换为随机字母组合
                    result.append(randomizeWord(token));
                }
            } else {
                // 非单词内容（标点、数字、空格等）保留
                result.append(token);
            }
        }

        return result.toString();
    }

    /**
     * 将单词替换为相同长度的随机小写字母组合
     */
    private static String randomizeWord(String word) {
        StringBuilder result = new StringBuilder(word.length());

        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);

            // 保留大小写模式
            if (Character.isUpperCase(c)) {
                result.append((char) ('A' + Random.Int(26)));
            } else {
                result.append((char) ('a' + Random.Int(26)));
            }
        }

        return result.toString();
    }

    /**
     * 判断字符是否为常用中文字符
     */
    private static boolean isCommonChineseCharacter(char c) {
        return COMMON_CHINESE_CHARS.indexOf(c) >= 0;
    }

    /**
     * 判断字符是否为中文字符（CJK统一汉字）
     */
    private static boolean isChineseCharacter(char c) {
        return c >= '\u4E00' && c <= '\u9FFF';
    }

    /**
     * 获取一个随机的生僻字符
     */
    private static char getRandomRareCharacter() {
        int index = Random.Int(RARE_CHINESE_CHARS.length());
        return RARE_CHINESE_CHARS.charAt(index);
    }

    /**
     * 清空缓存（可在游戏重启或语言切换时调用）
     */
    public static void clearCache() {
        cache.clear();
    }

    /**
     * 将数值转换为十六进制字符串（用于文盲挑战）
     * @param value 数值对象
     * @return 十六进制字符串，如 "0x1A" 或 "0x1A.8"
     */
    public static String convertToHexString(Object value) {
        if (value == null) {
            return "null";
        }

        // 处理整数类型
        if (value instanceof Integer || value instanceof Short || value instanceof Byte || value instanceof Long) {
            long num = ((Number)value).longValue();
            return "0x" + Long.toHexString(num).toUpperCase();
        }

        // 处理浮点类型 - 转换为十六进制整数部分和小数部分
        if (value instanceof Float || value instanceof Double) {
            double num = ((Number)value).doubleValue();

            // 分离整数和小数部分
            long intPart = (long) num;
            double fracPart = num - intPart;

            // 构建十六进制字符串
            StringBuilder result = new StringBuilder("0x");
            result.append(Long.toHexString(intPart).toUpperCase());

            // 如果有小数部分，添加小数
            if (fracPart > 0) {
                result.append(".");
                // 将小数部分转换为2位十六进制（约等于）
                int fracHex = (int) (fracPart * 256);
                result.append(Integer.toHexString(fracHex).toUpperCase());
            }

            return result.toString();
        }

        // 其他类型直接返回字符串形式
        return value.toString();
    }

    /**
     * 在文本中查找并替换所有数字为十六进制格式
     * 匹配整数和小数（包括负数）
     */
    public static String convertNumbersInText(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        // 重要：先检查当前是否应该转换数字
        // 必须同时满足：hero存在 + 启用了文盲挑战 + 当前场景是GameScene
        boolean isInGame = Dungeon.hero != null &&
            Game.scene() instanceof GameScene;

        if (!isInGame || !Dungeon.isChallenged(Challenges.ILLITERACY)) {
            return text;
        }

        // 正则表达式匹配：整数或小数（包括负数）
        // 改进：不使用 \b 单词边界，避免无法匹配 "x5" 这种情况
        // 匹配格式：-123, 123, 123.45, -123.45, x5, x10.5
        Pattern pattern = Pattern.compile("(?<!\\d)-?\\d+(?:\\.\\d+)?(?!\\d)");
        Matcher matcher = pattern.matcher(text);

        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String numberStr = matcher.group();

            try {
                // 判断是整数还是小数
                if (numberStr.contains(".")) {
                    // 小数
                    double num = Double.parseDouble(numberStr);
                    String hexStr = convertToHexString(num);
                    matcher.appendReplacement(result, hexStr);
                } else {
                    // 整数
                    long num = Long.parseLong(numberStr);
                    String hexStr = convertToHexString(num);
                    matcher.appendReplacement(result, hexStr);
                }
            } catch (NumberFormatException e) {
                // 如果转换失败，保留原数字
                matcher.appendReplacement(result, numberStr);
            }
        }

        matcher.appendTail(result);
        return result.toString();
    }
}
