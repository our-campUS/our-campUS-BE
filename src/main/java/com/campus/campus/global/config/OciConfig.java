package com.campus.campus.global.config;

import com.oracle.bmc.Region;
import com.oracle.bmc.auth.SimpleAuthenticationDetailsProvider;
import com.oracle.bmc.auth.SimplePrivateKeySupplier;
import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.ObjectStorageClient;
import jakarta.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Set;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Slf4j
@Configuration
public class OciConfig {

    private static final String PEM_HEADER = "-----BEGIN PRIVATE KEY-----";
    private static final String PEM_FOOTER = "-----END PRIVATE KEY-----";
    private static final String KEY_DIR_NAME = ".oci-keys";
    private static final String KEY_FILE_NAME = "private-key.pem";
    private static final String OBJECT_STORAGE_URL_TEMPLATE =
            "https://objectstorage.%s.oraclecloud.com/n/%s/b/%s/o/%s";

    @Getter
    @Value("${oci.region}")
    private String region;

    @Getter
    @Value("${oci.namespace}")
    private String namespace;

    @Getter
    @Value("${oci.bucket-name}")
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

    private Path keyFilePath;

    @Bean
    @Scope("singleton")
    public ObjectStorage objectStorage() {
        log.info(">>> [OCI] Initializing ObjectStorage client...");

        String normalizedPem = normalizePem(privateKeyRaw);

        try {

            keyFilePath = createSecureKeyFile(normalizedPem);

            // Provider 생성
            SimplePrivateKeySupplier keySupplier =
                    new SimplePrivateKeySupplier(keyFilePath.toString());

            SimpleAuthenticationDetailsProvider provider =
                    SimpleAuthenticationDetailsProvider.builder()
                            .tenantId(tenancyOcid)
                            .userId(userOcid)
                            .fingerprint(fingerprint)
                            .privateKeySupplier(keySupplier)
                            .passPhrase(passPhrase)
                            .region(Region.fromRegionId(region))
                            .build();

            ObjectStorage client = new ObjectStorageClient(provider);

            log.info(">>> [OCI] ObjectStorage client initialized successfully for region: {}", region);

            return client;

        } catch (Exception e) {
            cleanupKeyFile();
            log.error(">>> [OCI ERROR] Failed to initialize ObjectStorage", e);
            throw new IllegalStateException("Failed to initialize ObjectStorage", e);
        }
    }

    private String normalizePem(String pem) {
        if (pem == null || pem.isBlank()) {
            throw new IllegalArgumentException("Private key cannot be null or empty");
        }

        String normalized = pem
                .replace("\\n", "\n")
                .trim();

        String firstLine = normalized.lines().findFirst().orElse("");
        String lastLine = normalized.lines().reduce((a, b) -> b).orElse("");

        if (!firstLine.equals(PEM_HEADER)) {
            throw new IllegalArgumentException("Invalid PEM header: " + firstLine);
        }
        if (!lastLine.equals(PEM_FOOTER)) {
            throw new IllegalArgumentException("Invalid PEM footer: " + lastLine);
        }

        long lineCount = normalized.lines().count();
        log.info(">>> [PEM] Normalization successful, line count: {}", lineCount);

        return normalized;
    }

    /**
     * 앱 내부 디렉토리에 보안 키 파일 생성
     * 경로: {user.dir}/.oci-keys/private-key.pem
     */
    private Path createSecureKeyFile(String pem) throws IOException {
        // 1. 앱 실행 디렉토리 하위에 .oci-keys 디렉토리 생성
        Path workingDir = Paths.get(System.getProperty("user.dir"));
        Path keyDir = workingDir.resolve(KEY_DIR_NAME);

        Files.createDirectories(keyDir);
        log.info(">>> [PEM] Key directory: {}", keyDir.toAbsolutePath());

        setDirectoryPermissions(keyDir);

        Path keyFile = keyDir.resolve(KEY_FILE_NAME);

        Files.deleteIfExists(keyFile);

        Files.writeString(keyFile, pem, StandardCharsets.UTF_8);

        setFilePermissions(keyFile);

        log.info(">>> [PEM] Secure key file created: {}", keyFile.toAbsolutePath());

        return keyFile;
    }

    /**
     * 디렉토리 권한 설정 (700 - owner만 접근)
     */
    private void setDirectoryPermissions(Path dir) throws IOException {
        File d = dir.toFile();

        try {

            Set<PosixFilePermission> perms = Set.of(
                    PosixFilePermission.OWNER_READ,
                    PosixFilePermission.OWNER_WRITE,
                    PosixFilePermission.OWNER_EXECUTE
            );
            Files.setPosixFilePermissions(dir, perms);
            log.debug(">>> [PEM] Directory POSIX permissions set (700)");

        } catch (UnsupportedOperationException e) {
            // Windows
            d.setReadable(false, false);
            d.setReadable(true, true);
            d.setWritable(false, false);
            d.setWritable(true, true);
            d.setExecutable(false, false);
            d.setExecutable(true, true);
            log.debug(">>> [PEM] Directory Windows permissions set");
        }
    }

    /**
     * 파일 권한 설정 (600 - owner만 읽기/쓰기)
     */
    private void setFilePermissions(Path file) throws IOException {
        File f = file.toFile();

        try {
            Set<PosixFilePermission> perms = Set.of(
                    PosixFilePermission.OWNER_READ,
                    PosixFilePermission.OWNER_WRITE
            );
            Files.setPosixFilePermissions(file, perms);
            log.debug(">>> [PEM] File POSIX permissions set (600)");

        } catch (UnsupportedOperationException e) {
            // Windows
            f.setReadable(false, false);
            f.setReadable(true, true);
            f.setWritable(false, false);
            f.setWritable(true, true);
            f.setExecutable(false, false);
            log.debug(">>> [PEM] File Windows permissions set");
        }
    }

    /**
     * Object Storage 전체 URL 생성 (URL 인코딩 포함)
     */
    public String fullObjectUrl(String objectName) {
        if (objectName == null || objectName.isBlank()) {
            throw new IllegalArgumentException("Object name cannot be null or empty");
        }

        String encodedObjectName = URLEncoder.encode(objectName, StandardCharsets.UTF_8);

        return String.format(
                OBJECT_STORAGE_URL_TEMPLATE,
                region,
                namespace,
                bucketName,
                encodedObjectName
        );
    }

    /**
     * 애플리케이션 종료 시 키 파일 정리
     */
    @PreDestroy
    public void cleanup() {
        log.info(">>> [OCI] Cleaning up resources...");
        cleanupKeyFile();
    }

    /**
     * 키 파일 삭제
     */
    private void cleanupKeyFile() {
        if (keyFilePath != null) {
            try {
                boolean deleted = Files.deleteIfExists(keyFilePath);
                if (deleted) {
                    log.info(">>> [PEM] Key file deleted: {}", keyFilePath);

                    // 빈 디렉토리도 삭제 시도
                    Path keyDir = keyFilePath.getParent();
                    if (keyDir != null && Files.isDirectory(keyDir)) {
                        try {
                            Files.delete(keyDir);
                            log.info(">>> [PEM] Key directory deleted: {}", keyDir);
                        } catch (IOException e) {
                            // 디렉토리가 비어있지 않거나 삭제 실패 시 무시
                            log.debug(">>> [PEM] Key directory not deleted (may contain other files)");
                        }
                    }
                } else {
                    log.warn(">>> [PEM] Key file not found: {}", keyFilePath);
                }
            } catch (IOException e) {
                log.error(">>> [PEM ERROR] Failed to delete key file: {}", keyFilePath, e);
            }
        }
    }

}