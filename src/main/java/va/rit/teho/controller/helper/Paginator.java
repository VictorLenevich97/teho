package va.rit.teho.controller.helper;

public class Paginator {

    private Paginator() {
    }

    public static Long getPageNum(int pageSize, long rowCount) {
        int additionalPageNum = rowCount % pageSize == 0 ? 0 : 1;
        return (pageSize == 0 ? 1 : rowCount / pageSize + additionalPageNum);
    }

}
