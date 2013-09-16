cmake_minimum_required(VERSION 2.8)
project(argos3_multivesta)


#
# Called to bundle multivesta along with the generated JAR
#
function (_bundle_multivesta_jar target)
  add_custom_command(
    TARGET ${target}
    POST_BUILD
    COMMAND cmake -E remove_directory multivesta_jar
    COMMAND cmake -E make_directory multivesta_jar
    COMMAND cmake -E chdir multivesta_jar jar xf ${CMAKE_SOURCE_DIR}/multivesta/multivesta.jar
    COMMAND cmake -E remove_directory multivesta_jar/META-INF
    COMMAND cmake -E chdir multivesta_jar jar uf ${CMAKE_BINARY_DIR}/${target}.jar .)
endfunction (_bundle_multivesta_jar target)

#
# Check required packages
#

# Find the ARGoS package
find_package(PkgConfig)
pkg_check_modules(ARGOS REQUIRED argos3_simulator)
set(CMAKE_MODULE_PATH ${CMAKE_MODULE_PATH} ${ARGOS_PREFIX}/share/argos3/cmake)

# Find Java
find_package(Java REQUIRED)

# Find JNI
find_package(JNI REQUIRED)

#
# Set up C++ compilation environment
#

# Set up C++ include directories
include_directories(${ARGOS_INCLUDE_DIRS} ${JNI_INCLUDE_DIRS})

# Set up C++ link directories
link_directories(${ARGOS_LIBRARY_DIRS} ${JNI_LIBRARIES})

#
# Compile the C++ ARGoS-MultiVeStA wrapper
#

# Header list
set(ARGOS3_MULTIVESTA_WRAPPER_HEADERS
  argos3/argos_multivesta_wrapper.h)

# Source file list
set(ARGOS3_MULTIVESTA_WRAPPER_SOURCES
  ${ARGOS3_MULTIVESTA_WRAPPER_HEADERS}
  argos3/argos_multivesta_wrapper.cpp)

# Create dynamic library
add_library(argos3_multivesta SHARED ${ARGOS3_MULTIVESTA_WRAPPER_SOURCES})
target_link_libraries(argos3_multivesta argos3core_simulator)

#
# Set up Java compilation environment
#

# Enable Java support
include(UseJava)

# Set up Java include path
set(CMAKE_JAVA_INCLUDE_PATH multivesta/multivesta.jar)

#
# Compile the Java ARGoS state and its tests
#

# ARGoS state
set(MULTIVESTA_ARGOS_STATE_SOURCES multivesta/ARGoSState.java)

# Test class
set(MULTIVESTA_ARGOS_STATE_TESTING_SOURCES testing/ARGoSMultiVestaTesting.java)

# Create test jar
set(CMAKE_JAVA_JAR_ENTRY_POINT testing/ARGoSMultiVestaTesting)
add_jar(ARGoSMultiVestaTesting
  ${MULTIVESTA_ARGOS_STATE_SOURCES}
  ${MULTIVESTA_ARGOS_STATE_TESTING_SOURCES})
# Bundle multivesta.jar with it
_bundle_multivesta_jar(ARGoSMultiVestaTesting)
