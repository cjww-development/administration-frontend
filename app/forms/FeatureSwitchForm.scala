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

import models.Feature
import play.api.data.Forms._
import play.api.data.format.Formatter
import play.api.data.{Form, FormError, Forms, Mapping}

object FeatureSwitchForm {

  def featureFormatter(serviceName: String): Formatter[(String, List[Feature])] = new Formatter[(String, List[Feature])] {
    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], (String, List[Feature])] = {
      val features = data.toList.filterNot(_._1 == "csrfToken") map { case (feature, state) =>
        val index = feature.replace(s"$serviceName[", "").replace("]", "")
        val cleansedState = state match {
          case "on"  => true
          case "off" => false
          case bool  => bool.toBoolean
        }
        Feature(index, cleansedState)
      }
      Right(serviceName -> features)
    }

    override def unbind(key: String, value: (String, List[Feature])): Map[String, String] = {
      value._2.map(feature => (feature.name, feature.state.toString)).toMap
    }
  }

  def features(serviceName: String): Mapping[(String, List[Feature])] = {
    Forms.of[(String, List[Feature])](featureFormatter(serviceName))
  }

  def form(serviceName: String): Form[(String, List[Feature])] = Form(
    single(
      serviceName -> features(serviceName)
    )
  )
}
