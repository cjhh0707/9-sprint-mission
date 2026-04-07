package com.sprint.mission.discodeit.storage.s3;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class S3BinaryContentStorageTest {

  private static S3BinaryContentStorage storage;
  private static final byte[] TEST_CONTENT = "S3BinaryContentStorage Test Content".getBytes();

  @BeforeAll
  static void setUp() throws Exception {
    Properties props = new Properties();
    props.load(new FileInputStream("../.env"));

    String accessKey = props.getProperty("AWS_S3_ACCESS_KEY");
    String secretKey = props.getProperty("AWS_S3_SECRET_KEY");
    String region = props.getProperty("AWS_S3_REGION");
    String bucket = props.getProperty("AWS_S3_BUCKET");
    long expiration = 600L;

    storage = new S3BinaryContentStorage(accessKey, secretKey, region, bucket, expiration);
  }

  @Test
  void put_업로드_성공() {
    UUID id = UUID.randomUUID();
    UUID result = storage.put(id, TEST_CONTENT);

    assertThat(result).isEqualTo(id);
    System.out.println("put 성공 - id: " + id);
  }

  @Test
  void get_다운로드_성공() throws Exception {
    UUID id = UUID.randomUUID();
    storage.put(id, TEST_CONTENT);

    try (InputStream inputStream = storage.get(id)) {
      byte[] downloaded = inputStream.readAllBytes();
      assertThat(downloaded).isEqualTo(TEST_CONTENT);
      System.out.println("get 성공 - content: " + new String(downloaded));
    }
  }

  @Test
  void download_presignedUrl_리다이렉트_반환() {
    UUID id = UUID.randomUUID();
    storage.put(id, TEST_CONTENT);

    BinaryContentDto dto = new BinaryContentDto(
        id,
        "test-file.txt",
        (long) TEST_CONTENT.length,
        "text/plain"
    );

    ResponseEntity<Void> response = storage.download(dto);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
    assertThat(response.getHeaders().getLocation()).isNotNull();
    System.out.println("download 성공 - Location: " + response.getHeaders().getLocation());
  }
}
