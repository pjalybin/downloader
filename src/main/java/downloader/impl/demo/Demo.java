package downloader.impl.demo;

import downloader.DownloadController;
import downloader.DownloadResponse;
import downloader.URLRequest;
import downloader.URLResponse;
import downloader.impl.HttpDownloadHandler;
import downloader.impl.HttpDownloadManager;
import downloader.impl.HttpResponseHeader;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

/**
 * @author pjalybin
 * @since 23.08.14 16:22
 */
public class Demo {
    public static void main(String[] args) {

        try(HttpDownloadManager manager = new HttpDownloadManager(10)) {

            List<DownloadController> controllers = new ArrayList<>();
            for (int i = 0; i < 100; i++) {
                DownloadController downloadController = manager.download()
                        .urls(
                                new URL("http://yandex.ru/images/today?size=1920x1080"),
                                new URL("http://yandex.ru/images/today?size=1920x1080"),
                                new URL("http://yandex.ru/images/today?size=1920x1080"),
                                new URL("http://yandex.ru/images/today?size=1920x1080"),
                                new URL("http://yandex.ru/images/today?size=1920x1080")
                        )
                        .handler(new HttpDownloadHandler() {
                                     @Override
                                     public boolean onHttpStatus(DownloadController controller, URLRequest request, int httpStatusCode) {
                                         System.out.println(request.hashCode() + " statuscode " + httpStatusCode);
                                         return false;
                                     }

                                     @Override
                                     public boolean onHeader(DownloadController controller, URLRequest request, HttpResponseHeader header) {
                                         StringBuilder sb = new StringBuilder();
                                         sb.append(request.hashCode()).append(" header ");
                                         for (Map.Entry<String, List<String>> e : header.getFields().entrySet()) {
                                             sb.append(e.getKey()).append("=").append(e.getValue()).append(";");
                                         }
                                         System.out.println(sb.toString());
                                         return false;
                                     }

                                     @Override
                                     public boolean onContent(DownloadController controller, URLRequest request, InputStream stream) {
                                         System.out.println(controller.hashCode() + " " +
                                                 request.hashCode() + " starting content download");
                                         return false;
                                     }

                                     @Override
                                     public void onDownloadStarted(DownloadController controller) {
                                         System.out.println(controller.hashCode() + " " +
                                                 "download started");
                                     }

                                     @Override
                                     public boolean onDownloadStartedURL(DownloadController controller, URLRequest urlRequest) {
                                         System.out.println(controller.hashCode() + " " +
                                                 urlRequest.hashCode() + " download url started");
                                         return false;
                                     }

                                     @Override
                                     public void onDownloadFinishedURL(DownloadController controller, URLResponse urlResponse) {
                                         System.out.println(controller.hashCode() + " " +
                                                 urlResponse.getURLRequest().hashCode() + " download url finished " + urlResponse.length());
                                     }

                                     @Override
                                     public boolean onDownloadFailedURL(DownloadController controller, URLRequest urlRequest, Throwable cause) {
                                         System.out.println(
                                                 controller.hashCode() + " " + urlRequest.hashCode() +
                                                         " download url failed " + cause.getMessage() + cause.getClass());
                                         return false;
                                     }

                                     @Override
                                     public void onDownloadFailed(DownloadController controller, Throwable cause) {
                                         System.out.println(controller.hashCode() + " " +
                                                 "download failed " + cause.getMessage() + " " + cause.getClass());
                                     }

                                     @Override
                                     public void onDownloadFinished(DownloadController controller, DownloadResponse response) {
                                         System.out.println(controller.hashCode() + " download finished ");
                                     }
                                 }
                        ).start();
                controllers.add(downloadController);
                System.out.println("Created controller " + controllers.size());
            }

            Thread.sleep(5000);

            for (DownloadController controller : controllers) {
                controller.pause();
            }
            Thread.sleep(5000);

            for (DownloadController controller : controllers) {
                controller.resume();
            }
            Thread.sleep(5000);
            for (DownloadController controller : controllers) {
                controller.restart();
            }
            Thread.sleep(5000);

            for (DownloadController controller : controllers) {
                if (!controller.isDone()) {
                    controller.stop();
                }
            }


            for (DownloadController downloadController : controllers) {
                try {
                    System.out.print(downloadController.hashCode() + " ");
                    System.out.print(downloadController.getCurrentDownloadInfo().getState());
                    DownloadResponse response = downloadController.get();
                    System.out.println(" controller future get " + response.getURLResponses().size());
                } catch (InterruptedException e) {
                    System.out.println(" InterruptedException");
                } catch (CancellationException e) {
                    System.out.println(" CancellationException");
                } catch (ExecutionException e) {
                    System.out.println(" ExecutionException");
                } catch (Throwable e) {
                    System.out.println(" unknown Exception " + e.getClass());

                }

            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        System.out.println("Done.");
    }
}
