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

import common.{Authorisation, FrontendController}
import connectors.AdminConnector
import javax.inject.Inject
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import services.HealthService
import views.html.AppStateView

import scala.concurrent.ExecutionContext

class DefaultAppStateController @Inject()(val controllerComponents: ControllerComponents,
                                          val adminConnector: AdminConnector,
                                          val healthService: HealthService,
                                          implicit val ec: ExecutionContext) extends AppStateController

trait AppStateController extends FrontendController with Authorisation {

  val healthService: HealthService

  def show(): Action[AnyContent] = isAuthorised { implicit request => implicit user =>
    healthService.getServicesHealth map {
      map => Ok(AppStateView(map))
    }
  }
}
