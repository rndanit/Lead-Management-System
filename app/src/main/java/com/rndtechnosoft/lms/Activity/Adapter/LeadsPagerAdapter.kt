package com.rndtechnosoft.lms.Activity.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.rndtechnosoft.lms.Fragment.AllFragment
import com.rndtechnosoft.lms.Fragment.DynamicFragment

class LeadsPagerAdapter(
    fragmentActivity: FragmentActivity,
    private val statusTypes: List<String> // Change the type to List<String> to include the "All" tab
) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = statusTypes.size

    override fun createFragment(position: Int): Fragment {
        // Return different fragments based on position
        return if (position == 0) {
            // Return a fragment for the "All" tab
            AllFragment()
        } else {
            val statusType = statusTypes[position]
            DynamicFragment.newInstance(statusType)
        }
    }
}

