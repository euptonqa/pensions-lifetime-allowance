/*
 * Copyright 2016 HM Revenue & Customs
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

package events

import play.api.libs.json.JsValue
import uk.gov.hmrc.play.audit.model.DataEvent
import uk.gov.hmrc.play.audit.EventTypes
import uk.gov.hmrc.play.audit.AuditExtensions._
import uk.gov.hmrc.play.http.HeaderCarrier
import uk.gov.hmrc.play.http._
import play.api.libs.json.{JsObject, JsPath}
import uk.gov.hmrc.time.DateTimeUtils

class NPSAmendLTAEvent(
    nino: String,
    id: Int,
    npsRequestBodyJs: JsObject,
    npsResponseBodyJs: JsObject,
    statusCode: Int,
    path: String)(implicit hc: HeaderCarrier)
  extends NPSBaseLTAEvent(
    ltaAuditType = "AmendAllowance",
    transactionName="amend-pensions-lifetime-allowance",
    nino = nino,
    npsRequestBodyJs = npsRequestBodyJs,
    npsResponseBodyJs = npsResponseBodyJs,
    statusCode=statusCode,
    path = path,
    extraDetail = Map("protectionId" -> id.toString))
