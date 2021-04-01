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

package com.esri.samples.viewshed_location;

import java.util.concurrent.ExecutionException;

import javafx.beans.binding.Bindings;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import com.esri.arcgisruntime.geoanalysis.LocationViewshed;
import com.esri.arcgisruntime.geoanalysis.Viewshed;
import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.PointBuilder;
import com.esri.arcgisruntime.layers.ArcGISSceneLayer;
import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.ArcGISTiledElevationSource;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Surface;
import com.esri.arcgisruntime.mapping.view.AnalysisOverlay;
import com.esri.arcgisruntime.mapping.view.Camera;
import com.esri.arcgisruntime.mapping.view.SceneView;

public class ViewshedLocationController {

  @FXML private SceneView sceneView;
  @FXML private ToggleButton visibilityToggle;
  @FXML private ToggleButton frustumToggle;
  @FXML private Slider headingSlider;
  @FXML private Slider pitchSlider;
  @FXML private Slider horizontalAngleSlider;
  @FXML private Slider verticalAngleSlider;
  @FXML private Slider minDistanceSlider;
  @FXML private Slider maxDistanceSlider;

  public void initialize() {
    // create a scene and add a basemap to it
    ArcGISScene scene = new ArcGISScene();
    scene.setBasemap(Basemap.createImagery());
    sceneView.setArcGISScene(scene);

    // add base surface for elevation data
    Surface surface = new Surface();
    final String localElevationImageService = "http://scene.arcgis" +
        ".com/arcgis/rest/services/BREST_DTM_1M/ImageServer";
    surface.getElevationSources().add(new ArcGISTiledElevationSource(localElevationImageService));
    scene.setBaseSurface(surface);

    // add a scene layer
    final String buildings = "http://tiles.arcgis.com/tiles/P3ePLMYs2RVChkJx/arcgis/rest/services/Buildings_Brest/SceneServer/layers/0";
    ArcGISSceneLayer sceneLayer = new ArcGISSceneLayer(buildings);
    scene.getOperationalLayers().add(sceneLayer);

    // create a viewshed from the camera
    Point location = new Point(-4.50, 48.4,100.0);
    LocationViewshed viewshed = new LocationViewshed(location, headingSlider.getValue(), pitchSlider.getValue(),
        horizontalAngleSlider.getValue(), verticalAngleSlider.getValue(), minDistanceSlider.getValue(),
        maxDistanceSlider.getValue());
    // set the colors of the visible and obstructed areas
    Viewshed.setVisibleColor(0xCC00FF00);
    Viewshed.setObstructedColor(0xCCFF0000);
    // set the color and show the frustum outline
    Viewshed.setFrustumOutlineColor(0xCC0000FF);
    viewshed.setFrustumOutlineVisible(true);

    // set the camera
    Camera camera = new Camera(location, 200.0, 20.0, 70.0, 0.0);
    sceneView.setViewpointCamera(camera);

    // create an analysis overlay to add the viewshed to the scene view
    AnalysisOverlay analysisOverlay = new AnalysisOverlay();
    analysisOverlay.getAnalyses().add(viewshed);
    sceneView.getAnalysisOverlays().add(analysisOverlay);

    // create a listener to update the viewshed location when the mouse moves
    EventHandler<MouseEvent> mouseMoveEventHandler = new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent event) {
        Point2D point2D = new Point2D(event.getX(), event.getY());
        ListenableFuture<Point> pointFuture = sceneView.screenToLocationAsync(point2D);
        pointFuture.addDoneListener(() -> {
          try {
            Point point = pointFuture.get();
            PointBuilder pointBuilder = new PointBuilder(point);
            pointBuilder.setZ(point.getZ() + 50.0);
            viewshed.setLocation(pointBuilder.toGeometry());
            // add listener back
            sceneView.setOnMouseMoved(this);
          } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
          }
        });
        // disable listener until location is updated (for performance)
        sceneView.setOnMouseMoved(null);
      }
    };
    // remove the default listener for mouse move events
    sceneView.setOnMouseMoved(null);

    // click to start/stop moving viewshed with mouse
    sceneView.setOnMouseClicked(event -> {
      if (event.isStillSincePress() && event.getButton() == MouseButton.PRIMARY) {
        if (sceneView.getOnMouseMoved() == null) {
          sceneView.setOnMouseMoved(mouseMoveEventHandler);
        } else {
          sceneView.setOnMouseMoved(null);
        }
      }
    });

    // toggle visibility
    visibilityToggle.selectedProperty().addListener(e -> viewshed.setVisible(visibilityToggle.isSelected()));
    visibilityToggle.textProperty().bind(Bindings.createStringBinding(() -> visibilityToggle.isSelected() ? "ON" :
        "OFF", visibilityToggle.selectedProperty()));
    frustumToggle.selectedProperty().addListener(e -> viewshed.setFrustumOutlineVisible(frustumToggle.isSelected()));
    frustumToggle.textProperty().bind(Bindings.createStringBinding(() -> frustumToggle.isSelected() ? "ON" :
        "OFF", frustumToggle.selectedProperty()));
    // heading slider
    headingSlider.valueProperty().addListener(e -> viewshed.setHeading(headingSlider.getValue()));
    // pitch slider
    pitchSlider.valueProperty().addListener(e -> viewshed.setPitch(pitchSlider.getValue()));
    // horizontal angle slider
    horizontalAngleSlider.valueProperty().addListener(e -> viewshed.setHorizontalAngle(horizontalAngleSlider.getValue()));
    // vertical angle slider
    verticalAngleSlider.valueProperty().addListener(e -> viewshed.setVerticalAngle(verticalAngleSlider
        .getValue()));
    // distance sliders
    minDistanceSlider.valueProperty().addListener(e -> viewshed.setMinDistance(minDistanceSlider.getValue()));
    maxDistanceSlider.valueProperty().addListener(e -> viewshed.setMaxDistance(maxDistanceSlider.getValue()));
  }

  /**
   * Stops the animation and disposes of application resources.
   */
  void terminate() {

    if (sceneView != null) {
      sceneView.dispose();
    }
  }
}
