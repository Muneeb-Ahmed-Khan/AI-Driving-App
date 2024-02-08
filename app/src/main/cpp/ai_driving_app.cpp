#include <jni.h>
#include <string>
#include <android/log.h>
#include "camxscanner.h"

#define TAG "NativeLib"

extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_ai_1driving_1app_SplashScreenActivity_stringFromJNI(JNIEnv *env, jobject thiz) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}