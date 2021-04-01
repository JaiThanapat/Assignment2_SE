# Vector tiled layer URL

Load an ArcGIS vector tiled layer from a URL.

![Image of Vector tiled layer url](VectorTiledLayerURL.png)

## Use case

Vector tile basemaps can be created in ArcGIS Pro and published as offline packages or online services. An ArcGIS vector tiled layer has many advantages over traditional raster based basemaps (ArcGIS tiled layer), including smooth scaling between different screen DPIs, smaller package sizes, and the ability to rotate symbols and labels dynamically.

## How to use the sample

Select one of the vector tile basemaps from the list view to see it loaded in the map view.

## How it works

1. Construct an `ArcGISVectorTiledLayer` with an ArcGIS Online service URL.
2. Instantiate a new `Basemap` passing in the vector tiled layer as a parameter.
3. Create a new `ArcGISMap` object by passing in the basemap as a parameter.

## Relevant API

* ArcGISVectorTiledLayer
* Basemap

## Tags

tiles, vector, vector basemap, vector tiled layer, vector tiles
