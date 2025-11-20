package com.adminpro.core.base.util;

import com.adminpro.core.exceptions.BaseRuntimeException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.regex.Pattern;

public class CryptUtil {
    private CryptUtil() {
    }

    public static final String UTF_8 = "UTF-8";

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static KeyPair genKeyPair() {
        KeyPairGenerator keyPairGen;
        try {
            keyPairGen = KeyPairGenerator.getInstance("RSA", "BC");

        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new BaseRuntimeException(e);
        }
        keyPairGen.initialize(1024);
        return keyPairGen.generateKeyPair();
    }

    /**
     * generate certification
     *
     * @param pubKey
     * @param privKey
     * @return
     */
    public static X509Certificate genCertificate(PublicKey pubKey, PrivateKey privKey) {
        String mySigAlgo = "Sha1withRSA";
        SubjectPublicKeyInfo publicKeyInfo = SubjectPublicKeyInfo.getInstance(pubKey.getEncoded());

        X500NameBuilder nameBuilder = new X500NameBuilder();
        nameBuilder.addRDN(BCStyle.C, "CN");
        nameBuilder.addRDN(BCStyle.ST, "JS");
        nameBuilder.addRDN(BCStyle.L, "SZ");
        nameBuilder.addRDN(BCStyle.CN, "DL");
        nameBuilder.addRDN(BCStyle.E, "dailiang@szyh.info");
        nameBuilder.addRDN(BCStyle.O, "O");
        nameBuilder.addRDN(BCStyle.OU, "OU");

        X500Name x500Name = nameBuilder.build();

        LocalDate now = LocalDate.now();
        ZoneId zone = ZoneId.systemDefault();
        java.util.Date fromDate = java.util.Date.from(now.atStartOfDay(zone).toInstant());
        java.util.Date toDate = java.util.Date.from(now.plusYears(100).atStartOfDay(zone).toInstant());
        BigInteger bigInteger = new BigInteger(64, new SecureRandom());
        X509v3CertificateBuilder certBuilder = new X509v3CertificateBuilder(x500Name, bigInteger, fromDate, toDate, x500Name, publicKeyInfo);
        ContentSigner signer;
        try {
            signer = new JcaContentSignerBuilder(mySigAlgo).build(privKey);

        } catch (OperatorCreationException e) {
            throw new BaseRuntimeException(e);
        }

        X509CertificateHolder certHolder = certBuilder.build(signer);
        X509Certificate cert;
        try {
            cert = (new JcaX509CertificateConverter()).getCertificate(certHolder);

        } catch (CertificateException e) {
            throw new BaseRuntimeException(e);
        }
        return cert;
    }

    /**
     * read from certification bytes
     *
     * @param bytes
     * @return
     */
    public static PublicKey readPublicKey(byte[] bytes) {
        try (ByteArrayInputStream inStream = new ByteArrayInputStream(bytes)) {
            CertificateFactory certFac = CertificateFactory.getInstance("X.509", "BC");
            Certificate cert = certFac.generateCertificate(inStream);
            return cert.getPublicKey();

        } catch (CertificateException | NoSuchProviderException | IOException e) {
            throw new BaseRuntimeException(e);
        }
    }

    /**
     * read from private key bytes
     *
     * @param bytes
     * @return
     */
    public static PrivateKey readPrivateKey(byte[] bytes) {
        KeySpec keySpec = new PKCS8EncodedKeySpec(bytes);
        try {
            KeyFactory keyFac = KeyFactory.getInstance("RSA");
            return keyFac.generatePrivate(keySpec);

        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            throw new BaseRuntimeException(e);
        }
    }

    public static byte[] encrypt(byte[] content, String key) {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128, new SecureRandom(key.getBytes()));
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(keyGen.generateKey().getEncoded(), "AES"));
            return cipher.doFinal(content);
        } catch (Exception e) {
            throw new BaseRuntimeException(e);
        }
    }

    public static byte[] decrypt(byte[] content, String key) {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128, new SecureRandom(key.getBytes()));
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(keyGen.generateKey().getEncoded(), "AES"));
            return cipher.doFinal(content);
        } catch (Exception e) {
            throw new BaseRuntimeException(e);
        }
    }

    // [start] signer
    public static String signAsMD5(String str) {
        return DigestUtils.md5Hex(str);
    }

    public static String signAsSHA1(String str) {
        return DigestUtils.sha1Hex(str);
    }

    // [end]

    // [start] base64
    public static String encodeBase64(String str) {
        try {
            return Base64.encodeBase64String(str.getBytes(UTF_8));

        } catch (UnsupportedEncodingException e) {
            throw new BaseRuntimeException(e);
        }
    }

    public static String encodeBase64(byte[] bytes) {
        return Base64.encodeBase64String(bytes);

    }

    public static String encodeBase64(String str, boolean isChunked) {
        if (!isChunked) {
            return encodeBase64(str);
        }

        try {
            return new String(Base64.encodeBase64Chunked(str.getBytes("UTF-8")));

        } catch (UnsupportedEncodingException e) {
            throw new BaseRuntimeException(e);
        }
    }

    public static String encodeBase64(byte[] bytes, boolean isChunked) {
        if (!isChunked) {
            return Base64.encodeBase64String(bytes);
        }

        return new String(Base64.encodeBase64Chunked(bytes));

    }

    public static String encodeBase64URLSafe(String str) {
        try {
            return Base64.encodeBase64URLSafeString(str.getBytes("UTF-8"));

        } catch (UnsupportedEncodingException e) {
            throw new BaseRuntimeException(e);
        }
    }

    public static String encodeBase64URLSafe(byte[] bytes) {
        return Base64.encodeBase64URLSafeString(bytes);
    }

    public static String decodeBase64AsString(String str) {
        return new String(Base64.decodeBase64(str));
    }

    public static byte[] decodeBase64(String str) {
        return Base64.decodeBase64(str);
    }

    public static boolean isBase64(String str) {
        String base64Pattern = "^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{4}|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)$";
        return Pattern.matches(base64Pattern, str);
    }
}
