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

package helpers.connectors

import connectors.AccountsConnector
import helpers.other.{Fixtures, MockRequest}
import org.mockito.Mockito.{reset, when}
import org.mockito.stubbing.OngoingStubbing
import org.mockito.ArgumentMatchers.any
import org.scalatest.BeforeAndAfterEach
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.libs.ws.WSResponse
import play.api.test.Helpers.OK

import scala.concurrent.Future

trait MockAccountsConnector extends BeforeAndAfterEach with MockitoSugar with Fixtures with MockRequest {
  self: PlaySpec =>

  val mockAccountsConnector: AccountsConnector = mock[AccountsConnector]

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockAccountsConnector)
  }

  def mockGetUserId(found: Boolean): OngoingStubbing[Future[Either[WSResponse, String]]] = {
    when(mockAccountsConnector.getUserId(any())(any(), any()))
      .thenReturn(if(found) Future.successful(Right("testUserId")) else Future.successful(Left(fakeHttpResponse(statusCode = OK))))
  }
}
