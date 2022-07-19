package za.co.wethinkcode.weshare;

import io.javalin.Javalin;
import za.co.wethinkcode.weshare.rating.ratingController;

import static za.co.wethinkcode.weshare.rating.ratingController.PATH;

public class ratingServerWeShare {
    private final Javalin server;
    static Integer RATINGS_PORT; // Port value the server is running on locally

    /**
     * The server constructor creates a Javalin instance of the rating server
     * GET we use to retrieve the data from the database
     * POST we use to send data from the database
     */
    public ratingServerWeShare(){
        server = Javalin.create(config -> config.defaultContentType = "application/json");
        server.get(PATH, ratingController::renderRatings);
        server.post(PATH, ratingController::updatePerson);
    }

    /**
     * main runs the server
     */
    public static void main(String[] args) {
        ratingServerWeShare server = new ratingServerWeShare();
        RATINGS_PORT = Integer.parseInt(System.getenv("RATINGS_PORT"));
        server.start(RATINGS_PORT);
    }

    /**
     *
     * @param port the server is running on
     */
    public void start(int port) {
        this.server.start(port);
    }

    /**
     * Stops the server
     */
     public void stop() {
        this.server.stop();
    }
}
