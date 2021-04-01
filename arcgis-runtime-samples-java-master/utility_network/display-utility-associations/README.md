# Display utility associations

Create graphics for utility associations in a utility network.

![Image of display utility associations](DisplayUtilityAssociations.png)

## Use case

Visualizing utility associations can help you to better understand trace results and the topology of your utility network. For example, connectivity associations allow you to model connectivity between two junctions that don't have geometric coincidence (are not in the same location); structural attachment associations allow you to model equipment that may be attached to structures; and containment associations allow you to model features contained within other features.

## How to use the sample

Pan and zoom around the map. Observe graphics that show utility associations between junctions.

## How it works

1. Create and load a `UtilityNetwork` with a feature service URL.
2. Add a `FeatureLayer` to the map for every `UtilityNetworkSource` of type `EDGE` or `JUNCTION`.
3. Create a `GraphicsOverlay` for the utility associations.
4. Add a `NavigationChangedListener` to listen for `NavigationChangedEvent`s.
5. When the sample starts and every time the viewpoint changes:
    * Get the geometry of the map view's extent.
    * Get the associations that are within the current extent using `getAssociationsAsync(extent)`.
    * Get the `UtilityAssociationType` for each association.
    * Create a `Graphic` using the `Geometry` property of the association and a preferred symbol.
    * Add the graphic to the graphics overlay.

## Relevant API

* GraphicsOverlay
* UtilityAssociation
* UtilityAssociationType
* UtilityNetwork

## About the data

The [Naperville electrical](https://sampleserver7.arcgisonline.com/server/rest/services/UtilityNetwork/NapervilleElectric/FeatureServer) network feature service, hosted on ArcGIS Online, contains a utility network used to display the utility associations in this sample.

## Tags

associating, association, attachment, connectivity, containment, relationships
