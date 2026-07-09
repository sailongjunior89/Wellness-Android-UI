package nus.iss.wellnessapp.notification
// Tan Pang Wee
import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object NotificationPermissionHelper {

    const val REQUEST_CODE = 100

    fun requestOrRun(
        activity: Activity,
        onGranted: () -> Unit
    ) {
        Log.d("PWT", "NotificationPermissionHelper requestOrRun")
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            onGranted()
            return
        }

        if (ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            onGranted()
        } else {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                REQUEST_CODE
            )
        }
    }

    fun handleResult(
        activity: Activity,
        requestCode: Int,
        grantResults: IntArray,
        onGranted: () -> Unit
    ) {
        if (requestCode == REQUEST_CODE &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("PWT", "NotificationPermissionHelper granted")
            onGranted()
        } else {
            Toast.makeText(
                activity,
                "Notification permission denied",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}