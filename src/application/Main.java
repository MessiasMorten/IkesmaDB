package application;
	
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {
	
	
	//Main method in class. Launches JavaFX into the IkesmaGUI.fxml, a startup stage
	@Override
	public void start(Stage primaryStage) {
		try {
			
			Parent root = FXMLLoader.load(getClass().getResource("IkesmaGUI.fxml"));
			Scene scene = new Scene(root,400,600);
			primaryStage.setScene(scene);
			primaryStage.setTitle("Ikesma - Ikea Visma mashup");
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
