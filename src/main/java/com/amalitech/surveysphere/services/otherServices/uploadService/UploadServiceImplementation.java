package com.amalitech.surveysphere.services.otherServices.uploadService;

import com.amalitech.surveysphere.dto.requestDto.FileUploadDto;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Implementation of the UploadService interface for uploading survey logos to AWS S3. */
@Service
@RequiredArgsConstructor
public class UploadServiceImplementation implements UploadService {
  private final S3Client s3Client;
  private final Environment env;
  private final PasswordEncoder passwordEncoder;
  private final Random random = new Random();

  /**
   * Uploads a file to AWS S3.
   *
   * @param file The FileUploadDto containing the file to upload.
   * @return A String representing the URL of the uploaded file.
   */
  @Override
  public String uploadFile(FileUploadDto file) {
    String bucketName = env.getProperty("aws_bucket_name");
    String url = file.getUrl();

    String regex = "https://survey-sphere-bucket.s3.amazonaws.com";
    if (url.contains(regex)) return url;

    String[] urlInfo = file.getUrl().split(",");
    Pattern pattern = Pattern.compile("data:image/([a-zA-Z0-9]+);");
    Matcher matcher = pattern.matcher(urlInfo[0]);
    String key = null;
    String contentType = null;

    if (matcher.find()) {
      key = generateKey(matcher.group(1));
      contentType = "image/" + matcher.group(1);
    }

    byte[] fileBytes = Base64.getDecoder().decode(urlInfo[1]);
    InputStream stream = new ByteArrayInputStream(fileBytes);
    s3Client.putObject(
        PutObjectRequest.builder().bucket(bucketName).key(key).contentType(contentType).build(),
        RequestBody.fromInputStream(stream, fileBytes.length));

    return "https://" + bucketName + ".s3.amazonaws.com/" + key;
  }

  @Override
  public String uploadExcel(byte[] bytes, String filename) {
    String bucketName = env.getProperty("aws_bucket_name");
    String key = filename.replaceAll("\\s", "");

    s3Client.putObject(
        PutObjectRequest.builder()
            .bucket(env.getProperty("aws_bucket_name"))
            .key(key)
            .contentType("application/vnd.ms-excel")
            .build(),
        RequestBody.fromBytes(bytes));

    return "https://" + bucketName + ".s3.amazonaws.com/" + key;
  }

  private String generateKey(String extension) {
    int randomNumber = random.nextInt(10000);

    return passwordEncoder.encode(extension + randomNumber);
  }
}
