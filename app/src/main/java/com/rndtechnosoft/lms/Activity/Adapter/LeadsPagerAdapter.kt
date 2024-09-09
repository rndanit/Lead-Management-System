package com.rndtechnosoft.lms.Activity.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.rndtechnosoft.lms.Activity.DataModel.DataXXX
import com.rndtechnosoft.lms.Fragment.DynamicFragment

class LeadsPagerAdapter(
    fragmentActivity: FragmentActivity,
    private val statusTypes: List<DataXXX>
) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = statusTypes.size

    override fun createFragment(position: Int): Fragment {
        val statusType = statusTypes[position]
        return DynamicFragment.newInstance(statusType.status_type)
    }
}
