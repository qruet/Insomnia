package dev.qruet.insomnia.http;

import dev.qruet.insomnia.Insomnia;
import dev.qruet.insomnia.io.config.ConfigIO;
import dev.qruet.insomnia.io.texture.ResourcePackIO;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

/**
 * This will handle http requests by the client for a resource pack
 */
public class WebServerHandler {

    private String ip;
    private int port;
    private final JavaPlugin plugin;

    private HttpServer httpServer;

    public WebServerHandler(JavaPlugin plugin) {
        this.plugin = plugin;
        ip = Bukkit.getServer().getIp();
        if (ip.equals("")) {
            ip = "localhost";
        }

        ConfigIO config = Insomnia.getInsomniaConfig();
        if(config.contains("Resource Pack Server.Port")) {
            port = config.get("Resource Pack Server.Port", Integer.class, 301);
        } else {
            Insomnia.logger().severe("Failed to retrieve port value from config!");
            port = 301;
        }
    }

    /**
     * Start the web server
     * @return
     */
    public boolean start() {
        try {
            httpServer = Vertx.vertx().createHttpServer();
            httpServer.requestHandler(httpServerRequest -> {
                httpServerRequest.response().sendFile(getFileLocation());
            });
            httpServer.listen(port);
        } catch (Exception ex) {
            Insomnia.logger().severe("Unable to bind to port, perhaps it's already in use? Please assign a different port.");
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Stop the webserver
     */
    public void stop() {
        httpServer.close();
    }

    private String getFileLocation() {
        return new File(ResourcePackIO.getResourcePath()).getAbsolutePath();
    }

    public int getPort() {
        return port;
    }

    public String getAddress() {
        return ip;
    }
}