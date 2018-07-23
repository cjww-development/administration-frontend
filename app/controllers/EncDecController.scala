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

import com.cjwwdev.security.encryption.{DataSecurity, SHA512}
import common.{Authorisation, FrontendController, Permissions}
import connectors.AdminConnector
import forms.{DataSecurityForm, SHA512Form}
import javax.inject.Inject
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import views.html.{DataSecurityView, EncDecOptionsView, SHA512View}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class DefaultEncDecController @Inject()(val adminConnector: AdminConnector,
                                        val controllerComponents: ControllerComponents) extends EncDecController

trait EncDecController extends FrontendController with Authorisation {

  def showEncDecOptions(): Action[AnyContent] = isAuthorised { implicit request => implicit user =>
    permissionsGuard(Permissions.encDec) {
      Future(Ok(EncDecOptionsView()))
    }
  }

  def showSHA512(): Action[AnyContent] = isAuthorised { implicit request => implicit user =>
    permissionsGuard(Permissions.encDec) {
      Future(Ok(SHA512View(SHA512Form.form)))
    }
  }

  def submitSHA512(): Action[AnyContent] = isAuthorised { implicit request => implicit user =>
    permissionsGuard(Permissions.encDec) {
      SHA512Form.form.bindFromRequest.fold(
        errors => Future(BadRequest(SHA512View(errors))),
        string => Future(Ok(SHA512View(SHA512Form.form.fill(SHA512.encrypt(string)), finished = true)))
      )
    }
  }

  def showDataSecurity(): Action[AnyContent] = isAuthorised { implicit request => implicit user =>
    permissionsGuard(Permissions.encDec) {
      Future(Ok(DataSecurityView(DataSecurityForm.form)))
    }
  }

  def submitDataSecurity(): Action[AnyContent] = isAuthorised { implicit request => implicit user =>
    permissionsGuard(Permissions.encDec) {
      DataSecurityForm.form.bindFromRequest.fold(
        errors => Future(BadRequest(DataSecurityView(errors))),
        form   => {
          val (data, dataType, mode) = form
          val processedData = dataType match {
            case "string" => if(mode == "enc") {
              DataSecurity.encryptString(data)
            } else {
              DataSecurity.decryptString(data)
            }
            case "json"   => if(mode == "enc") {
              DataSecurity.encryptType[JsValue](Json.parse(data))
            } else {
              Json.prettyPrint(DataSecurity.decryptIntoType[JsValue](data).get)
            }
          }
          Future(Ok(DataSecurityView(DataSecurityForm.form.fill(processedData, dataType, mode), finished = true)))
        }
      )
    }
  }
}
