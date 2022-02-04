package com.alfrosoft.fastchatapp.adapterclasses

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.alfrosoft.fastchatapp.fragments.ChatsFragment
import com.alfrosoft.fastchatapp.fragments.SearchFragment
import com.alfrosoft.fastchatapp.fragments.SettingsFragment


class ViewPagerAdapter(appCompatActivity : AppCompatActivity) : FragmentStateAdapter(appCompatActivity) {

    private var fragments:ArrayList<Fragment> = ArrayList<Fragment>()
    private var titles:ArrayList<String> = ArrayList<String>()

    fun getCount(): Int {
        return fragments.size
    }

    override fun getItemCount(): Int {

        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ChatsFragment()
            1 -> SearchFragment()
            2 -> SettingsFragment()
            else -> ChatsFragment()
        }
    }

    fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    fun addFragment(fragment: Fragment,title:String)
    {
        fragments.add(fragment)
        titles.add(title)
    }

    fun getPageTitle(position: Int): CharSequence? {
        return titles[position]
    }
}