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

public class ChatClient extends Application {
    private String username;
    private TextArea clientLog = new TextArea();
    private ObjectOutputStream toServer = null;
    private ObjectInputStream fromServer = null;

    @Override
    public void start( Stage primaryStage ) throws Exception {
        //Connect client to server
        doConnect();

        //Ask user for username
        createUser();

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

        //Handle submit
        submitButton.setOnAction( e -> {
            try {
                //Send object to server
                toServer.writeObject( "DATA " + username + ":" + clientInput.getText().trim() + "\n" );
                toServer.flush();

                clientLog.appendText( fromServer.readObject().toString() );
            } catch ( ClassNotFoundException ex ) {
                ex.printStackTrace();
            } catch ( IOException ex ) {
                ex.printStackTrace();
            }
        } );
    }

    private void doConnect() {
        try {
            //Connect
            Socket socket = new Socket( "localhost", 7331 );

            //Create output stream
            toServer = new ObjectOutputStream( socket.getOutputStream() );

            //Get response from server
            fromServer = new ObjectInputStream( socket.getInputStream() );
        } catch ( IOException ex ) {
            ex.printStackTrace();
        }
    }

    private void createUser() {
        try {
            username = ClientPopUp.getUsername();

            //Send object to server
            toServer.writeObject( "JOIN " + username + ",");
            toServer.flush();
        } catch ( IOException ex ) {
            ex.printStackTrace();
        }
    }

    public static void main( String[] args ) {
        launch( args );
    }
}
