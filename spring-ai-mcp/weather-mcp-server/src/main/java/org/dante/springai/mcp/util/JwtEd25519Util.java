package org.dante.springai.mcp.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class JwtEd25519Util {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String generateJwt(String kid, String sub) throws Exception {
        return generateJwt("qweather-ed25519-private.pem", kid, sub);
    }

    /**
     * 生成 JWT
     *
     * @param privateKeyPath Ed25519 私钥文件路径，PEM 格式
     * @param kid            key id
     * @param sub            subject
     * @return JWT 字符串
     * @throws Exception 异常
     */
    public static String generateJwt(String privateKeyPath, String kid, String sub) throws Exception {
        String alg = "EdDSA";

        // iat = 当前时间戳 - 30秒
        long iat = Instant.now().getEpochSecond() - 30;
        // exp = 当前时间戳 + 20小时
        long exp = Instant.now().getEpochSecond() + 20 * 3600;

        // 1. 构建 header 和 payload
        Map<String, Object> header = new HashMap<>();
        header.put("alg", alg);
        header.put("kid", kid);

        Map<String, Object> payload = new HashMap<>();
        payload.put("sub", sub);
        payload.put("iat", iat);
        payload.put("exp", exp);

        // 2. JSON 转字符串
        String headerJson = objectMapper.writeValueAsString(header);
        String payloadJson = objectMapper.writeValueAsString(payload);

        // 3. Base64URL 编码
        String encodedHeader = base64UrlEncode(headerJson.getBytes(StandardCharsets.UTF_8));
        String encodedPayload = base64UrlEncode(payloadJson.getBytes(StandardCharsets.UTF_8));

        // 4. 拼接签名输入
        String signingInput = encodedHeader + "." + encodedPayload;

        // 5. 读取私钥并签名
        PrivateKey privateKey = loadEd25519PrivateKey(privateKeyPath);
        byte[] signature = signEd25519(privateKey, signingInput.getBytes(StandardCharsets.UTF_8));

        // 6. Base64URL 编码签名
        String encodedSignature = base64UrlEncode(signature);

        // 7. 拼接完整 JWT
        return signingInput + "." + encodedSignature;
    }

    private static String base64UrlEncode(byte[] data) {
        String base64 = Base64.getEncoder().encodeToString(data);
        // 替换 + / 为 - _，去掉 =
        return base64.replace('+', '-').replace('/', '_').replaceAll("=+$", "");
    }

    private static PrivateKey loadEd25519PrivateKey(String privateKeyPath) throws Exception {
        try (InputStream is = JwtEd25519Util.class.getClassLoader().getResourceAsStream(privateKeyPath)) {
            if (is == null) {
                throw new IllegalArgumentException("Resource not found: " + privateKeyPath);
            }
            // 读取 PEM 文件内容
            String pem = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8)).lines().filter(line -> !line.startsWith("-----BEGIN") && !line.startsWith("-----END")).collect(Collectors.joining());

            byte[] keyBytes = Base64.getDecoder().decode(pem);

            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance("Ed25519");
            return kf.generatePrivate(keySpec);
        }
    }

    private static byte[] signEd25519(PrivateKey privateKey, byte[] data) throws Exception {
        Signature sig = Signature.getInstance("Ed25519");
        sig.initSign(privateKey);
        sig.update(data);
        return sig.sign();
    }

}