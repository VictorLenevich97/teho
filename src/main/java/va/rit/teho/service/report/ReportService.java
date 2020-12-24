package va.rit.teho.service.report;

public interface ReportService<T> {

    byte[] generateReport(T data);

}
