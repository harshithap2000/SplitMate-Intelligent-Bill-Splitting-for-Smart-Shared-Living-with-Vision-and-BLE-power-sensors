package com.example.splitmategamma.dashboard.principalTenant.home.model

data class BillResponse(
    val houseId: String,
    val billingPeriod: BillingPeriod,
    val electricUsage: List<Usage>,
    val waterUsage: List<Usage>,
    val gasUsage: List<Usage>
)