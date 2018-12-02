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

import com.cjwwdev.frontendUI.builders.NavBarLinkBuilder
import com.cjwwdev.logging.output.Logger
import connectors.AdminConnector
import models.AccountDetails
import play.api.i18n.Lang
import play.api.mvc._
import views.html.IncorrectPermissionsView

import scala.concurrent.{ExecutionContext, Future}

trait Authorisation extends Logger {
  self: BaseController =>

  val adminConnector: AdminConnector

  private type AuthorisedAction = Request[AnyContent] => AccountDetails => Future[Result]

  protected def isAuthorised(f: => AuthorisedAction)(implicit ec: ExecutionContext): Action[AnyContent] = Action.async { implicit request =>
    request.session.get("cookieId") match {
      case Some(id) => adminConnector.getManagementUser(id) flatMap { user =>
        LogAt.info(s"[isAuthorised] - Authenticated as ${user.managementId} on ${request.path}")
        f(request)(user)
      }
      case None     =>
        LogAt.warn(s"[isAuthorised] - Unauthenticated user attempting to access ${request.path}; redirecting to login")
        Action(Redirect(controllers.routes.LoginController.login()))(request)
    }
  }

  protected def permissionsGuard(routePermissions: List[String])(f: => Future[Result])(implicit request: Request[_],
                                                                                                user: AccountDetails,
                                                                                                lang: Lang,
                                                                                                links: Seq[NavBarLinkBuilder],
                                                                                                navBarRoutes: Map[String, Call]): Future[Result] = {
    val permissionGranted = (user.permissions intersect routePermissions).nonEmpty
    if(permissionGranted) f else Future.successful(Forbidden(IncorrectPermissionsView()))
  }
}
