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
import java.util.*;

public class ChatServer extends Application {
    private TextArea serverLog = new TextArea();
    private static Map<String, HandleChatClient> clientMap = new HashMap<>();

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

                while ( true ) {
                    //Listen for a new connection request
                    Socket socket = serverSocket.accept();

                    //Create a new thread for the new client
                    HandleChatClient hcc = new HandleChatClient( socket );
                    clientMap.put( "tempname", hcc );
                    new Thread( hcc ).start();
                }
            }  catch (IOException ex) {
                ex.printStackTrace();
            }
        } ).start();
    }

    private synchronized boolean checkUsernameAvailability( Message message, ObjectOutputStream outputToClient ) {
        boolean answer = false;
        String username = message.getMessage().trim().substring(0, message.getMessage().indexOf( ',' ) - 1 );

        try {
            if ( checkUsernameChars( username ) && username.length() < 13 &&
                    !clientMap.containsKey( username ) ) {
                //Add to hashmap
                HandleChatClient tempHCC = clientMap.get( "tempname" );
                clientMap.remove( "tempname" );
                clientMap.put( username, tempHCC );

                //Inform about new list
                writeClientsListToAll( tempHCC );

                //Print JOIN message in server log
                serverLog.appendText( message.toString() + "\n" );

                //Return J_OK to client
                outputToClient.writeObject( new Message("J_OK", "") );
                answer = true;
            } else {
                //Return J_ERR to client
                outputToClient.writeObject( new Message("J_ERR", "") );
            }
        } catch ( IOException ex ) {
            ex.printStackTrace();
        }

        return answer;
    }

    private boolean checkUsernameChars( String str ) {
        for ( int i = 0; i < str.length(); i++ ) {
            char currentChar = str.charAt( i );

            if ( !Character.isLetterOrDigit( currentChar ) ) {
                if ( currentChar != '_' && currentChar != '-' ) {
                    return false;
                }
            }
        }

        return true;
    }

    private void writeClientsListToAll( HandleChatClient excludeThisHCC ) {
        Message msg = new Message( "LIST", "" );
        String userList = "";

        //Loop through keys and add to message string
        for ( String key : clientMap.keySet() ) {
            userList += key + ", ";
        }

        //Remove last comma
        userList = userList.substring( 0, userList.length() - 2 );

        //Add string to message object
        msg.setMessage( " " + userList );

        //Write to clients, excluding the new client
        try {
            for ( HandleChatClient clientConnection : clientMap.values() ) {
                if ( clientConnection != excludeThisHCC ) {
                    ObjectOutputStream oos = clientConnection.getOutputStream();
                    oos.writeObject( msg );
                }
            }
        } catch ( IOException ex ) {
            ex.printStackTrace();
        }
    }

    private synchronized void writeToAllClients( Message message ) {
        try {
            for ( HandleChatClient clientConnection : clientMap.values() ) {
                ObjectOutputStream oos = clientConnection.getOutputStream();
                oos.writeObject( message );
            }
        } catch ( IOException ex ) {
            ex.printStackTrace();
        }
    }

    private synchronized void disconnectUser( String username ) {
        HandleChatClient temp;

        //Remove the disconnecting client
        for ( String key : clientMap.keySet() ) {
            if ( key.equals( username.trim() ) ) {
                temp = clientMap.get( key );
                clientMap.remove( key );

                if( clientMap.size() > 0 ) {
                    writeClientsListToAll(temp);
                }
            }
        }
    }

    public static void main( String[] args ) {
        launch( args );
    }

    class HandleChatClient implements Runnable {
        private boolean running = true;
        private Socket socket;
        private ObjectInputStream inputFromClient;
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
                inputFromClient = new ObjectInputStream( socket.getInputStream() );
                outputToClient = new ObjectOutputStream( socket.getOutputStream() );

                while ( running ) {
                    //Get object returned from client
                    Message messageFromClient = (Message) inputFromClient.readObject();
                    String msgType = messageFromClient.getType();

                    switch (msgType) {
                        case "JOIN":
                            checkUsernameAvailability( messageFromClient, outputToClient );
                            break;
                        case "DATA":
                            writeToAllClients( messageFromClient );
                            break;
                        case "ALVE":
                            System.out.println( messageFromClient.toString() );
                            break;
                        case "QUIT":
                            serverLog.appendText( messageFromClient.toString() );
                            disconnectUser( messageFromClient.getMessage() );
                            stopRunning();
                            break;
                        default:
                            System.out.println("Something that shouldn't happen happened.. Sorry");
                            break;
                    }
                }
            } catch ( ClassNotFoundException ex ) {
                ex.printStackTrace();
            } catch ( IOException ex ) {
                ex.printStackTrace();
            }
        }

        public void stopRunning() {
            try {
                running = false;
                inputFromClient.close();
                outputToClient.close();
                socket.close();
            } catch ( IOException ex ) {
                ex.printStackTrace();
            }
        }
    }
}
