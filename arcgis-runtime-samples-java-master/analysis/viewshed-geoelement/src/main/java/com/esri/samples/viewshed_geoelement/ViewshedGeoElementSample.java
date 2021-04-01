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

package com.esri.samples.viewshed_geoelement;

import java.io.File;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import com.esri.arcgisruntime.geoanalysis.GeoElementViewshed;
import com.esri.arcgisruntime.geometry.AngularUnit;
import com.esri.arcgisruntime.geometry.AngularUnitId;
import com.esri.arcgisruntime.geometry.GeodeticCurveType;
import com.esri.arcgisruntime.geometry.GeodeticDistanceResult;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.LinearUnit;
import com.esri.arcgisruntime.geometry.LinearUnitId;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.ArcGISSceneLayer;
import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.ArcGISTiledElevationSource;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Surface;
import com.esri.arcgisruntime.mapping.view.AnalysisOverlay;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.LayerSceneProperties;
import com.esri.arcgisruntime.mapping.view.OrbitGeoElementCameraController;
import com.esri.arcgisruntime.mapping.view.SceneView;
import com.esri.arcgisruntime.symbology.ModelSceneSymbol;
import com.esri.arcgisruntime.symbology.Renderer;
import com.esri.arcgisruntime.symbology.SceneSymbol;
import com.esri.arcgisruntime.symbology.SimpleRenderer;

public class ViewshedGeoElementSample extends Application {

  private SceneView sceneView;
  private Graphic tank;
  private Timeline animation;
  private Point waypoint;

  private static final LinearUnit METERS = new LinearUnit(LinearUnitId.METERS);
  private static final AngularUnit DEGREES = new AngularUnit(AngularUnitId.DEGREES);

  @Override
  public void start(Stage stage) {

    try {

      // create stack pane and JavaFX app scene
      StackPane stackPane = new StackPane();
      Scene fxScene = new Scene(stackPane);

      // set title, size, and add JavaFX scene to stage
      stage.setTitle("Viewshed GeoElement Sample");
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
      stackPane.getChildren().addAll(sceneView);

      // add base surface for elevation data
      Surface surface = new Surface();
      final String localElevationImageService = "https://scene.arcgis.com/arcgis/rest/services/BREST_DTM_1M/ImageServer";
      surface.getElevationSources().add(new ArcGISTiledElevationSource(localElevationImageService));
      scene.setBaseSurface(surface);

      // add a scene layer
      final String buildings = "https://tiles.arcgis.com/tiles/P3ePLMYs2RVChkJx/arcgis/rest/services/Buildings_Brest/SceneServer/layers/0";
      ArcGISSceneLayer sceneLayer = new ArcGISSceneLayer(buildings);
      scene.getOperationalLayers().add(sceneLayer);

      // create a graphics overlay for the tank
      GraphicsOverlay graphicsOverlay = new GraphicsOverlay();
      graphicsOverlay.getSceneProperties().setSurfacePlacement(LayerSceneProperties.SurfacePlacement.RELATIVE);
      sceneView.getGraphicsOverlays().add(graphicsOverlay);

      // set up heading expression for tank
      SimpleRenderer renderer3D = new SimpleRenderer();
      Renderer.SceneProperties renderProperties = renderer3D.getSceneProperties();
      renderProperties.setHeadingExpression("[HEADING]");
      graphicsOverlay.setRenderer(renderer3D);

      // create a graphic of a tank
      String modelURI = new File(System.getProperty("data.dir"), "./samples-data/bradley_low_3ds/bradle.3ds").getAbsolutePath();
      ModelSceneSymbol tankSymbol = new ModelSceneSymbol(modelURI.replace("\\", "/"), 10.0);
      tankSymbol.setHeading(90);
      tankSymbol.setAnchorPosition(SceneSymbol.AnchorPosition.BOTTOM);
      tankSymbol.loadAsync();
      tank = new Graphic(new Point(-4.506390, 48.385624, SpatialReferences.getWgs84()), tankSymbol);
      tank.getAttributes().put("HEADING", 0.0);
      graphicsOverlay.getGraphics().add(tank);

      // create a viewshed to attach to the tank
      GeoElementViewshed geoElementViewshed = new GeoElementViewshed(tank, 90.0, 40.0, 0.1, 250.0, 0.0, 0.0);
      // offset viewshed observer location to top of tank
      geoElementViewshed.setOffsetZ(3.0);

      // create an analysis overlay to add the viewshed to the scene view
      AnalysisOverlay analysisOverlay = new AnalysisOverlay();
      analysisOverlay.getAnalyses().add(geoElementViewshed);
      sceneView.getAnalysisOverlays().add(analysisOverlay);

      // set the waypoint where the user clicks
      sceneView.setOnMouseClicked(e -> {
        if (e.isStillSincePress() && e.getButton() == MouseButton.PRIMARY) {
          // create a point from where the user clicked
          Point2D point = new Point2D(e.getX(), e.getY());

          // set the new waypoint
          waypoint = sceneView.screenToBaseSurface(point);
        }
      });

      // set camera controller to follow tank
      OrbitGeoElementCameraController cameraController = new OrbitGeoElementCameraController(tank, 200.0);
      cameraController.setCameraPitchOffset(45.0);
      sceneView.setCameraController(cameraController);

      // create a timeline to animate the tank
      animation = new Timeline();
      animation.setCycleCount(-1);
      animation.getKeyFrames().add(new KeyFrame(Duration.millis(100), e -> animate()));
      animation.play();

    } catch (Exception e) {
      // on any error, display the stack trace.
      e.printStackTrace();
    }
  }

  /**
   * Moves the tank toward the current waypoint a short distance.
   */
  private void animate() {
    if (waypoint != null) {
      // get current location and distance from waypoint
      Point location = (Point) tank.getGeometry();
      GeodeticDistanceResult distance = GeometryEngine.distanceGeodetic(location, waypoint, METERS, DEGREES,
          GeodeticCurveType.GEODESIC);

      // move toward waypoint a short distance
      location = GeometryEngine.moveGeodetic(location, 1.0, METERS, distance.getAzimuth1(), DEGREES,
          GeodeticCurveType.GEODESIC);
      tank.setGeometry(location);

      // rotate toward waypoint
      double heading = (double) tank.getAttributes().get("HEADING");
      tank.getAttributes().put("HEADING", heading + ((distance.getAzimuth1() - heading) / 10));

      // reached waypoint, stop moving
      if (distance.getDistance() <= 5) {
        waypoint = null;
      }
    }
  }

  /**
   * Stops and releases all resources used in application.
   */
  @Override
  public void stop() {

    // stop the animation
    animation.stop();

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
