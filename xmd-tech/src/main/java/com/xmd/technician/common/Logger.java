package com.xmd.technician.common;


import android.util.Log;

import com.shidou.commonlibrary.helper.XLogger;

public class Logger {

    public static final String TAG = "9358";

    /**
     * Send a {@link #VERBOSE} log message.
     *
     * @param msg The message you would like logged.
     */
    public static void v(String msg) {
        XLogger.v(TAG, msg);
    }

    /**
     * Send a {@link #VERBOSE} log message and log the exception.
     *
     * @param msg The message you would like logged.
     * @param tr  An exception to log
     */
    public static void v(String msg, Throwable tr) {
        XLogger.v(TAG, msg, tr);
    }

    /**
     * Send a {@link #VERBOSE} log message.
     *
     * @param tag Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static void v(String tag, String msg) {
        XLogger.v(tag, msg);
    }

    /**
     * Send a {@link #VERBOSE} log message and log the exception.
     *
     * @param tag Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr  An exception to log
     */
    public static void v(String tag, String msg, Throwable tr) {
        XLogger.v(tag, msg, tr);
    }

    /**
     * Send a {@link #DEBUG} log message.
     *
     * @param msg The message you would like logged.
     */
    public static void d(String msg) {
        XLogger.d(TAG, msg);
    }

    /**
     * Send a {@link #DEBUG} log message and log the exception.
     *
     * @param msg The message you would like logged.
     * @param tr  An exception to log
     */
    public static void d(String msg, Throwable tr) {
        XLogger.d(TAG, msg, tr);
    }

    /**
     * Send a {@link #DEBUG} log message.
     *
     * @param tag Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static void d(String tag, String msg) {
        XLogger.d(tag, msg);
    }

    /**
     * Send a {@link #DEBUG} log message and log the exception.
     *
     * @param tag Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr  An exception to log
     */
    public static void d(String tag, String msg, Throwable tr) {
        XLogger.d(tag, msg, tr);
    }

    /**
     * Send an {@link #INFO} log message.
     *
     * @param msg The message you would like logged.
     */
    public static void i(String msg) {
        XLogger.i(TAG, msg);
    }

    /**
     * Send a {@link #INFO} log message and log the exception.
     *
     * @param msg The message you would like logged.
     * @param tr  An exception to log
     */
    public static void i(String msg, Throwable tr) {
        XLogger.i(TAG, msg, tr);
    }

    /**
     * Send an {@link #INFO} log message.
     *
     * @param tag Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static void i(String tag, String msg) {
        XLogger.i(tag, msg);
    }

    /**
     * Send a {@link #INFO} log message and log the exception.
     *
     * @param tag Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr  An exception to log
     */
    public static void i(String tag, String msg, Throwable tr) {
        XLogger.i(tag, msg, tr);
    }

    /**
     * Send a {@link #WARN} log message.
     *
     * @param msg The message you would like logged.
     */
    public static void w(String msg) {
        XLogger.w(TAG, msg);
    }

    /**
     * Send a {@link #WARN} log message and log the exception.
     *
     * @param msg The message you would like logged.
     * @param tr  An exception to log
     */
    public static void w(String msg, Throwable tr) {
        XLogger.w(TAG, msg, tr);
    }

    /**
     * Send a {@link #WARN} log message.
     *
     * @param tag Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static void w(String tag, String msg) {
        XLogger.w(tag, msg);
    }

    /**
     * Send a {@link #WARN} log message and log the exception.
     *
     * @param tag Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr  An exception to log
     */
    public static void w(String tag, String msg, Throwable tr) {
        XLogger.w(tag, msg, tr);
    }

    /**
     * Send an {@link #ERROR} log message.
     *
     * @param msg The message you would like logged.
     */
    public static void e(String msg) {
        XLogger.e(TAG, msg);
    }

    /**
     * Send a {@link #ERROR} log message and log the exception.
     *
     * @param msg The message you would like logged.
     * @param tr  An exception to log
     */
    public static void e(String msg, Throwable tr) {
        XLogger.e(TAG, msg, tr);
    }

    /**
     * Send an {@link #ERROR} log message.
     *
     * @param tag Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static void e(String tag, String msg) {
        XLogger.e(tag, msg);
    }

    /**
     * Send a {@link #ERROR} log message and log the exception.
     *
     * @param tag Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr  An exception to log
     */
    public static void e(String tag, String msg, Throwable tr) {
        XLogger.e(tag, msg, tr);
    }
}

