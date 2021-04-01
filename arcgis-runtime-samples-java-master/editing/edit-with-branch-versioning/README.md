# Edit with branch versioning

Create, query and edit a specific server version using service geodatabase.

![Image of edit with branch versioning](EditWithBranchVersioning.png)

## Use case

Workflows often progress in discrete stages, with each stage requiring the allocation of a different set of resources and business rules. Typically, each stage in the overall process represents a single unit of work, such as a work order or job. To manage these, you can create a separate, isolated version and modify it. Once this work is complete, you can integrate the changes into the default version.

## How to use the sample

Once loaded, the map will zoom to the extent of the feature layer. The current version is indicated in the top left corner of the map. You can create a new version by specifying the version information (name, access, and description) in the form in the top right corner, and then clicking "Create version". See the *Additional Information* section for restrictions on the version name.

Select a feature using the primary mouse button to edit an attribute and/or click again with the secondary mouse button to relocate the point.

Click the "Switch version" button in the top left corner to switch back and forth between the version you created and the default version. Edits will automatically be applied to your version when switching to the default version.

## How it works

1. Create and load a `ServiceGeodatabase` with a feature service URL that has enabled [Version Management](https://pro.arcgis.com/en/pro-app/latest/help/data/geodatabases/overview/publish-branch-versioned-data.htm).
2. Get the `ServiceFeatureTable` from the service geodatabase.
3. Create a `FeatureLayer` from the service feature table.
4. Create `ServiceVersionParameters` with a unique name, `VersionAccess`, and description.
    * Note - See the additional information section for more restrictions on the version name.
5. Create a new version by calling `ServiceGeodatabase.createVersionAsync()` and passing in the service version parameters.
6. Retrieve the result of the async call, to obtain the `ServiceVersionInfo` of the version created.
7. Switch to the version you have just created using `ServiceGeodatabase.switchVersionAsync()`, passing in the version name obtained from the service version info.
8. Select a `Feature` from the map to edit its "TYPDAMAGE" attribute and location.
9. Apply these edits to your version by calling `ServiceGeodatabase.applyEditsAsync()`.

## Relevant API

* FeatureLayer
* ServiceFeatureTable
* ServiceGeodatabase
* ServiceVersionInfo
* ServiceVersionParameters
* VersionAccess

## About the data

The feature layer used in this sample is [Damage to commercial buildings](https://sampleserver7.arcgisonline.com/server/rest/services/DamageAssessment/FeatureServer/0) located in Naperville, Illinois.

## Additional information

The name of the version must meet the following criteria:

1. Must not exceed 62 characters
2. Must not include: Period (.), Semicolon (;), Single quotation mark ('), Double quotation mark (")
3. Must not include a space for the first character

**Note**: the version name will have the username and a period (.) prepended to it. E.g "editor01.MyNewUniqueVersionName"

Branch versioning access permission:

1. Public - Any portal user can view and edit the version.
2. Protected - Any portal user can view, but only the version owner, feature layer owner, and portal administrator can edit the version.
3. Private - Only the version owner, feature layer owner, and portal administrator can view and edit the version.

## Tags

branch versioning, edit, version control, version management server
