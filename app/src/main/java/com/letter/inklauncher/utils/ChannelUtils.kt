package com.letter.inklauncher.utils

import android.content.Context

private const val MI_READER_PACKAGE_NAME = "com.moan.moanwm"

/**
 * 渠道判断工具
 *
 * @author Letter(nevermindzzt@gmail.com)
 * @since 1.0.0
 */
object ChannelUtils {

    /**
     * 判断是否为小米电纸书
     * @param context Context context
     * @return Boolean 是否为小米电纸书
     */
    fun isMiReader(context: Context) =
        context.packageName == MI_READER_PACKAGE_NAME

}