package net.cattaka.android.learnscopedstorage

import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.cattaka.android.learnscopedstorage.adapter.OperationInfoAdapter
import net.cattaka.android.learnscopedstorage.data.OperationInfo
import net.cattaka.android.learnscopedstorage.data.OperationTarget
import net.cattaka.android.learnscopedstorage.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var adapter: OperationInfoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        adapter = OperationInfoAdapter(mutableListOf(
                OperationInfo(
                        "Image direct",
                        "${Environment.getExternalStorageDirectory().path}/image_direct.png",
                        OperationTarget.IMAGE
                )
        ))

        binding.recyclerView.apply {
            this.layoutManager = LinearLayoutManager(this@MainActivity, RecyclerView.VERTICAL, false)
            this.adapter = adapter
        }
    }
}
