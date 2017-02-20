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
        Stage window = new Stage();
        VBox layout = new VBox( 10 );

        Label message = new Label( "Please enter a username" );
        TextField username = new TextField();
        Button submitButton = new Button( "Submit" );

        window.initModality( Modality.APPLICATION_MODAL );
        window.setTitle( "Client Username" );
        window.setMinWidth( 250 );

        layout.getChildren().addAll( message, username, submitButton );
        layout.setAlignment( Pos.CENTER );
        layout.setPadding( new Insets( 10, 10, 10, 10 ) );

        submitButton.setOnAction( e -> window.close() );

        window.setScene( new Scene( layout ) );
        window.showAndWait();

        return username.getText().trim();
    }
}
