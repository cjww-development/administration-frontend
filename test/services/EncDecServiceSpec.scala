/*
 * Copyright 2018 CJWW Development
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package services

import com.cjwwdev.security.deobfuscation.DecryptionError
import helpers.services.ServiceSpec
import play.api.libs.json.{JsValue, Json, Writes}

class EncDecServiceSpec extends ServiceSpec {

  trait Test[T] {
    val testValue: String
    val result: Either[T, DecryptionError]
  }

  val testService: EncDecService = new EncDecService {}

  val testSalt = "testSalt"
  val testKey  = "testKey"

  implicit class JsonOps[T](value: T)(implicit writes: Writes[T]) {
    def toJson: JsValue = Json.toJson(value)
  }

  "DeObfuscation" should {
    "succeed" when {
      "de-obfuscate an int" in new Test[Int] {
        override val testValue: String                    = testService.encrypt(616.toJson, testSalt, testKey)
        override val result: Either[Int, DecryptionError] = testService.decrypt[Int](testValue, testSalt, testKey)

        assert(result.isLeft)
        result.left.get mustBe 616
      }

      "de-obfuscate a short" in new Test[Short] {
        override val testValue: String                      = testService.encrypt(1.toJson, testSalt, testKey)
        override val result: Either[Short, DecryptionError] = testService.decrypt[Short](testValue, testSalt, testKey)

        assert(result.isLeft)
        result.left.get mustBe 1
      }

      "de-obfuscate a byte" in new Test[Byte] {
        override val testValue: String                     = testService.encrypt(2.toJson, testSalt, testKey)
        override val result: Either[Byte, DecryptionError] = testService.decrypt[Byte](testValue, testSalt, testKey)

        assert(result.isLeft)
        result.left.get mustBe 2
      }

      "de-obfuscate a long" in new Test[Long] {
        override val testValue: String                     = testService.encrypt(123456789987654321L.toJson, testSalt, testKey)
        override val result: Either[Long, DecryptionError] = testService.decrypt[Long](testValue, testSalt, testKey)

        assert(result.isLeft)
        result.left.get mustBe 123456789987654321L
      }

      "de-obfuscate a float" in new Test[Float] {
        override val testValue: String                      = testService.encrypt(1.0F.toJson, testSalt, testKey)
        override val result: Either[Float, DecryptionError] = testService.decrypt[Float](testValue, testSalt, testKey)

        assert(result.isLeft)
        result.left.get mustBe 1.0F
      }

      "de-obfuscate a double" in new Test[Double] {
        override val testValue: String                       = testService.encrypt(1.1234.toJson, testSalt, testKey)
        override val result: Either[Double, DecryptionError] = testService.decrypt[Double](testValue, testSalt, testKey)

        assert(result.isLeft)
        result.left.get mustBe 1.1234
      }

      "de-obfuscate a big decimal" in new Test[BigDecimal] {
        override val testValue: String                           = testService.encrypt(1.567.toJson, testSalt, testKey)
        override val result: Either[BigDecimal, DecryptionError] = testService.decrypt[BigDecimal](testValue, testSalt, testKey)

        assert(result.isLeft)
        result.left.get mustBe 1.567
      }

      "de-obfuscate a boolean" in new Test[Boolean] {
        override val testValue: String                        = testService.encrypt(true.toJson, testSalt, testKey)
        override val result: Either[Boolean, DecryptionError] = testService.decrypt[Boolean](testValue, testSalt, testKey)

        assert(result.isLeft)
        assert(result.left.get)
      }

      "de-obfuscate a string" in new Test[String] {
        override val testValue: String                       = testService.encrypt("testString".toJson, testSalt, testKey)
        override val result: Either[String, DecryptionError] = testService.decrypt[String](testValue, testSalt, testKey)

        assert(result.isLeft)
        result.left.get mustBe "testString"
      }
    }

    "fail" when {
      "the input string isn't correctly padded" in new Test[Int] {
        override val testValue: String = "invalid-string"
        override val result: Either[Int, DecryptionError] = testService.decrypt[Int](testValue, testSalt, testKey)

        assert(result.isRight)
        result.right.get.message mustBe "Input length must be multiple of 16 when decrypting with padded cipher"
      }
    }
  }
}
