package com.xmd.technician.chat.runtimepermissions;

/**
 * Created by sdcm on 17-3-31.
 */

/**
 * Enum class to handle the different states
 * of permissions since the PackageManager only
 * has a granted and denied state.
 */
enum Permissions {
    GRANTED, //授予
    DENIED,//拒绝
    NOT_FOUND//未找到
}
