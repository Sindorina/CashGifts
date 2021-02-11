package com.sin.cashgift

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.PopupWindow
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.sin.cashgift.adapter.CashAdapter
import com.sin.cashgift.data.CashBean
import com.sin.cashgift.databinding.ActivityMainBinding
import com.sin.cashgift.databinding.SelectWindowBinding
import com.sin.cashgift.utils.FileUtils
import com.sin.cashgift.utils.InjectUtils
import com.sin.cashgift.utils.LogUtil
import com.sin.cashgift.viewmodels.CashViewModel

class MainActivity : AppCompatActivity() {
    private val cashViewModel by viewModels<CashViewModel> {
        InjectUtils.getCashViewModelFactory(this)
    }
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
                this, R.layout.activity_main)
        checkPermission()
    }


    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initData()
        }
    }


    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val state = ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            if (state != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        1
                )
            } else {
                initData()
            }
        } else {
            initData()
        }
    }

    private var hasAfterInitCalled = false
    private fun initData() {
        if (hasAfterInitCalled) return
        hasAfterInitCalled = true
        val adapter = CashAdapter()
        binding.cashList.adapter = adapter
        subscribeUi(adapter)
//        binding.btn.setOnClickListener {
//            val intent = Intent(Intent.ACTION_GET_CONTENT)
//            intent.type = "*/*"//无类型限制
//            intent.addCategory(Intent.CATEGORY_OPENABLE)
//            startActivityForResult(intent, 1000)
//        }
        binding.name.setOnClickListener {
            if (cashViewModel.keyword.value != CashViewModel.NO_KEY_WORD) {
                cashViewModel.keyword.value = CashViewModel.NO_KEY_WORD
                binding.name.text = "姓名(全部)"
            } else {
                showWindow()
            }

        }
    }

    private fun subscribeUi(adapter: CashAdapter) {
        //初始化
        cashViewModel.getStoreCashList()
        cashViewModel.cashList.observe(this, {
            if (!it.isNullOrEmpty()) {
                Log.e("tag", "数据: $it")
                adapter.submitList(it)
                setMoneyText(it)
            }
        })
    }

    private fun setMoneyText(list: List<CashBean>) {
        var num = 0
        list.forEach {
            num += it.money.toInt()
        }
        binding.cash.text = "礼金(${num})"
    }

    private var path = ""

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        data?.let {
            if (resultCode == Activity.RESULT_OK) {
                val uri = it.data
                uri?.let { u ->
                    if ("file".equals(uri.scheme, ignoreCase = true)) { //使用第三方应用打开
                        path = u.path ?: ""
                        return
                    }
                    path = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) { //4.4以后
                        FileUtils.getPath(this, u) ?: ""
                    } else { //4.4以下下系统调用方法
                        FileUtils.getRealPathFromURI(u, this) ?: ""
                    }
                }
                if (path.endsWith(".xls") || path.endsWith(".xlsx")) {
                    FileUtils.copyFile(path) {
                        LogUtil.logE("TAG", "文件拷贝完成!")
                    }
                }
                LogUtil.logE("TAG", "选择文件path:  $path")
            }
        }
    }

    private var selectWindow: PopupWindow? = null
    private fun showWindow() {
        if (selectWindow == null) {
            val windowBinding = SelectWindowBinding.inflate(LayoutInflater.from(this))
            selectWindow = PopupWindow(windowBinding.root, 600, 340)
            selectWindow?.isFocusable = true
            val editText = windowBinding.keyword
            windowBinding.submit.setOnClickListener {
                val key = editText.text.toString()
                if (key.isNotEmpty()) {
                    cashViewModel.keyword.value = key
                    binding.name.text = "姓名(${key})"
                    selectWindow?.dismiss()
                } else {
                    Toast.makeText(this, "请输入名字中包含的字!", Toast.LENGTH_SHORT).show()
                }
            }
        }
        selectWindow?.showAtLocation(binding.main, Gravity.CENTER, 0, 0)
    }
}