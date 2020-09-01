package va.rit.teho.enums;

public enum ReportTemplatePathEnum {

    REPAIR_NORM_REPORT_TEMPLATE_PATH("reporttemplate/repair_norm_report_template.xls"),
    AVERAGE_DAILY_OUTPUT_TEMPLATE_PATH("reporttemplate/average_daily_output_report_template.xls"),
    REPAIR_FUND_DISTRIBUTION_TEMPLATE_PATH("reporttemplate/repair_fund_distribution_report_template.xls");

    private final String path;

    ReportTemplatePathEnum(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
