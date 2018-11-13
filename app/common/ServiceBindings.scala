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

import com.cjwwdev.config.{ConfigurationLoader, DefaultConfigurationLoader}
import connectors._
import controllers._
import play.api.{Configuration, Environment}
import play.api.inject.{Binding, Module}
import services._

class ServiceBindings extends Module {
  override def bindings(environment: Environment, configuration: Configuration): Seq[Binding[_]] =
    bindOther() ++ bindConnectors() ++ bindServices() ++ bindControllers()

  private def bindOther(): Seq[Binding[_]] = Seq(
    bind(classOf[ConfigurationLoader]).to(classOf[DefaultConfigurationLoader]).eagerly()
  )

  private def bindConnectors(): Seq[Binding[_]] = Seq(
    bind(classOf[AdminConnector]).to(classOf[DefaultAdminConnector]).eagerly(),
    bind(classOf[ShutteringConnector]).to(classOf[DefaultShutteringConnector]).eagerly(),
    bind(classOf[FeatureSwitchConnector]).to(classOf[DefaultFeatureSwitchConnector]).eagerly(),
    bind(classOf[HealthConnector]).to(classOf[DefaultHealthConnector]).eagerly()
  )

  private def bindServices(): Seq[Binding[_]] = Seq(
    bind(classOf[LoginService]).to(classOf[DefaultLoginService]).eagerly(),
    bind(classOf[ShutteringService]).to(classOf[DefaultShutteringService]).eagerly(),
    bind(classOf[FeatureSwitchService]).to(classOf[DefaultFeatureSwitchService]).eagerly(),
    bind(classOf[HealthService]).to(classOf[DefaultHealthService]).eagerly()
  )

  private def bindControllers(): Seq[Binding[_]] = Seq(
    bind(classOf[LoginController]).to(classOf[DefaultLoginController]).eagerly(),
    bind(classOf[DashboardController]).to(classOf[DefaultDashboardController]).eagerly(),
    bind(classOf[EncDecController]).to(classOf[DefaultEncDecController]).eagerly(),
    bind(classOf[HeadersController]).to(classOf[DefaultHeadersController]).eagerly(),
    bind(classOf[AppIdController]).to(classOf[DefaultAppIdController]).eagerly(),
    bind(classOf[ShutteringController]).to(classOf[DefaultShutteringController]).eagerly(),
    bind(classOf[FeatureSwitchController]).to(classOf[DefaultFeatureSwitchController]).eagerly(),
    bind(classOf[AppStateController]).to(classOf[DefaultAppStateController]).eagerly()
  )
}
