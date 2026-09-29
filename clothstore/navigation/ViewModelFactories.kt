package com.imran.clothstore.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.imran.clothstore.data.model.Entry
import com.imran.clothstore.data.model.EntryCategory
import com.imran.clothstore.ui.screens.detail.DetailViewModel
import com.imran.clothstore.ui.screens.fvlist.FvListViewModel

/**
 * FvListViewModel ও DetailViewModel কনস্ট্রাক্টরে প্যারামিটার (category, entry) নেয় বলে
 * ডিফল্ট viewModel() ফ্যাক্টরি কাজ করবে না — তাই এই ছোট ফ্যাক্টরিগুলো ব্যবহার করা হয়।
 */
class FvListViewModelFactory(private val category: EntryCategory) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FvListViewModel(category) as T
    }
}

class DetailViewModelFactory(
    private val category: EntryCategory,
    private val entryId: Long,
    private val entry: Entry
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DetailViewModel(category, entryId, entry) as T
    }
}
