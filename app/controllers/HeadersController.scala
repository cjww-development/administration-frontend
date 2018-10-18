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

package controllers

import com.cjwwdev.http.headers.HeaderPackage
import com.cjwwdev.implicits.ImplicitDataSecurity._
import common.{Authorisation, FrontendController, Permissions}
import connectors.AdminConnector
import forms.HeadersForm
import javax.inject.Inject
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import views.html.GenerateHeadersView

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class DefaultHeadersController @Inject()(val adminConnector: AdminConnector,
                                         val controllerComponents: ControllerComponents) extends HeadersController

trait HeadersController extends FrontendController with Authorisation {

  def headers(): Action[AnyContent] = isAuthorised { implicit request => implicit user =>
    permissionsGuard(Permissions.headers) {
      Future(Ok(GenerateHeadersView(HeadersForm.form)))
    }
  }

  def headersSubmit(): Action[AnyContent] = isAuthorised { implicit request => implicit user =>
    permissionsGuard(Permissions.headers) {
      HeadersForm.form.bindFromRequest.fold(
        errors => Future(BadRequest(GenerateHeadersView(errors))),
        tuple  => {
          val (appId, cookieId) = tuple
          val headerPackage: Option[String] = Some(HeaderPackage(appId, cookieId).encrypt)
          Future(Ok(GenerateHeadersView(HeadersForm.form.fill(tuple), header = headerPackage)))
        }
      )
    }
  }
}