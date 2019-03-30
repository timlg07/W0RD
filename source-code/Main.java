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
	File currentFile;
	
	@Override
	public void init() {
		new File("data").mkdir();
		currentFile = new File("data\\file"+ index + ".html");
		isRunning = true;
	}
	
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
			
			// TODO save with STRG + S 
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
			
			Button exportAsHTML = new Button("Als HTML exportieren...");
			exportAsHTML.setOnAction((ActionEvent e) -> {
				export(primaryStage);
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
			
			Thread autosave = new Thread(() -> {
				while(isRunning){
					try {
						Thread.sleep(8_000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					save(currentFile);
				}
			});
			autosave.start();
			
			tools.getItems().addAll(saveButton,saveUnderButton,exportAsHTML,loadFileButton);
			tools.getItems().add(new Separator());
			tools.getItems().add(new Label("Datei auswählen: "));
			tools.getItems().add(select);
			tools.getItems().add(stateOut);
			root.getChildren().add(editor);
			root.getChildren().add(tools);
			
			Scene scene = new Scene(root,1200,800);
			primaryStage.setMinHeight(300);
			primaryStage.setMinWidth (300);
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
		setStateOut("speichern...");
		
		PrintWriter pWriter = null; 
		try { 
		    pWriter = new PrintWriter(file); 
		    pWriter.println(editor.getHtmlText());
		    setStateOut("bereit");
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
		FileChooser fc = new FileChooser();
		fc.setTitle("Wählen Sie Verzeichniss und Name");
		fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Website","*.html"));
		fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Quelltext","*.txt"));
		File file = fc.showSaveDialog(primaryStage);
		save(file);
	}
	
	public void export(Window primaryStage) {
		
		FileChooser fc = new FileChooser();
		fc.setTitle("Wählen Sie Verzeichniss und Name");
		fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Website","*.html"));
		File file = fc.showSaveDialog(primaryStage); // TODO File Format
		
		new Thread( () -> {
			setStateOut("exportieren...");
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
		    pWriter.println(toNormHTML(editor.getHtmlText()));
		} catch (IOException ioe) { 
		    ioe.printStackTrace(); 
		} finally { 
		    if (pWriter != null){ 
		    	pWriter.flush(); 
		        pWriter.close(); 
		    } 
		}
	}
	
	private static String toNormHTML(String text) {
		return text
			.replaceFirst("<body contenteditable=\"true\">", "<body>")
			.replaceFirst("</body>", "<div style=\"position:fixed;bottom:4px;left:50%;text-align:center;transform:translate(-50%,0%);-webkit-transform:translate(-50%,0%);\"><a style=\"text-decoration:none\" href=\"https://github.com/timlg07/W0RD\"><font face=\"Source Code Pro Black\" size=\"2\" color=\"#f2f2f2\" style=\"background-color: rgb(51, 51, 51);\">&nbsp;This file was created with W0RD - a free, fast and simple text editor by Tim Greller&nbsp;</font></a></div></body>")
		;
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
	
	public static void main(String[] args) {
		launch(args);
	}
}
