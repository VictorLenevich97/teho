package va.rit.teho.controller.helper;

import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class ReportResponseEntity {

    private ReportResponseEntity() {
    }

    public static ResponseEntity<byte[]> ok(String name, byte[] data) throws UnsupportedEncodingException {
        String encode = URLEncoder.encode(name + ".xls", "UTF-8");
        return ResponseEntity.ok()
                             .contentLength(data.length)
                             .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                             .cacheControl(CacheControl.noCache())
                             .header("Content-Disposition", "attachment; filename=" + encode)
                             .body(data);
    }

}
