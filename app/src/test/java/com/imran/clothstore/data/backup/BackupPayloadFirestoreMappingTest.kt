package com.imran.clothstore.data.backup

import com.google.firebase.firestore.PropertyName
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class BackupPayloadFirestoreMappingTest {

    @Test
    fun isPrefixedFieldsDeclareExactFirestoreNamesOnGettersAndSetters() {
        assertNotNull(
            "Firestore deserialization requires a no-arg constructor",
            BackupPayload::class.java.getDeclaredConstructor()
        )

        val expectedNames = listOf(
            "is_c1_list",
            "is_c2_list",
            "is_c3_list",
            "is_c4_list",
            "is_deleted_ids"
        )

        expectedNames.forEach { expectedName ->
            val getter = BackupPayload::class.java.methods.firstOrNull { method ->
                method.parameterCount == 0 &&
                    method.returnType != Void.TYPE &&
                    method.getAnnotation(PropertyName::class.java)?.value == expectedName
            }
            assertNotNull("Firestore getter alias missing for $expectedName", getter)
            assertEquals(expectedName, getter!!.getAnnotation(PropertyName::class.java).value)

            val setter = BackupPayload::class.java.methods.firstOrNull { method ->
                method.parameterCount == 1 &&
                    method.returnType == Void.TYPE &&
                    method.getAnnotation(PropertyName::class.java)?.value == expectedName
            }
            assertNotNull("Firestore setter alias missing for $expectedName", setter)
            assertEquals(expectedName, setter!!.getAnnotation(PropertyName::class.java).value)
        }
    }
}
