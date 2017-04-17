package com.exallium.mvvmcleanexample.app.users

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.exallium.mvvmcleanexample.presentation.users.UserEditViewModel
import javax.inject.Inject

class UserEditView : AppCompatActivity() {

    @Inject
    lateinit var viewModel: UserEditViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


}