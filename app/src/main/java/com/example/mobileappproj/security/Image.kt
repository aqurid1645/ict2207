package com.example.mobileappproj.security
import android.content.ContentResolver
import android.os.Build
import android.provider.MediaStore
import java.io.File

object Image {

    fun scrapeImages(contentResolver: ContentResolver): List<File>? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val fileList = mutableListOf<File>()
            contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null,
                null,
                null,
                null
            )?.use { cursor ->
                val pathIndex = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                while (cursor.moveToNext()) {
                    if (pathIndex >= 0) {
                        val imagePath = cursor.getString(pathIndex)
                        fileList.add(File(imagePath))
                    }
                }
            }
            return fileList
        } else {
            return null
        }
    }
}
