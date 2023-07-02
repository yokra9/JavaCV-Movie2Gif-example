# JavaCV-Movie2Gif-example

[JavaCV](https://github.com/bytedeco/javacv) を利用して動画をアニメーション GIF に変換するサンプルです。

```bash
# ./big_buck_bunny.mp4 を 640x360, 5fps のアニメーション GIF に変換する
mvn clean compile exec:java
```

```bash
# コンパイルして実行する
mvn clean compile dependency:copy-dependencies 
java -cp "target/classes/:./target/dependency/*" Movie2Gif big_buck_bunny.mp4 640 360 5
```