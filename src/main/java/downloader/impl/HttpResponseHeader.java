package downloader.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author pjalybin
 * @since 23.08.14 11:51
 */
public class HttpResponseHeader {

    final private Map<String, List<String>> fields;

    HttpResponseHeader(Map<String, List<String>> headerFields) {
        this.fields = headerFields;
    }

    public Map<String, List<String>> getFields() {
        return Collections.unmodifiableMap(fields);
    }
}
