package data.kaysaar.aotd.vok.plugins

import com.fs.starfarer.api.ui.UIComponentAPI
import com.fs.starfarer.api.ui.UIPanelAPI
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType

class ReflectionUtilis {

    companion object{
        private val fieldClass = Class.forName("java.lang.reflect.Field", false, Class::class.java.classLoader)
        private val setFieldHandle = MethodHandles.lookup().findVirtual(fieldClass, "set", MethodType.methodType(Void.TYPE, Any::class.java, Any::class.java))
        private val getFieldHandle = MethodHandles.lookup().findVirtual(fieldClass, "get", MethodType.methodType(Any::class.java, Any::class.java))
        private val getFieldNameHandle = MethodHandles.lookup().findVirtual(fieldClass, "getName", MethodType.methodType(String::class.java))
        private val setFieldAccessibleHandle = MethodHandles.lookup().findVirtual(fieldClass,"setAccessible", MethodType.methodType(Void.TYPE, Boolean::class.javaPrimitiveType))

        private val methodClass = Class.forName("java.lang.reflect.Method", false, Class::class.java.classLoader)
        private val getMethodNameHandle = MethodHandles.lookup().findVirtual(methodClass, "getName", MethodType.methodType(String::class.java))
        private val invokeMethodHandle = MethodHandles.lookup().findVirtual(methodClass, "invoke", MethodType.methodType(Any::class.java, Any::class.java, Array<Any>::class.java))
        //Taken fully form Grand Colonies code
        @JvmStatic
        fun setPrivateVariable(fieldName: String, instanceToModify: Any, newValue: Any?) {
            val fieldClass = Class.forName("java.lang.reflect.Field", false, Class::class.java.classLoader)
            val setMethod = MethodHandles.lookup()
                .findVirtual(fieldClass, "set", MethodType.methodType(Void.TYPE, Any::class.java, Any::class.java))
            val getNameMethod =
                MethodHandles.lookup().findVirtual(fieldClass, "getName", MethodType.methodType(String::class.java))
            val setAcessMethod = MethodHandles.lookup().findVirtual(
                fieldClass,
                "setAccessible",
                MethodType.methodType(Void.TYPE, Boolean::class.javaPrimitiveType)
            )

            val instancesOfFields: Array<out Any> = instanceToModify.javaClass.getDeclaredFields()
            for (obj in instancesOfFields) {
                setAcessMethod.invoke(obj, true)
                val name = getNameMethod.invoke(obj)
                if (name.toString() == fieldName) {
                    setMethod.invoke(obj, instanceToModify, newValue)
                }
            }
        }
        @JvmStatic
        fun getPrivateVariable(fieldName: String, instanceToGetFrom: Any): Any? {
            val fieldClass = Class.forName("java.lang.reflect.Field", false, Class::class.java.classLoader)
            val getMethod = MethodHandles.lookup()
                .findVirtual(fieldClass, "get", MethodType.methodType(Any::class.java, Any::class.java))
            val getNameMethod =
                MethodHandles.lookup().findVirtual(fieldClass, "getName", MethodType.methodType(String::class.java))
            val setAcessMethod = MethodHandles.lookup().findVirtual(
                fieldClass,
                "setAccessible",
                MethodType.methodType(Void.TYPE, Boolean::class.javaPrimitiveType)
            )

            val instancesOfFields: Array<out Any> = instanceToGetFrom.javaClass.declaredFields
            for (obj in instancesOfFields) {
                setAcessMethod.invoke(obj, true)
                val name = getNameMethod.invoke(obj)
                if (name.toString() == fieldName) {
                    return getMethod.invoke(obj, instanceToGetFrom)
                }
            }
            return null
        }        @JvmStatic
        fun getPrivateVariableFromSuperClass(fieldName: String, instanceToGetFrom: Any): Any? {
            val fieldClass = Class.forName("java.lang.reflect.Field", false, Class::class.java.classLoader)
            val getMethod = MethodHandles.lookup()
                .findVirtual(fieldClass, "get", MethodType.methodType(Any::class.java, Any::class.java))
            val getNameMethod =
                MethodHandles.lookup().findVirtual(fieldClass, "getName", MethodType.methodType(String::class.java))
            val setAcessMethod = MethodHandles.lookup().findVirtual(
                fieldClass,
                "setAccessible",
                MethodType.methodType(Void.TYPE, Boolean::class.javaPrimitiveType)
            )

            val instancesOfFields: Array<out Any> = instanceToGetFrom.javaClass.superclass.declaredFields
            for (obj in instancesOfFields) {
                setAcessMethod.invoke(obj, true)
                val name = getNameMethod.invoke(obj)
                if (name.toString() == fieldName) {
                    return getMethod.invoke(obj, instanceToGetFrom)
                }
            }
            return null
        }
        @JvmStatic
        fun set(fieldName: String, instanceToModify: Any, newValue: Any?)
        {
            var field: Any? = null
            try {  field = instanceToModify.javaClass.getField(fieldName) } catch (e: Throwable) {
                try {  field = instanceToModify.javaClass.getDeclaredField(fieldName) } catch (e: Throwable) { }
            }

            setFieldAccessibleHandle.invoke(field, true)
            setFieldHandle.invoke(field, instanceToModify, newValue)
        }
        @JvmStatic
        fun get(fieldName: String, instanceToGetFrom: Any): Any? {
            var field: Any? = null
            try {  field = instanceToGetFrom.javaClass.getField(fieldName) } catch (e: Throwable) {
                try {  field = instanceToGetFrom.javaClass.getDeclaredField(fieldName) } catch (e: Throwable) { }
            }

            setFieldAccessibleHandle.invoke(field, true)
            return getFieldHandle.invoke(field, instanceToGetFrom)
        }
        @JvmStatic
        fun hasMethodOfName(name: String, instance: Any, contains: Boolean = false) : Boolean {
            val instancesOfMethods: Array<out Any> = instance.javaClass.getDeclaredMethods()

            if (!contains) {
                return instancesOfMethods.any { getMethodNameHandle.invoke(it) == name }
            }
            else  {
                return instancesOfMethods.any { (getMethodNameHandle.invoke(it) as String).contains(name) }
            }
        }
        @JvmStatic
        fun hasVariableOfName(name: String, instance: Any) : Boolean {

            val instancesOfFields: Array<out Any> = instance.javaClass.getDeclaredFields()
            return instancesOfFields.any { getFieldNameHandle.invoke(it) == name }
        }
        @JvmStatic
        fun instantiate(clazz: Class<*>, vararg arguments: Any?) : Any?
        {
            val args = arguments.map { it!!::class.javaPrimitiveType ?: it!!::class.java }
            val methodType = MethodType.methodType(Void.TYPE, args)

            val constructorHandle = MethodHandles.lookup().findConstructor(clazz, methodType)
            val instance = constructorHandle.invokeWithArguments(arguments.toList())

            return instance
        }
        @JvmStatic
        fun hasMethodOfName(name: String, instance: Any) : Boolean {

            val instancesOfMethods: Array<out Any> = instance.javaClass.getDeclaredMethods()
            return instancesOfMethods.any { getMethodNameHandle.invoke(it) == name }
        }
        @JvmStatic
        fun invokeMethod(methodName: String, instance: Any, vararg arguments: Any?) : Any?
        {
            var method: Any? = null

            val clazz = instance.javaClass
            val args = arguments.map { it!!::class.javaPrimitiveType ?: it::class.java }
            val methodType = MethodType.methodType(Void.TYPE, args)

            method = clazz.getMethod(methodName, *methodType.parameterArray())

            return invokeMethodHandle.invoke(method, instance, arguments)
        }
        @JvmStatic
        fun invoke(methodName: String, instance: Any, vararg arguments: Any?, declared: Boolean = false) : Any?
        {
            var method: Any? = null

            val clazz = instance.javaClass
            val args = arguments.map { it!!::class.javaPrimitiveType ?: it::class.java }
            val methodType = MethodType.methodType(Void.TYPE, args)

            if (!declared) {
                method = clazz.getMethod(methodName, *methodType.parameterArray())
            }
            else  {
                method = clazz.getDeclaredMethod(methodName, *methodType.parameterArray())
            }

            return invokeMethodHandle.invoke(method, instance, arguments)
        }
        @JvmStatic
        //Extends the UI API by adding the required method to get the child objects of a panel, only when used within this class.
        fun UIPanelAPI.getChildrenCopy() : List<UIComponentAPI> {
            return invokeMethod("getChildrenCopy", this) as List<UIComponentAPI>
        }
    }


}