#include <jni.h>

#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_com_learn_hellojni_HelloJniActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}