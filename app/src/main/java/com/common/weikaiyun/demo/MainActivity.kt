package com.common.weikaiyun.demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.common.weikaiyun.R
import com.common.weikaiyun.demo.db.User
import com.common.weikaiyun.demo.viewmodel.UserViewModel

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val viewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        viewModel.userList.observe(this, Observer {
            it.forEach() { user ->
                Log.i("User Info", user.toString())
            }
        })

        val user1 = User(1, "kaiyun1", "kaiyun1", 1, "female")
        val user2 = User(2, "kaiyun2", "kaiyun2", 2, "male")
        val user3 = User(3, "kaiyun3", "kaiyun3", 3, "female")
        val user4 = User(4, "kaiyun4", "kaiyun4", 4, "male")
        val user5 = User(5, "kaiyun5", "kaiyun5", 5, "female")


        viewModel.insertAll(user1, user2, user3, user4, user5)
    }
}
