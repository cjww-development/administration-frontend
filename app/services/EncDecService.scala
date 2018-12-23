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

import java.security.MessageDigest
import java.util

import com.cjwwdev.security.deobfuscation.DecryptionError
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject
import org.apache.commons.codec.binary.Base64
import play.api.libs.json.{JsPath, JsValue, Json, JsonValidationError, Reads}

import scala.reflect.ClassTag
import scala.util.Try

class DefaultEncDecService @Inject()() extends EncDecService

trait EncDecService {

  def encrypt(json: JsValue, salt: String, key: String): String = {
    val bytes = json.toString.getBytes("UTF-8")
    cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec(salt, key))
    Base64.encodeBase64URLSafeString(cipher.doFinal(bytes))
  }

  def decrypt[T](value: String, salt: String, key: String)(implicit reads: Reads[T], tag: ClassTag[T]): Either[T, DecryptionError] = {
    cipher.init(Cipher.DECRYPT_MODE, secretKeySpec(salt, key))
    Try(cipher.doFinal(Base64.decodeBase64(value))).fold(
      fetchError[T],
      array => jsonToType[T](new String(array))
    )
  }

  private def fetchError[T](throwable: Throwable)(implicit tag: ClassTag[T]): Right[T, DecryptionError] = {
    val error = DecryptionError(throwable.getMessage)
    error.logError
    Right(error)
  }

  private def readableJsError[T](errors: Seq[(JsPath, Seq[JsonValidationError])])(implicit tag: ClassTag[T]): DecryptionError = {
    val seq = errors map { case (path, error) => Json.obj(path.toJsonString.replace("obj.", "") -> error.map(_.message).mkString) }
    val decryptionError = DecryptionError(Json.prettyPrint(seq.foldLeft(Json.obj())((obj, a) => obj.deepMerge(a))))
    decryptionError.logValidateError[T]
    decryptionError
  }

  private def jsonToType[T](value: String)(implicit reads: Reads[T], tag: ClassTag[T]): Either[T, DecryptionError] = {
    Json.parse(value).validate[T].fold(
      err  => Right(readableJsError[T](err)),
      data => Left(data)
    )
  }

  private val LENGTH: Int = 16

  private val cipher: Cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")

  private def secretKeySpec(salt: String, key: String): SecretKeySpec = {
    val sha512 = MessageDigest.getInstance("SHA-512").digest(s"$salt$key".getBytes("UTF-8"))
    new SecretKeySpec(util.Arrays.copyOf(sha512, LENGTH), "AES")
  }
}
