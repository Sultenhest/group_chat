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

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;

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

        new Thread( () -> {
            try {
                //Create server socket
                ServerSocket serverSocket = new ServerSocket( 7331 );
                serverLog.appendText( new Date() + ": Chat server is running!\n" );

                //Listen for connection request
                Socket socket = serverSocket.accept();

                //Creating input and output streams
                ObjectInputStream inputFromClient = new ObjectInputStream( socket.getInputStream() );
                ObjectOutputStream outputToClient = new ObjectOutputStream( socket.getOutputStream() );

                while ( true ) {
                    String userInput = (String) inputFromClient.readObject();

                    serverLog.appendText( userInput );

                    if ( userInput.substring( 0, 4 ).equals( "DATA" ) ) {
                        outputToClient.writeObject(userInput + "\n");
                    }
                }
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
            }  catch (IOException ex) {
                ex.printStackTrace();
            }
        } ).start();
    }

    public static void main( String[] args ) {
        launch( args );
    }
}
