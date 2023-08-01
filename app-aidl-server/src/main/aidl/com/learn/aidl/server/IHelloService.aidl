// IHelloService.aidl
package com.learn.aidl.server;

import com.learn.aidl.server.Student;

// Declare any non-default types here with import statements

interface IHelloService {
    int getPid();

    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString);

    // - in : 客户端流向服务端
    // - out : 服务端流向客户端
    // - inout : 双向流通 - 原语默认是in
    void addStudent(in Student student);

    List<Student> getStudentList();
}