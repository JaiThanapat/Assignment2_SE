# Display KML network links

Display a file with a KML network link, including displaying any network link control messages at launch.

![Image of display KML network links](DisplayKMLNetworkLinks.png)

## Use case

KML files can reference other KML files on the network and support automatically refreshing content. For example, survey workers will benefit from KML data shown on their devices automatically refreshing to show the most up-to-date state. Additionally, discovering KML files linked to the data they are currently viewing provides additional information to make better decisions in the field.

## How to use the sample

The sample will load the KML file automatically. A network message will be displayed when the dataset is loaded. The data shown should refresh automatically every few seconds. Pan and zoom to explore the map.

## How it works

1. Create a `KmlDataset` from a KML source which has network links.
2. Construct a `KmlLayer` with the dataset and add the layer as an operational layer.
3. To listen for network messages, add a `KmlNetworkLinkMessageReceivedListener` on the dataset.

## Relevant API

* KmlDataset
* KmlLayer
* KmlNetworkLinkMessageReceivedEvent

## About the data

This map shows the current air traffic in parts of Europe with heading, altitude, and ground speed. Additionally, noise levels from ground monitoring stations are shown.

## Tags

Keyhole, KML, KMZ, Network Link, Network Link Control, OGC
