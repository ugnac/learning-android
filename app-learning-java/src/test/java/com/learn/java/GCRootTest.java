package com.learn.java;

import org.junit.Test;

/**
 * GC Roots:
 * 1. 方法区：类静态属性的对象
 * 2. 方法区：常量的对象
 * 3. 虚拟机栈（本地变量表）中的对象
 * 4. 本地方法栈JNI(Native方法)中的对象
 */
public class GCRootTest {
    /**
     * 可达性分析算法
     */
    Object o = new Object();

    static Object GCRoot1 = new Object(); // GC Roots

    final static Object GCRoot2 = new Object();// GC Roots

    @Test
    public void method() {
        //可达
        Object object1 = GCRoot1; //=不是赋值，在对象中是引用，传递的是右边对象的地址
        Object object2 = object1;
        Object object3 = object1;
        Object object4 = object3;
    }

    /**
     * 方法未执行完之前，5， 6， 7都是本地变量表中的对象，属于GCRoots，方法执行完之后这些对象就被移除除GCRoots
     */
    @Test
    public void king() {
        // 不可达（方法运行完后可回收）
        // 5, 6, 7都是本地变量表中的对象
        Object object5 = o; // o不是GCRoots
        Object object6 = object5;
        Object object7 = object5;
    }

    // 本地变量表中引用的对象
    @Test
    public void stack() {
        Object ostack = new Object();    // 本地变量表的对象
        Object object9 = ostack;
        //以上object9 在方法没有(运行完)出栈前都是可达的
    }
}
