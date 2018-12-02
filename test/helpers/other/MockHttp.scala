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

package helpers.other

import com.cjwwdev.http.verbs.Http
import com.cjwwdev.http.responses.EvaluateResponse.{ConnectorResponse, SuccessResponse, ErrorResponse}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, when}
import org.mockito.stubbing.OngoingStubbing
import org.scalatest.BeforeAndAfterEach
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.libs.ws.WSResponse

import scala.concurrent.Future

trait MockHttp extends BeforeAndAfterEach with MockitoSugar with TestDataGenerator {
  self: PlaySpec =>

  val mockHttp = mock[Http]

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockHttp)
  }

  def mockHttpHead(response: WSResponse): OngoingStubbing[Future[ConnectorResponse]] = {
    when(mockHttp.head(any(), any())(any(), any()))
      .thenReturn(if(response.status < 299) {
        Future.successful(SuccessResponse(response))
      } else {
        Future.successful(ErrorResponse(response))
      })
  }

  def mockHttpGet(response: WSResponse): OngoingStubbing[Future[ConnectorResponse]] = {
    when(mockHttp.get(any(), any())(any(), any()))
      .thenReturn(if(response.status < 299) {
        Future.successful(SuccessResponse(response))
      } else {
        Future.successful(ErrorResponse(response))
      })
  }

  def mockHttpPost(response: WSResponse): OngoingStubbing[Future[ConnectorResponse]] = {
    when(mockHttp.post(any(), any(), any(), any())(any(), any(), any(), any()))
      .thenReturn(if(response.status < 299) {
        Future.successful(SuccessResponse(response))
      } else {
        Future.successful(ErrorResponse(response))
      })
  }

  def mockHttpPostString(response: WSResponse): OngoingStubbing[Future[ConnectorResponse]] = {
    when(mockHttp.postString(any(), any(), any(), any())(any(), any()))
      .thenReturn(if(response.status < 299) {
        Future.successful(SuccessResponse(response))
      } else {
        Future.successful(ErrorResponse(response))
      })
  }

  def mockHttpPut(response: WSResponse): OngoingStubbing[Future[ConnectorResponse]] = {
    when(mockHttp.put(any(), any(), any(), any())(any(), any(), any(), any()))
      .thenReturn(if(response.status < 299) {
        Future.successful(SuccessResponse(response))
      } else {
        Future.successful(ErrorResponse(response))
      })
  }

  def mockHttpPatch(response: WSResponse): OngoingStubbing[Future[ConnectorResponse]] = {
    when(mockHttp.patch(any(), any(), any(), any())(any(), any(), any(), any()))
      .thenReturn(if(response.status < 299) {
        Future.successful(SuccessResponse(response))
      } else {
        Future.successful(ErrorResponse(response))
      })
  }

  def mockHttpPatchString(response: WSResponse): OngoingStubbing[Future[ConnectorResponse]] = {
    when(mockHttp.patchString(any(), any(), any(), any())(any(), any()))
      .thenReturn(if(response.status < 299) {
        Future.successful(SuccessResponse(response))
      } else {
        Future.successful(ErrorResponse(response))
      })
  }

  def mockHttpDelete(response: WSResponse): OngoingStubbing[Future[ConnectorResponse]] = {
    when(mockHttp.delete(any(), any())(any(), any()))
      .thenReturn(if(response.status < 299) {
        Future.successful(SuccessResponse(response))
      } else {
        Future.successful(ErrorResponse(response))
      })
  }
}
