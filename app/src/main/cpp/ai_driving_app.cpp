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
extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_ai_1driving_1app_SignDetection_stringFromJNI(JNIEnv *env, jobject thiz) {
    std::string hello = "Hello from C++ to Sign Detection Java Class.";
    return env->NewStringUTF(hello.c_str());
}



extern "C"
JNIEXPORT jobjectArray JNICALL
Java_com_example_ai_1driving_1app_SignDetection_processFrameFromJNI(JNIEnv *env, jobject thiz,
                                                                    jlong mat) {
    clock_t begin = clock();

    std::vector<std::string> labels = processFrame(mat);

    // Create a Java String array
    jobjectArray result = env->NewObjectArray(labels.size(), env->FindClass("java/lang/String"), nullptr);

    for (size_t i = 0; i < labels.size(); ++i) {
        // Convert each C++ string to a Java string
        jstring javaString = env->NewStringUTF(labels[i].c_str());

        // Set the Java string in the array
        env->SetObjectArrayElement(result, i, javaString);

        // Don't forget to delete the local reference to avoid memory leaks
        env->DeleteLocalRef(javaString);
    }

    double totalTime = double(clock() - begin) / CLOCKS_PER_SEC;
    __android_log_print(ANDROID_LOG_INFO, TAG, "AI computation time = %f seconds\n", totalTime);
    return result;
}


extern "C"
JNIEXPORT jboolean JNICALL
Java_com_example_ai_1driving_1app_SplashScreenActivity_loadOnnxModel(JNIEnv *env, jobject thiz,
                                                                     jstring model_path) {
    clock_t begin = clock();
    const char *str = env->GetStringUTFChars(model_path, nullptr);
    if (str == nullptr) {
        return false; // handle error
    }
    bool res = loadOnnxModel(str);
    if(res){
        double totalTime = double(clock() - begin) / CLOCKS_PER_SEC;
        __android_log_print(ANDROID_LOG_INFO, TAG, "Model Loading computation time = %f seconds\n", totalTime);
        __android_log_print(ANDROID_LOG_INFO, TAG, "Model Loaded Sucessfully.\n");
    } else{
        __android_log_print(ANDROID_LOG_INFO, TAG, "Model Loading Failed.\n");
    }
    env->ReleaseStringUTFChars(model_path, str);
    return res;
}