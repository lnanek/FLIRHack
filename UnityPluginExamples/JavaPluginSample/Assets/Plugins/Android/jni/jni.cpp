#include <jni.h>
#undef CALL_TYPE_METHOD
#undef CALL_TYPE_METHODV
#undef CALL_TYPE_METHODA
#undef CALL_NONVIRT_TYPE_METHOD
#undef CALL_NONVIRT_TYPE_METHODV
#undef CALL_NONVIRT_TYPE_METHODA
#undef CALL_STATIC_TYPE_METHOD
#undef CALL_STATIC_TYPE_METHODV
#undef CALL_STATIC_TYPE_METHODA
#include <android/log.h>
#define trace(fmt, ...) __android_log_print(ANDROID_LOG_DEBUG, "JNI", "trace: %s (%i) " fmt, __FUNCTION__, __LINE__, __VA_ARGS__)

static __thread JNIEnv* jni_env;
extern "C"
{
	
jint GetVersion()
{ return jni_env->GetVersion(); }

jclass DefineClass(const char *name, jobject loader, const jbyte* buf,
    jsize bufLen)
{ return jni_env->DefineClass(name, loader, buf, bufLen); }

jclass FindClass(const char* name)
{ return jni_env->FindClass(name); }

jmethodID FromReflectedMethod(jobject method)
{ return jni_env->FromReflectedMethod(method); }

jfieldID FromReflectedField(jobject field)
{ return jni_env->FromReflectedField(field); }

jobject ToReflectedMethod(jclass cls, jmethodID methodID, jboolean isStatic)
{ return jni_env->ToReflectedMethod(cls, methodID, isStatic); }

jclass GetSuperclass(jclass clazz)
{ return jni_env->GetSuperclass(clazz); }

jboolean IsAssignableFrom(jclass clazz1, jclass clazz2)
{ return jni_env->IsAssignableFrom(clazz1, clazz2); }

jobject ToReflectedField(jclass cls, jfieldID fieldID, jboolean isStatic)
{ return jni_env->ToReflectedField(cls, fieldID, isStatic); }

jint Throw(jthrowable obj)
{ return jni_env->Throw(obj); }

jint ThrowNew(jclass clazz, const char* message)
{ return jni_env->ThrowNew(clazz, message); }

jthrowable ExceptionOccurred()
{ return jni_env->ExceptionOccurred(); }

void ExceptionDescribe()
{ jni_env->ExceptionDescribe(); }

void ExceptionClear()
{ jni_env->ExceptionClear(); }

void FatalError(const char* msg)
{ jni_env->FatalError(msg); }

jint PushLocalFrame(jint capacity)
{ return jni_env->PushLocalFrame(capacity); }

jobject PopLocalFrame(jobject result)
{ return jni_env->PopLocalFrame(result); }

jobject NewGlobalRef(jobject obj)
{ return jni_env->NewGlobalRef(obj); }

void DeleteGlobalRef(jobject globalRef)
{ jni_env->DeleteGlobalRef(globalRef); }

void DeleteLocalRef(jobject localRef)
{ jni_env->DeleteLocalRef(localRef); }

jboolean IsSameObject(jobject ref1, jobject ref2)
{ return jni_env->IsSameObject(ref1, ref2); }

jobject NewLocalRef(jobject ref)
{ return jni_env->NewLocalRef(ref); }

jint EnsureLocalCapacity(jint capacity)
{ return jni_env->EnsureLocalCapacity(capacity); }

jobject AllocObject(jclass clazz)
{ return jni_env->AllocObject(clazz); }

jobject NewObject(jclass clazz, jmethodID methodID, ...)
{
    va_list args;
    va_start(args, methodID);
    jobject result = jni_env->NewObjectV(clazz, methodID, args);
    va_end(args);
    return result;
}

jobject NewObjectV(jclass clazz, jmethodID methodID, va_list args)
{ 	return jni_env->NewObjectV(clazz, methodID, args); }

jobject NewObjectA(jclass clazz, jmethodID methodID, jvalue* args)
{ return jni_env->NewObjectA(clazz, methodID, args); }

jclass GetObjectClass(jobject obj)
{ return jni_env->GetObjectClass(obj); }

jboolean IsInstanceOf(jobject obj, jclass clazz)
{ return jni_env->IsInstanceOf(obj, clazz); }

jmethodID GetMethodID(jclass clazz, const char* name, const char* sig)
{ return jni_env->GetMethodID(clazz, name, sig); }

#define CALL_TYPE_METHOD(_jtype, _jname)             \
_jtype Call##_jname##Method(jobject obj, jmethodID methodID, ...)       \
{  \
    _jtype result;        \
    va_list args;         \
    va_start(args, methodID);                    \
    result = jni_env->Call##_jname##MethodV(obj, methodID,      \
                args);    \
    va_end(args);         \
    return result;        \
}
#define CALL_TYPE_METHODV(_jtype, _jname)            \
_jtype Call##_jname##MethodV(jobject obj, jmethodID methodID,           \
    va_list args)         \
{ return jni_env->Call##_jname##MethodV(obj, methodID, args); }
#define CALL_TYPE_METHODA(_jtype, _jname)            \
_jtype Call##_jname##MethodA(jobject obj, jmethodID methodID,           \
    jvalue* args)         \
{ return jni_env->Call##_jname##MethodA(obj, methodID, args); }

#define CALL_TYPE(_jtype, _jname)                    \
CALL_TYPE_METHOD(_jtype, _jname)                 \
CALL_TYPE_METHODV(_jtype, _jname)                \
CALL_TYPE_METHODA(_jtype, _jname)

CALL_TYPE(jobject, Object)
CALL_TYPE(jboolean, Boolean)
CALL_TYPE(jbyte, Byte)
CALL_TYPE(jchar, Char)
CALL_TYPE(jshort, Short)
CALL_TYPE(jint, Int)
CALL_TYPE(jlong, Long)
CALL_TYPE(jfloat, Float)
CALL_TYPE(jdouble, Double)

void CallVoidMethod(jobject obj, jmethodID methodID, ...)
{
    va_list args;
    va_start(args, methodID);
    jni_env->CallVoidMethodV(obj, methodID, args);
    va_end(args);
}
void CallVoidMethodV(jobject obj, jmethodID methodID, va_list args)
{ jni_env->CallVoidMethodV(obj, methodID, args); }
void CallVoidMethodA(jobject obj, jmethodID methodID, jvalue* args)
{ jni_env->CallVoidMethodA(obj, methodID, args); }

#define CALL_NONVIRT_TYPE_METHOD(_jtype, _jname)     \
_jtype CallNonvirtual##_jname##Method(jobject obj, jclass clazz,        \
    jmethodID methodID, ...)                     \
{  \
    _jtype result;        \
    va_list args;         \
    va_start(args, methodID);                    \
    result = jni_env->CallNonvirtual##_jname##MethodV(obj,      \
                clazz, methodID, args);          \
    va_end(args);         \
    return result;        \
}
#define CALL_NONVIRT_TYPE_METHODV(_jtype, _jname)    \
_jtype CallNonvirtual##_jname##MethodV(jobject obj, jclass clazz,       \
    jmethodID methodID, va_list args)            \
{ return jni_env->CallNonvirtual##_jname##MethodV(obj, clazz,   \
    methodID, args); }
#define CALL_NONVIRT_TYPE_METHODA(_jtype, _jname)    \
_jtype CallNonvirtual##_jname##MethodA(jobject obj, jclass clazz,       \
    jmethodID methodID, jvalue* args)            \
{ return jni_env->CallNonvirtual##_jname##MethodA(obj, clazz,   \
    methodID, args); }

#define CALL_NONVIRT_TYPE(_jtype, _jname)            \
CALL_NONVIRT_TYPE_METHOD(_jtype, _jname)         \
CALL_NONVIRT_TYPE_METHODV(_jtype, _jname)        \
CALL_NONVIRT_TYPE_METHODA(_jtype, _jname)

CALL_NONVIRT_TYPE(jobject, Object)
CALL_NONVIRT_TYPE(jboolean, Boolean)
CALL_NONVIRT_TYPE(jbyte, Byte)
CALL_NONVIRT_TYPE(jchar, Char)
CALL_NONVIRT_TYPE(jshort, Short)
CALL_NONVIRT_TYPE(jint, Int)
CALL_NONVIRT_TYPE(jlong, Long)
CALL_NONVIRT_TYPE(jfloat, Float)
CALL_NONVIRT_TYPE(jdouble, Double)

void CallNonvirtualVoidMethod(jobject obj, jclass clazz,
    jmethodID methodID, ...)
{
    va_list args;
    va_start(args, methodID);
    jni_env->CallNonvirtualVoidMethodV(obj, clazz, methodID, args);
    va_end(args);
}
void CallNonvirtualVoidMethodV(jobject obj, jclass clazz,
    jmethodID methodID, va_list args)
{ jni_env->CallNonvirtualVoidMethodV(obj, clazz, methodID, args); }
void CallNonvirtualVoidMethodA(jobject obj, jclass clazz,
    jmethodID methodID, jvalue* args)
{ jni_env->CallNonvirtualVoidMethodA(obj, clazz, methodID, args); }

jfieldID GetFieldID(jclass clazz, const char* name, const char* sig)
{ return jni_env->GetFieldID(clazz, name, sig); }

jobject GetObjectField(jobject obj, jfieldID fieldID)
{ return jni_env->GetObjectField(obj, fieldID); }
jboolean GetBooleanField(jobject obj, jfieldID fieldID)
{ return jni_env->GetBooleanField(obj, fieldID); }
jbyte GetByteField(jobject obj, jfieldID fieldID)
{ return jni_env->GetByteField(obj, fieldID); }
jchar GetCharField(jobject obj, jfieldID fieldID)
{ return jni_env->GetCharField(obj, fieldID); }
jshort GetShortField(jobject obj, jfieldID fieldID)
{ return jni_env->GetShortField(obj, fieldID); }
jint GetIntField(jobject obj, jfieldID fieldID)
{ return jni_env->GetIntField(obj, fieldID); }
jlong GetLongField(jobject obj, jfieldID fieldID)
{ return jni_env->GetLongField(obj, fieldID); }
jfloat GetFloatField(jobject obj, jfieldID fieldID)
{ return jni_env->GetFloatField(obj, fieldID); }
jdouble GetDoubleField(jobject obj, jfieldID fieldID)
{ return jni_env->GetDoubleField(obj, fieldID); }

void SetObjectField(jobject obj, jfieldID fieldID, jobject value)
{ jni_env->SetObjectField(obj, fieldID, value); }
void SetBooleanField(jobject obj, jfieldID fieldID, jboolean value)
{ jni_env->SetBooleanField(obj, fieldID, value); }
void SetByteField(jobject obj, jfieldID fieldID, jbyte value)
{ jni_env->SetByteField(obj, fieldID, value); }
void SetCharField(jobject obj, jfieldID fieldID, jchar value)
{ jni_env->SetCharField(obj, fieldID, value); }
void SetShortField(jobject obj, jfieldID fieldID, jshort value)
{ jni_env->SetShortField(obj, fieldID, value); }
void SetIntField(jobject obj, jfieldID fieldID, jint value)
{ jni_env->SetIntField(obj, fieldID, value); }
void SetLongField(jobject obj, jfieldID fieldID, jlong value)
{ jni_env->SetLongField(obj, fieldID, value); }
void SetFloatField(jobject obj, jfieldID fieldID, jfloat value)
{ jni_env->SetFloatField(obj, fieldID, value); }
void SetDoubleField(jobject obj, jfieldID fieldID, jdouble value)
{ jni_env->SetDoubleField(obj, fieldID, value); }

jmethodID GetStaticMethodID(jclass clazz, const char* name, const char* sig)
{ return jni_env->GetStaticMethodID(clazz, name, sig); }

#define CALL_STATIC_TYPE_METHOD(_jtype, _jname)      \
_jtype CallStatic##_jname##Method(jclass clazz, jmethodID methodID,     \
    ...)                  \
{  \
    _jtype result;        \
    va_list args;         \
    va_start(args, methodID);                    \
    result = jni_env->CallStatic##_jname##MethodV(clazz,        \
                methodID, args);                 \
    va_end(args);         \
    return result;        \
}
#define CALL_STATIC_TYPE_METHODV(_jtype, _jname)     \
_jtype CallStatic##_jname##MethodV(jclass clazz, jmethodID methodID,    \
    va_list args)         \
{ return jni_env->CallStatic##_jname##MethodV(clazz, methodID,  \
    args); }
#define CALL_STATIC_TYPE_METHODA(_jtype, _jname)     \
_jtype CallStatic##_jname##MethodA(jclass clazz, jmethodID methodID,    \
    jvalue* args)         \
{ return jni_env->CallStatic##_jname##MethodA(clazz, methodID,  \
    args); }

#define CALL_STATIC_TYPE(_jtype, _jname)             \
CALL_STATIC_TYPE_METHOD(_jtype, _jname)          \
CALL_STATIC_TYPE_METHODV(_jtype, _jname)         \
CALL_STATIC_TYPE_METHODA(_jtype, _jname)

CALL_STATIC_TYPE(jobject, Object)
CALL_STATIC_TYPE(jboolean, Boolean)
CALL_STATIC_TYPE(jbyte, Byte)
CALL_STATIC_TYPE(jchar, Char)
CALL_STATIC_TYPE(jshort, Short)
CALL_STATIC_TYPE(jint, Int)
CALL_STATIC_TYPE(jlong, Long)
CALL_STATIC_TYPE(jfloat, Float)
CALL_STATIC_TYPE(jdouble, Double)

void CallStaticVoidMethod(jclass clazz, jmethodID methodID, ...)
{
    va_list args;
    va_start(args, methodID);
    jni_env->CallStaticVoidMethodV(clazz, methodID, args);
    va_end(args);
}
void CallStaticVoidMethodV(jclass clazz, jmethodID methodID, va_list args)
{ jni_env->CallStaticVoidMethodV(clazz, methodID, args); }
void CallStaticVoidMethodA(jclass clazz, jmethodID methodID, jvalue* args)
{ jni_env->CallStaticVoidMethodA(clazz, methodID, args); }

jfieldID GetStaticFieldID(jclass clazz, const char* name, const char* sig)
{ return jni_env->GetStaticFieldID(clazz, name, sig); }

jobject GetStaticObjectField(jclass clazz, jfieldID fieldID)
{ return jni_env->GetStaticObjectField(clazz, fieldID); }
jboolean GetStaticBooleanField(jclass clazz, jfieldID fieldID)
{ return jni_env->GetStaticBooleanField(clazz, fieldID); }
jbyte GetStaticByteField(jclass clazz, jfieldID fieldID)
{ return jni_env->GetStaticByteField(clazz, fieldID); }
jchar GetStaticCharField(jclass clazz, jfieldID fieldID)
{ return jni_env->GetStaticCharField(clazz, fieldID); }
jshort GetStaticShortField(jclass clazz, jfieldID fieldID)
{ return jni_env->GetStaticShortField(clazz, fieldID); }
jint GetStaticIntField(jclass clazz, jfieldID fieldID)
{ return jni_env->GetStaticIntField(clazz, fieldID); }
jlong GetStaticLongField(jclass clazz, jfieldID fieldID)
{ return jni_env->GetStaticLongField(clazz, fieldID); }
jfloat GetStaticFloatField(jclass clazz, jfieldID fieldID)
{ return jni_env->GetStaticFloatField(clazz, fieldID); }
jdouble GetStaticDoubleField(jclass clazz, jfieldID fieldID)
{ return jni_env->GetStaticDoubleField(clazz, fieldID); }

void SetStaticObjectField(jclass clazz, jfieldID fieldID, jobject value)
{ jni_env->SetStaticObjectField(clazz, fieldID, value); }
void SetStaticBooleanField(jclass clazz, jfieldID fieldID, jboolean value)
{ jni_env->SetStaticBooleanField(clazz, fieldID, value); }
void SetStaticByteField(jclass clazz, jfieldID fieldID, jbyte value)
{ jni_env->SetStaticByteField(clazz, fieldID, value); }
void SetStaticCharField(jclass clazz, jfieldID fieldID, jchar value)
{ jni_env->SetStaticCharField(clazz, fieldID, value); }
void SetStaticShortField(jclass clazz, jfieldID fieldID, jshort value)
{ jni_env->SetStaticShortField(clazz, fieldID, value); }
void SetStaticIntField(jclass clazz, jfieldID fieldID, jint value)
{ jni_env->SetStaticIntField(clazz, fieldID, value); }
void SetStaticLongField(jclass clazz, jfieldID fieldID, jlong value)
{ jni_env->SetStaticLongField(clazz, fieldID, value); }
void SetStaticFloatField(jclass clazz, jfieldID fieldID, jfloat value)
{ jni_env->SetStaticFloatField(clazz, fieldID, value); }
void SetStaticDoubleField(jclass clazz, jfieldID fieldID, jdouble value)
{ jni_env->SetStaticDoubleField(clazz, fieldID, value); }

jstring NewString(const jchar* unicodeChars, jsize len)
{ return jni_env->NewString(unicodeChars, len); }

jsize GetStringLength(jstring string)
{ return jni_env->GetStringLength(string); }

const jchar* GetStringChars(jstring string, jboolean* isCopy)
{ return jni_env->GetStringChars(string, isCopy); }

void ReleaseStringChars(jstring string, const jchar* chars)
{ jni_env->ReleaseStringChars(string, chars); }

jstring NewStringUTF(const char* bytes)
{ return jni_env->NewStringUTF(bytes); }

jsize GetStringUTFLength(jstring string)
{ return jni_env->GetStringUTFLength(string); }

const char* GetStringUTFChars(jstring string, jboolean* isCopy)
{ return jni_env->GetStringUTFChars(string, isCopy); }

void ReleaseStringUTFChars(jstring string, const char* utf)
{ jni_env->ReleaseStringUTFChars(string, utf); }

jsize GetArrayLength(jarray array)
{ return jni_env->GetArrayLength(array); }

jobjectArray NewObjectArray(jsize length, jclass elementClass,
    jobject initialElement)
{ return jni_env->NewObjectArray(length, elementClass,
    initialElement); }

jobject GetObjectArrayElement(jobjectArray array, jsize index)
{ return jni_env->GetObjectArrayElement(array, index); }

void SetObjectArrayElement(jobjectArray array, jsize index, jobject value)
{ jni_env->SetObjectArrayElement(array, index, value); }

jbooleanArray NewBooleanArray(jsize length)
{ return jni_env->NewBooleanArray(length); }
jbyteArray NewByteArray(jsize length)
{ return jni_env->NewByteArray(length); }
jcharArray NewCharArray(jsize length)
{ return jni_env->NewCharArray(length); }
jshortArray NewShortArray(jsize length)
{ return jni_env->NewShortArray(length); }
jintArray NewIntArray(jsize length)
{ return jni_env->NewIntArray(length); }
jlongArray NewLongArray(jsize length)
{ return jni_env->NewLongArray(length); }
jfloatArray NewFloatArray(jsize length)
{ return jni_env->NewFloatArray(length); }
jdoubleArray NewDoubleArray(jsize length)
{ return jni_env->NewDoubleArray(length); }

jboolean* GetBooleanArrayElements(jbooleanArray array, jboolean* isCopy)
{ return jni_env->GetBooleanArrayElements(array, isCopy); }
jbyte* GetByteArrayElements(jbyteArray array, jboolean* isCopy)
{ return jni_env->GetByteArrayElements(array, isCopy); }
jchar* GetCharArrayElements(jcharArray array, jboolean* isCopy)
{ return jni_env->GetCharArrayElements(array, isCopy); }
jshort* GetShortArrayElements(jshortArray array, jboolean* isCopy)
{ return jni_env->GetShortArrayElements(array, isCopy); }
jint* GetIntArrayElements(jintArray array, jboolean* isCopy)
{ return jni_env->GetIntArrayElements(array, isCopy); }
jlong* GetLongArrayElements(jlongArray array, jboolean* isCopy)
{ return jni_env->GetLongArrayElements(array, isCopy); }
jfloat* GetFloatArrayElements(jfloatArray array, jboolean* isCopy)
{ return jni_env->GetFloatArrayElements(array, isCopy); }
jdouble* GetDoubleArrayElements(jdoubleArray array, jboolean* isCopy)
{ return jni_env->GetDoubleArrayElements(array, isCopy); }

void ReleaseBooleanArrayElements(jbooleanArray array, jboolean* elems,
    jint mode)
{ jni_env->ReleaseBooleanArrayElements(array, elems, mode); }
void ReleaseByteArrayElements(jbyteArray array, jbyte* elems,
    jint mode)
{ jni_env->ReleaseByteArrayElements(array, elems, mode); }
void ReleaseCharArrayElements(jcharArray array, jchar* elems,
    jint mode)
{ jni_env->ReleaseCharArrayElements(array, elems, mode); }
void ReleaseShortArrayElements(jshortArray array, jshort* elems,
    jint mode)
{ jni_env->ReleaseShortArrayElements(array, elems, mode); }
void ReleaseIntArrayElements(jintArray array, jint* elems,
    jint mode)
{ jni_env->ReleaseIntArrayElements(array, elems, mode); }
void ReleaseLongArrayElements(jlongArray array, jlong* elems,
    jint mode)
{ jni_env->ReleaseLongArrayElements(array, elems, mode); }
void ReleaseFloatArrayElements(jfloatArray array, jfloat* elems,
    jint mode)
{ jni_env->ReleaseFloatArrayElements(array, elems, mode); }
void ReleaseDoubleArrayElements(jdoubleArray array, jdouble* elems,
    jint mode)
{ jni_env->ReleaseDoubleArrayElements(array, elems, mode); }

void GetBooleanArrayRegion(jbooleanArray array, jsize start, jsize len,
    jboolean* buf)
{ jni_env->GetBooleanArrayRegion(array, start, len, buf); }
void GetByteArrayRegion(jbyteArray array, jsize start, jsize len,
    jbyte* buf)
{ jni_env->GetByteArrayRegion(array, start, len, buf); }
void GetCharArrayRegion(jcharArray array, jsize start, jsize len,
    jchar* buf)
{ jni_env->GetCharArrayRegion(array, start, len, buf); }
void GetShortArrayRegion(jshortArray array, jsize start, jsize len,
    jshort* buf)
{ jni_env->GetShortArrayRegion(array, start, len, buf); }
void GetIntArrayRegion(jintArray array, jsize start, jsize len,
    jint* buf)
{ jni_env->GetIntArrayRegion(array, start, len, buf); }
void GetLongArrayRegion(jlongArray array, jsize start, jsize len,
    jlong* buf)
{ jni_env->GetLongArrayRegion(array, start, len, buf); }
void GetFloatArrayRegion(jfloatArray array, jsize start, jsize len,
    jfloat* buf)
{ jni_env->GetFloatArrayRegion(array, start, len, buf); }
void GetDoubleArrayRegion(jdoubleArray array, jsize start, jsize len,
    jdouble* buf)
{ jni_env->GetDoubleArrayRegion(array, start, len, buf); }

void SetBooleanArrayRegion(jbooleanArray array, jsize start, jsize len,
    const jboolean* buf)
{ jni_env->SetBooleanArrayRegion(array, start, len, buf); }
void SetByteArrayRegion(jbyteArray array, jsize start, jsize len,
    const jbyte* buf)
{ jni_env->SetByteArrayRegion(array, start, len, buf); }
void SetCharArrayRegion(jcharArray array, jsize start, jsize len,
    const jchar* buf)
{ jni_env->SetCharArrayRegion(array, start, len, buf); }
void SetShortArrayRegion(jshortArray array, jsize start, jsize len,
    const jshort* buf)
{ jni_env->SetShortArrayRegion(array, start, len, buf); }
void SetIntArrayRegion(jintArray array, jsize start, jsize len,
    const jint* buf)
{ jni_env->SetIntArrayRegion(array, start, len, buf); }
void SetLongArrayRegion(jlongArray array, jsize start, jsize len,
    const jlong* buf)
{ jni_env->SetLongArrayRegion(array, start, len, buf); }
void SetFloatArrayRegion(jfloatArray array, jsize start, jsize len,
    const jfloat* buf)
{ jni_env->SetFloatArrayRegion(array, start, len, buf); }
void SetDoubleArrayRegion(jdoubleArray array, jsize start, jsize len,
    const jdouble* buf)
{ jni_env->SetDoubleArrayRegion(array, start, len, buf); }

jint RegisterNatives(jclass clazz, const JNINativeMethod* methods,
    jint nMethods)
{ return jni_env->RegisterNatives(clazz, methods, nMethods); }

jint UnregisterNatives(jclass clazz)
{ return jni_env->UnregisterNatives(clazz); }

jint MonitorEnter(jobject obj)
{ return jni_env->MonitorEnter(obj); }

jint MonitorExit(jobject obj)
{ return jni_env->MonitorExit(obj); }

jint GetJavaVM(JavaVM** vm)
{ return jni_env->GetJavaVM(vm); }

void GetStringRegion(jstring str, jsize start, jsize len, jchar* buf)
{ jni_env->GetStringRegion(str, start, len, buf); }

void GetStringUTFRegion(jstring str, jsize start, jsize len, char* buf)
{ return jni_env->GetStringUTFRegion(str, start, len, buf); }

void* GetPrimitiveArrayCritical(jarray array, jboolean* isCopy)
{ return jni_env->GetPrimitiveArrayCritical(array, isCopy); }

void ReleasePrimitiveArrayCritical(jarray array, void* carray, jint mode)
{ jni_env->ReleasePrimitiveArrayCritical(array, carray, mode); }

const jchar* GetStringCritical(jstring string, jboolean* isCopy)
{ return jni_env->GetStringCritical(string, isCopy); }

void ReleaseStringCritical(jstring string, const jchar* carray)
{ jni_env->ReleaseStringCritical(string, carray); }

jweak NewWeakGlobalRef(jobject obj)
{ return jni_env->NewWeakGlobalRef(obj); }

void DeleteWeakGlobalRef(jweak obj)
{ jni_env->DeleteWeakGlobalRef(obj); }

jboolean ExceptionCheck()
{ return jni_env->ExceptionCheck(); }

jobject NewDirectByteBuffer(void* address, jlong capacity)
{ return jni_env->NewDirectByteBuffer(address, capacity); }

void* GetDirectBufferAddress(jobject buf)
{ return jni_env->GetDirectBufferAddress(buf); }

jlong GetDirectBufferCapacity(jobject buf)
{ return jni_env->GetDirectBufferCapacity(buf); }

/* added in JNI 1.6 */
jobjectRefType GetObjectRefType(jobject obj)
{ return jni_env->GetObjectRefType(obj); }
}

JavaVM* java_vm;

extern "C"
{
	
jint DestroyJavaVM()
{ return java_vm->DestroyJavaVM(); }
jint AttachCurrentThread()
{	int ret = java_vm->AttachCurrentThread(&jni_env, 0);
	if (ret < 0)	// Returns ?0? on success; returns a negative number on failure.
		__android_log_assert("AttachCurrentThread failed!", "JNI", "AttachCurrentThread returned %i", ret);
	return ret;
}
jint DetachCurrentThread()
{ return java_vm->DetachCurrentThread(); }
jint GetEnv(jint version)
{	return java_vm->GetEnv((void**)&jni_env, version); }
jint AttachCurrentThreadAsDaemon()
{ return java_vm->AttachCurrentThreadAsDaemon(&jni_env, 0); }

jint JNI_OnLoad(JavaVM* vm, void* reserved)
{
	java_vm = vm;
	AttachCurrentThread();
	return JNI_VERSION_1_6;		// minimum JNI version
}
	
}
