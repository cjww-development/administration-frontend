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

package common

import models.AccountDetails
import play.twirl.api.Html

object Permissions {
  val rootOnly   = List("all")
  val encDec     = rootOnly ++ List("encdec")
  val headers    = rootOnly ++ List("headers")
  val shuttering = rootOnly ++ List("shuttering")


  def permissionGranted(pagePermissions: List[String])(implicit user: AccountDetails): Boolean = {
    (pagePermissions intersect user.permissions).nonEmpty
  }

  def permissionViewGuard(pagePermissions: List[String])(html: => Html)(implicit user: AccountDetails): Html = {
    if((pagePermissions intersect user.permissions).nonEmpty) html else Html("")
  }
}
