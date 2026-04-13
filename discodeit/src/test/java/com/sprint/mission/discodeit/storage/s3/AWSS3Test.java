package com.sprint.mission.discodeit.storage.s3;

import java.io.FileInputStream;
import java.io.InputStream;
import java.time.Duration;
import java.util.Properties;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

@Disabled("로컬 .env 파일 필요 - CI 환경에서 실행 불가")
public class AWSS3Test {

  private static String accessKey;
  private static String secretKey;
  private static String region;
  private static String bucket;

  private static final String TEST_KEY = "test-object.txt";
  private static final byte[] TEST_CONTENT = "Hello, S3!".getBytes();

  @BeforeAll
  static void loadEnv() throws Exception {
    Properties props = new Properties();
    props.load(new FileInputStream("../.env"));
    accessKey = props.getProperty("AWS_S3_ACCESS_KEY");
    secretKey = props.getProperty("AWS_S3_SECRET_KEY");
    region = props.getProperty("AWS_S3_REGION");
    bucket = props.getProperty("AWS_S3_BUCKET");
  }

  @Test
  void upload() {
    S3Client s3Client = getS3Client();
    PutObjectRequest request = PutObjectRequest.builder()
        .bucket(bucket)
        .key(TEST_KEY)
        .build();
    s3Client.putObject(request, RequestBody.fromBytes(TEST_CONTENT));
    System.out.println("업로드 성공: " + TEST_KEY);
  }

  @Test
  void download() throws Exception {
    S3Client s3Client = getS3Client();
    GetObjectRequest request = GetObjectRequest.builder()
        .bucket(bucket)
        .key(TEST_KEY)
        .build();
    try (InputStream inputStream = s3Client.getObject(request)) {
      byte[] bytes = inputStream.readAllBytes();
      System.out.println("다운로드 성공: " + new String(bytes));
    }
  }

  @Test
  void generatePresignedUrl() {
    S3Presigner presigner = S3Presigner.builder()
        .region(Region.of(region))
        .credentialsProvider(StaticCredentialsProvider.create(
            AwsBasicCredentials.create(accessKey, secretKey)
        ))
        .build();

    GetObjectRequest getObjectRequest = GetObjectRequest.builder()
        .bucket(bucket)
        .key(TEST_KEY)
        .build();

    GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
        .signatureDuration(Duration.ofMinutes(10))
        .getObjectRequest(getObjectRequest)
        .build();

    PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(presignRequest);
    System.out.println("Presigned URL 생성 성공: " + presignedRequest.url());
  }

  private S3Client getS3Client() {
    return S3Client.builder()
        .region(Region.of(region))
        .credentialsProvider(StaticCredentialsProvider.create(
            AwsBasicCredentials.create(accessKey, secretKey)
        ))
        .build();
  }
}
