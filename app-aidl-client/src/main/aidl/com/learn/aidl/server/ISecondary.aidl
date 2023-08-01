/*
 * Copyright (C) 2007 The Android Open Source Project
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

package com.learn.aidl.server;

/**
 * Example of a secondary interface associated with a service.  (Note that
 * the interface itself doesn't impact, it is just a matter of how you
 * retrieve it from the service.)
 */
interface ISecondary {
    /**
     * Request the PID of this service, to do evil things with it.
     */
    int getPid();
    
    /**
     * Demonstrates some basic types that you can use as parameters and return values in AIDL.
     * 1. int、long、char、boolean
     * 2. String
     * 3. CharSequence
     * 4. List
     * List 中的所有元素必须是以上列表中支持的数据类型，或者您所声明的由 AIDL 生成的其他接口或 Parcelable 类型。
     * 您可选择将 List 用作“泛型”类（例如，List<String>）。
     * 尽管生成的方法旨在使用 List 接口，但另一方实际接收的具体类始终是 ArrayList
     * 5. Map
     * Map 中的所有元素必须是以上列表中支持的数据类型，或者您所声明的由 AIDL 生成的其他接口或 Parcelable 类型。
     * 不支持泛型 Map（如 Map<String,Integer> 形式的 Map）。
     * 尽管生成的方法旨在使用 Map 接口，但另一方实际接收的具体类始终是 HashMap。
     *
     * 即使您在与接口相同的包内定义上方未列出的附加类型，亦须为其各自加入一条 import 语句。
     *
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);
}
