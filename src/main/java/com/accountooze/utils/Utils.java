package com.accountooze.utils;


import com.accountooze.config.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.util.Base64;
import java.util.Random;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

@Component
public class Utils {

    @Autowired
    JwtUtil jwtUtil;


    public static int getOtp() {
        return new Random().nextInt(900000) + 100000;
    }

    private static final String ALGORITHM = "AES";
    private static final String MODE = "AES/ECB/PKCS5Padding";

    public static String encrypt(String data, String key) throws Exception {

        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(MODE);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public static String decrypt(String encryptedData, String key) throws Exception {

        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), ALGORITHM);

        Cipher cipher = Cipher.getInstance(MODE);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
        return new String(decryptedBytes);
    }


    public static String compress(String data) throws IOException {

        ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length());
        GZIPOutputStream gzip = new GZIPOutputStream(bos);
        gzip.write(data.getBytes());
        gzip.close();
        byte[] compressed = bos.toByteArray();
        bos.close();
        return new String(Base64.getEncoder().encode(compressed));
    }

    public static String decompress(byte[] compressed) throws IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream(compressed);
        GZIPInputStream gis = new GZIPInputStream(bis);
        BufferedReader br = new BufferedReader(new InputStreamReader(gis, "UTF-8"));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();
        gis.close();
        bis.close();
        return sb.toString();
    }

    public static boolean validateApicallTime(long ts, long currentDateTime) {
        return ts + (Constants.EXPIRE_MIN * Constants.MIN) >= currentDateTime;
    }


    public Long getLoginUserId(HttpServletRequest request) {
        String jwtToken = request.getHeader("Authorization");
        Long userIdFromClaim = null;
        if (jwtToken != null && !jwtToken.isBlank()) {
            jwtToken = jwtToken.replace("Bearer", "");
            Claims claims = jwtUtil.extractAllClaims(jwtToken);
            userIdFromClaim = Long.valueOf(((Integer) claims.get("USER_ID")).longValue());
        }
        return userIdFromClaim;
    }


}
