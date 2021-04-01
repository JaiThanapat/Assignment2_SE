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

package com.esri.samples.update_geometries;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.ArcGISFeature;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureEditResult;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.BasemapStyle;
import com.esri.arcgisruntime.mapping.GeoElement;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.IdentifyLayerResult;
import com.esri.arcgisruntime.mapping.view.MapView;

public class UpdateGeometriesSample extends Application {

  private MapView mapView;
  private ServiceFeatureTable featureTable;
  private FeatureLayer featureLayer;
  private ArcGISFeature selectedFeature; // keep loadable in scope to avoid garbage collection

  private static final String FEATURE_LAYER_URL =
      "https://sampleserver6.arcgisonline.com/arcgis/rest/services/DamageAssessment/FeatureServer/0";

  @Override
  public void start(Stage stage) {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);

      // set title, size, and add scene to stage
      stage.setTitle("Update Geometries Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // authentication with an API key or named user is required to access basemaps and other location services
      String yourAPIKey = System.getProperty("apiKey");
      ArcGISRuntimeEnvironment.setApiKey(yourAPIKey);

      // create a map with the streets basemap style
      ArcGISMap map = new ArcGISMap(BasemapStyle.ARCGIS_STREETS);

      // create a map view and set the map to it
      mapView = new MapView();
      mapView.setMap(map);

      // set a viewpoint on the map view
      mapView.setViewpoint(new Viewpoint(40, -95, 36978595));

      // add features from a feature service
      featureTable = new ServiceFeatureTable(FEATURE_LAYER_URL);
      featureLayer = new FeatureLayer(featureTable);
      map.getOperationalLayers().add(featureLayer);

      // handle clicks on the map view to select and move features
      mapView.setOnMouseClicked((MouseEvent event) -> {
        if (event.isStillSincePress() && event.getButton() == MouseButton.PRIMARY) {
          // get screen point where user clicked
          Point2D point = new Point2D(event.getX(), event.getY());

          // get map location corresponding to screen point
          Point mapPoint = mapView.screenToLocation(point);

          // identify any clicked feature
          ListenableFuture<IdentifyLayerResult> results = mapView.identifyLayerAsync(featureLayer, point, 1, false, 1);
          results.addDoneListener(() -> {
            try {

              // get selected feature
              List<GeoElement> elements = results.get().getElements();
              if (elements.size() > 0 && elements.get(0) instanceof ArcGISFeature) {

                // clicked on a feature, select it
                ArcGISFeature selected = (ArcGISFeature) elements.get(0);
                featureLayer.clearSelection(); //clear previous selections
                featureLayer.selectFeature(selected);

              } else {

                // didn't click on a feature
                ListenableFuture<FeatureQueryResult> selectedQuery = featureLayer.getSelectedFeaturesAsync();
                selectedQuery.addDoneListener(() -> {
                  try {
                    // check if a feature is currently selected
                    FeatureQueryResult selectedQueryResult = selectedQuery.get();
                    Iterator<Feature> features = selectedQueryResult.iterator();
                    if (features.hasNext()) {
                      // move selected feature to clicked location
                      selectedFeature = (ArcGISFeature) features.next();
                      selectedFeature.loadAsync();
                      selectedFeature.addDoneLoadingListener(() -> {
                        if (selectedFeature.canUpdateGeometry()) {
                          selectedFeature.setGeometry(mapPoint);
                          ListenableFuture<Void> featureTableResult = featureTable.updateFeatureAsync(selectedFeature);
                          // apply the edits to the service
                          featureTableResult.addDoneListener(this::applyEdits);
                        }
                      });

                    } // else nothing currently selected, do nothing

                  } catch (InterruptedException | ExecutionException e) {
                    displayMessage("Exception getting selected feature", e.getCause().getMessage());
                  }
                });
              }
            } catch (InterruptedException | ExecutionException e) {
              displayMessage("Exception getting clicked feature", e.getCause().getMessage());
            }
          });

          // on secondary mouse click, clear feature selection
        } else if (event.isStillSincePress() && event.getButton() == MouseButton.SECONDARY) {
          featureLayer.clearSelection();
        }
      });

      // add the map view to stack pane
      stackPane.getChildren().add(mapView);

    } catch (Exception e) {
      // on any error, display the stack trace
      e.printStackTrace();
    }
  }

  /**
   * Sends any edits on the ServiceFeatureTable to the server.
   */
  private void applyEdits() {

    // apply the changes to the server
    ListenableFuture<List<FeatureEditResult>> editResult = featureTable.applyEditsAsync();
    editResult.addDoneListener(() -> {
      try {
        List<FeatureEditResult> edits = editResult.get();
        // check if the server edit was successful
        if (edits != null && edits.size() > 0) {
          if (!edits.get(0).hasCompletedWithErrors()) {
            displayMessage(null, "Geometry updated");
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
