package net.cattaka.android.learnscopedstorage

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import net.cattaka.android.learnscopedstorage.databinding.ActivityPathListBinding
import java.io.File


class PathListActivity : AppCompatActivity() {
    lateinit var binding: ActivityPathListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_path_list)

        createList()
    }

    private fun createList() {
        val pairs = mutableListOf<Pair<String, Any?>>(
            Pair("cacheDir", cacheDir),
            Pair("codeCacheDir", codeCacheDir),
            Pair("externalCacheDir", externalCacheDir),
            Pair("externalCacheDirs", externalCacheDirs),
            Pair("externalMediaDirs", externalMediaDirs),
            Pair("filesDir", filesDir),
            Pair("obbDir", obbDir),
            Pair("obbDirs", obbDirs)
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            pairs.add(Pair("dataDir", dataDir))
        }


        val text = pairs.joinToString("\n") { pair ->
            val paths = pair.second.let {
                when {
                    it is File -> it.absolutePath
                    it is Array<*> && it.isArrayOf<File>() -> {
                        it.joinToString("\n") { f -> (f as File).absolutePath }
                    }
                    else -> "???"
                }
            }
            "${pair.first}\n$paths\n"
        }

        binding.textView.text = text
    }
}
