#ifndef ARGOS_MULTIVESTA_WRAPPER_H
#define ARGOS_MULTIVESTA_WRAPPER_H

#include <argos3/core/utility/datatypes/datatypes.h>

namespace argos {

   /**
    * A wrapper class to allow MultiVeStA to interact with ARGoS.
    */
   class ARGoSMultiVeStAWrapper {

   public:

      /**
       * Class constructor.
       */
      ARGoSMultiVeStAWrapper();

      /**
       * Class destructor.
       */
      virtual ~ARGoSMultiVeStAWrapper();

      /**
       * Returns the current clock as a real value.
       * @return the current clock as a real value.
       */
      virtual Real GetTime() const;

      /**
       * Sets the random seed of ARGoS.
       */
      virtual void SetRandomSeed(UInt32 un_random_seed);

      /**
       * Performs one simulation step.
       */
      virtual void Step();

      /**
       * Performs a simulation up to completion.
       */
      virtual void Run();

      /**
       * Returns an observation.
       * Internally, this calls CMultiVeStALF::Observe().
       * @param n_index The index of the observation.
       * @return The value of the wanted observation.
       * @see CMultiVeStaLF
       */
      virtual Real Observe(SInt32 n_index);

   };

}

#endif
