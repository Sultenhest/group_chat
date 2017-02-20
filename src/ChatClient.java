import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class ChatClient extends Application {
    @Override
    public void start( Stage primaryStage ) throws Exception {
        Stage window = primaryStage;
        window.setTitle( "Client" );

        BorderPane clientLayout = new BorderPane();
        HBox bottomInputArea = new HBox();

        TextArea chatLog = new TextArea();
        TextField clientInput = new TextField();
        Button submitButton = new Button( "Submit" );

        bottomInputArea.getChildren().addAll( clientInput, submitButton );
        clientLayout.setBottom( bottomInputArea );
        clientLayout.setCenter( chatLog );

        //Create Scene
        Scene scene = new Scene( clientLayout, 500, 400 );

        //Set Scene and Show Stage
        window.setScene( scene );
        window.show();

        System.out.println( ClientPopUp.getUsername() );
    }

    public static void main( String[] args ) {
        launch( args );
    }
}
