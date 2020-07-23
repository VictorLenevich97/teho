package by.varb.teho.enums;

public enum ReportTemplatePathEnum {

    REPAIR_NORM_REPORT_TEMPLATE_PATH("reporttemplate/repair_norm_report_template.xls"),
    AVERAGE_DAILY_OUTPUT_TEMPLATE_PATH("reporttemplate/average_daily_output_report_template.xls");

    private final String path;

    ReportTemplatePathEnum(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
