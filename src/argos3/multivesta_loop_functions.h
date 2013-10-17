#ifndef MULTIVESTA_LOOP_FUNCTIONS_H
#define MULTIVESTA_LOOP_FUNCTIONS_H

namespace argos {
   class CMultiVeStALoopFunctions;
}

#include <argos3/core/simulator/loop_functions.h>

namespace argos {

   class CMultiVeStALoopFunctions : public CLoopFunctions {

   public:

      /**
       * Class constructor.
       */
      CMultiVeStALoopFunctions() {}

      /**
       * Class destructor.
       */
      virtual ~CMultiVeStALoopFunctions() {}

      /**
       * Performs an observation on the current experiment.
       * NOTE: values between 0 and 2 for n_observation are taken care of
       * directly by the Java method ARGoSState.rval(). Observe() is called
       * only for values > 2.
       * @param n_observation The number of the observation.
       * @return The value of the observation.
       */
      virtual Real Observe(SInt32 n_observation) = 0;

   };
}

#endif
