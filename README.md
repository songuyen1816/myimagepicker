# Simple image picker [![](https://jitpack.io/v/songuyen1816/myimagepicker.svg)](https://jitpack.io/#songuyen1816/myimagepicker)
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
	implementation 'com.github.songuyen1816:myimagepicker:latest_version'
}
```
## Usage
- Simple using
```
MyImagePicker.getInstance().start(activity);
```
```
MyImagePicker.getInstance().takePhoto(activity);
```
```
MyImagePicker.getInstance().pickImage(activity);
```
- Customize
```
PickerConfig config = new PickerConfig.Builder()
		.allowPickVideo(true)
                .setMaxCount(2)
		.setCompressed(true)
                .setPickerTitle("Pick images / videos")
                .setStyleColor(Color.parseColor("#3498db")).build();
                
MyImagePicker.getInstance().setPickerConfig(config).start(activity);
```
- Get results
```
@Override
protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
     super.onActivityResult(requestCode, resultCode, data);
     if (requestCode == PickerConfig.IMAGE_PICKER_REQUEST && resultCode == RESULT_OK) {
         if (data != null) {
             List<String> filePath = data.getStringArrayListExtra(PickerConfig.FILE_PATH_DATA);
                
         }
     }
}
```

## Thank you
