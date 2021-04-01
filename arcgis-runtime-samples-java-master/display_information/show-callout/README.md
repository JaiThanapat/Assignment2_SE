# Show callout

Show a callout with the latitude and longitude of user-clicked points.

![Image of show callout](ShowCallout.png)

## Use case

Callouts are used to display temporary detail content on a map. You can display text and arbitrary UI controls in callouts.

## How to use the sample

Click anywhere on the map. A callout showing the WGS84 coordinates for the clicked point will appear.

## How it works

1. Use `MapView.setOnMouseClicked()` to create a click event handler.
2. Capture the click event and use its x and y coordinates to create a new `Point2D` representing the screen point.
3. Get the screen point's location on the map using `MapView.screenToLocation(Point2D)`.
4. Get the `MapView`'s callout.
5. Set the title and detail of the callout to display the map point's coordinates.
6. Display the callout at the map point `callout.showCalloutAt(mapPoint)`.

## Relevant API

* Callout
* MapView
* Point

## Tags

balloon, bubble, callout, click, flyout, flyover, info window, popup