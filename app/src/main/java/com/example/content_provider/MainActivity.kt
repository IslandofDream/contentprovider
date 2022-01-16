package com.example.content_provider


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import com.example.content_provider.databinding.ActivityMainBinding
import android.content.pm.PackageManager
import android.widget.*
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    var cols = listOf<String>(
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.NUMBER,
                        ContactsContract.CommonDataKinds.Phone._ID,
    ).toTypedArray()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, Array(1){android.Manifest.permission.READ_CONTACTS},111)
            //유저가 연락처 권한을 가지고 있는지 확인 하는 것이다.
        }else
            readContact() // 가지고 있으면 readContact함수 실행
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 111 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            readContact()
    }

    private fun readContact() {

//        var from = listOf<String>(ContactsContract.CommonDataKinds.Phone._ID,
//            ContactsContract.CommonDataKinds.Phone.NUMBER).toTypedArray() // 두개의 열이 필요

        var from = listOf<String>(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER).toTypedArray() // 두개의 열이 필요

        var to = intArrayOf(android.R.id.text1, android.R.id.text2)

        var rs = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            cols,null,null,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME) // Uri

//        if(rs?.moveToNext()!!)
//            Toast.makeText(applicationContext,rs.getString(1), Toast.LENGTH_LONG).show()
//        // 여기서 getString을 무엇으로 하는가에 따라서 달라짐

        var adapter = SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, rs,from,to,0 )
        binding.listView.adapter = adapter


        binding.searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean { //해당부분 쿼리 변경으로 다양한 작용 가능
                var rs = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, cols,
                    "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} LIKE ?",Array(1){"%$newText"}
                    ,ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                adapter.changeCursor(rs)
                return false
            }
        })


    }

}