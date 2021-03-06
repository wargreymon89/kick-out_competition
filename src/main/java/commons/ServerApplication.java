package commons;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import commons.network.server.ServerRestController;
import spacerace.server.communication.SpaceRaceServerController;
import spacerace.server.communication.rest.SpaceRaceRestController;
import spacerace.server.communication.socket.SpaceRaceSocketServer;

@SpringBootApplication
@ComponentScan(basePackageClasses = ServerRestController.class)
@ComponentScan(basePackageClasses = SpaceRaceRestController.class)
@ComponentScan(basePackageClasses = SpaceRaceSocketServer.class)
@ComponentScan(basePackageClasses = SpaceRaceServerController.class)
public class ServerApplication {

    public static void main(final String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }
}
