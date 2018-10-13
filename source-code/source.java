package application;
	
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.VBox;
import javafx.scene.web.HTMLEditor;


public class Main extends Application {
	
	HTMLEditor editor;			
	static String fileName = "htmlText.txt";
	
	@Override
	public void start(Stage primaryStage) {
		try {			
			VBox root = new VBox();
			
			editor = new HTMLEditor();
			editor.setHtmlText(load(fileName));
			
			ToolBar tools = new ToolBar();
			
			Button saveButton = new Button("Speichern");
			saveButton.setOnAction(new EventHandler<ActionEvent>(){
				@Override
				public void handle(ActionEvent event) {
					save(fileName);
				}
			});
			
			tools.getItems().add(saveButton);
			root.getChildren().add(editor);
			root.getChildren().add(tools);
			
			Scene scene = new Scene(root,600,500);
			primaryStage.setScene(scene);
			primaryStage.show();
			primaryStage.setTitle(fileName);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void save(String name) {
		File file = new File(name);
		
		PrintWriter pWriter = null; 
		try { 
		    pWriter = new PrintWriter(new BufferedWriter(new FileWriter(file))); 
		    pWriter.println(editor.getHtmlText());
		} catch (IOException ioe) { 
		    ioe.printStackTrace(); 
		} finally { 
		    if (pWriter != null){ 
		    	pWriter.flush(); 
		        pWriter.close(); 
		    } 
		} 
	}
	
	private static String load(String datName) { 

        File file = new File(datName); 

        if (!file.canRead() || !file.isFile()) 
            return ""; 

            BufferedReader in = null; 
        try { 
            in = new BufferedReader(new FileReader(datName)); 
            return(in.readLine());  
        } catch (IOException e) { 
            e.printStackTrace(); 
            return "";
        } finally { 
            if (in != null) 
                try { 
                    in.close(); 
                } catch (IOException e) {} 
        } 
    } 
	
	@Override
	public void stop() throws Exception {
		save(fileName);
	}
	
	public static void main(String[] args) {
		if(args.length == 1) {fileName = args[0];}
		launch(args);
	}
}
