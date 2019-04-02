
package tk.alltrue.mediaplayer;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.OpenableColumns;

import java.net.URISyntaxException;

import tk.alltrue.mediaplayer.MainActivity;

public class PathUtil {


    public static String getPath(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            System.out.println("getPath() uri: " + uri.toString());
            System.out.println("getPath() uri authority: " + uri.getAuthority());
            System.out.println("getPath() uri path: " + uri.getPath());

            // ExternalStorageProvider
            //     if ("com.android.externalstorage.documents".equals(uri.getAuthority())) {
            final String docId = DocumentsContract.getDocumentId(uri);
            final String[] split = docId.split(":");
            final String type = split[0];
            System.out.println("getPath() docId: " + docId + ", split: " + split.length + ", type: " + type);

            // This is for checking Main Memory
            if ("primary".equalsIgnoreCase(type)) {
                if (split.length > 1) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1] + "/";
                } else {
                    return Environment.getExternalStorageDirectory() + "/";
                }
                // This is for checking SD Card
            } else {
                return "storage" + "/" + docId.replace(":", "/");
            }

        }
        //   }
        return null;
    }

    public static String getFileName(Uri uri, Context context) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = (context).getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
}
