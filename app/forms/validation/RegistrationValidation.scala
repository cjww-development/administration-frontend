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

package forms.validation

import models.Registration
import play.api.data.Forms._
import play.api.data.Mapping
import play.api.data.validation.{Constraint, Invalid, Valid, ValidationError}

object RegistrationValidation extends CommonValidation {
  def xPasswordValidation: Constraint[Registration] = Constraint("constraints.password")({ urForm =>
    if(urForm.password == urForm.confirmPassword) {
      Valid
    } else if(urForm.password.isEmpty) {
      Invalid(Seq(ValidationError("You have not entered a password")))
    } else if(urForm.confirmPassword.isEmpty) {
      Invalid(Seq(ValidationError("You have not confirmed your password")))
    } else {
      Invalid(Seq(ValidationError("The passwords you have entered don't match")))
    }
  })
}
