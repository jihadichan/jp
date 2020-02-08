package crap;

import com.atilika.kuromoji.ipadic.Token;
import com.atilika.kuromoji.ipadic.Tokenizer;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

public class Kuromoji {

    private static final Tokenizer kuromoji = new Tokenizer.Builder().build();

    @Test
    public void asdf() {
        final List<Token> tokens = kuromoji.tokenize("可愛かったです、でも\n今は可愛くないです。");
        for (final Token token : tokens) {
            System.out.println(token.getSurface() + " - " + token.getPosition() + " - " + token.getPartOfSpeechLevel1());
        }
    }

    @Test
    public void nonJapChars() {
        final Pattern standardWhiteSpace = Pattern.compile(" ");
        final Pattern japWhiteSpace = Pattern.compile("　");
        final String errorSentence = "フランス 人です";
        System.out.println("Has standard whitespace? " + standardWhiteSpace.matcher(errorSentence).find());
        System.out.println("Has jap whitespace? " + japWhiteSpace.matcher(errorSentence).find());


    }

    @Test
    public void yxcv() {
        final Pattern quotePattern = Pattern.compile("\"");
        final String val = "Some \"text\" and shit";
        System.out.println(val);
        System.out.println(quotePattern.matcher(val).replaceAll("\\\\\""));
    }

    @Test
    public void qwer() {
        final String raw = "いいえ、あれは富士山じゃありません。でも、山です。";
        final String base64 = Base64.getEncoder().encodeToString(raw.getBytes());
        System.out.println(base64);
        System.out.println(new String(Base64.getDecoder().decode(base64)));

        final String urlEncoded = URLEncoder.encode(raw, StandardCharsets.UTF_8);
        System.out.println(urlEncoded);
        System.out.println(URLDecoder.decode(urlEncoded, StandardCharsets.UTF_8));
    }

    @Test
    public void sdfg() {
        String name = "ジョージはとても疲れていたので医者は彼にもっとよく体に気をつけるようにと忠告した";
        if (name.length() > 35) {
            name = name.substring(0, 35) + "-" + ThreadLocalRandom.current().nextInt(1000, 9999);
        }
        System.out.println(name);
    }

    @Test
    public void dghgh() throws Exception {
        System.out.println(DigestUtils.md5Hex("asdf"));
        System.out.println(DigestUtils.md5Hex("asdf"));
        System.out.println(DigestUtils.md5Hex("asdf"));
        System.out.println(DigestUtils.md5Hex("asdf1"));
        System.out.println(DigestUtils.md5Hex("asdf1"));

    }

}
