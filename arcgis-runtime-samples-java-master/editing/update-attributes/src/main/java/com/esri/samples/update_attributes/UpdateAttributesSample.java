/*
 * Copyright 2017 Esri.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.esri.samples.update_attributes;

import java.util.List;
import java.util.concurrent.ExecutionException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.ArcGISFeature;
import com.esri.arcgisruntime.data.FeatureEditResult;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.BasemapStyle;
import com.esri.arcgisruntime.mapping.GeoElement;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.IdentifyLayerResult;
import com.esri.arcgisruntime.mapping.view.MapView;

public class UpdateAttributesSample extends Application {

  private ArcGISFeature identifiedFeature;
  private ServiceFeatureTable featureTable;
  private MapView mapView;

  private ComboBox<String> comboBox;

  @Override
  public void start(Stage stage) {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);
      scene.getStylesheets().add(getClass().getResource("/update_attributes/style.css").toExternalForm());

      // size the stage, add a title, and set scene to stage
      stage.setTitle("Update Attributes Sample");
      stage.setHeight(700);
      stage.setWidth(800);
      stage.setScene(scene);
      stage.show();

      // authentication with an API key or named user is required to access basemaps and other location services
      String yourAPIKey = System.getProperty("apiKey");
      ArcGISRuntimeEnvironment.setApiKey(yourAPIKey);

      // create a control panel
      VBox controlsVBox = new VBox(6);
      controlsVBox.setBackground(new Background(new BackgroundFill(Paint.valueOf("rgba(0,0,0,0.3)"), CornerRadii.EMPTY,
          Insets.EMPTY)));
      controlsVBox.setPadding(new Insets(10.0));
      controlsVBox.setMaxSize(180, 80);
      controlsVBox.getStyleClass().add("panel-region");

      // create damage type label
      Label typeDamageLabel = new Label("Damage type:");
      typeDamageLabel.getStyleClass().add("panel-label");

      // create list of damage types
      ObservableList<String> damageList = FXCollections.observableArrayList();
      damageList.add("Destroyed");
      damageList.add("Inaccessible");
      damageList.add("Major");
      damageList.add("Minor");
      damageList.add("Affected");

      // create combo box
      comboBox = new ComboBox<>(damageList);
      comboBox.setMaxWidth(Double.MAX_VALUE);
      comboBox.setDisable(true);

      // handle type damage selection
      comboBox.getSelectionModel().selectedItemProperty().addListener((o, p, n) -> {
        if (!identifiedFeature.getAttributes().get("typdamage").equals(n)) {
          try {
            updateAttributes(identifiedFeature);
          } catch (Exception e) {
            displayMessage("Cannot update attributes", e.getCause().getMessage());
          }
        }
      });

      // add damage type label and comboBox to the control panel
      controlsVBox.getChildren().addAll(typeDamageLabel, comboBox);

      // create a map with the streets basemap style
      ArcGISMap map = new ArcGISMap(BasemapStyle.ARCGIS_STREETS);

      // create a map view and set the map to it
      mapView = new MapView();
      mapView.setMap(map);

      // set a viewpoint on the map view
      mapView.setViewpoint(new Viewpoint(40, -95,36978595));

      // create service feature table from URL
      featureTable = new ServiceFeatureTable("https://sampleserver6.arcgisonline.com/arcgis/rest/services/DamageAssessment/FeatureServer/0");

      // create a feature layer with the feature table
      FeatureLayer featureLayer = new FeatureLayer(featureTable);

      // add the feature layer to the map
      map.getOperationalLayers().add(featureLayer);

      // show alert if layer fails to load
      featureLayer.addDoneLoadingListener(()->{
        if (featureLayer.getLoadStatus() != LoadStatus.LOADED) {
          displayMessage("Error", "Error loading feature layer");
        }
      });

      mapView.setOnMouseClicked(event -> {
        // accept only primary mouse click
        if (event.isStillSincePress() && event.getButton() == MouseButton.PRIMARY) {
          // create point from where user clicked
          Point2D point = new Point2D(event.getX(), event.getY());

          // clear any previous selection
          featureLayer.clearSelection();
          comboBox.setDisable(true);

          // get the clicked feature
          ListenableFuture<IdentifyLayerResult> results = mapView.identifyLayerAsync(featureLayer, point, 1, false);
          results.addDoneListener(() -> {
            try {
              IdentifyLayerResult layer = results.get();
              List<GeoElement> identified = layer.getElements();
              if (!identified.isEmpty()) {
                GeoElement element = identified.get(0);
                // get selected feature
                if (element instanceof ArcGISFeature) {
                  identifiedFeature = (ArcGISFeature) element;
                  featureLayer.selectFeature(identifiedFeature);
                  identifiedFeature.loadAsync();
                  identifiedFeature.addDoneLoadingListener(() -> {
                    if (identifiedFeature.getLoadStatus() == LoadStatus.LOADED) {
                      comboBox.getSelectionModel().select((String) identifiedFeature.getAttributes().get("typdamage"));
                    } else {
                      Alert alert = new Alert(Alert.AlertType.ERROR, "Element Failed to Load!");
                      alert.show();
                    }
                  });
                  comboBox.setDisable(false);
                }
              }
            } catch (InterruptedException | ExecutionException e) {
              displayMessage("Exception getting identify result", e.getCause().getMessage());
            }
          });
        }
      });

      // add the map view and control box to stack pane
      stackPane.getChildren().addAll(mapView, controlsVBox);
      StackPane.setAlignment(controlsVBox, Pos.TOP_LEFT);
      StackPane.setMargin(controlsVBox, new Insets(10, 0, 0, 10));

    } catch (Exception e) {
      // on any error, display the stack trace
      e.printStackTrace();
    }
  }

  /**
   * Applies changes to the feature, Service Feature Table, and server.
   */
  private void updateAttributes(ArcGISFeature feature) {

    if (featureTable.canUpdate(feature)) {
      // update attribute
      identifiedFeature.getAttributes().put("typdamage", comboBox.getValue());

      // update feature in the feature table
      ListenableFuture<Void> editResult = featureTable.updateFeatureAsync(feature);
      editResult.addDoneListener(() -> applyEdits(featureTable));
    } else {
      displayMessage(null, "Cannot update this feature.");
    }
  }

  /**
   * Sends any edits on the ServiceFeatureTable to the server.
   *
   * @param featureTable service feature table
   */
  private void applyEdits(ServiceFeatureTable featureTable) {

    // apply the changes to the server
    ListenableFuture<List<FeatureEditResult>> editResult = featureTable.applyEditsAsync();
    editResult.addDoneListener(() -> {
      try {
        List<FeatureEditResult> edits = editResult.get();
        // check if the server edit was successful
        if (edits != null && edits.size() > 0) {
          if (!edits.get(0).hasCompletedWithErrors()) {
            displayMessage(null, "Attributes updated.");
          } else {
            throw edits.get(0).getError();
          }
        }
      } catch (InterruptedException | ExecutionException e) {
        displayMessage("Error applying edits on server", e.getCause().getMessage());
      }
    });
  }

  /**
   * Shows a message in an alert dialog.
   *
   * @param title title of alert
   * @param message message to display
   */
  private void displayMessage(String title, String message) {

    Platform.runLater(() -> {
      Alert dialog = new Alert(Alert.AlertType.INFORMATION);
      dialog.initOwner(mapView.getScene().getWindow());
      dialog.setHeaderText(title);
      dialog.setContentText(message);
      dialog.showAndWait();
    });
  }

  /**
   * Stops and releases all resources used in application.
   */
  @Override
  public void stop() {

    // release resources when the application closes
    if (mapView != null) {
      mapView.dispose();
    }
  }

  /**
   * Opens and runs application.
   * 
   * @param args arguments passed to this application
   */
  public static void main(String[] args) {

    Application.launch(args);
  }

}
