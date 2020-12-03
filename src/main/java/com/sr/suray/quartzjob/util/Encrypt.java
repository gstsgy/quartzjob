package com.sr.suray.quartzjob.util;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

/**
 * @author guyue
 * @version 0.0.0.1
 * @date 2019-12-26
 * @desc 数据加密相关
 */
public class Encrypt {
    /**
     * 进行MD5加密
     *
     * @param info 要加密的信息
     * @return String 加密后的字符串
     */
    public static String encryptToMD5(String info) {
        byte[] digesta = null;
        try {
            // 得到一个md5的消息摘要
            MessageDigest alga = MessageDigest.getInstance("MD5");
            // 添加要进行计算摘要的信息
            alga.update(info.getBytes());
            // 得到该摘要
            digesta = alga.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        // 将摘要转为字符串
        String rs = byte2hex(digesta);
        return rs;
    }

    /**
     * 进行SHA加密
     *
     * @param info 要加密的信息
     * @return String 加密后的字符串
     */
    public static String encryptToSHA(String info) {
        byte[] digesta = null;
        try {
            // 得到一个SHA-1的消息摘要
            MessageDigest alga = MessageDigest.getInstance("SHA-1");
            // 添加要进行计算摘要的信息
            alga.update(info.getBytes());
            // 得到该摘要
            digesta = alga.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        // 将摘要转为字符串
        String rs = byte2hex(digesta);
        return rs;
    }

    /**
     * AES加密字符串
     *
     * @param content
     *            需要被加密的字符串
     * @param password
     *            加密需要的密码
     * @return 密文
     */
    public static byte[] encryptToAES(String content, String password) {
        try {
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG") ;
            secureRandom.setSeed(password.getBytes());
            KeyGenerator kgen = KeyGenerator.getInstance("AES");// 创建AES的Key生产者
            kgen.init(128, secureRandom);
            SecretKey secretKey = kgen.generateKey();// 根据用户密码，生成一个密钥
            byte[] enCodeFormat = secretKey.getEncoded();// 返回基本编码格式的密钥，如果此密钥不支持编码，则返回
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");// 转换为AES专用密钥
            Cipher cipher = Cipher.getInstance("AES");// 创建密码器
            byte[] byteContent = content.getBytes("utf-8");
            cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化为加密模式的密码器
            byte[] result;// 加密
            result = cipher.doFinal(byteContent);
            return result;
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | UnsupportedEncodingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 解密AES加密过的字符串
     *
     * @param content
     *            AES加密过过的内容
     * @param password
     *            加密时的密码
     * @return 明文
     */
    public static byte[] decryptToAES(byte[] content, String password) {
        try {

            //kg.init(keysize, secureRandom);
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG") ;
            secureRandom.setSeed(password.getBytes());
            KeyGenerator kgen = KeyGenerator.getInstance("AES");// 创建AES的Key生产者
            kgen.init(128, secureRandom);
            SecretKey secretKey = kgen.generateKey();// 根据用户密码，生成一个密钥
            byte[] enCodeFormat = secretKey.getEncoded();// 返回基本编码格式的密钥
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");// 转换为AES专用密钥
            Cipher cipher = Cipher.getInstance("AES");// 创建密码器
            cipher.init(Cipher.DECRYPT_MODE, key);// 初始化为解密模式的密码器
            byte[] result;
            result = cipher.doFinal(content);
            return result; // 明文
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | InvalidKeyException | BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * RSA公钥加密
     *
     * @param str
     *            加密字符串
     * @param publicKey
     *            公钥
     * @return 密文
     * @throws Exception
     *             加密过程中的异常信息
     */
    public static String encryptToRSA( String str, String publicKey ){
        byte[] decoded = Base64.decodeBase64(publicKey);
        RSAPublicKey pubKey = null;
        try {
            pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
        try {
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        try {
            return Base64.encodeBase64String(cipher.doFinal(str.getBytes("UTF-8")));
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * RSA私钥解密
     *
     * @param str
     *            加密字符串
     * @param privateKey
     *            私钥
     * @return 铭文
     * @throws Exception
     *             解密过程中的异常信息
     */
    public static String decryptToRSA(String str, String privateKey) throws Exception{
        //64位解码加密后的字符串
        byte[] inputByte = Base64.decodeBase64(str.getBytes("UTF-8"));
        //base64编码的私钥
        byte[] decoded = Base64.decodeBase64(privateKey);
        RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decoded));
        //RSA解密
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, priKey);
        String outStr = new String(cipher.doFinal(inputByte));
        return outStr;
    }

    /**
     * 随机生成密钥对
     * @throws NoSuchAlgorithmException  1 私钥   0 公 钥
     */
    public static Map<Integer, String> genRSAKeyPair() throws NoSuchAlgorithmException {
         Map<Integer, String> keyMap = new HashMap<Integer, String>();
        // KeyPairGenerator类用于生成公钥和私钥对，基于RSA算法生成对象
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        // 初始化密钥对生成器，密钥大小为96-1024位
        keyPairGen.initialize(1024,new SecureRandom());
        // 生成一个密钥对，保存在keyPair中
        KeyPair keyPair = keyPairGen.generateKeyPair();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();   // 得到私钥
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();  // 得到公钥
        String publicKeyString = new String(Base64.encodeBase64(publicKey.getEncoded()));
        // 得到私钥字符串
        String privateKeyString = new String(Base64.encodeBase64((privateKey.getEncoded())));
        // 将公钥和私钥保存到Map
        keyMap.put(0,publicKeyString);  //0表示公钥
        keyMap.put(1,privateKeyString);  //1表示私钥
        return keyMap;
    }



    /**
     * 将二进制转化为16进制字符串
     *
     * @param b 二进制字节数组
     * @return String
     */
    public static String byte2hex(byte[] buf) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();

    }

    /**
     * 十六进制字符串转化为2进制
     *
     * @param hex
     * @return
     */
    public static byte[] hex2byte(String hexStr) {
        if (hexStr.length() < 1)
            return null;
        byte[] result = new byte[hexStr.length()/2];
        for (int i = 0;i< hexStr.length()/2; i++) {
            int high = Integer.parseInt(hexStr.substring(i*2, i*2+1), 16);
            int low = Integer.parseInt(hexStr.substring(i*2+1, i*2+2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }

    /**
     * 将两个ASCII字符合成一个字节； 如："EF"--> 0xEF
     *
     * @param src0 byte
     * @param src1 byte
     * @return byte
     */
    public static byte uniteBytes(byte src0, byte src1) {
        byte _b0 = Byte.decode("0x" + new String(new byte[]{src0}))
                .byteValue();
        _b0 = (byte) (_b0 << 4);
        byte _b1 = Byte.decode("0x" + new String(new byte[]{src1}))
                .byteValue();
        byte ret = (byte) (_b0 ^ _b1);
        return ret;
    }

    /**
     * 将指定的对象写入指定的文件
     *
     * @param file 指定写入的文件
     * @param objs 要写入的对象
     */
    public static void doObjToFile(String file, Object[] objs) {
        ObjectOutputStream oos = null;
        try {
            FileOutputStream fos = new FileOutputStream(file);
            oos = new ObjectOutputStream(fos);
            for (int i = 0; i < objs.length; i++) {
                oos.writeObject(objs[i]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                oos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 返回在文件中指定位置的对象
     *
     * @param file 指定的文件
     * @param i    从1开始
     * @return
     */
    public static Object getObjFromFile(String file, int i) {
        ObjectInputStream ois = null;
        Object obj = null;
        try {
            FileInputStream fis = new FileInputStream(file);
            ois = new ObjectInputStream(fis);
            for (int j = 0; j < i; j++) {
                obj = ois.readObject();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                ois.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return obj;
    }

    /**
     * 测试
     *
     * @param args
     */
    public static void main(String[] args) throws Exception{

        /*System.out.println("Hello经过MD5:" + encryptToMD5("123456"));
        String str = "hello world!";
        String password = "1314521";
        //System.out.println("aes加密："+new String();
        byte[] byte1=encryptToAES(str,password);
        String p1 = byte2hex(byte1);
        byte[] p2 = hex2byte(p1);
        byte[] byte2 = decryptToAES(p2,password);
        //Arrays.asList(byte1).stream().forEach(System.out::println);
        //

        System.out.println("aes加密："+p1);
        //byte[] bytes = new byte[]{0x6,0x2,0xe,0xe,0x6,0x8,0xd,0x8};
        //byte[] byte2 = decryptToAES(bytes,password);
        System.out.println("aes解密："+new String(byte2));

        Map map = genRSAKeyPair();
        System.out.println(map);

        String pubk = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCn3uvb2QvpxWz9dtK9WdWhvTICpbozwlAiHs581D0cXxK9ub41xznuGY23+FusQ1afQKUDNnJO+uxiuwN3abOiPOhzvaCjZspEi8ExaL7NUeVDZ1lMVytU2D4dF3hIXu+aBr57jWj/Lkq6o9x6jtdKFcpgCJt7eNMB7HnYtgHGZQIDAQAB";
        String prik="MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAKfe69vZC+nFbP120r1Z1aG9MgKlujPCUCIeznzUPRxfEr25vjXHOe4Zjbf4W6xDVp9ApQM2ck767GK7A3dps6I86HO9oKNmykSLwTFovs1R5UNnWUxXK1TYPh0XeEhe75oGvnuNaP8uSrqj3HqO10oVymAIm3t40wHsedi2AcZlAgMBAAECgYBzMmGuS0KGmGlTgi/ZqoUUaY2hDXM6w5RT5coTFV4eUwu8UHM72kxhnP2IipD2JHuT6zuRedJ1anG+ytD1J/8RLwYE+dxMouVJBeK7YEM0Y9Z5zK46vyltCDe2vRoSbN5c9P+lYyeotMVZaTV7keto4lmZEOnCW6FHjnuJNW58gQJBAOb6kWFP5nCyWbjE68K1Gjppbu9FlPzsLXJNBw9y3Z/c9TAKt9yWsu9UjJxJEnMGvNSQbvWDfnWC0lSIqXdRFaECQQC6DkMVvZnCTLJg2EFfqpPUULZ7KX4XZqbVd4EfJ+AMS7umwKNh3+ivYmO0cLgSZN2qZ+7ixdGBI1LkkkdM/bJFAkBNQw+SP4nTtLTya1x5et7VHyNughX+LHpfO4jbEA82RFD3yv4JmZctG1kVhhPXyi8r40xab6vDXwlAIZ6ePM9BAkB4MqyOSA4ko239+NfLGSUl3fr1+GlYbt3LPMgzO4yYBPk3Em661W+6loK5WLTiVT2LUFfZM1lU52AvAH2FtMRNAkEAzNEAMNTXE0IfYhNwuRh7o3l7bz+4B742NautiF4X9DEqO5izR1HYLzcDax6iVicR1vRJs5OfmYLLj1DLnRWIbw==";

        String p3 = encryptToRSA(str,pubk);
        System.out.println("rsa加密："+p3);
        System.out.println("rsa解密："+decryptToRSA(p3,prik));*/
        //Map map = genRSAKeyPair();
         String p3 = encryptToRSA("suray2020","MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCa9yaMZuvRGWiJ9YD4rUEF39588I89aE6H6208kNVm3qaOpLZMU5xXHKL1UsKDqjaoV5vJnban3Gu2hWpkHP/IKPB3MxUjTEsLOKkCt/8G0JEEtOd3LOr3SGo6b4ofWSAEFegfsd9F9BC3/q/Ag5TY18uTFBZ9mopDYGZfQCZkuwIDAQAB");
        System.out.println(p3);
    }

}
