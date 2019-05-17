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

import java.util.UUID

import common.{Authorisation, FrontendController}
import connectors.AdminConnector
import javax.inject.Inject
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import views.html.UUIDGeneratorView

import scala.concurrent.{ExecutionContext, Future}

class DefaultAppIdController @Inject()(val adminConnector: AdminConnector,
                                       val controllerComponents: ControllerComponents,
                                       implicit val ec: ExecutionContext) extends AppIdController

trait AppIdController extends FrontendController with Authorisation {
  def showUUIDGenerator(): Action[AnyContent] = isAuthorised { implicit request => implicit user =>
    Future.successful(Ok(UUIDGeneratorView(UUID.randomUUID().toString)))
  }
}