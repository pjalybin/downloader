downloader
==========
Asynchronous downloader with HTTP implementation

## Sample usage:
```java
    try(HttpDownloadManager manager = new HttpDownloadManager()) {
        DownloadResponse response = manager.download().urls(new URL("http://ya.ru")).start().get();
        InputStream inputStream = response.getURLResponses().get(0).getInputStream();
    }
```
