#include "multivesta_argos.h"
#include <argos3/core/simulator/simulator.h>
#include <argos3/core/utility/plugins/dynamic_loading.h>

using namespace argos;

/****************************************/
/****************************************/

CSimulator* pcSimulator = NULL;

/****************************************/
/****************************************/

JNIEXPORT void JNICALL Java_multivesta_ARGoSState_initARGoS(JNIEnv* pc_env, jobject t_obj, jstring str_config_file) {
   /* Create the simulator instance */
   pcSimulator = &CSimulator::GetInstance();
   /* Load all ARGoS plug-ins */
   CDynamicLoading::LoadAllLibraries();
   /* Convert Java string to C string */
   const char* pchConfigFile = pc_env->GetStringUTFChars(str_config_file, 0);
   /* Set the experiment file name */
   pcSimulator->SetExperimentFileName(pchConfigFile);
   /* Release memory for C string */
   pc_env->ReleaseStringUTFChars(str_config_file, pchConfigFile);
   /* Load the file */
   pcSimulator->LoadExperiment();
}

/****************************************/
/****************************************/

JNIEXPORT void JNICALL Java_multivesta_ARGoSState_destroyARGoS(JNIEnv* pc_env, jobject t_obj) {
   pcSimulator->Destroy();
   LOG.Flush();
   LOGERR.Flush();
}

/****************************************/
/****************************************/

JNIEXPORT jdouble JNICALL Java_multivesta_ARGoSState_getTimeFromARGoS(JNIEnv* pc_env, jobject t_obj) {
   return pcSimulator->GetMaxSimulationClock();
}

/****************************************/
/****************************************/

JNIEXPORT void JNICALL Java_multivesta_ARGoSState_resetARGoS(JNIEnv* pc_env, jobject t_obj, jint n_random_seed) {
   pcSimulator->Reset(n_random_seed);
}

/****************************************/
/****************************************/

JNIEXPORT void JNICALL Java_multivesta_ARGoSState_stepARGoS(JNIEnv* pc_env, jobject t_obj) {
   pcSimulator->UpdateSpace();
}

/****************************************/
/****************************************/

JNIEXPORT void JNICALL Java_multivesta_ARGoSState_runARGoS(JNIEnv* pc_env, jobject t_obj) {
   pcSimulator->Execute();
}

/****************************************/
/****************************************/

JNIEXPORT jdouble JNICALL Java_multivesta_ARGoSState_observeARGoS(JNIEnv* pc_env, jobject t_obj, jint n_observation) {
   return 0;
}

/****************************************/
/****************************************/

JNIEXPORT jboolean JNICALL Java_multivesta_ARGoSState_isExperimentFinishedInARGoS(JNIEnv* pc_env, jobject t_obj) {
   return pcSimulator->IsExperimentFinished();
}

/****************************************/
/****************************************/
