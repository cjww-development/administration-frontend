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

import forms.validation.CommonValidation
import play.api.data.{Form, Mapping}
import play.api.data.Forms._
import play.api.data.validation.{Constraint, Invalid, Valid, ValidationError}

object CustomDataSecurityForm extends CommonValidation {

  val form = Form(tuple(
    "mode" -> radioButtonValidation(validData = List("enc", "dec")),
    "salt" -> hasTextBeenEntered("salt"),
    "key"  -> hasTextBeenEntered("key"),
    "data" -> hasTextBeenEntered("data")
  ))

  private def radioButtonValidation(validData: List[String]): Mapping[String] = {
    val radioButtonConstraint: Constraint[String] = Constraint("constraint.displayName")({ displayName =>
      val errors = if(validData.contains(displayName)) Nil else Seq(ValidationError(""))
      if(errors.isEmpty) Valid else Invalid(errors)
    })
    text.verifying(radioButtonConstraint)
  }
}
