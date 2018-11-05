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

import common.{Authorisation, FrontendController}
import connectors.AdminConnector
import forms.{ServiceShutterForm, Shutters}
import javax.inject.Inject
import models.ServiceShutter
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import services.ShutteringService
import views.html.ShutteringView

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DefaultShutteringController @Inject()(val controllerComponents: ControllerComponents,
                                            val adminConnector: AdminConnector,
                                            val shutteringService: ShutteringService) extends ShutteringController

trait ShutteringController extends FrontendController with Authorisation {

  val shutteringService: ShutteringService

  def show(): Action[AnyContent] = isAuthorised { implicit request => implicit user =>
    shutteringService.getShutterStates map { states =>
      val shutterValues = states.map { case (service, value) => ServiceShutter(service, value) }.toList
      Ok(ShutteringView(ServiceShutterForm.form.fill(Shutters(shutterValues))))
    }
  }

  def submit(): Action[AnyContent] = isAuthorised { implicit request => implicit user =>
    implicit val writesqwe = Json.writes[ServiceShutter]
    implicit val writes = Json.writes[Shutters]
    ServiceShutterForm.form.bindFromRequest.fold(
      errs  => Future(BadRequest("BAD")),
      valid => shutteringService.shutterServices(valid.shutterState.map(x => (x.service, x.value)).toMap) map {
        _ => Redirect(routes.ShutteringController.show())
      }
    )
  }
}
