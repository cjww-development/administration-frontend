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
import forms.EventLogFilteringForm
import javax.inject.Inject
import models.EventFiltering
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import services.EventLogService
import views.html.UserEventLogView

import scala.concurrent.{ExecutionContext, Future}

class DefaultUserEventLogController @Inject()(val controllerComponents: ControllerComponents,
                                              val eventLogService: EventLogService,
                                              val adminConnector: AdminConnector) extends UserEventLogController {
  override implicit val ec: ExecutionContext = controllerComponents.executionContext
}

trait UserEventLogController extends FrontendController with Authorisation {

  val eventLogService: EventLogService

  def show(): Action[AnyContent] = isAuthorised { implicit req => implicit user =>
    eventLogService.getEventLogs(EventFiltering.empty) map { events =>
      Ok(UserEventLogView("", events, EventLogFilteringForm.form))
    }
  }

  def submit(): Action[AnyContent] = isAuthorised { implicit req => implicit user =>
    EventLogFilteringForm.form.bindFromRequest.fold(
      err     => Future.successful(BadRequest(UserEventLogView("", List(), err))),
      filters => eventLogService.getEventLogs(filters) map { events =>
        Ok(UserEventLogView(filters.userName, events, EventLogFilteringForm.form.fill(filters)))
      }
    )
  }
}
