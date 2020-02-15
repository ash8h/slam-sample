package jp.ash8h.slam

import android.Manifest
import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.opencv.android.CameraBridgeViewBase
import org.opencv.android.OpenCVLoader
import org.opencv.core.Core
import org.opencv.core.Mat
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.OnPermissionDenied
import permissions.dispatcher.RuntimePermissions
import timber.log.Timber

@RuntimePermissions
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (!OpenCVLoader.initDebug()) {
            Timber.e("OpenCVLoader.initDebug() failed.")
            Toast.makeText(this, "OpenCV init failed", Toast.LENGTH_SHORT).show()
            finish()
        }

        initCameraWithPermissionCheck()
    }

    override fun onDestroy() {
        super.onDestroy()
        camera_view.disableView()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }

    @NeedsPermission(Manifest.permission.CAMERA)
    fun initCamera() {
        camera_view.setCvCameraViewListener(object : CameraBridgeViewBase.CvCameraViewListener2 {
            override fun onCameraViewStarted(width: Int, height: Int) {
                Timber.d("onCameraViewStarted: width${width}, height=${height}")
            }

            override fun onCameraViewStopped() {
                Timber.d("onCameraViewStopped")
            }

            override fun onCameraFrame(inputFrame: CameraBridgeViewBase.CvCameraViewFrame?): Mat {
                val mat = requireNotNull(inputFrame).rgba()
                Core.bitwise_not(mat, mat)
                return mat
            }
        })

        camera_view.setCameraPermissionGranted()
        camera_view.enableView()
    }

    @OnPermissionDenied(Manifest.permission.CAMERA)
    fun onCameraDenied() {
        Toast.makeText(this, "CAMERA permission is required", Toast.LENGTH_SHORT).show()
        setResult(Activity.RESULT_CANCELED)
        finish()
    }
}
