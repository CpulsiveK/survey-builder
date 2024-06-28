package com.amalitech.surveysphere.services.otherServices.uploadService;

import com.amalitech.surveysphere.dto.requestDto.FileUploadDto;

/** Interface for uploading survey logos. */
public interface UploadService {
  /**
   * Uploads a file to AWS S3.
   *
   * @param file The FileUploadDto containing the file to upload.
   * @return A String representing the URL of the uploaded file.
   */
  String uploadFile(FileUploadDto file);

  /**
   * Uploads an excel file to AWS S3.
   *
   * @param file The FileUploadDto containing the file to upload.
   * @return A String representing the URL of the uploaded file.
   */
  String uploadExcel(byte[] bytes, String filename);
}
