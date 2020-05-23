# Simple image picker [![](https://jitpack.io/v/hscoraline/myimagepicker.svg)](https://jitpack.io/#hscoraline/myimagepicker)
Just a simple library to help you pick images in external storage, give a star if you find it useful
## Get it
```
allprojects {
	repositories {
		maven { url 'https://jitpack.io' }
	}
}
```
```
dependencies {
	implementation 'com.github.hscoraline:myimagepicker:latest_version'
}
```
## Usage
-Simple using
```
MyImagePicker.getInstance().start(activity);
```
-Customize
```
PickerConfig config = new PickerConfig.Builder()
                .setMaxCount(2)
                .setPickerTitle("Pick your images")
                .setStyleColor(Color.parseColor("#3498db")).build();
                
MyImagePicker.getInstance().setPickerConfig(config).start(activity);
```
## Thank you