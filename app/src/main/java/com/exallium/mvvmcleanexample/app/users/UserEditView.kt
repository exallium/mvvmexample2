package com.exallium.mvvmcleanexample.app.users

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.exallium.mvvmcleanexample.R
import com.exallium.mvvmcleanexample.app.application.App
import com.exallium.mvvmcleanexample.app.di.domain.users.DomainUserModule
import com.exallium.mvvmcleanexample.app.nav.AppRouter
import com.exallium.mvvmcleanexample.app.users.di.DaggerUserEditComponent
import com.exallium.mvvmcleanexample.app.users.di.UserEditModule
import com.exallium.mvvmcleanexample.databinding.UserEditViewBinding
import com.exallium.mvvmcleanexample.presentation.users.UserEditViewModel
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class UserEditView : AppCompatActivity() {

    @Inject
    lateinit var viewModel: UserEditViewModel

    @Inject
    lateinit var router: AppRouter

    val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<UserEditViewBinding>(this, R.layout.user_edit_view)

        DaggerUserEditComponent.builder()
                .appComponent(App.component)
                .domainUserModule(DomainUserModule())
                .userEditModule(UserEditModule())
                .build()
                .inject(this)

        binding.viewModel = viewModel

        router.transitions().subscribe {
            when (it) {
                is AppRouter.Transition.GoBack -> onBackPressed()
                is AppRouter.Transition.DisplayMessage -> displayMessage(it.message)
                is AppRouter.Transition.DisplayError -> displayError(binding.root, it.message)
            }
        }
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        viewModel.cancel()
        super.onDestroy()
    }

    private fun displayMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun displayError(view: View, message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
    }

}