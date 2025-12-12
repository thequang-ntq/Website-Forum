package Support;

import okhttp3.OkHttpClient;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class OkHttpClientManager implements ServletContextListener {
    private static OkHttpClient client;

    public static synchronized OkHttpClient getClient() {
        if (client == null) {
            client = new OkHttpClient.Builder()
                .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .build();
        }
        return client;
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // Không cần làm gì, client sẽ được tạo lazy khi dùng lần đầu
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (client != null) {
            client.dispatcher().executorService().shutdown();
            client.connectionPool().evictAll();
            // Nếu có cache: client.cache().close();
            client = null;
        }
    }
}