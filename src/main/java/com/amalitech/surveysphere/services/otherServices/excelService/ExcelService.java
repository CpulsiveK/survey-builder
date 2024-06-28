package com.amalitech.surveysphere.services.otherServices.excelService;

import java.io.IOException;

public interface ExcelService {

    byte[] generateExcelFiles(String surveyId) throws IOException;
}
