import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Date;

public class ChatClient extends Application {
    private String username;
    private TextArea clientLog = new TextArea();
    boolean programOpen = true;

    private Socket socket;
    private ObjectOutputStream toServer = null;
    private ObjectInputStream fromServer = null;

    @Override
    public void start( Stage primaryStage ) throws Exception {
        //Connect client to server
        doConnect();

        //Create window
        Stage window = primaryStage;
        window.setTitle( "Client" );

        //Create layout
        BorderPane clientLayout = new BorderPane();
        HBox bottomInputArea = new HBox();

        //Create components
        TextField clientInput = new TextField();
        Button submitButton = new Button( "Submit" );
        submitButton.setDefaultButton( true );

        //Assemble layout
        bottomInputArea.getChildren().addAll( clientInput, submitButton );
        clientLayout.setBottom( bottomInputArea );
        clientLayout.setCenter( clientLog );

        //Create Scene
        Scene scene = new Scene( clientLayout, 500, 400 );

        //Set Scene and Show Stage
        window.setScene( scene );
        window.show();

        //Ask user for username and update window title
        createUser();
        window.setTitle( "Client: " + username );

        //Handle submit
        submitButton.setOnAction( e -> {
            try {
                //Check length of user message
                if ( clientInput.getText().trim().length() > 250 ) {
                    clientLog.appendText( "J_ERR : Message too long.\n" );
                } else {
                    Message message = new Message();

                    if ( clientInput.getText().trim().equals( "QUIT" ) ) {
                        message.setType( "QUIT" );
                        message.setMessage( username );
                        programOpen = false;
                    } else {
                        //Create message object
                        message.setType( "DATA" );
                        message.setMessage( " " + username + ": " + clientInput.getText().trim() );
                    }

                    //Send object to server
                    toServer.writeObject( message );
                    toServer.flush();

                    //Empty textfield
                    clientInput.clear();
                }

                if ( !programOpen ) {
                    e.consume();
                    window.close();
                }

            } catch ( IOException ex ) {
                ex.printStackTrace();
            }
        } );

        //Create a new thread that always check for new messages
        new Thread( () -> {
            //While program is running
            while ( programOpen ) {
                try {
                    //Sleep for 2 milliseconds
                    Thread.sleep( 200 );

                    //Get response from server
                    Object result = fromServer.readObject();

                    //Add to log
                    clientLog.appendText(result.toString() + "\n");
                } catch ( InterruptedException ex ) {
                    ex.printStackTrace();
                } catch ( ClassNotFoundException ex ) {
                    ex.printStackTrace();
                } catch ( IOException ex ) {
                    ex.printStackTrace();
                }
            }

            try {
                //Close connection when programOpen == false
                toServer.close();
                fromServer.close();
                socket.close();
            } catch ( IOException ex ) {
                ex.printStackTrace();
            }

        } ).start();
    }

    private void doConnect() {
        try {
            //Connect
            socket = new Socket( "localhost", 1337 );

            //Create output stream
            toServer = new ObjectOutputStream( socket.getOutputStream() );

            //Get response from server
            fromServer = new ObjectInputStream( socket.getInputStream() );

            //Print a nice message
            clientLog.appendText( new Date() + ": Chat client is open!\n" );
        } catch ( IOException ex ) {
            ex.printStackTrace();
        }
    }

    private void createUser() {
        try {
            //Get username
            username = ClientPopUp.getUsername();

            //Create message object
            Message msg = new Message( "JOIN", " " + username + ", " + socket.getInetAddress().getHostAddress() + ":1337" );

            //Send object to server
            toServer.writeObject( msg );
            toServer.flush();

            //Get response from server
            Message result = (Message) fromServer.readObject();

            //Add ok message to log
            clientLog.appendText( result.getType() + "\n");

            if ( result.getType().equals( "J_ERR" ) ) {
                createUser();
            }
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch ( IOException ex ) {
            ex.printStackTrace();
        }
    }

    public static void main( String[] args ) {
        launch( args );
    }
}
