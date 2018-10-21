package application;
	
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.VBox;
import javafx.scene.web.HTMLEditor;
import javafx.stage.Stage;
import javafx.util.Callback;


public class Main extends Application {
	
	HTMLEditor editor;
	int activeFile = 0;
	boolean isRunning = false;
	Label stateOut = new Label("bereit");
	
	@Override
	public void start(Stage primaryStage) {
		try {			
			VBox root = new VBox();
			
			editor = new HTMLEditor();
			editor.setHtmlText(load("data\\file"+ activeFile + ".html"));
			
			ToolBar tools = new ToolBar();
			
			Button saveButton = new Button("Speichern");
			saveButton.setOnAction(new EventHandler<ActionEvent>(){
				@Override
				public void handle(ActionEvent event) {
					save("data\\file"+ activeFile + ".html");
				}
			});
			
			Pagination select = new Pagination();
			select.setPageFactory(new Callback<Integer, Node>(){
				@Override
				public Node call(Integer param){
					save("data\\file"+ activeFile + ".html");
					activeFile = param;
					editor.setHtmlText(load("data\\file"+ param + ".html"));
					int showIndex = activeFile+1;
					primaryStage.setTitle("Datei: " + showIndex);
					return new Label("Datei: "+showIndex);
				}
			});
			
			new Thread(() -> {
				while(isRunning){
					try {
						Thread.sleep(8_000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					save("data\\file"+ activeFile + ".html");
				}
			}).start();
			
			tools.getItems().add(saveButton);
			tools.getItems().add(new Label("Datei auswählen: "));
			tools.getItems().add(select);
			tools.getItems().add(stateOut);
			root.getChildren().add(editor);
			root.getChildren().add(tools);
			
			Scene scene = new Scene(root,700,600);
			primaryStage.setScene(scene);
			primaryStage.show();
			primaryStage.setTitle("Datei: " + activeFile);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setStateOut(String newValue){
		Platform.runLater(() -> {
			stateOut.setText(newValue);
		});
	}
	
	public void save(String name) {
		new Thread( () -> {
			setStateOut("speichern...");
			try {
				Thread.sleep(1_000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			setStateOut((isRunning)? "bereit" : "Fehler");
		}).start();
		
		
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
		save("data\\file"+ activeFile + ".html");
		isRunning = false;
	}
	
	@Override
	public void init() throws Exception {
		isRunning = true;
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
