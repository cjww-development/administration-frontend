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

package forms

import models.ServiceShutter
import play.api.data.Forms._
import play.api.data.format.Formatter
import play.api.data.{Form, FormError, Forms, Mapping}

case class Shutters(shutterState: List[ServiceShutter])

object ServiceShutterForm {

  def shutterFormatter: Formatter[List[ServiceShutter]] = new Formatter[List[ServiceShutter]] {
    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], List[ServiceShutter]] = {
      val shutters = data.toList.filterNot(_._1 == "csrfToken") map { case (service, state) =>
        val index = service.replace("shutterState[", "").replace("]", "")
        val cleansedState = state match {
          case "on"  => false
          case "off" => true
          case bool  => bool.toBoolean
        }
          ServiceShutter(index, cleansedState)
      }
      Right(shutters)
    }

    override def unbind(key: String, value: List[ServiceShutter]): Map[String, String] = {
      value.map(shutter => (shutter.service, shutter.value.toString)).toMap
    }
  }

  val shutter: Mapping[List[ServiceShutter]] = Forms.of[List[ServiceShutter]](shutterFormatter)

  val form: Form[Shutters] = Form(
    mapping(
      "shutterState" -> shutter
    )(Shutters.apply)(Shutters.unapply)
  )
}
