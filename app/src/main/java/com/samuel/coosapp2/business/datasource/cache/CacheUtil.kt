package com.samuel.coosapp2.business.datasource.cache

class CacheUtil {

    companion object{
        private val TAG: String = "CacheUtilDebug"

        // values
        const val VIDEO_ORDER_ASC: String = ""
        const val VIDEO_ORDER_DESC: String = "-"
        const val VIDEO_FILTER_PREACHER = "preacher"
        const val VIDEO_FILTER_DATE_CREATED = "date_created"

        val ORDER_BY_ASC_DATE_CREATED = VIDEO_ORDER_ASC + VIDEO_FILTER_DATE_CREATED
        val ORDER_BY_DESC_DATE_CREATED = VIDEO_ORDER_DESC + VIDEO_FILTER_DATE_CREATED
        val ORDER_BY_ASC_PREACHER = VIDEO_ORDER_ASC + VIDEO_FILTER_PREACHER
        val ORDER_BY_DESC_PREACHER = VIDEO_ORDER_DESC + VIDEO_FILTER_PREACHER
    }

}