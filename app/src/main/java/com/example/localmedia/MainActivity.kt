package com.example.localmedia

import android.app.*
import android.os.Bundle
import android.content.*
import android.net.Uri
import android.view.*
import android.widget.*
import android.graphics.Color

class MainActivity : Activity() {
    private lateinit var grid: GridView
    private val prefs by lazy { getSharedPreferences("library", MODE_PRIVATE) }
    private val items = mutableListOf<Uri>()
    private val PICK = 100

    override fun onCreate(b: Bundle?) {
        super.onCreate(b)
        setContentView(R.layout.activity_main)
        grid = findViewById(R.id.grid)
        load()
        findViewById<Button>(R.id.addButton).setOnClickListener { pick() }
        refresh()
    }
    private fun pick() {
        val i = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "*/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        startActivityForResult(i, PICK)
    }
    override fun onActivityResult(r:Int, c:Int, d:Intent?) {
        super.onActivityResult(r,c,d)
        if (r != PICK || c != RESULT_OK || d == null) return
        val uris = mutableListOf<Uri>()
        d.clipData?.let { cd -> for (x in 0 until cd.itemCount) uris.add(cd.getItemAt(x).uri) }
            ?: d.data?.let { uris.add(it) }
        for (u in uris) try {
            contentResolver.takePersistableUriPermission(u, Intent.FLAG_GRANT_READ_URI_PERMISSION)
        } catch (_:Exception) {}
        items.addAll(uris.filter { !items.contains(it) })
        save(); refresh()
    }
    private fun load() { prefs.getStringSet("uris", emptySet())!!.forEach { items.add(Uri.parse(it)) } }
    private fun save() { prefs.edit().putStringSet("uris", items.map(Uri::toString).toSet()).apply() }
    private fun refresh() {
        grid.adapter = object: BaseAdapter() {
            override fun getCount()=items.size
            override fun getItem(p:Int)=items[p]
            override fun getItemId(p:Int)=p.toLong()
            override fun getView(p:Int,v:View?,parent:ViewGroup):View {
                val iv=(v as? ImageView) ?: ImageView(this@MainActivity)
                iv.layoutParams=AbsListView.LayoutParams(-1, 230)
                iv.scaleType=ImageView.ScaleType.CENTER_CROP
                iv.setBackgroundColor(Color.rgb(25,25,25))
                iv.setImageURI(items[p])
                iv.setOnClickListener { open(items[p]) }
                iv.setOnLongClickListener {
                    AlertDialog.Builder(this@MainActivity).setTitle("إزالة من المكتبة؟")
                      .setMessage("لن يتم حذف الملف من الهاتف.")
                      .setNegativeButton("إلغاء",null)
                      .setPositiveButton("إزالة"){_,_->items.removeAt(p);save();refresh()}.show()
                    true
                }
                return iv
            }
        }
    }
    private fun open(u:Uri) {
        val mime=contentResolver.getType(u) ?: "*/*"
        startActivity(Intent(Intent.ACTION_VIEW,u).apply {
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); type=mime
        })
    }
}
