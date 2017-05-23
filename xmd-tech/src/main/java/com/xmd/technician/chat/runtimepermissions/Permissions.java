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
    GRANTED,
    DENIED,
    NOT_FOUND
}
