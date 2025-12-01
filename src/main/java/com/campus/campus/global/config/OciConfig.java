package com.campus.campus.global.config;


import com.oracle.bmc.Region;
import com.oracle.bmc.auth.SimpleAuthenticationDetailsProvider;
import com.oracle.bmc.auth.SimplePrivateKeySupplier;
import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.ObjectStorageClient;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
@Slf4j
@Configuration
public class OciConfig {

    @Getter @Value("${oci.region}")
    private String region;

    @Getter @Value("${oci.namespace}")
    private String namespace;

    @Getter @Value("${oci.bucket-name}")
    private String bucketName;

    @Value("${oci.tenancy-ocid}")
    private String tenancyOcid;

    @Value("${oci.user-ocid}")
    private String userOcid;

    @Value("${oci.fingerprint}")
    private String fingerprint;

    @Value("${oci.private-key}")
    private String privateKeyRaw;

    @Value("${oci.passphrase:}")
    private String passPhrase;


    @Bean
    public ObjectStorage objectStorage() {

        // 1) PEM 문자열 정규화
        String normalizedPem = normalizePem(privateKeyRaw);

        // 2) TEMP 파일에 저장
        String privateKeyPath = writePemToTempFile(normalizedPem);

        // 3) Supplier는 파일 경로만 지원
        SimplePrivateKeySupplier keySupplier = new SimplePrivateKeySupplier(privateKeyPath);

        // 4) Provider 생성
        SimpleAuthenticationDetailsProvider provider =
                SimpleAuthenticationDetailsProvider.builder()
                        .tenantId(tenancyOcid)
                        .userId(userOcid)
                        .fingerprint(fingerprint)
                        .privateKeySupplier(keySupplier)
                        .passPhrase(passPhrase)
                        .region(Region.fromRegionId(region))
                        .build();

        return new ObjectStorageClient(provider);
    }


    /** PEM 문자열 정규화 */
    private String normalizePem(String pem) {
        String normalized = pem
                .replace("\\n", "\n")  // 환경변수에서 \n이 들어왔을 때 실제 줄바꿈으로 변경
                .trim();

        String first = normalized.lines().findFirst().orElse("");
        String last = normalized.lines().reduce((a, b) -> b).orElse("");

        if (!first.equals("-----BEGIN PRIVATE KEY-----")) {
            throw new IllegalArgumentException("Invalid PEM header: " + first);
        }
        if (!last.equals("-----END PRIVATE KEY-----")) {
            throw new IllegalArgumentException("Invalid PEM footer: " + last);
        }

        log.info(">>> [PEM] normalize success, lineCount={}",
                normalized.lines().count());

        return normalized;
    }


    /** TEMP 파일로 PEM 저장 */
    private String writePemToTempFile(String pem) {
        try {
            File temp = File.createTempFile("oci-key-", ".pem");
            temp.deleteOnExit();

            try (Writer writer = new FileWriter(temp, StandardCharsets.UTF_8)) {
                writer.write(pem);
            }

            log.info(">>> [PEM] Temp Key File Created: {}", temp.getAbsolutePath());
            return temp.getAbsolutePath();

        } catch (Exception e) {
            log.error(">>> [PEM ERROR] Failed to create temp PEM file", e);
            throw new RuntimeException("Failed to create temp PEM file", e);
        }
    }
}
