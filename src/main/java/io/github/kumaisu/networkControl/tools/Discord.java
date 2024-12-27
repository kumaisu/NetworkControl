package io.github.kumaisu.networkControl.tools;

import io.github.kumaisu.networkControl.config.Config;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

/**
 *
 * @author NineTailedFox
 */
public class Discord {

    public static void sendMessage( String name, String message, String world ) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(Config.webhook );
            post.setHeader("Content-Type", "application/json; charset=UTF-8");

//            String json = String.format("{\"username\": \"%s\"}", name);
//            post.setEntity(new StringEntity(json));

            String msg = "[" + world + "] " + message;
            String json = String.format( "{\"username\": \"%s\",\"content\": \"%s\"}", name, msg );
            StringEntity entity = new StringEntity( json, "UTF-8" );
            post.setEntity( entity );

            try (CloseableHttpResponse response = client.execute(post)) {
                System.out.println( "メッセージが送信されました。ステータスコード: " + response.getStatusLine().getStatusCode() );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
