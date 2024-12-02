package com.example.myshop

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.fragment.app.Fragment
import com.example.myshop.databinding.ActivityMainBinding
import com.example.myshop.fragments.CartFragment
import com.example.myshop.fragments.LikeFragment
import com.example.myshop.fragments.ProductsFragment
import com.example.myshop.fragments.ProfileFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding

    private val productsFragment = ProductsFragment()
    private val likeFragment = LikeFragment()
    private val cartFragment = CartFragment()
    private val profileFragment = ProfileFragment()

    private var activeFragment:Fragment = productsFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        supportFragmentManager.beginTransaction().apply {
            add(R.id.main_frame, productsFragment, "Product")
            add(R.id.main_frame, likeFragment, "Like")
            add(R.id.main_frame, cartFragment, "Cart")
            add(R.id.main_frame, profileFragment, "Profile")
        }.commit()

        binding.mainBottomView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.product ->{
                    supportFragmentManager.beginTransaction().hide(activeFragment).show(productsFragment).commit()
                    activeFragment = productsFragment
                    true
                }

                R.id.like ->{
                    supportFragmentManager.beginTransaction().hide(activeFragment).show(likeFragment).commit()
                    activeFragment = likeFragment
                    true
                }

                R.id.cart ->{
                    supportFragmentManager.beginTransaction().hide(activeFragment).show(cartFragment).commit()
                    activeFragment = cartFragment
                    true
                }

                R.id.profile ->{
                    supportFragmentManager.beginTransaction().hide(activeFragment).show(profileFragment).commit()
                    activeFragment = profileFragment
                    true
                }

                else -> false
            }
        }

        binding.mainBottomView.setOnItemReselectedListener {  }

        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                if (activeFragment == profileFragment){
                    finish()
                }else{
                    val id = binding.mainBottomView.menu.getItem(0)
                    id.isChecked = true
                    supportFragmentManager.beginTransaction().hide(activeFragment).show(productsFragment).commit()
                    activeFragment = productsFragment
                }
            }

        })
    }
}