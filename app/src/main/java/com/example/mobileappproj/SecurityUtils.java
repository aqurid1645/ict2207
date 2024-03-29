package com.example.mobileappproj;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import androidx.security.crypto.MasterKey;
import androidx.security.crypto.EncryptedSharedPreferences;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class SecurityUtils {
    private static final String TAG = "SecurityUtils"; // TAG for logging
    public static boolean verifyDexIntegrity(Context context, String expectedDexHash) {
        try {
            String sourceDir = context.getPackageManager().getApplicationInfo(context.getPackageName(), 0).sourceDir;
            ZipFile zf = new ZipFile(sourceDir);
            ZipEntry dexEntry = zf.getEntry("classes.dex");

            InputStream is = new BufferedInputStream(zf.getInputStream(dexEntry));
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] buffer = new byte[1024];
            int read;
            while ((read = is.read(buffer)) > 0) {
                digest.update(buffer, 0, read);
            }

            byte[] hash = digest.digest();
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }

            is.close();
            zf.close();

            Log.d(TAG, "Computed DEX Hash: " + hexString);

            if (hexString.toString().equalsIgnoreCase(expectedDexHash)) {
                Log.d(TAG, "DEX hash verification PASSED.");
                return true;
            }
        } catch (Exception e) {
            Log.e(TAG, "DEX hash verification FAILED.", e);
        }
        Log.d(TAG, "DEX hash verification FAILED.");
        return false;
    }

}
