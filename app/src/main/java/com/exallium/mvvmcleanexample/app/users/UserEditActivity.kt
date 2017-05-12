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
import com.exallium.mvvmcleanexample.presentation.utils.autoViewBinding
import com.exallium.mvvmcleanexample.presentation.utils.disposable
import javax.inject.Inject

class UserEditActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModel: UserEditViewModel

    @Inject
    lateinit var router: AppRouter

    private var binding: UserEditViewBinding? by autoViewBinding<UserEditViewBinding>(null)
    private var disposable by disposable(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<UserEditViewBinding>(this, R.layout.user_edit_view).also(this::init)
    }

    private fun init(binding: UserEditViewBinding) {
        DaggerUserEditComponent.builder()
                .appComponent(App.component)
                .domainUserModule(DomainUserModule())
                .userEditModule(UserEditModule())
                .build()
                .inject(this)

        binding.viewModel = viewModel

        router.transitions().subscribe(this::onNextTransition)
    }

    private fun onNextTransition(transition: AppRouter.Transition) {
        when (transition) {
            is AppRouter.Transition.GoBack -> finish()
            is AppRouter.Transition.DisplayMessage -> displayMessage(transition.message)
            is AppRouter.Transition.DisplayError -> displayError(binding!!.root, transition.message)
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.start()
    }

    override fun onStop() {
        super.onStop()
        viewModel.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
        disposable = null
    }

    private fun displayMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun displayError(view: View, message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
    }

}