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

import common.FrontendController
import forms.LoginForm
import javax.inject.Inject
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import services.LoginService
import views.html.LoginView

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class DefaultLoginController @Inject()(val controllerComponents: ControllerComponents,
                                       val loginService: LoginService) extends LoginController

trait LoginController extends FrontendController {

  val loginService: LoginService

  def login(): Action[AnyContent] = Action { implicit request =>
    Ok(LoginView(LoginForm.form))
  }

  def loginSubmit(): Action[AnyContent] = Action.async { implicit request =>
    LoginForm.form.bindFromRequest.fold(
      errors => Future(BadRequest(LoginView(errors))),
      creds  => loginService.processLoginAttempt(creds) map {
        case Some(session) => Redirect(controllers.routes.DashboardController.dashboard()).withSession(session)
        case None          => BadRequest(LoginView(LoginForm.form.fill(creds)
          .withError("username", messagesApi("pages.user-login.text-entry.login-error"))
          .withError("password", "")
        ))
      }
    )
  }

  def signOut(): Action[AnyContent] = Action {
    Redirect(controllers.routes.LoginController.login()).withNewSession
  }
}