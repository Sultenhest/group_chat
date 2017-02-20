import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ClientPopUp {
    public static String getUsername() {
        //Create window
        Stage window = new Stage();
        window.initModality( Modality.APPLICATION_MODAL );
        window.setTitle( "Client Username" );
        window.setMinWidth( 250 );

        //VBox layout w 10 padding
        VBox layout = new VBox( 10 );

        //Create components
        Label message = new Label( "Please enter a username" );
        TextField username = new TextField();
        Button submitButton = new Button( "Submit" );

        //Assemble layout
        layout.getChildren().addAll( message, username, submitButton );
        layout.setAlignment( Pos.CENTER );
        layout.setPadding( new Insets( 10, 10, 10, 10 ) );

        //Handle submit
        submitButton.setOnAction( e -> window.close() );
        submitButton.setDefaultButton( true );

        //Set scene
        window.setScene( new Scene( layout ) );
        window.showAndWait();

        //Return user input
        return username.getText().trim();
    }
}
