package com.learn.java;

import org.junit.Test;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;

/**
 * + 强引用： Object obj = new Object();
 * + 软引用：内存不足时回收
 * + 弱引用： GC到来时回收
 * + 虚引用：GC回收时可得到一个通知
 */
public class ObjReferenceTest {
    @Test
    public void testSoftReference() {
        User user = new User(1, "Andy");
        SoftReference<User> userSoft = new SoftReference<User>(user);
        user = null;
        System.out.println(userSoft.get());
        System.gc();
        System.out.println("After gc");
        System.out.println(userSoft.get());
        //向堆中填充数据,导致OOM
        List<byte[]> list = new LinkedList<>();
        try {
            for (int i = 0; i < 100; i++) {
                System.out.println("for===========" + userSoft.get());
                list.add(new byte[1024 * 1024 * 1]);
            }
        } catch (Throwable e) {
            System.out.println("Exception======" + userSoft.get());
        }
    }

    @Test
    public void testWeakReference() {
        User user = new User(1, "Andy");
        WeakReference<User> userWeakReference = new WeakReference<>(user);
        user = null;
        System.out.println(userWeakReference.get());
        System.gc();
        System.out.println("After gc");
        System.out.println(userWeakReference.get());
    }

    @Test
    public void testPhantomReference() throws InterruptedException {
        //虚引用：功能，不会影响到对象的生命周期的，
        // 但是能让程序员知道该对象什么时候被 回收了
        ReferenceQueue<Object> referenceQueuee = new ReferenceQueue<>();
        Object phantomObject = new Object();
        PhantomReference phantomReference = new PhantomReference(phantomObject, referenceQueuee);
        phantomObject = null;
        System.out.println("phantomObject:" + phantomObject);//null
        System.out.println("phantomReference" + referenceQueuee.poll());//null
        System.gc();
        Thread.sleep(2000);
        System.out.println("referenceQueuee:" + referenceQueuee.poll());
    }

    class User {
        Integer id;
        String name;

        public User(Integer id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}
