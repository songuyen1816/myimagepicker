# Simple image picker [![](https://jitpack.io/v/songuyen1816/myimagepicker.svg)](https://jitpack.io/#songuyen1816/myimagepicker)
Just a simple library to help you pick images in external storage, it also allows take photo from camera and pick videos from gallery, give a star if you find it useful

<p float="left">
<img src="https://i.postimg.cc/5xQJPz16/z3277208639433-2d02baf37adfcf73d289de8064c06875.jpg" width="300" height="600"/>
<img src="https://i.postimg.cc/brRCNP9R/z3278319799795-0f02c5c5ea5b38cf2eed1043953151f2-1.jpg" width="300" height="600"/>
<img src="https://i.postimg.cc/1mxQbz5M/z3277208344474-e0fe2bf7c04c1a0cba7b418504de3cbb.jpg" width="300" height="600"/>
</p>

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

- Both take photo from camera and pick image from gallery
```
MyImagePicker.getInstance().start(activity);
```
- Just take photo from camera
```
MyImagePicker.getInstance().takePhoto(activity);
```
- Just pick image from gallery
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
