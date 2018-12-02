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

import com.cjwwdev.config.ConfigurationLoader
import common.{Authorisation, FrontendController}
import connectors.AdminConnector
import forms.FeatureSwitchForm
import javax.inject.Inject
import play.api.mvc._
import services.FeatureSwitchService
import views.html.FeatureSwitchView

import scala.concurrent.{ExecutionContext, Future}

class DefaultFeatureSwitchController @Inject()(val controllerComponents: ControllerComponents,
                                               val adminConnector: AdminConnector,
                                               val config: ConfigurationLoader,
                                               val featureSwitchService: FeatureSwitchService,
                                               implicit val ec: ExecutionContext) extends FeatureSwitchController {
  override val services: List[String] = config.get[Seq[String]]("microservice.features.services").toList
}

trait FeatureSwitchController extends FrontendController with Authorisation {

  val featureSwitchService: FeatureSwitchService

  val services: List[String]

  def show(): Action[AnyContent] = isAuthorised { implicit request => implicit user =>
    featureSwitchService.getFeatures map { featureSet =>
      val featuresAndForm = featureSet.map { case (service, features) => service -> (features, FeatureSwitchForm.form(service)) }
      Ok(FeatureSwitchView(featuresAndForm))
    }
  }

  def submit(): Action[AnyContent] = isAuthorised { implicit request => implicit user =>
    Future
      .sequence(services map processFeaturesForService)
      .map(_ => Redirect(routes.FeatureSwitchController.show()))
  }

  private def processFeaturesForService(serviceName: String)(implicit request: Request[_]): Future[Boolean] = {
    FeatureSwitchForm.form(serviceName).bindFromRequest.fold(
      _     => Future.successful(false),
      valid => featureSwitchService.setFeature(valid._1, valid._2) map(_ => true)
    )
  }
}
