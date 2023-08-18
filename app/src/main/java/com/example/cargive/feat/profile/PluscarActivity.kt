package com.example.cargive.feat.profile

import android.content.ContentValues
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ImageDecoder
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.example.cargive.R
import com.example.cargive.databinding.ActivityPluscarBinding
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.math.max

class PluscarActivity : AppCompatActivity() {
    private val binding by lazy { ActivityPluscarBinding.inflate(layoutInflater) }
    val REQUEST_IMAGE_CAPTURE = 1
    var picture_flag = 0
    val max_card=4
    var car_model:String=""
    var car_number:String=""
    var car_distance:String=""
    var var_img:Bitmap?=null
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
                            var_img=bitmap
                        }
                        else{
                            val decode=ImageDecoder.createSource(this.contentResolver,
                                Uri.fromFile(file.absoluteFile))
                            bitmap=ImageDecoder.decodeBitmap(decode)
                            binding.callImg.setImageBitmap(bitmap)
                            var_img=bitmap
                        }
                        if(bitmap!=null){
                            saveImageFile(file.name,getExtension(file.name),bitmap)
                        }
                    }
                }
            }
        binding.backItem.setOnClickListener {finish()}
        binding.callImg.setOnClickListener {
            visibleChoose()
        }
        binding.chooseCamera.setOnClickListener{
            requestPermission()
            goneChoose()

        }
        binding.chooseGallary.setOnClickListener{
            requestGallary()
            goneChoose()
        }
        binding.closeBtn.setOnClickListener {
            goneChoose()
        }
        binding.plusCard.setOnClickListener {
            visibleCard()
        }
        binding.closeB.setOnClickListener {
            goneCard()
        }
        binding.addMycar.setOnClickListener {
        }
        binding.carAttribute.addTextChangedListener(object :TextWatcher{
            var maxText=""
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                maxText=binding.carAttribute.text.toString()
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(binding.carAttribute.length()==0){
                    binding.sendCard.setBackgroundResource(R.drawable.add_card)
                }
                else{
                    binding.sendCard.setBackgroundResource(R.drawable.add_cardok)
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })
        binding.sendCard.setOnClickListener {
            val string=binding.carAttribute.text.toString()
            if(string.isNullOrEmpty()){
                Toast.makeText(this,"특징을 입력해 주세요",Toast.LENGTH_SHORT).show()
            }else{
                val chipCount=binding.cardGroup.childCount
                if(chipCount<max_card){
                    val chip = Chip(this, null, R.style.ChipStyle)
                    chip.text=string
                    chip.chipBackgroundColor= ColorStateList.valueOf(Color.rgb(0, 128,245))
                    chip.isCloseIconVisible=true
                    chip.setTextColor(Color.WHITE)
                    chip.setTextSize(TypedValue.COMPLEX_UNIT_SP,14f)
                    chip.apply { setOnCloseIconClickListener{binding.cardGroup.removeView(this)} }
                    val layoutParams= ChipGroup.LayoutParams(
                        ChipGroup.LayoutParams.WRAP_CONTENT,
                        ChipGroup.LayoutParams.WRAP_CONTENT
                    )
                    chip.layoutParams=layoutParams
                    binding.cardGroup.addView(chip)
                    goneCard()
                    binding.carAttribute.setText("")
                }
                else{
                    Toast.makeText(this,"최대 $max_card 개의 특징을 추가할 수 있습니다",Toast.LENGTH_SHORT).show()
                    goneCard()
                }
            }
        }
        var isINfoVisible=false
        var handler:Handler= Handler(Looper.getMainLooper())
        binding.infoCard.setOnTouchListener { v:View, event:MotionEvent ->
            when(event.action){
                MotionEvent.ACTION_DOWN->{
                    binding.infoShow.visibility=View.VISIBLE
                    isINfoVisible=true
                    handler.postDelayed({
                        binding.infoShow.visibility=View.GONE
                        isINfoVisible=false
                    },3000)

                }
            }
            true
        }
        resultLauncher_g=
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
                if(it.resultCode== RESULT_OK){
                    it.data?.data?.let { uri ->
                        val imageUri:Uri?=it.data?.data
                        if(imageUri!=null){
                            Glide.with(applicationContext).load(imageUri).override(500,500)
                                .into(binding.callImg)
                            var_img=getBitmapFromView(binding.callImg)
                            /*Handler(Looper.getMainLooper()).postDelayed({var_img=getBitmapFromView(binding.callImg)
                                                                        Log.d("img","$var_img")
                                                                        binding.callImg.setImageDrawable(null)},2000)
                            Handler(Looper.getMainLooper()).postDelayed({binding.callImg.setImageBitmap(var_img)},4000)*/
                        }
                    }
                }
            }

    }

    private fun getBitmapFromView(view: ImageView): Bitmap? {
        if(view.drawable is BitmapDrawable) {
            val bitmap = (view.drawable as BitmapDrawable).bitmap
            return bitmap
        }
        return null
    }


    private fun goneCard() {
        binding.addMycar.visibility=View.VISIBLE
        binding.addAttribute.visibility=View.GONE
    }

    private fun visibleCard() {
        binding.addMycar.visibility=View.GONE
        binding.addAttribute.visibility=View.VISIBLE
    }

    private fun goneChoose() {
        binding.chooseBtn.visibility=View.GONE
        binding.addMycar.visibility=View.VISIBLE
    }

    private fun visibleChoose() {
        binding.chooseBtn.visibility=View.VISIBLE
        binding.addMycar.visibility=View.GONE
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