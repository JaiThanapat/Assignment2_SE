# Display device location with autopan modes

Display your position on the map and switch between different types of autopan modes.

![Image of display device location with autopan modes](DisplayDeviceLocationWithAutopanModes.gif)

## Use case

When using a map within a GIS, it may be helpful for a user to know their own location within a map, whether that's to aid the user's navigation or to provide an easy means of identifying/collecting geospatial information at their location.

## How to use the sample

The sample loads with a symbol marking a simulated location on the map. Explore the available autopan modes by selecting an option from the drop down box:

* Off - Shows the location with no autopan mode set.
* Re-Center - In this mode, the map re-centers on the location symbol when the symbol moves outside a "wander extent".
* Navigation -  This mode is best suited for in-vehicle navigation.
* Compass - This mode is better suited for waypoint navigation when the user is walking.

Uncheck the box to stop the location display.

## How it works

1. Create a `MapView`.
2. Get the `LocationDisplay` object by calling `getLocationDisplay()` on the map view.
3. Create a `SimulatedLocationDataSource` and call its `setLocations()` method, passing the route `Polyline` and new `SimulationParameters` as parameters.
4. Start the location display with `startAsync()` to begin receiving location updates.
5. Use `locationDisplay.setAutoPanMode()` to change how the map view behaves when location updates are received.

## Relevant API

* LocationDisplay
* LocationDisplay.AutoPanMode
* MapView
* SimulatedLocationDataSource
* SimulationParameters

## Additional information

A custom set of points (provided in JSON format) is used to create a `Polyline` and configure a `SimulatedLocationDataSource`. The simulated location data source enables easier testing and allows the sample to be used on devices without an actively updating GPS signal. To display a user's real position, use `NMEALocationDataSource` instead.

## Tags

compass, GPS, location, map view, mobile, navigation