# PictureLoader-Android
이미지를 카메라 또는 앨범에서 불러와서 이미지를 자르고 URI를 콜백으로 반환하는 다이얼로그 라이브러리


사용법

build.gradle에서 com.khgkjg12.pictureloader:pictureloader:0.1.0 을 dependencies에 추가. 기존의 dependency와 충돌이 일어난다면 해당 라이브러리를 높은 버전으로 업데이트하거나 그 라이브러리를 빼고 빌드하면 된다.

PictureLoaderDialog.OnLoadPictureListener을 구현한 엑티비티나 프레그먼트에서 PictureLoaderDialog를 호출해 주면된다. 그러면 이미지의 URI를 리스너 콜백함수의 매개변수로 받을 수 있다.
