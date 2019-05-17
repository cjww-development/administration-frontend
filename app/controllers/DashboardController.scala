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

package controllers

import javax.inject.Inject
import common.{Authorisation, FrontendController, Permissions}
import connectors.AdminConnector
import forms.RegistrationForm
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import views.html.{DashboardView, RegisterNewUserView, UserView, UsersOverviewView}
import views.html.StandardErrorView

import scala.concurrent.{ExecutionContext, Future}

class DefaultDashboardController @Inject()(val controllerComponents: ControllerComponents,
                                           val adminConnector: AdminConnector,
                                           implicit val ec: ExecutionContext) extends DashboardController

trait DashboardController extends FrontendController with Authorisation {

  def dashboard(): Action[AnyContent] = isAuthorised { implicit request => implicit user =>
    Future.successful(Ok(DashboardView()))
  }

  def registerUser(): Action[AnyContent] = isAuthorised { implicit request => implicit user =>
    permissionsGuard(Permissions.rootOnly) {
      Future.successful(Ok(RegisterNewUserView(RegistrationForm.form)))
    }
  }

  def registerUserSubmit(): Action[AnyContent] = isAuthorised { implicit request => implicit user =>
    permissionsGuard(Permissions.rootOnly) {
      RegistrationForm.form.bindFromRequest.fold(
        errors  => Future.successful(BadRequest(RegisterNewUserView(errors))),
        newUser => adminConnector.registerNewUser(newUser) map {
          if(_) {
            Redirect(routes.DashboardController.usersOverview())
          } else {
            InternalServerError(StandardErrorView(messagesApi("pages.registration.errors.failed-register")))
          }
        }
      )
    }
  }

  def usersOverview(): Action[AnyContent] = isAuthorised { implicit request => implicit user =>
    permissionsGuard(Permissions.rootOnly) {
      adminConnector.getAllManagementUsers map {
        users => Ok(UsersOverviewView(users))
      }
    }
  }

  def viewUser(managementId: String): Action[AnyContent] = isAuthorised { implicit request => implicit user =>
    permissionsGuard(Permissions.rootOnly) {
      adminConnector.getManagementUser(managementId) map {
        acc => Ok(UserView(acc))
      }
    }
  }

  def deleteUser(managementId: String): Action[AnyContent] = isAuthorised { implicit request => implicit user =>
    permissionsGuard(Permissions.rootOnly) {
      adminConnector.deleteManagementUser(managementId) map {
        _ => Redirect(routes.DashboardController.usersOverview())
      }
    }
  }
}
