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

package common

import com.cjwwdev.frontendUI.builders.NavBarLinkBuilder
import play.api.mvc.{Call, RequestHeader}
import controllers.routes

trait AppConfig {
  implicit def serviceLinks(implicit requestHeader: RequestHeader): Seq[NavBarLinkBuilder] = {
    Seq(NavBarLinkBuilder(routes.DashboardController.dashboard().url, "glyphicon-home", "Home", "home"))
  }

  implicit def standardNavBarRoutes(implicit requestHeader: RequestHeader): Map[String, Call] = Map(
    "navBarLogo"   -> routes.Assets.versioned("images/logo.png"),
    "globalAssets" -> routes.Assets.versioned("stylesheets/global-assets.css"),
    "favicon"      -> routes.Assets.versioned("images/favicon.ico"),
    "login"        -> routes.LoginController.login(),
    "dashboard"    -> routes.DashboardController.dashboard(),
    "signOut"      -> routes.LoginController.signOut()
  )
}
