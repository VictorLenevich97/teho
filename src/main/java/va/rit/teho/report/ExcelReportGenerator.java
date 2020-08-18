package va.rit.teho.report;

import va.rit.teho.exception.ExcelReportGeneratorException;
import org.jxls.common.Context;
import org.jxls.util.JxlsHelper;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class ExcelReportGenerator<T> {

    /**
     * Переменная в шаблоне excel, куда подставляются данные
     */
    private static final String VAR_NAME = "data";

    public byte[] generateFileFromTemplate(String templateFilePath, List<T> data) throws ExcelReportGeneratorException {
        byte[] reportFile;

        try (InputStream inputStream = new ClassPathResource(templateFilePath).getInputStream(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Context context = new Context();
            context.putVar(VAR_NAME, data);
            JxlsHelper.getInstance().processTemplate(inputStream, outputStream, context);
            reportFile = outputStream.toByteArray();
        } catch (IOException e) {
            throw new ExcelReportGeneratorException(e);
        }

        return reportFile;
    }
}
