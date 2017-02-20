/*
 * Phuong Quan Thai & Simon Konstantyner
 * Dat16v2
 *
 * Mandatory Assignment 1(SWC3 + TECH2)
 * - Chat system with multiple clients
 */

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class ChatServer extends Application {
    @Override
    public void start( Stage primaryStage ) throws Exception {
        //Set window
        Stage window = primaryStage;
        window.setTitle( "Chat Server" );

        //Create Server Log area
        TextArea serverLog = new TextArea();
        serverLog.setEditable( false );

        //Create Scene
        Scene scene = new Scene( serverLog, 500, 700 );

        //Set Scene and Show Stage
        window.setScene( scene );
        window.show();
    }

    public static void main( String[] args ) {
        launch( args );
    }
}
