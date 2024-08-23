package com.rndtechnosoft.lms.Activity.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.rndtechnosoft.lms.Fragment.CompletedFragment
import com.rndtechnosoft.lms.Fragment.InProgressFragment
import com.rndtechnosoft.lms.Fragment.NewLeadFragment

class LeadsPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> NewLeadFragment()
            1 -> InProgressFragment()
            2 -> CompletedFragment()
            else -> throw IllegalStateException("Unexpected position $position")
        }
    }
}