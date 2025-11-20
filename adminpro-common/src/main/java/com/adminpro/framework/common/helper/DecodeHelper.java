package com.adminpro.framework.common.helper;

import com.adminpro.core.base.util.Base64Util;
import com.adminpro.core.base.util.Md5Util;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Security;

public class DecodeHelper {
    private static final String ALGORITHM = "AES";

    private static final String ALGORITHM_MODE_PADDING = "AES/ECB/PKCS7Padding";

    public static String decryptData(String key, String base64Data) throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        SecretKeySpec secretKey = new SecretKeySpec(Md5Util.MD5Encode(key, "UTF-8").toLowerCase().getBytes(), ALGORITHM);

        Cipher cipher = Cipher.getInstance(ALGORITHM_MODE_PADDING);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return new String(cipher.doFinal(Base64Util.decode(base64Data)));
    }

    /*public static void main(String[] args) throws Exception {
        String A = "3UBQLrJhgk1/xAUCZRakfWC91bTo9SvO3D0oLtyfBKO+B4QurYHINXBUEo60KnxqwXiSCl6A+6O55vyUmTMSYn/HVolGfX06po2cW2d3V8VcyiEbFu0+SrG5L9ln5ocPYfpgSYFkD2KRZfRkp6oc3sAyIqNatjx8HXH/LCfMmbog918BvTNpy6WXKdUvkASRLkGSBBly3ZsUxzqnDQA8R3xERVoQt/JXMI+6ckLxGbrX6fF2ttZIU8dUkpeWehwvzTgQg6ta/jjSpFcktZQ9Ygacwu0mVmJ5yFuEF3L8UE1glg6fPO5kHmAeDDypeFlbnCE+EmD8RBQ1NA6uzEooMsNuqX/zx3ZDWOF9sFtHarmJkOjVridBCPcy5mR9fPfwYLwQvqpVE4LKvrioibAiunvr1mzVRkwn6yJrUodGuoFz4R22Snq1atrUMYpTSmClMJV4319c2p3ZwbMeV1B8XIO0SDfdszLRJuif0qfP+g6TEuZ5n9I3YgyaxSy2pFBY4xMWenPk2hmQVBjVS+bwjnWFjg4EtH6kYgxq50BIxmzU3Lu5H4HhLasfMmiyrghbiAuFParhDjJVPmeuWxcuGCOj9mHBkNx/PRb0x76WZU8JjpLqrhXHKoGhJo9u3UY62wMkGZLghv8zBMzTde8j21PPMbalRBFx3tK85MZpMNYGnMLtJlZiechbhBdy/FBNj/AXm+Qe90Ru3hY7/BRJH5139w7IfsYEMnzJpx/3t/UweWG2UtkK5w1Y97Nk3VzqoWksmxW9lU8Ys+gz/0ob7RfowQ545AbGwA2Ihwf5WdPtHZQzxsIb1ZUpS8OaeRmtxNnwoJhxqzb1RgmckbeMxktW4XOYmtCYR8DbvFrshR3N1CxmOwa5wnI7rl2f1K0GjqwO5z/28+AgJ01f3vNzZvbZcZObTDoI4X+eYSHgxmbZoGiGNiWG1t9l+sxzf6SBEQF6etDQLhRGpnPYGoakwxR+KoHnVK19AlPrKizHXkz3y4pOH8ch15giqMS3UL+uSzwsVHOY1myKB+aR1fQ7Zj41bz+tHrqoRWn3L/czx/E\u003d";
        String B = DecodeUtil.decryptData(key, A);
        Map<String, String> stringStringMap = WXPayUtil.xmlToMap(B);
        System.out.println(new Gson().toJson(stringStringMap));
    }*/
}
