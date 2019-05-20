/*
 * Copyright 2019 CJWW Development
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

package models

import com.cjwwdev.security.deobfuscation.{DeObfuscation, DeObfuscator, DecryptionError}
import play.api.libs.json._
import play.api.libs.functional.syntax._

case class Feature(name: String, state: Boolean)

object Feature {
  implicit val reads: Reads[Feature] = (
    (__ \ "feature").read[String] and
    (__ \ "state").read[Boolean]
  )(Feature.apply _)

  implicit val deObfuscator: DeObfuscator[List[Feature]] = new DeObfuscator[List[Feature]] {
    override def decrypt(value: String): Either[List[Feature], DecryptionError] = {
      DeObfuscation.deObfuscate[List[Feature]](value)
    }
  }
}
