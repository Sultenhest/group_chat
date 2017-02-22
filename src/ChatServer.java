/*
 * Phuong Quan Thai & Simon Konstantyner
 * Dat16v2
 *
 * Mandatory Assignment 1 (SWC3 + TECH2)
 * - Chat system with multiple clients
 */

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class ChatServer extends Application {
    private TextArea serverLog = new TextArea();
    private static List<HandleChatClient> clientList = new ArrayList<>();

    @Override
    public void start( Stage primaryStage ) throws Exception {
        //Set window
        Stage window = primaryStage;
        window.setTitle( "Chat Server" );

        //Turn off server log
        serverLog.setEditable( false );

        //Create Scene
        Scene scene = new Scene( serverLog, 500, 700 );

        //Set Scene and Show Stage
        window.setScene( scene );
        window.show();

        new Thread( () -> {
            try {
                //Create server socket
                ServerSocket serverSocket = new ServerSocket( 1337  );
                serverLog.appendText( new Date() + ": Chat server is running!\n" );

                while (true) {
                    //Listen for a new connection request
                    Socket socket = serverSocket.accept();

                    //Create a new thread for the new client
                    HandleChatClient hcc = new HandleChatClient( socket );
                    clientList.add( hcc );
                    new Thread( hcc ).start();
                }
            }  catch (IOException ex) {
                ex.printStackTrace();
            }
        } ).start();
    }

    public boolean checkUsernameAvailability( Message message, ObjectOutputStream outputToClient ) {
        boolean answer = false;

        try {
            //Print JOIN message in server log
            serverLog.appendText(message.toString() + "\n");

            //Return J_OK to client
            outputToClient.writeObject( new Message( "J_OK", "" ) );
            answer = true;

            //Return J_ERR to client
            //outputToClient.writeObject(new Message("J_ERR", ""));
            //answer = false;
        } catch ( IOException ex ) {
            ex.printStackTrace();
        }

        return answer;
    }

    public void writeToAllClients( Message message ) {
        try {
            for ( HandleChatClient client : clientList ) {
                ObjectOutputStream oos = client.getOutputStream();

                oos.writeObject( message );
            }
        } catch ( IOException ex ) {
            ex.printStackTrace();
        }
    }

    public static void main( String[] args ) {
        launch( args );
    }

    class HandleChatClient implements Runnable {
        private Socket socket;
        private ObjectOutputStream outputToClient;

        public HandleChatClient( Socket socket ) {
            this.socket = socket;
        }

        public ObjectOutputStream getOutputStream() {
            return outputToClient;
        }

        public void run() {
            try {
                //Creating input and output streams
                ObjectInputStream inputFromClient = new ObjectInputStream( socket.getInputStream() );
                outputToClient = new ObjectOutputStream( socket.getOutputStream() );

                while ( true ) {
                    //Get object returned from client
                    Message messageFromClient = (Message) inputFromClient.readObject();
                    String msgType = messageFromClient.getType();

                    switch ( msgType ) {
                        case "JOIN":
                            checkUsernameAvailability( messageFromClient, outputToClient );
                            break;
                        case "DATA":
                            //Send message to all clients
                            writeToAllClients( messageFromClient );
                            break;
                        case "ALVE":
                            break;
                        case "QUIT":
                            break;
                        default:
                            System.out.println( "Something that shouldn't happen happened.. Sorry" );
                            break;
                    }
                }
            } catch ( ClassNotFoundException ex ) {
                ex.printStackTrace();
            } catch ( IOException ex ) {
                ex.printStackTrace();
            }
        }
    }
}
