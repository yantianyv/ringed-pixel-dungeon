package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.ClericSpell;
import com.watabou.noosa.Image;
import com.watabou.noosa.TextureFilm;

//icons for hero subclasses and abilities atm, maybe add classes?
public class HeroIcon extends Image {

    private static TextureFilm film;
    private static final int SIZE = 16;

    //transparent icon
    public static final int NONE = 127;

    //subclasses
    public static final int BERSERKER = 0;
    public static final int GLADIATOR = 1;
    public static final int MUKBANGER = 1000;// 美食家
    public static final int BATTLEMAGE = 2;
    public static final int WARLOCK = 3;
    public static final int ASSASSIN = 4;
    public static final int FREERUNNER = 5;
    public static final int SNIPER = 6;
    public static final int WARDEN = 7;
    public static final int CHAMPION = 8;
    public static final int MONK = 9;
    public static final int PRIEST = 10;
    public static final int PALADIN = 11;

    //abilities
    public static final int HEROIC_LEAP = 16;
    public static final int SHOCKWAVE = 17;
    public static final int ENDURE = 18;
    public static final int ELEMENTAL_BLAST = 19;
    public static final int WILD_MAGIC = 20;
    public static final int WARP_BEACON = 21;
    public static final int SMOKE_BOMB = 22;
    public static final int DEATH_MARK = 23;
    public static final int SHADOW_CLONE = 24;
    public static final int SPECTRAL_BLADES = 25;
    public static final int NATURES_POWER = 26;
    public static final int SPIRIT_HAWK = 27;
    public static final int CHALLENGE = 28;
    public static final int ELEMENTAL_STRIKE = 29;
    public static final int FEINT = 30;
    public static final int ASCENDED_FORM = 31;
    public static final int TRINITY = 32;
    public static final int POWER_OF_MANY = 33;
    public static final int RATMOGRIFY = 34;

    //cleric spells
    public static final int GUIDING_LIGHT = 40;
    public static final int HOLY_WEAPON = 41;
    public static final int HOLY_WARD = 42;
    public static final int HOLY_INTUITION = 43;
    public static final int SHIELD_OF_LIGHT = 44;
    public static final int RECALL_GLYPH = 45;
    public static final int SUNRAY = 46;
    public static final int DIVINE_SENSE = 47;
    public static final int BLESS = 48;
    public static final int CLEANSE = 49;
    public static final int RADIANCE = 50;
    public static final int HOLY_LANCE = 51;
    public static final int HALLOWED_GROUND = 52;
    public static final int MNEMONIC_PRAYER = 53;
    public static final int SMITE = 54;
    public static final int LAY_ON_HANDS = 55;
    public static final int AURA_OF_PROTECTION = 56;
    public static final int WALL_OF_LIGHT = 57;
    public static final int DIVINE_INTERVENTION = 58;
    public static final int JUDGEMENT = 59;
    public static final int FLASH = 60;
    public static final int BODY_FORM = 61;
    public static final int MIND_FORM = 62;
    public static final int SPIRIT_FORM = 63;
    public static final int BEAMING_RAY = 64;
    public static final int LIFE_LINK = 65;
    public static final int STASIS = 66;

    //all cleric spells have a separate icon with no background for the action indicator
    public static final int SPELL_ACTION_OFFSET = 32;

    //action indicator visuals
    public static final int BERSERK = 104;
    public static final int COMBO = 105;
    public static final int PREPARATION = 106;
    public static final int MOMENTUM = 107;
    public static final int SNIPERS_MARK = 108;
    public static final int WEAPON_SWAP = 109;
    public static final int MONK_ABILITIES = 110;

    public HeroIcon(HeroSubClass subCls) {
        this(subCls.icon());
    }

    public HeroIcon(ArmorAbility abil) {
        this(abil.icon());
    }

    public HeroIcon(ActionIndicator.Action action) {
        this(action.actionIcon());
    }

    public HeroIcon(ClericSpell spell) {
        this(spell.icon());
    }

    // Private constructor that handles the actual icon logic
    private HeroIcon(int icon) {
        super(getIconResource(icon)); // Choose the correct texture based on icon value
        if (film == null) {
            film = new TextureFilm(texture, SIZE, SIZE);
        }
        frame(film.get(getAdjustedIcon(icon)));
    }

    // Helper method: returns the correct texture based on icon value
    private static Object getIconResource(int icon) {
        if (icon < 1000) {
            return Assets.Interfaces.HERO_ICONS;
        } else {
            return Assets.Interfaces.HERO_ICONS_RINGED;
        }
    }

    // Helper method: adjusts icon value if >= 1000
    private static int getAdjustedIcon(int icon) {
        return (icon < 1000) ? icon : icon - 1000;
    }
}
