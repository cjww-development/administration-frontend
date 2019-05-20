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

package models

case class EventFiltering(userName: String,
                          start: Option[String],
                          end: Option[String],
                          service: Option[String],
                          sessionId: Option[String],
                          requestId: Option[String],
                          deviceId: Option[String],
                          ipAddress: Option[String],
                          eventCodes: Option[String]) {

  def toQueryParams(userId: String): String = {
    List(
      s"?userId=$userId",
      start.fold("")(date => s"&start=$date"),
      end.fold("")(date => s"&end=$date"),
      service.fold("")(srv => s"&service=$srv"),
      sessionId.fold("")(id => s"&sessionId=$id"),
      requestId.fold("")(id => s"&requestId=$id"),
      deviceId.fold("")(id => s"&deviceId=$id"),
      ipAddress.fold("")(ip => s"&ipAddress=$ip"),
      eventCodes.fold("")(codes => s"&types=${codes.replace(" ", "-")}")
    ).mkString
  }
}

object EventFiltering {
  val empty = EventFiltering("", None, None, None, None, None, None, None, None)
}
