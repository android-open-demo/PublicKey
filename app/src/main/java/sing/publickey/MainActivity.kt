package sing.publickey

import android.content.ClipData
import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    lateinit var etContent:EditText
    lateinit var tvGet:TextView
    lateinit var tvCopy:TextView
    lateinit var tvHint:TextView

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etContent = findViewById(R.id.et_content)
        tvGet = findViewById(R.id.tv_get)
        tvCopy = findViewById(R.id.tv_copy)
        tvHint = findViewById(R.id.tv_hint)
        tvCopy.visibility = View.GONE
        tvCopy.setOnClickListener {
            val cm = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager

            // 将要复制的数据放到一个剪贴对象中
            val clipData = ClipData.newPlainText("", tvHint.text)

            // 设置数据为敏感内容，则在剪贴板中文本会显示为星号
            clipData.description.extras = PersistableBundle().apply {
                putBoolean(ClipDescription.EXTRA_IS_SENSITIVE, true)
            }

            // 把剪贴对象放到剪切板中
            cm.setPrimaryClip(clipData)

            Toast.makeText(this, "已复制", Toast.LENGTH_SHORT).show()
        }

        tvGet.setOnClickListener {
            val content = etContent.text.trim().toString()
            if (content.isEmpty()){
                Toast.makeText(this,"请输入包名",Toast.LENGTH_SHORT).show()
            }else{
               getPublicKey(content)
            }
        }
    }

    private fun getPublicKey(packageName:String){
        try {
            // 替换 "your.package.name" 为您的应用包名
            val packageName = packageName

            // 获取应用程序的PackageInfo，包括签名信息
            val packageInfo =  packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)

            // 获取应用程序的签名信息
            val signatures: Array<Signature> = packageInfo.signatures

            // 获取第一个签名证书
            val signature: Signature = signatures[0]

            // 获取签名证书的字节数组
            val signatureBytes: ByteArray = signature.toByteArray()

            // 使用Base64编码将字节数组转换为字符串表示
            val base64Signature: String = Base64.encodeToString(signatureBytes, Base64.DEFAULT)

            // 公钥信息
            tvCopy.visibility = View.VISIBLE
            tvHint.text = base64Signature
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            tvCopy.visibility = View.GONE
            tvHint.text = "请确保你已安装该应用"
        } catch (e: Exception) {
            e.printStackTrace()
            tvCopy.visibility = View.GONE
            tvHint.text = e.message
        }
    }
}