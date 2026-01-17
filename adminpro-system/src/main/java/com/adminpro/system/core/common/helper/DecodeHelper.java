package com.adminpro.system.core.common.helper;

import com.adminpro.framework.base.util.Base64Util;
import com.adminpro.framework.base.util.Md5Util;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Security;

public class DecodeHelper {
    private static final String ALGORITHM = "AES";

    private static final String ALGORITHM_MODE_PADDING = "AES/ECB/PKCS7Padding";

    public static String decryptData(String key, String base64Data) throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        SecretKeySpec secretKey = new SecretKeySpec(Md5Util.MD5Encode(key, "UTF-8").toLowerCase().getBytes(),
                ALGORITHM);

        Cipher cipher = Cipher.getInstance(ALGORITHM_MODE_PADDING);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return new String(cipher.doFinal(Base64Util.decode(base64Data)));
    }

}
