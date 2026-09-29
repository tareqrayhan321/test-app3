package com.imran.clothstore.data.model

/**
 * ওয়েব অ্যাপের fabricPurchaseData_v2 (একটা কাপড়ের নাম/গ্রুপ, একাধিক purchase) এর সমতুল্য।
 * এই array Firestore imran_store/backup ডকুমেন্টের fabricPurchaseData_v2 ফিল্ডে থাকে —
 * আলাদা কালেকশনে নয়। id সংখ্যা (Date.now()); merge হয় id দিয়ে (ওয়েব অ্যাপের _FB_KEYS লজিক)।
 */
data class FabricGroup(
    val id: Long = 0L,
    val name: String = "",
    val unit: String = "গজ",
    val purchases: List<FabricPurchase> = emptyList()
) {
    val totalQty: Double get() = purchases.sumOf { it.qty }
    val totalAmount: Double get() = purchases.sumOf { it.total }
    val avgRate: Double get() = if (totalQty > 0) totalAmount / totalQty else 0.0
}

/** একটা গ্রুপের ভেতরে একটা ক্রয় এন্ট্রি (DCA রো), id সংখ্যা */
data class FabricPurchase(
    val id: Long = 0L,
    val date: String = "",
    val qty: Double = 0.0,
    val rate: Double = 0.0,
    val total: Double = 0.0
)
