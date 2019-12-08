/*****************************************************************************
 * MomentExpVBox.java
 *****************************************************************************
 * Copyright é–¿ç‡‚æ‹· 2017 uPMT
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston MA 02110-1301, USA.
 *****************************************************************************/

package controller;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Optional;
import java.util.ResourceBundle;

import NewModel.IDescriptemeAdapter;
import SchemaTree.Cell.Models.IPropertyAdapter;
import javafx.scene.control.ColorPicker;

import application.Main;
import controller.command.ChangeColorMomentCommand;
import controller.command.ChangeDateMomentCommand;
import controller.command.ChangeExtractMomentCommand;
import controller.command.RemoveMomentCommand;
import controller.command.RenameMomentCommand;
import controller.controller.MomentAddTypeController;
import controller.controller.MomentColorController;
import controller.controller.MomentExtractController;
import controller.controller.MomentNameController;
import controller.controller.MomentRemoveTypeController;
import controller.controller.Observer;
import controller.controller.Observable;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.AutoCompletionService;
import model.Category;
import model.Descripteme;
import model.MomentExperience;
import model.Type;
import utils.MainViewTransformations;
import utils.Serializer;
import utils.UndoCollector;
import utils.Utils;
import javafx.scene.control.Tooltip;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;

public class MomentExpVBox extends VBox implements Initializable, Observer, Serializable{
	
	private MomentExperience moment;
	private BorderPane momentPane;
	private @FXML Label label;
	private @FXML FlowPane typeSpace;
	private GridPane sousMomentPane;
	private @FXML BorderPane borderPaneLabel;
	private @FXML BorderPane momentCardPane;
	private Main main;
	private @FXML Button hasExtractImage;
	private Tooltip extractTooltip;
	private @FXML MenuButton momentMenuAction;
	private @FXML ScrollPane scrollTypesPane;
	
	//Controllers
	private MomentNameController nameController;
	private MomentColorController colorController;
	private MomentExtractController extractController;
	private MomentAddTypeController addTypeController;
	private MomentRemoveTypeController momentRemoveTypeController;
	
	public static DataFormat df = new DataFormat("controller.MomentExpVBox");
	public static DataFormat realCol = new DataFormat("controller.MomentExpVBox.realCol");
	public static DataFormat rootCol = new DataFormat("controller.MomentExpVBox.rootCol");
	private static final long serialVersionUID = 1420672609912364060L;
	
	private MomentExpVBox momentParent=null;

	
	// Stack of redoable Classes
	private Deque<TypeCategoryRepresentationController> stack = new ArrayDeque<TypeCategoryRepresentationController>();

	public MomentExpVBox(MomentExperience mexp, Main main) {
		
		this.main = main;
		moment = mexp;
        this.setPrefWidth(USE_COMPUTED_SIZE);
        this.setMaxWidth(USE_COMPUTED_SIZE);
        this.setMinHeight(200);
        loadMomentPane();
        
        extractTooltip = new Tooltip("");
		extractTooltip.setWrapText(true);
		extractTooltip.setMaxWidth(500);
        BorderPane.setMargin(this.momentPane,(new Insets(10,10,10,10)));
        VBox.setMargin(this,(new Insets(10,10,10,10)));
        //tooltip implementation
        hasExtractImage.setOnMouseEntered(new EventHandler<MouseEvent>() {
		    @Override
		    public void handle(MouseEvent event) {
		    	hasExtractImage.setCursor(Cursor.HAND);
		        javafx.geometry.Point2D p = hasExtractImage.localToScreen(hasExtractImage.getLayoutBounds().getMaxX(), hasExtractImage.getLayoutBounds().getMaxY()); 
		        extractTooltip.setOpacity(1);
		        extractTooltip.show(hasExtractImage, p.getX(), p.getY());
		    }
		});
		hasExtractImage.setOnMouseExited(new EventHandler<MouseEvent>() {
		    @Override
		    public void handle(MouseEvent event) {
		    	hasExtractImage.setCursor(Cursor.DEFAULT);
		    	extractTooltip.setOpacity(0);
		    	extractTooltip.hide();
		    }
		});
		this.hideExtractIcon();
		
		
		// creation of the name observer
		nameController = new MomentNameController(this.moment);
		nameController.addObserver(this);
		nameController.addObserver(main.getMainViewController());
		// creation creation of the color observer
		colorController = new MomentColorController(this.moment);
		colorController.addObserver(this);
		colorController.addObserver(main.getMainViewController());
		// creation creation of the extract observer
		extractController = new MomentExtractController(this.moment);
		extractController.addObserver(this);
		extractController.addObserver(main.getMainViewController());
		// creation creation of the adding class observer
		addTypeController = new MomentAddTypeController(this.moment);
		addTypeController.addObserver(this);
		addTypeController.addObserver(main.getMainViewController());
		// creation creation of the deleting class observer
		this.momentRemoveTypeController = new MomentRemoveTypeController(this.moment);
        this.momentRemoveTypeController.addObserver(this);
        addTypeController.addObserver(main.getMainViewController());
        
        MainViewTransformations.setCursor(this.borderPaneLabel, Cursor.MOVE); 
        MainViewTransformations.setCursor(this.momentMenuAction, Cursor.DEFAULT);
        borderPaneLabel.setOnDragDetected(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
            	Dragboard db = borderPaneLabel.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = new ClipboardContent();
                // Store node ID in order to know what is dragged.
                content.putString("moveMoment");
                content.put(MomentExpVBox.rootCol, MomentExpVBox.this.getColOfRootMoment());
				content.put(MomentExpVBox.realCol, MomentExpVBox.this.getCol());
				try {
					content.put(MomentExpVBox.df, Serializer.serialize(MomentExpVBox.this.getMoment()));
				}
				catch(IOException e) {
					e.printStackTrace();
				}
                db.setContent(content);
                event.consume();
            }
        });
        hasExtractImage.setOnDragOver(new EventHandler<DragEvent>() {
        	public void handle(DragEvent event) {
        		if(event.getDragboard().getString().equals("dragDescripteme")) {
	        		dragExtractIcon();
	        		event.acceptTransferModes(TransferMode.ANY);
	        		event.consume();
        		}
        	}
        });
        hasExtractImage.setOnDragExited(new EventHandler<DragEvent>() {
        	public void handle(DragEvent event) {
        		if(moment.getDescriptemes().isEmpty())
        			hideExtractIcon();
        		else
        			showExtractIcon(moment.getDescriptemes());
        		event.consume();
        	}
        });
        hasExtractImage.setOnDragDropped(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent event) {
				LinkedList<IDescriptemeAdapter> newDescriptemes = new LinkedList<IDescriptemeAdapter>();
	        	for(IDescriptemeAdapter d : moment.getDescriptemes()) {
	        		newDescriptemes.add(new Descripteme(d.getTexte()));
	    		}
	        	newDescriptemes.add(new Descripteme((String)event.getDragboard().getContent(DataFormat.HTML)));
				ChangeExtractMomentCommand cmd = new ChangeExtractMomentCommand(
						extractController,
						moment.getDescriptemes(),
						newDescriptemes,
						main
	        			);
				cmd.execute();
				UndoCollector.INSTANCE.add(cmd);
				event.consume();
			}
        });
        
        momentMenuAction.getItems().clear();
        MenuItem menu1 = new MenuItem(main._langBundle.getString("delete"));
        menu1.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				deleteMoment();
			}
        });
        menu1.setStyle("-fx-padding: 0 0 0 -170");
        
        StackPane root = new StackPane();
        final ColorPicker colorssPicker = new ColorPicker();
        colorssPicker.setStyle("-fx-background-color: white;");
        System.out.println("hhhhh " + moment.getName());
        colorssPicker.setValue(Color.web(moment.getColor()));
        colorssPicker.setPrefWidth(170);
        
        MenuItem menu2 = new MenuItem();
        final StackPane root2 = new StackPane(); 
        root2.getChildren().add(colorssPicker);
        root2.setAlignment(Pos.CENTER);
        menu2.setGraphic(root2);
        
        menu2.setStyle("-fx-padding: -10 -10 0 0");
        menu2.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				String colorString = Utils.toRGBCode(colorssPicker.getValue());
				setColor(colorString);
				setBorderColor(colorString);
				//getMomentColorController().update(colorString);
				colorPicked(colorssPicker.getValue());
			}
        });
        
        MenuItem menu3 = new MenuItem(main._langBundle.getString("add_comment"));
        menu3.setStyle("-fx-padding: 0 0 0 -170");
        menu3.setDisable(true);
        menuTime = new MenuItem(main._langBundle.getString("edit_time") + " ('"+moment.getDateString()+"')");
        menuTime.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				ButtonType edit = new ButtonType(main._langBundle.getString("edit"), ButtonData.OK_DONE);
				ButtonType close = new ButtonType(main._langBundle.getString("close"), ButtonData.CANCEL_CLOSE);
				Dialog dialog = new TextInputDialog(moment.getDateString());
				dialog.getDialogPane().getButtonTypes().clear();
				dialog.getDialogPane().getButtonTypes().addAll(edit, close);
				dialog.setTitle(main._langBundle.getString("time"));
				dialog.setHeaderText(main._langBundle.getString("time_alert") + " " + moment.getName() + " " + main._langBundle.getString("is") + " " + moment.getDateString());
				dialog.initModality(Modality.APPLICATION_MODAL);
				Optional<String> result = dialog.showAndWait();
				if (result.isPresent()) {
					try {
						ChangeDateMomentCommand cmd = new ChangeDateMomentCommand(
								result.get(),
								moment.getDateString(),
								MomentExpVBox.this,
								main);
						cmd.execute();
						UndoCollector.INSTANCE.add(cmd);
						
					}catch(Exception e){}
				}
				else {
					
				}
				//actionStatus.setText("Text entered: " + entered);
			}
        });
        //menuTime.setDisable(true);
        //momentMenuAction.setMaxWidth(10);
        momentMenuAction.setPrefSize(100, 100);
        //.setPrefWidth(10);
        menuTime.setStyle("-fx-padding: 0 0 0 -170");
        momentMenuAction.getItems().addAll(menu1, menu2, menu3, menuTime);
	}
	
	public void editMenuTime(String text) {
		menuTime.setText(main._langBundle.getString("edit_time") + " ("+text+")");
	}
	private MenuItem menuTime;
	
	
	public void colorPicked(Color cp) {
		Color couleur = cp;
		String colorString = Utils.toRGBCode(couleur);
		ChangeColorMomentCommand cmd = new ChangeColorMomentCommand(
				this.getMomentColorController(),
				moment.getColor(),
				colorString,
				main);
		cmd.execute();
		UndoCollector.INSTANCE.add(cmd);
		this.getMomentColorController().update(colorString);
	}
	
	public MomentExpVBox(Main main){
		this(new MomentExperience(), main);
	}
	

	private void loadMomentPane(){
		sousMomentPane = new GridPane();
        try {
        	FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/view/MomentExperience.fxml"));
            loader.setController(this);
			momentPane = (BorderPane) loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.setLabelChangeName(main,this);
		// adding the deletion of the moment by suppr / del
		this.setOnKeyPressed(new EventHandler<KeyEvent>()
	    {
	        @Override
	        public void handle(KeyEvent ke)
	        {
	            if ((ke.getCode().equals(KeyCode.DELETE) || ke.getCode().equals(KeyCode.BACK_SPACE)) && isFocused())
	            {
	                deleteMoment();
	            }
	        }
	    });
	}
	
	public void setCurrentProperty(IPropertyAdapter n) {
		moment.setCurrentProperty(n);
	}
	
	public IPropertyAdapter getCurrentProperty() {
		return moment.getCurrentProperty();
	}
	
	public void LoadMomentData(){
		label.setText(moment.getName());
		if (this.moment.getColor() != null) {
			setColor(this.moment.getColor());
		}
		if(!this.moment.getDescriptemes().isEmpty()){
			showExtractIcon(this.moment.getDescriptemes());
		}
		//Detect whether the text is likely to be white or black depending on the background.
		label.setTextFill(MainViewTransformations.ContrastColor(Color.web(moment.getColor())));
	}
	
	private String cssShadow="-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0, 0, 0); ";
	
	public void setColor(String col){
		String styleLabel = "-fx-background-color: "+col+"; ";
		if(Main.activateBetaDesign)
			styleLabel+= 		"-fx-border-color: "+col+"; ";
		else
			styleLabel+= 		"-fx-border-color: black;";
		//this.borderPaneLabel.setStyle(cssShadow+styleLabel);
		this.scrollTypesPane.setStyle(styleLabel);
		this.setBorderColor(col);
		label.setTextFill(MainViewTransformations.ContrastColor(Color.web(moment.getColor())));
	}
	
	public void showExtractIcon(LinkedList<IDescriptemeAdapter> tooltips){
		this.hasExtractImage.getStyleClass().clear();
		this.hasExtractImage.getStyleClass().add("button");
		this.hasExtractImage.getStyleClass().add("buttonMomentView");
		String tooltip = "";
		
		for(int i=0; i<tooltips.size();i++) {
			tooltip+="[Descripteme "+(i+1)+"]: "+tooltips.get(i).getTexte();
			if(i!=tooltips.size()-1) tooltip+="\n";
		}
		extractTooltip.setText(tooltip);
		extractTooltip.setOpacity(0);
    	extractTooltip.hide();
	}
	
	public void hideExtractIcon(){
		this.hasExtractImage.getStyleClass().clear();
		this.hasExtractImage.getStyleClass().add("button");
		this.hasExtractImage.getStyleClass().add("buttonMomentViewDisabled");
		
		extractTooltip.setText(main._langBundle.getString("no_descripteme"));
		extractTooltip.setOpacity(0);
    	extractTooltip.hide();
	}
	
	public void dragExtractIcon(){
		this.hasExtractImage.getStyleClass().clear();
		this.hasExtractImage.getStyleClass().add("button");
		this.hasExtractImage.getStyleClass().add("buttonMomentViewDrag");
		extractTooltip.setOpacity(0);
    	extractTooltip.hide();
	}

	public void deleteMoment(){
		
		Alert alert = new Alert(AlertType.CONFIRMATION);
    	alert.setTitle(main._langBundle.getString("delete_warning"));
    	alert.setHeaderText(main._langBundle.getString("delete_moment_text_alert"));
    	alert.setContentText(main._langBundle.getString("continue_alert"));
    	alert.initStyle(StageStyle.UTILITY);

    	Optional<ButtonType> result = alert.showAndWait();
    	if (result.get() == ButtonType.OK){
    		RemoveMomentCommand cmd = new RemoveMomentCommand(this.getMoment(), main);
    		cmd.execute();
    		UndoCollector.INSTANCE.add(cmd);
    	}
	}
	
	
	public void copyMoment() {
		Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
	}
	
	private void setLabelChangeName(Main main,MomentExpVBox thiss){
		label.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent arg0) {
				if(arg0.getClickCount() == 2){
					//System.out.println("DoubleClick");
					editNameMode();
				}
			}
		});
	}
	
	public static boolean isStringNullOrWhiteSpace(String value) {
	    if (value == null) {
	        return true;
	    }

	    for (int i = 0; i < value.length(); i++) {
	        if (!Character.isWhitespace(value.charAt(i))) {
	            return false;
	        }
	    }

	    return true;
	}
	
	private void editNameMode() {
		TextField t = new TextField();
		AutoCompletionService auto = new AutoCompletionService(main.getCurrentProject(),moment);
		
		t.setMaxWidth(180);
		t.setText(moment.getName());
		t.requestFocus();

		/*TextFields.bindAutoCompletion(t, te -> {
			Set<String> autolist=new TreeSet<String>();

			autolist.addAll( auto.getSuggestedMoments(moment).stream().filter(elem ->
		    {
		    	if(te.getUserText().toLowerCase().toString().equals(" ")) {
		    		//System.out.println("yo1");
	    			return true;
		    	}
		    	else {
		    		//System.out.println("yo :"+te.getUserText().toLowerCase()+"R");
		    		return elem.toLowerCase().startsWith(te.getUserText().toLowerCase());

		    	}
		    }).collect(Collectors.toList()));

			if(!te.getUserText().toString().equals(" "))
				autolist.add(te.getUserText().toString());
			
			
		return autolist;
		    /*return auto.getSuggestedMoments(moment).stream().filter(elem -> 
		    {	
		    	if(te.getUserText().toLowerCase().toString().equals(" ")) {
		    		//System.out.println("yo1");
	    			return true;
		    	}
		    	else {
		    		//System.out.println("yo :"+te.getUserText().toLowerCase()+"R");
		    		return elem.toLowerCase().startsWith(te.getUserText().toLowerCase());
		    		
		    	}
		    }).collect(Collectors.toList());*/
//		});
		
		
		
		ChangeListener<Boolean>	 listener = new ChangeListener<Boolean>() {
			 @Override
			    public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue)
			    {
			        if (!newPropertyValue)
			        {
			        	if (isStringNullOrWhiteSpace(t.getText())) {
			        		RenameMomentCommand cmd = new RenameMomentCommand(
				        			nameController,
				        			moment.getName(),
				        			"____",
				        			main);
							cmd.execute();
							UndoCollector.INSTANCE.add(cmd);
							borderPaneLabel.setCenter(label);
							t.focusedProperty().removeListener(this);
			        	
			        	} else {
			        	RenameMomentCommand cmd = new RenameMomentCommand(
			        			nameController,
			        			moment.getName(),
			        			t.getText(),
			        			main);
						cmd.execute();
						UndoCollector.INSTANCE.add(cmd);
						borderPaneLabel.setCenter(label);
						//borderPaneLabel.setCenter(label);
						t.focusedProperty().removeListener(this);
			        }
			        }
			    }
		};
		t.setOnKeyPressed(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				if(event.getCode() == KeyCode.ENTER){
					t.setText(t.getText());
					borderPaneLabel.setCenter(label);
					//borderPaneLabel.setCenter(label);
				}
				if(event.getCode() == KeyCode.ESCAPE){
					borderPaneLabel.setCenter(label);
					//borderPaneLabel.setCenter(label);
				}
			}
		});
		t.focusedProperty().addListener(listener);
		Platform.runLater(()->t.requestFocus());
		Platform.runLater(()->t.selectAll());
		borderPaneLabel.setCenter(t);
	}
	
	public TypeCategoryRepresentationController getTypeClassRep(Category item) {
		for(Node n : typeSpace.getChildren()){
			TypeCategoryRepresentationController tcr = (TypeCategoryRepresentationController) n;
			if(tcr.getClasse().equals(item)){
				return tcr;
			}
		}
		return null;
	}
	
	public void removeTypeClassRep(TypeCategoryRepresentationController tcrc) {
		if(this.typeSpace.getChildren().contains(tcrc)) {
			this.typeSpace.getChildren().remove(tcrc);
			this.moment.getCategories().remove(tcrc.getClasse());
			stack.push(tcrc);
		}
	}
	
	public void putPreviousClassRep() {
		if(!stack.isEmpty()) {
			TypeCategoryRepresentationController tcrc = stack.getFirst();
			if(!this.typeSpace.getChildren().contains(tcrc)) {
				this.typeSpace.getChildren().add(tcrc);
				this.moment.getCategories().add(tcrc.getClasse());
				stack.pop();
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						/*if(main.getMainViewController().isInspectorOpen()) {
							main.getMainViewController().renderInspector();
						}*/
					}
				});		
			}
		}	
	}
	
	public void showMoment(){
		this.getChildren().add(0, momentPane);
		this.setPadding(new Insets(10,0,0,0));
	}
	
	public void hideMoment() {
		this.getChildren().remove(momentPane);
		this.setPadding(new Insets(0));
	}
	
	public MomentExperience getMoment() {
		return moment;
	}

	public void setMoment(MomentExperience moment) {
		this.moment = moment;
		nameController.updateModel(moment);
		colorController.updateModel(moment);
		extractController.updateModel(moment);
		addTypeController.updateModel(moment);
		momentRemoveTypeController.updateModel(moment);
	}
	
	@Override
	public void updateVue(Observable obs, Object value) {

		if(obs.getClass().equals(MomentNameController.class)) {
			label.setText((String) value);
		}
		if(obs.getClass().equals(MomentColorController.class)) {
			this.setColor((String) value);
		}
		if(obs.getClass().equals(MomentExtractController.class)) {
			//System.out.println("Change detected!");
			if(value != null) {
				if(!((LinkedList<IDescriptemeAdapter>) value).isEmpty())
					this.showExtractIcon((LinkedList<IDescriptemeAdapter>) value);
				else this.hideExtractIcon();
			}else {
				this.hideExtractIcon();
			}
		}
		if(obs.getClass().equals(MomentAddTypeController.class)) {
			if(value != null) {
				TypeCategoryRepresentationController elementPane = new TypeCategoryRepresentationController((Category) value,this,main);
				
					MainViewTransformations.addTypeListener(elementPane, this, (Type) value, main);
					//((TypeTreeViewControllerClass)((TypeTreeView)Main.tempDragReference).getController()).getNameController().addObserver(elementPane);
					this.typeSpace.getChildren().add(elementPane);
			}
			else {
				this.typeSpace.getChildren().remove(this.typeSpace.getChildren().size()-1);
			}
			
		}
		if(obs.getClass().equals(MomentRemoveTypeController.class)) {
			TypeCategoryRepresentationController t = (TypeCategoryRepresentationController) value;
			boolean contains = TypeCategoryRepresentationController.ListcontainsTypeClassRep(this.typeSpace.getChildren(), t);
			if(contains){
				TypeCategoryRepresentationController.RemoveTypeClassRepFromList(this.typeSpace.getChildren(), t);
			}else {
				this.typeSpace.getChildren().add((TypeCategoryRepresentationController)value);
			}
		}		
	}
	
	public void setBorderColor(String couleur) {
		if(Main.activateBetaDesign && couleur == "black") couleur = moment.getColor();
		momentPane.setStyle("-fx-border-color : "+couleur);
		String styleLabel = "-fx-background-color: "+moment.getColor()+"; -fx-border-color:"+couleur +";";
		this.borderPaneLabel.setStyle(styleLabel);
	}

	public int getCol() {
		return this.moment.getGridCol();
	}

	public Label getLabel() {
		return label;
	}

	public void setLabelText(String label) {
		this.label.setText(label);
	}

	public FlowPane getTypeSpace() {
		return typeSpace;
	}

	public void setTypeSpace(FlowPane typeSpace) {
		this.typeSpace = typeSpace;
	}

	public BorderPane getMomentPane() {
		return momentPane;
	}

	public GridPane getSousMomentPane() {
		return sousMomentPane;
	}

	public void setSousMomentPane(GridPane sousMomentPane) {
		this.sousMomentPane = sousMomentPane;
	}
	
	public BorderPane getborderPaneLabel(){
		return this.borderPaneLabel;
	}
	
	public MomentNameController getMomentNameController() {
		return this.nameController;
	}
	
	public MomentColorController getMomentColorController() {
		return this.colorController;
	}
	
	public MomentExtractController getMomentExtractController() {
		return this.extractController;
	}
	
	public MomentAddTypeController getMomentAddTypeController() {
		return this.addTypeController;
	}
	
	public MomentRemoveTypeController getMomentRemoveTypeController() {
		return this.momentRemoveTypeController;
	}
	
	public MomentExpVBox getVBoxParent() {
		return this.momentParent;
	}
	
	public void setVBoxParent(MomentExpVBox parent) {
		this.momentParent = parent;
	}
	
	public int getColOfRootMoment() {
		if(hasParent()) return this.momentParent.getColOfRootMoment();
		else return this.getCol();
	}
	
	public boolean isAChildOf(MomentExperience p) {
		if(hasParent()) {
			if(p.equals(this.getVBoxParent().getMoment()))
				return true;
			else 
				return this.getVBoxParent().isAChildOf(p);
		}
		else return false;
	}
	
	public boolean isAParentOf(MomentExperience p) {
		boolean ret = false;
		for(Node n : this.getSousMomentPane().getChildren()) {
			if(ret)break;
			MomentExpVBox m = (MomentExpVBox)n;
			if(m.getMoment().equals(p)) ret = true;
			else {
				ret = m.isAParentOf(p);
			}
		}
		return ret;
	}
	
	public boolean isDirectParentOf(MomentExperience p) {
		boolean ret = false;
		for(Node n : this.getSousMomentPane().getChildren()) {
			MomentExpVBox m = (MomentExpVBox)n;
			if(m.getMoment().equals(p)) {
				ret = true;
				break;
			}
			else {

			}
		}
		return ret;
	}
	
	public boolean hasParent() {
		return this.momentParent!=null;
	}
	
	@Override
	public boolean equals(Object obj) {
		try {
			return ((MomentExpVBox)obj).getMoment().equals(this.getMoment());
		}catch(Exception e) {return false;}
	}
	
	public String toString() {
		String ret="";
		ret="{Nom:"+this.moment.getName()+"; Sous-Moments:[";
		for(Node n : this.getSousMomentPane().getChildren()) {
			MomentExpVBox m = (MomentExpVBox)n;
			ret+=m.toString();
		}
		ret+="]}\n";
		return ret;
	}
	
	@FXML
	public void pickExtract() {
		main.setCurrentMoment(this);
		Stage promptWindow = new Stage(StageStyle.UTILITY);
		promptWindow.setTitle(main._langBundle.getString("select_extract"));
		//promptWindow.setAlwaysOnTop(true);
		promptWindow.initModality(Modality.APPLICATION_MODAL);
		/*try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/view/SelectDescriptemePart.fxml"));
			loader.setController(new SelectDescriptemePartController(main, promptWindow, moment.getDescriptemes()));
			loader.setResources(main._langBundle);
			BorderPane layout = (BorderPane) loader.load();
			Scene launchingScene = new Scene(layout);
			promptWindow.setScene(launchingScene);
			promptWindow.show();

		} catch (IOException e) {
			// TODO Exit Program
			e.printStackTrace();
		}*/
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/view/DescriptemeViewer.fxml"));
			loader.setController(new DescriptemeViewerController(main, promptWindow, moment, extractController));
			loader.setResources(main._langBundle);
			BorderPane layout = (BorderPane) loader.load();
			Scene launchingScene = new Scene(layout);
			promptWindow.setScene(launchingScene);
			promptWindow.show();

		} catch (IOException e) {
			// TODO Exit Program
			e.printStackTrace();
		}
	}
	
	public ScrollPane getScrollPane() {
		return this.scrollTypesPane;
	}
}