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
import com.cjwwdev.logging.Logging
import connectors.AdminConnector
import models.AccountDetails
import play.api.i18n.{Lang, MessagesApi}
import play.api.mvc._
import views.html.IncorrectPermissionsView

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait Authorisation extends Logging {
  self: BaseController =>

  val adminConnector: AdminConnector

  private type AuthorisedAction = Request[AnyContent] => AccountDetails => Future[Result]

  protected def isAuthorised(f: => AuthorisedAction): Action[AnyContent] = Action.async { implicit request =>
    request.session.get("cookieId") match {
      case Some(id) => adminConnector.getManagementUser(id) flatMap { user =>
        logger.info(s"Authenticated as ${user.managementId} on ${request.path}")
        f(request)(user)
      }
      case None     =>
        logger.warn(s"Unauthenticated user attempting to access ${request.path}; redirecting to login")
        Action(Redirect(controllers.routes.LoginController.login()))(request)
    }
  }

  protected def permissionsGuard(usersPermissions: List[String], routePermissions: List[String])
                      (f: => Future[Result])
                      (implicit request: Request[_], lang: Lang, links: Seq[NavBarLinkBuilder], navBarRoutes: Map[String, Call]): Future[Result] = {
    val allowances = usersPermissions.map(permission => routePermissions.contains(permission))
    if(allowances.contains(true)) {
      f
    } else {
      Future(Forbidden(IncorrectPermissionsView()))
    }
  }
}
