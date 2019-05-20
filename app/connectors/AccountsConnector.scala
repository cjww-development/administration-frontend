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

package connectors

import com.cjwwdev.config.ConfigurationLoader
import com.cjwwdev.security.obfuscation.Obfuscation._
import com.cjwwdev.security.deobfuscation.DeObfuscation._
import com.cjwwdev.implicits.ImplicitDataSecurity._
import com.cjwwdev.implicits.ImplicitJsValues._
import com.cjwwdev.http.verbs.Http
import com.cjwwdev.http.responses.EvaluateResponse._
import javax.inject.Inject
import play.api.libs.ws.WSResponse
import play.api.mvc.Request

import scala.concurrent.{Future, ExecutionContext => ExC}

class DefaultAccountsConnector @Inject()(val http: Http,
                                         val config: ConfigurationLoader) extends AccountsConnector {
  override val accountsRoute: String = config.getServiceUrl("accounts-microservice")
}

trait AccountsConnector {

  val http: Http

  val accountsRoute: String

  def getUserId(userName: String)(implicit req: Request[_], ec: ExC): Future[Either[WSResponse, String]] = {
    http.get(s"$accountsRoute/private/user-name/${userName.encrypt}/user-id").map {
      case SuccessResponse(resp) => resp.json.get[String]("body").decrypt[String].fold(Right(_), _ => Left(resp))
      case ErrorResponse(resp)   => Left(resp)
    }
  }
}
