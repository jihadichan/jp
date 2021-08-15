package core;

import java.lang.Character.Subset;
import java.util.HashSet;
import java.util.Set;

public class KanjiDetect {

    private static final Set<Subset> japaneseBlocks = new HashSet();
    static {
        japaneseBlocks.add(Character.UnicodeBlock.KATAKANA);
        japaneseBlocks.add(Character.UnicodeBlock.HIRAGANA);
        japaneseBlocks.add(Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS);
        japaneseBlocks.add(Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A);
        // add other blocks as necessary
    }
    public static boolean isKanji(char input) {
        Character.UnicodeBlock block = Character.UnicodeBlock.of(input);
        return block.equals(Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS) || block.equals(Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A);
    }

}
