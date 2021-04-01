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

package com.esri.samples.viewshed_camera;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.esri.arcgisruntime.geoanalysis.LocationViewshed;
import com.esri.arcgisruntime.layers.IntegratedMeshLayer;
import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.ArcGISTiledElevationSource;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Surface;
import com.esri.arcgisruntime.mapping.view.AnalysisOverlay;
import com.esri.arcgisruntime.mapping.view.Camera;
import com.esri.arcgisruntime.mapping.view.SceneView;

public class ViewshedCameraSample extends Application {

  private SceneView sceneView;

  @Override
  public void start(Stage stage) {

    try {

      // create stack pane and JavaFX app scene
      StackPane stackPane = new StackPane();
      Scene fxScene = new Scene(stackPane);

      // set title, size, and add JavaFX scene to stage
      stage.setTitle("Viewshed Camera Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(fxScene);
      stage.show();

      // create a scene and add a basemap to it
      ArcGISScene scene = new ArcGISScene();
      scene.setBasemap(Basemap.createImagery());

      // add the SceneView to the stack pane
      sceneView = new SceneView();
      sceneView.setArcGISScene(scene);

      // set the camera
      Camera camera = new Camera(41.985, 2.82691, 124.987, 332.131, 82.4732, 0.0);
      sceneView.setViewpointCamera(camera);

      // add base surface for elevation data
      Surface surface = new Surface();
      final String elevationSourceUri = "https://elevation3d.arcgis.com/arcgis/rest/services/WorldElevation3D/Terrain3D/ImageServer";
      surface.getElevationSources().add(new ArcGISTiledElevationSource(elevationSourceUri));
      scene.setBaseSurface(surface);

      // add an integrated mesh layer (city of Girona, Spain)
      IntegratedMeshLayer gironaMeshLayer =
        new IntegratedMeshLayer("https://tiles.arcgis.com/tiles/z2tnIkrLQ2BRzr6P/arcgis/rest/services/Girona_Spain/SceneServer");
      scene.getOperationalLayers().add(gironaMeshLayer);

      // create a viewshed from the camera
      LocationViewshed viewshed = new LocationViewshed(camera, 1.0, 500.0);
      viewshed.setFrustumOutlineVisible(true);

      // create an analysis overlay to add the viewshed to the scene view
      AnalysisOverlay analysisOverlay = new AnalysisOverlay();
      analysisOverlay.getAnalyses().add(viewshed);
      sceneView.getAnalysisOverlays().add(analysisOverlay);

      // create a button to update the viewshed with the current camera
      Button cameraButton = new Button("Update from camera");
      cameraButton.setOnMouseClicked(e -> viewshed.updateFromCamera(sceneView.getCurrentViewpointCamera()));

      // add the sceneview and button to the stackpane
      stackPane.getChildren().addAll(sceneView, cameraButton);
      StackPane.setAlignment(cameraButton, Pos.TOP_LEFT);
      StackPane.setMargin(cameraButton, new Insets(10, 0, 0, 10));

    } catch (Exception e) {
      // on any error, display the stack trace.
      e.printStackTrace();
    }
  }

  /**
   * Stops and releases all resources used in application.
   */
  @Override
  public void stop() {

    if (sceneView != null) {
      sceneView.dispose();
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
