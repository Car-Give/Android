package com.example.cargive.feat.profile

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.example.cargive.R
import com.example.cargive.databinding.ActivityPluscarBinding
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date

class PluscarActivity : AppCompatActivity() {
    private val binding by lazy { ActivityPluscarBinding.inflate(layoutInflater) }
    val REQUEST_IMAGE_CAPTURE = 1
    var picture_flag = 0
    companion object {
        lateinit var prefs: Carfile
    }
    lateinit var resultLauncher: ActivityResultLauncher<Intent>
    lateinit var resultLauncher_g: ActivityResultLauncher<Intent>
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        prefs = Carfile(applicationContext)
        resultLauncher=
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
                if(it.resultCode== RESULT_OK){
                    Log.d("원하는 거",prefs.getImgAddress(""))
                    if(prefs.getImgAddress("").isNotBlank()){
                        val file=File(prefs.getImgAddress(""))
                        var bitmap: Bitmap?=null
                        if(Build.VERSION.SDK_INT<28){
                            bitmap=MediaStore.Images.Media
                                .getBitmap(contentResolver,Uri.fromFile(file))
                            binding.callImg.setImageBitmap(bitmap)
                        }
                        else{
                            val decode=ImageDecoder.createSource(this.contentResolver,
                                Uri.fromFile(file.absoluteFile))
                            bitmap=ImageDecoder.decodeBitmap(decode)
                            binding.callImg.setImageBitmap(bitmap)
                        }
                        if(bitmap!=null){
                            saveImageFile(file.name,getExtension(file.name),bitmap)
                        }
                    }
                }
            }
        binding.backItem.setOnClickListener {finish()}
        binding.callImg.setOnClickListener {
            //requestPermission()
            requestGallary()
        }
        resultLauncher_g=
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
                if(it.resultCode== RESULT_OK){
                    it.data?.data?.let { uri ->
                        val imageUri:Uri?=it.data?.data
                        if(imageUri!=null){
                            Glide.with(applicationContext).load(imageUri).override(500,500)
                                .into(binding.callImg)
                        }
                    }
                }
            }

    }

    private fun requestGallary() {
        val permission= object : PermissionListener {
            override fun onPermissionGranted() {
                //Toast.makeText(this@PluscarActivity,"갤러리 허용",Toast.LENGTH_SHORT).show()
                move_gallery()
            }

            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                Toast.makeText(this@PluscarActivity, "갤러리 권한이 거부 되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }
        TedPermission.create()
            .setPermissionListener(permission)
            .setDeniedMessage("갤러리 권한을 허용해 주세요")
            .setPermissions(android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE)
            .check()
    }

        private fun move_gallery() {
        val intent=Intent(Intent.ACTION_PICK)
        intent.data=MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        intent.type="image/*"
        resultLauncher_g.launch(intent)
    }

    private fun saveImageFile(filename: String, mimeType: String, bitmap: Bitmap):Uri? {
        var values=ContentValues()
        values.put(MediaStore.Images.Media.DISPLAY_NAME,filename)
        values.put(MediaStore.Images.Media.MIME_TYPE,mimeType)
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q){
            values.put(MediaStore.Images.Media.IS_PENDING,1)
        }
        val uri=contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values)
        try {
            if(uri!=null){
                val descriptor=contentResolver.openFileDescriptor(uri,"w")
                if(descriptor!=null){
                    val fos=FileOutputStream(descriptor.fileDescriptor)
                    bitmap.compress(Bitmap.CompressFormat.JPEG,100,fos)
                    fos.close()
                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q){
                        values.clear()
                        values.put(MediaStore.Images.Media.IS_PENDING,0)
                        contentResolver.update(uri,values,null,null)
                    }
                }
            }
        } catch (e: java.lang.Exception){
            Log.e("File","error=")
        }
        return uri
    }

    private fun getExtension(fileStr: String): String {
        val fileExtension = fileStr.substring(fileStr.lastIndexOf(".")+1,fileStr.length);
        return fileExtension
    }

    private fun takePhoto() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                val photoFile: File? =try {
                    createImageFile()
                }catch (ex:IOException){
                    null
                }
                photoFile?.also {
                    val photoURI: Uri=FileProvider.getUriForFile(
                        this,
                        "com.example.cargive.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,photoURI)
                    resultLauncher.launch(takePictureIntent)
                }
            }
            picture_flag=2
        }
    }

    private fun createImageFile(): File? {
        val timestamp:String= SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDIR:File? =getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        //Log.d("ㄱ",File.createTempFile("JPEG_${timestamp}_",".jpg",storageDIR).absolutePath)
        return File.createTempFile("JPEG_${timestamp}_",".jpg",storageDIR)
            .apply { prefs.setImgAddress(absolutePath) }
    }

    private fun requestPermission(){
        val permission= object : PermissionListener {
            override fun onPermissionGranted() {
                //Toast.makeText(this@PluscarActivity,"카메라 시작",Toast.LENGTH_SHORT).show()
                takePhoto()
            }

            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                Toast.makeText(this@PluscarActivity, "카메라 권한이 거부 되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }
        TedPermission.create()
            .setPermissionListener(permission)
            .setDeniedMessage("카메라 권한을 허용해 주세요.")
            .setPermissions(android.Manifest.permission.CAMERA)
            .check()
    }
    fun startProcess(){
        takePhoto()
    }
}