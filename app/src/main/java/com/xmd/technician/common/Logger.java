package com.xmd.technician.common;


import android.util.Log;

import com.xmd.technician.BuildConfig;

public class Logger {

    public static final String TAG = "9358";

    /**
     * Send a {@link #VERBOSE} log message.
     *
     * @param msg
     *            The message you would like logged.
     */
    public static void v(String msg) {
        v(TAG, msg);
    }

    /**
     * Send a {@link #VERBOSE} log message and log the exception.
     *
     * @param msg
     *            The message you would like logged.
     * @param tr
     *            An exception to log
     */
    public static void v(String msg, Throwable tr) {
        v(TAG, msg, tr);
    }

    /**
     * Send a {@link #VERBOSE} log message.
     *
     * @param tag
     *            Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param msg
     *            The message you would like logged.
     */
    public static void v(String tag, String msg) {
        if(BuildConfig.DEBUG) {
            Log.v(tag, msg);
        }
    }

    /**
     * Send a {@link #VERBOSE} log message and log the exception.
     *
     * @param tag
     *            Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param msg
     *            The message you would like logged.
     * @param tr
     *            An exception to log
     */
    public static void v(String tag, String msg, Throwable tr) {
        Log.v(tag, msg, tr);
    }

    /**
     * Send a {@link #DEBUG} log message.
     *
     * @param msg
     *            The message you would like logged.
     */
    public static void d(String msg) {
        d(TAG, msg);
    }

    /**
     * Send a {@link #DEBUG} log message and log the exception.
     *
     * @param msg
     *            The message you would like logged.
     * @param tr
     *            An exception to log
     */
    public static void d(String msg, Throwable tr) {
        d(TAG, msg, tr);
    }

    /**
     * Send a {@link #DEBUG} log message.
     *
     * @param tag
     *            Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param msg
     *            The message you would like logged.
     */
    public static void d(String tag, String msg) {
        Log.d(tag, msg);
    }

    /**
     * Send a {@link #DEBUG} log message and log the exception.
     *
     * @param tag
     *            Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param msg
     *            The message you would like logged.
     * @param tr
     *            An exception to log
     */
    public static void d(String tag, String msg, Throwable tr) {

        Log.d(tag, msg, tr);
    }

    /**
     * Send an {@link #INFO} log message.
     *
     * @param msg
     *            The message you would like logged.
     */
    public static void i(String msg) {
        i(TAG, msg);
    }

    /**
     * Send a {@link #INFO} log message and log the exception.
     *
     * @param msg
     *            The message you would like logged.
     * @param tr
     *            An exception to log
     */
    public static void i(String msg, Throwable tr) {
        i(TAG, msg, tr);
    }

    /**
     * Send an {@link #INFO} log message.
     *
     * @param tag
     *            Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param msg
     *            The message you would like logged.
     */
    public static void i(String tag, String msg) {

        Log.i(tag, msg);
    }

    /**
     * Send a {@link #INFO} log message and log the exception.
     *
     * @param tag
     *            Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param msg
     *            The message you would like logged.
     * @param tr
     *            An exception to log
     */
    public static void i(String tag, String msg, Throwable tr) {

        Log.i(tag, msg, tr);
    }

    /**
     * Send a {@link #WARN} log message.
     *
     * @param msg
     *            The message you would like logged.
     */
    public static void w(String msg) {
        w(TAG, msg);
    }

    /**
     * Send a {@link #WARN} log message and log the exception.
     *
     * @param msg
     *            The message you would like logged.
     * @param tr
     *            An exception to log
     */
    public static void w(String msg, Throwable tr) {
        w(TAG, msg, tr);
    }

    /**
     * Send a {@link #WARN} log message.
     *
     * @param tag
     *            Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param msg
     *            The message you would like logged.
     */
    public static void w(String tag, String msg) {

        Log.w(tag, msg);
    }

    /**
     * Send a {@link #WARN} log message and log the exception.
     *
     * @param tag
     *            Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param msg
     *            The message you would like logged.
     * @param tr
     *            An exception to log
     */
    public static void w(String tag, String msg, Throwable tr) {

        Log.w(tag, msg, tr);
    }

    /**
     * Send an {@link #ERROR} log message.
     *
     * @param msg
     *            The message you would like logged.
     */
    public static void e(String msg) {
        e(TAG, msg);
    }

    /**
     * Send a {@link #ERROR} log message and log the exception.
     *
     * @param msg
     *            The message you would like logged.
     * @param tr
     *            An exception to log
     */
    public static void e(String msg, Throwable tr) {
        e(TAG, msg, tr);
    }

    /**
     * Send an {@link #ERROR} log message.
     *
     * @param tag
     *            Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param msg
     *            The message you would like logged.
     */
    public static void e(String tag, String msg) {

        Log.e(tag, msg);
    }

    /**
     * Send a {@link #ERROR} log message and log the exception.
     *
     * @param tag
     *            Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param msg
     *            The message you would like logged.
     * @param tr
     *            An exception to log
     */
    public static void e(String tag, String msg, Throwable tr) {
        Log.e(tag, msg, tr);
    }
}

