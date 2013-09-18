#include <jni.h>

#ifndef MULTIVESTA_ARGOSSTATE_H
#define MULTIVESTA_ARGOSSTATE_H

#ifdef __cplusplus
extern "C" {
#endif

   JNIEXPORT void JNICALL Java_multivesta_ARGoSState_initARGoS(JNIEnv* pc_env, jobject t_obj, jstring str_config_file);

   JNIEXPORT void JNICALL Java_multivesta_ARGoSState_destroyARGoS(JNIEnv* pc_env, jobject t_obj);

   JNIEXPORT jdouble JNICALL Java_multivesta_ARGoSState_getTimeFromARGoS(JNIEnv* pc_env, jobject t_obj);

   JNIEXPORT void JNICALL Java_multivesta_ARGoSState_resetARGoS(JNIEnv* pc_env, jobject t_obj, jint n_random_seed);

   JNIEXPORT void JNICALL Java_multivesta_ARGoSState_stepARGoS(JNIEnv* pc_env, jobject t_obj);

   JNIEXPORT void JNICALL Java_multivesta_ARGoSState_runARGoS(JNIEnv* pc_env, jobject t_obj);

   JNIEXPORT jdouble JNICALL Java_multivesta_ARGoSState_observeARGoS(JNIEnv* pc_env, jobject t_obj, jint n_observation);
   
   JNIEXPORT jboolean JNICALL Java_multivesta_ARGoSState_getIsExperimentFinishedFromARGoS(JNIEnv* pc_env, jobject t_obj);

#ifdef __cplusplus
}
#endif

#endif
