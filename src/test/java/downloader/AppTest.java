package downloader;

import downloader.impl.HttpDownloadManager;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Simple download test
     */
    public void testDownload() throws Exception
    {
        try(HttpDownloadManager manager = new HttpDownloadManager()) {

            DownloadResponse response = manager.download().urls(new URL("http://ya.ru")).start().get();

            List<URLResponse> urlResponses = response.getURLResponses();
            assertEquals(1,urlResponses.size());
            URLResponse urlResponse = urlResponses.get(0);
            assertFalse(urlResponse.isFailed());
            int length = urlResponse.length();
            InputStream inputStream = urlResponse.getInputStream();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            String html = new String(bytes, "UTF-8");
            assertTrue(html.contains("Яндекс"));
        }
    }
}
