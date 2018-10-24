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
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.VBox;
import javafx.scene.web.HTMLEditor;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Callback;


public class Main extends Application {
	
	HTMLEditor editor;
	int index = 0; int showIndex;
	boolean isRunning = false;
	Label stateOut = new Label("bereit");
	File currentFile = new File("data\\file"+ index + ".html");
	
	@Override
	public void start(Stage primaryStage) {
		try {			
			VBox root = new VBox();
			root.setAlignment(Pos.BASELINE_CENTER );
			
			editor = new HTMLEditor();
			editor.setPrefHeight(10000);
			editor.autosize();
			editor.setHtmlText(load(currentFile));
			
			ToolBar tools = new ToolBar();
			
			Button saveButton = new Button("Speichern");
			saveButton.setOnAction(new EventHandler<ActionEvent>(){
				@Override
				public void handle(ActionEvent event) {
					save(currentFile);
				}
			});
			
			Button saveUnderButton = new Button("Speichern unter...");
			saveUnderButton.setOnAction((ActionEvent e) -> {
					saveUnder(primaryStage);
			});
			
			Button loadFileButton = new Button("Öffnen...");
			loadFileButton.setOnAction(new EventHandler<ActionEvent>(){
				@Override
				public void handle(ActionEvent event) {
					editor.setHtmlText(loadFile(primaryStage));
				}
			});
			
			Pagination select = new Pagination();
			select.setPageFactory(new Callback<Integer, Node>(){
				@Override
				public Node call(Integer param){
					save(currentFile);
					index = param;
					currentFile = new File("data\\file"+ param + ".html");
					editor.setHtmlText(load(currentFile));
					showIndex = index+1;
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
					save(currentFile);
				}
			}).start();
			
			tools.getItems().add(saveButton);
			tools.getItems().add(saveUnderButton);
			tools.getItems().add(loadFileButton);
			tools.getItems().add(new Separator());
			tools.getItems().add(new Label("Datei auswählen: "));
			tools.getItems().add(select);
			tools.getItems().add(stateOut);
			root.getChildren().add(editor);
			root.getChildren().add(tools);
			
			Scene scene = new Scene(root,1200,800);
			primaryStage.setScene(scene);
			primaryStage.show();
			primaryStage.setTitle("Datei: " + showIndex);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setStateOut(String newValue){
		Platform.runLater(() -> {
			stateOut.setText(newValue);
		});
	}
	
	public void save(File file) {
		new Thread( () -> {
			setStateOut("speichern...");
			try {
				Thread.sleep(1_000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			setStateOut((isRunning)? "bereit" : "Fehler");
		}).start();
		
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
	
	public void saveUnder(Window primaryStage) {
		new Thread( () -> {
			setStateOut("speichern...");
			try {
				Thread.sleep(1_000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			setStateOut((isRunning)? "bereit" : "Fehler");
		}).start();
		
		FileChooser fc = new FileChooser();
		File file = fc.showSaveDialog(primaryStage);
		
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
	
	private static String load(File file) { 

        if (!file.canRead() || !file.isFile()) 
            return ""; 

            BufferedReader in = null; 
        try { 
            in = new BufferedReader(new FileReader(file)); 
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
	
	private static String loadFile(Window primaryStage) { 

		FileChooser fc = new FileChooser();
		File file = fc.showOpenDialog(primaryStage);
		
		((Stage) primaryStage).setTitle(file.getName());

        if (!file.canRead() || !file.isFile()) 
            return ""; 

            BufferedReader in = null; 
        try { 
            in = new BufferedReader(new FileReader(file)); 
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
		save(currentFile);
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
