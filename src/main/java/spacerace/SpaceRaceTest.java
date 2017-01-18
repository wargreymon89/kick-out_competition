package spacerace;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.IntSummaryStatistics;

import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import spacerace.client.RemoteGame;
import spacerace.client.communication.SocketServerAdapter;
import spacerace.domain.GameStatistics;
import spacerace.gameengine.ManualGameEngine;
import spacerace.server.response.ServerResponse;
import spacerace.server.socket.SocketRequest;
import spacerace.server.socket.SocketRequestType;

import static spacerace.server.socket.SpaceRaceSocketServer.PORT;

public class SpaceRaceTest {

    private static final String SERVER_IP = "127.0.0.1"; // If you run locally
    //    private static final String SERVER_IP = "10.46.1.42"; // Game server WIFI
    //    private static final String SERVER_IP = "10.46.1.69"; // Game server ETHERNET
    //    private static final String SERVER_IP = "10.46.1.19"; // Max J
    //    private static final String SERVER_IP = "192.168.1.174"; // Other

    public static void main(final String[] args) throws InterruptedException, IOException {
        //        startGameWithMultipleShips();
        testRestServerResponseTime();
        //        testSocketServerResponseTime();
        //        testSocketServer();
    }

    private static void startGameWithMultipleShips() throws IOException, InterruptedException {
        final String gameName = "Battle of Trustly7";
        new Thread(() -> {
            try {
                startGame("Robocop1", gameName);
            }
            catch (final Exception e) {
                e.printStackTrace();
            }
        }).start();
        //        new Thread(() -> {
        //            try {
        //                startGame("Robocop2", gameName);
        //            }
        //            catch (final Exception e) {
        //                e.printStackTrace();
        //            }
        //        }).start();
        //        new Thread(() -> {
        //            try {
        //                startGame("Robocop3", gameName);
        //            }
        //            catch (final Exception e) {
        //                e.printStackTrace();
        //            }
        //        }).start();
        //        new Thread(() -> {
        //            try {
        //                startGame("Robocop4", gameName);
        //            }
        //            catch (final Exception e) {
        //                e.printStackTrace();
        //            }
        //        }).start();
        //        new Thread(() -> {
        //            try {
        //                startGame("Robocop5", gameName);
        //            }
        //            catch (final Exception e) {
        //                e.printStackTrace();
        //            }
        //        }).start();
        //        new Thread(() -> {
        //            try {
        //                startGame("Robocop6", gameName);
        //            }
        //            catch (final Exception e) {
        //                e.printStackTrace();
        //            }
        //        }).start();
    }

    private static void startGame(final String playerName, final String gameName) throws IOException, InterruptedException {
        //        final RemoteServerAdapter server = new RemoteServerAdapter(SERVER_IP, playerName, gameName, 1);
        final SocketServerAdapter server           = new SocketServerAdapter(SERVER_IP, playerName, gameName, 1);
        final RemoteGame          remoteGame       = new RemoteGame(server, playerName, gameName);
        final ManualGameEngine    manualGameEngine = new ManualGameEngine();
        remoteGame.runGame(manualGameEngine, manualGameEngine);
    }

    private static void testRestServerResponseTime() throws InterruptedException {
        final RestTemplate restTemplate = new RestTemplate();

        final String url = "http://" + SERVER_IP + ":8080/test";

        final GameStatistics gameStatistics = new GameStatistics();
        String               statistics     = "";
        for (int i = 1; i <= 1000; i++) {
            final long   before    = System.currentTimeMillis();
            final String response  = restTemplate.getForObject(url, String.class);
            final Long   totalTime = System.currentTimeMillis() - before;
            //            System.out.println(response + "    " + totalTime);
            gameStatistics.addCycleTime(totalTime.intValue());

            Thread.sleep(17);
            if (i % 100 == 0) {
                statistics += getStatisticsString(gameStatistics);
            }
        }
        System.out.println(statistics);
        //        Min: 1
        //        Max: 41
        //        Average: 3.43
        //        Min: 1
        //        Max: 48
        //        Average: 2.98
    }

    private static void testSocketServerResponseTime() throws InterruptedException, IOException {
        try (final Socket socket = new Socket(SERVER_IP, PORT);
             //             final ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
             //             final ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream())
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(
             ), true)
        ) {
            final GameStatistics gameStatistics = new GameStatistics();
            final ObjectMapper   objectMapper   = new ObjectMapper();
            for (int i = 1; i <= 1000; i++) {
                final long before = System.currentTimeMillis();

                final SocketRequest request = new SocketRequest();
                request.setType(SocketRequestType.TEST);

                final String requestString;
                try {
                    requestString = objectMapper.writeValueAsString(request);
                }
                catch (final JsonProcessingException e) {
                    throw new IllegalArgumentException("Unable to map object to JSON", e);
                }
                out.println(requestString);
                //                outputStream.writeObject("AAAAAAAAAAAAAAAAAAAAABBBBBBBBBBBBBBBBBAAAAAAAAAAAAAAAAAAAAABBBBBBBBBBBBBBBBB" +
                //                                         "AAAAAAAAAAAAAAAAAAAAABBBBBBBBBBBBBBBBBAAAAAAAAAAAAAAAAAAAAABBBBBBBBBBBBBBBBB" +
                //                                         "AAAAAAAAAAAAAAAAAAAAABBBBBBBBBBBBBBBBBAAAAAAAAAAAAAAAAAAAAABBBBBBBBBBBBBBBBB" +
                //                                         "AAAAAAAAAAAAAAAAAAAAABBBBBBBBBBBBBBBBBAAAAAAAAAAAAAAAAAAAAABBBBBBBBBBBBBBBBB" +
                //                                         "AAAAAAAAAAAAAAAAAAAAABBBBBBBBBBBBBBBBBAAAAAAAAAAAAAAAAAAAAABBBBBBBBBBBBBBBBB" +
                //                                         "AAAAAAAAAAAAAAAAAAAAABBBBBBBBBBBBBBBBBAAAAAAAAAAAAAAAAAAAAABBBBBBBBBBBBBBBBB" +
                //                                         "AAAAAAAAAAAAAAAAAAAAABBBBBBBBBBBBBBBBBAAAAAAAAAAAAAAAAAAAAABBBBBBBBBBBBBBBBB" +
                //                                         "AAAAAAAAAAAAAAAAAAAAABBBBBBBBBBBBBBBBBAAAAAAAAAAAAAAAAAAAAABBBBBBBBBBBBBBBBB" +
                //                                         "AAAAAAAAAAAAAAAAAAAAABBBBBBBBBBBBBBBBBAAAAAAAAAAAAAAAAAAAAABBBBBBBBBBBBBBBBB" +
                //                                         "AAAAAAAAAAAAAAAAAAAAABBBBBBBBBBBBBBBBBAAAAAAAAAAAAAAAAAAAAABBBBBBBBBBBBBBBBB" +
                //                                         "AAAAAAAAAAAAAAAAAAAAABBBBBBBBBBBBBBBBBAAAAAAAAAAAAAAAAAAAAABBBBBBBBBBBBBBBBB" +
                //                                         "AAAAAAAAAAAAAAAAAAAAABBBBBBBBBBBBBBBBBAAAAAAAAAAAAAAAAAAAAABBBBBBBBBBBBBBBBB" +
                //                                         "AAAAAAAAAAAAAAAAAAAAABBBBBBBBBBBBBBBBBAAAAAAAAAAAAAAAAAAAAABBBBBBBBBBBBBBBBB" +
                //                                         "AAAAAAAAAAAAAAAAAAAAABBBBBBBBBBBBBBBBBAAAAAAAAAAAAAAAAAAAAABBBBBBBBBBBBBBBBB" +
                //                                         "AAAAAAAAAAAAAAAAAAAAABBBBBBBBBBBBBBBBBAAAAAAAAAAAAAAAAAAAAABBBBBBBBBBBBBBBBB" +
                //                                         "AAAAAAAAAAAAAAAAAAAAABBBBBBBBBBBBBBBBBAAAAAAAAAAAAAAAAAAAAABBBBBBBBBBBBBBBBB" +
                //                                         "AAAAAAAAAAAAAAAAAAAAABBBBBBBBBBBBBBBBBAAAAAAAAAAAAAAAAAAAAABBBBBBBBBBBBBBBBB");
                //                try {
                //                    //                    final ServerResponse response = (ServerResponse) inputStream.readObject();
                //                    final String response = (String) inputStream.readObject();
                //                    System.out.println(response);
                //                    //                    System.out.println(response);
                //                }
                //                catch (final IOException | ClassNotFoundException ex) {
                //                    ex.printStackTrace();
                //                }
                try {
                    final String response = in.readLine();
                    if (response == null) {
                        throw new IllegalArgumentException("Server response was null");
                    }
                    objectMapper.readValue(response, ServerResponse.class);
                }
                catch (final IOException e) {
                    throw new IllegalArgumentException(e);
                }

                final Long totalTime = System.currentTimeMillis() - before;
                //            System.out.println(response + "    " + totalTime);
                gameStatistics.addCycleTime(totalTime.intValue());

                Thread.sleep(17);

                if (i % 100 == 0) {
                    printStatistics(gameStatistics);
                }
            }
        }
    }

    private static void testSocketServer() throws InterruptedException, IOException {
        try (final Socket socket = new Socket(SERVER_IP, PORT);
             final ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
             final ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream())
        ) {
            final long before = System.currentTimeMillis();

            final SocketRequest request = new SocketRequest();
            request.setType(SocketRequestType.TEST);
            outputStream.writeObject(request);
            try {
                final ServerResponse response = (ServerResponse) inputStream.readObject();
            }
            catch (final IOException | ClassNotFoundException ex) {
                ex.printStackTrace();
            }
            System.out.println(System.currentTimeMillis() - before);
        }
    }


    private static void printStatistics(final GameStatistics gameStatistics) {
        final IntSummaryStatistics statistics = gameStatistics.getGameCycleStatistics();
        System.out.println("Min: " + statistics.getMin());
        System.out.println("Max: " + statistics.getMax());
        System.out.println("Average: " + statistics.getAverage());
        System.out.println("Median: " + gameStatistics.getGameCycleMedian());
        System.out.println("");
    }

    private static String getStatisticsString(final GameStatistics gameStatistics) {
        final IntSummaryStatistics statistics       = gameStatistics.getGameCycleStatistics();
        String                     statisticsString = "Min: " + statistics.getMin() + "\n";
        statisticsString += "Max: " + statistics.getMax() + "\n";
        statisticsString += "Average: " + statistics.getAverage() + "\n";
        statisticsString += "Median: " + gameStatistics.getGameCycleMedian() + "\n" + "\n";
        return statisticsString;
    }
}
