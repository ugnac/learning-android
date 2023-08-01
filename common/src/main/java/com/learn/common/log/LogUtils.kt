/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.learn.common.log

import android.os.Build
import android.util.Log

object LogUtils {
    /** Default logger used for generic logging, i.e TAG. when a specific log tag isn't specified.*/
    private val DEFAULT_LOGGER = Logger("learning-android")

    @JvmStatic
    fun v(tag: String, message: String, vararg args: Any?) {
        DEFAULT_LOGGER.v(tag, message, *args)
    }

    @JvmStatic
    fun d(tag: String, message: String, vararg args: Any?) {
        DEFAULT_LOGGER.d(tag, message, *args)
    }

    @JvmStatic
    fun i(tag: String, message: String, vararg args: Any?) {
        DEFAULT_LOGGER.i(tag, message, *args)
    }

    @JvmStatic
    fun w(tag: String, message: String, vararg args: Any?) {
        DEFAULT_LOGGER.w(tag, message, *args)
    }

    fun e(tag: String, message: String, vararg args: Any?) {
        DEFAULT_LOGGER.e(tag, message, *args)
    }

    @JvmStatic
    fun e(tag: String,message: String, e: Throwable) {
        DEFAULT_LOGGER.e(tag, message, e)
    }

    fun wtf(tag: String, message: String, vararg args: Any?) {
        DEFAULT_LOGGER.wtf(tag, message, *args)
    }

    @JvmStatic
    fun wtf(tag: String, e: Throwable) {
        DEFAULT_LOGGER.wtf(tag, e)
    }

    class Logger(val logTag: String) {
        private val isVerboseLoggable: Boolean
            get() = DEBUG || Log.isLoggable(logTag, Log.VERBOSE)

        private val isDebugLoggable: Boolean
            get() = DEBUG || Log.isLoggable(logTag, Log.DEBUG)

        private val isInfoLoggable: Boolean
            get() = DEBUG || Log.isLoggable(logTag, Log.INFO)

        private val isWarnLoggable: Boolean
            get() = DEBUG || Log.isLoggable(logTag, Log.WARN)

        private val isErrorLoggable: Boolean
            get() = DEBUG || Log.isLoggable(logTag, Log.ERROR)

        private val isWtfLoggable: Boolean
            get() = DEBUG || Log.isLoggable(logTag, Log.ASSERT)

        fun v(tag: String, message: String, vararg args: Any?) {
            if (isVerboseLoggable) {
                Log.v(logTag, msg(tag, args, message))
            }
        }

        fun d(tag: String, message: String, vararg args: Any?) {
            if (isDebugLoggable) {
                Log.d(logTag, msg(tag, args, message))
            }
        }

        fun i(tag: String, message: String, vararg args: Any?) {
            if (isInfoLoggable) {
                Log.i(logTag, msg(tag, args, message))
            }
        }

        fun w(tag: String, message: String, vararg args: Any?) {
            if (isWarnLoggable) {
                Log.w(logTag, msg(tag, args, message))
            }
        }

        fun e(tag: String, message: String, vararg args: Any?) {
            if (isErrorLoggable) {
                Log.e(logTag, msg(tag, args, message))
            }
        }

        fun e(tag: String, message: String, e: Throwable) {
            if (isErrorLoggable) {
                Log.e(logTag, message, e)
            }
        }

        fun wtf(tag: String, message: String, vararg args: Any?) {
            if (isWtfLoggable) {
                Log.wtf(logTag, msg(tag, args, message))
            }
        }

        private fun msg(tag: String, args: Array<out Any?>, message: String) =
            if (args.isEmpty() || args[0] == null) {
                "$tag: $message"
            } else {
                String.format("$tag: $message", *args)
            }

        fun wtf(tag: String, e: Throwable) {
            if (isWtfLoggable) {
                Log.wtf(logTag, "$tag: ", e)
            }
        }

        companion object {
            /** Log everything for debug builds or if running on a dev device. */
            val DEBUG = (/*BuildConfig.DEBUG ||*/ "eng" == Build.TYPE || "userdebug" == Build.TYPE)
        }
    }
}