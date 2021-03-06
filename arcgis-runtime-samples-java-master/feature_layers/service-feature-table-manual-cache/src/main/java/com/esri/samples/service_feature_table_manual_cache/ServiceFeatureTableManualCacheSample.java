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

package com.esri.samples.service_feature_table_manual_cache;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.BasemapStyle;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.MapView;

public class ServiceFeatureTableManualCacheSample extends Application {

  private MapView mapView;
  private FeatureLayer featureLayer; // keep loadable in scope to avoid garbage collection
  private Label featuresReturnLabel;
  private ServiceFeatureTable featureTable;
  private ListenableFuture<FeatureQueryResult> tableResult;

  private static final String SERVICE_FEATURE_URL =
      "https://sampleserver6.arcgisonline.com/arcgis/rest/services/SF311/FeatureServer/0";

  @Override
  public void start(Stage stage) {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);
      scene.getStylesheets().add(getClass().getResource("/service_feature_table_manual_cache/style.css").toExternalForm());

      // set title, size, and add scene to stage
      stage.setTitle("Service Feature Table Manual Cache Sample");
      stage.setWidth(800);
      stage.setHeight(700);
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
      controlsVBox.setMaxSize(200, 80);
      controlsVBox.getStyleClass().add("panel-region");

      // create button to request the service table's cache
      Button requestCacheButton = new Button("Request Cache");
      requestCacheButton.setMaxWidth(Double.MAX_VALUE);
      requestCacheButton.setDisable(true);

      requestCacheButton.setOnAction(e -> fetchCacheManually());

      // create a label to display number of features returned
      featuresReturnLabel = new Label("Features Returned: ");
      featuresReturnLabel.getStyleClass().add("panel-label");

      // add label and button to the control panel
      controlsVBox.getChildren().addAll(featuresReturnLabel, requestCacheButton);

      // create service feature table from a url
      featureTable = new ServiceFeatureTable(SERVICE_FEATURE_URL);

      // set request mode of service feature table to manual cache
      featureTable.setFeatureRequestMode(ServiceFeatureTable.FeatureRequestMode.MANUAL_CACHE);

      // create a feature layer from the service feature table
      featureLayer = new FeatureLayer(featureTable);

      // enable button when feature layer is done loading
      featureLayer.addDoneLoadingListener(() -> {
        if (featureLayer.getLoadStatus() == LoadStatus.LOADED) {
          requestCacheButton.setDisable(false);
        } else {
          Alert alert = new Alert(Alert.AlertType.ERROR, "Feature Layer Failed to Load!");
          alert.show();
        }
      });

      // create a map with the topographic basemap style
      ArcGISMap map = new ArcGISMap(BasemapStyle.ARCGIS_TOPOGRAPHIC);

      // create a map view and set the map to it
      mapView = new MapView();
      mapView.setMap(map);

      // add the feature layer to the map's operational layers
      map.getOperationalLayers().add(featureLayer);

      // set the starting viewpoint for the map view
      mapView.setViewpoint(new Viewpoint(new Point(-13630484, 4545415, SpatialReferences.getWebMercator()), 150000));

      // add the map view and control panel to stack pane
      stackPane.getChildren().addAll(mapView, controlsVBox);
      StackPane.setAlignment(controlsVBox, Pos.TOP_LEFT);
      StackPane.setMargin(controlsVBox, new Insets(10, 0, 0, 10));
    } catch (Exception e) {
      // on any error, display the stack trace
      e.printStackTrace();
    }
  }

  /**
   * Fetches the cache from a Service Feature Table manually.
   */
  private void fetchCacheManually() {

    // create query to select all tree or damage features
    QueryParameters queryParams = new QueryParameters();
    queryParams.setWhereClause("req_type = 'Tree Maintenance or Damage'");

    // * means all features
    List<String> outfields = Collections.singletonList("*");
    // get queried features from service feature table and clear previous cache
    tableResult = featureTable.populateFromServiceAsync(queryParams, true, outfields);

    tableResult.addDoneListener(() -> {
      try {
        // find the number of features returned from query
        AtomicInteger featuresReturned = new AtomicInteger();
        tableResult.get().forEach(feature -> featuresReturned.getAndIncrement());

        // display to user how many features where returned
        Platform.runLater(() -> featuresReturnLabel.setText("Features Returned: " + featuresReturned));
      } catch (Exception e) {
        // on any error, display the stack trace
        e.printStackTrace();
      }
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
