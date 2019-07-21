import java.io.FileOutputStream

fun tempReadExif(context: Context) {
    val itemUri: Uri = Uri.parse("")
    val resolver = context.contentResolver

    resolver.openInputStream(itemUri)?.use { fin ->
        androidx.exifinterface.media.ExifInterface(fin).let { exif ->
            // ここでexifから位置情報（latLong）を取得する
            // Nullableであることに注意すること
            exif.latLong
        }
    }
}

fun tempReadFile(context: Context) {
    val itemUri: Uri = Uri.parse("")
    val resolver = context.contentResolver
    resolver.openInputStream(itemUri)?.use { fin ->
        // ここでファイルのデータを読み込む
    }
}

fun tempWriteFile(context: Context) {
    val resolver = context.contentResolver

    // ファイルの情報を準備
    val values = ContentValues().apply {
        put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/hoge")
        put(MediaStore.MediaColumns.DISPLAY_NAME, "fuga.png")
        put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
        put(MediaStore.MediaColumns.IS_PENDING, 1)
    }

    // Imagesの保存先のUriを取得
    val collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
    // Media ProviderへInsertを実行
    val itemUri = resolver.insert(collection, values)!!

    // 実際にファイルを書きk無
    resolver.openFileDescriptor(itemUri, "w")
        ?.let { parcelFileDescriptor ->
            FileOutputStream(parcelFileDescriptor.fileDescriptor).use { fout ->
                // ここでファイルのデータを書き込む
            }
        }

    // Pending状態を解除する
    values.clear()
    values.put(MediaStore.MediaColumns.IS_PENDING, 0)
    resolver.update(itemUri, values, null, null)
}
