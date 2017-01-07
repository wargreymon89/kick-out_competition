package spacerace.server;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import spacerace.domain.Acceleration;
import spacerace.domain.GameState;
import spacerace.domain.GameStatus;
import spacerace.server.response.GameStateConverter;
import spacerace.server.response.ServerResponse;

@RestController
public class SpaceRaceRestController {

    private final GameHandler gameHandler = new GameHandler();

    @RequestMapping("/registerPlayer")
    public ServerResponse registerPlayer(
            @RequestParam(value = "gameName") final String gameName,
            @RequestParam(value = "playerName") final String playerName,
            @RequestParam(value = "levelNumber") final Integer levelNumber) {
        try {
            return registerNewPlayer(gameName, playerName, levelNumber);
        }
        catch (final Exception e) {
            return createErrorResponse(e);
        }
    }

    private ServerResponse createErrorResponse(final Exception e) {
        final StringWriter stringWriter = new StringWriter();
        final PrintWriter  printWriter  = new PrintWriter(stringWriter);
        e.printStackTrace(printWriter);
        final String errorMessage = e.getMessage() + "\n" + stringWriter.toString();

        final ServerResponse response = new ServerResponse();
        response.setMessage(errorMessage);
        return response;
    }

    ServerResponse registerNewPlayer(final String gameName, final String playerName, final int levelNumber) {
        final SpaceRaceGame game = gameHandler.getOrCreateGame(gameName, playerName, levelNumber);

        final ServerResponse response = new ServerResponse();
        if (game.getGameStatus() != GameStatus.JOINABLE) {
            response.setMessage("Game not joinable state");
            return response;
        }
        else {
            game.addShip(playerName);
        }
        response.setLevel(game.getLevel());
        return response;
    }

    @RequestMapping("/getGameState")
    public ServerResponse getGameState(@RequestParam(value = "gameName") final String gameName) {
        final SpaceRaceGame  game      = gameHandler.getGame(gameName);
        final GameState      gameState = GameStateConverter.convertGameToGameState(game);
        final ServerResponse response  = new ServerResponse();
        response.setGameState(gameState);
        return response;
    }

    @RequestMapping("/action")
    public ServerResponse action(
            @RequestParam(value = "gameName") final String gameName,
            @RequestParam(value = "playerName") final String playerName,
            @RequestParam(value = "accelerationX") final String accelerationX,
            @RequestParam(value = "accelerationY") final String accelerationY,
            @RequestParam(value = "stabilize") final boolean stabilize) {
        final SpaceRaceGame game = gameHandler.getGame(gameName);
        game.updateShipParameters(playerName, Acceleration.valueOf(accelerationX), Acceleration.valueOf(accelerationY), stabilize);
        return new ServerResponse();
    }

    @RequestMapping("/test")
    public String testing() {
        return "It still works!";
    }
    //    @RequestMapping("/test")
    //    public ServerResponse testing() {
    //
    //        final ServerResponse serverResponse = new ServerResponse();
    //        final GameState      gameState      = new GameState();
    //        gameState.setGameStatus(GameStatus.JOINABLE.toString());
    //
    //        final ShipState shipState1 = new ShipState();
    //        shipState1.setName("Max");
    //        shipState1.setStabilize(false);
    //        shipState1.setColor(Color.BLUE);
    //        shipState1.setSpeed(new Vector2D(0.01, 0.2));
    //        shipState1.setAccelerationDirection(new Vector2D(1, 1));
    //        shipState1.setPosition(new Vector2D(100, 200));
    //
    //        final ShipState shipState2 = new ShipState();
    //        shipState2.setName("Sandy");
    //        shipState2.setStabilize(false);
    //        shipState2.setColor(Color.RED);
    //        shipState2.setSpeed(new Vector2D(0.05, 0.9));
    //        shipState2.setAccelerationDirection(new Vector2D(0, 0));
    //        shipState2.setPosition(new Vector2D(450, 300));
    //
    //        gameState.setShipStates(Arrays.asList(shipState1, shipState2));
    //
    //        serverResponse.setGameState(gameState);
    //
    //        return serverResponse;
    //    }
}
