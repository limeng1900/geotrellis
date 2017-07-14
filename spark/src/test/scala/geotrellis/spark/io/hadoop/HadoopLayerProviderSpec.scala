/*
 * Copyright 2017 Azavea
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

package geotrellis.spark.io.hadoop

import geotrellis.spark.io._
import geotrellis.spark.testkit.TestEnvironment
import org.scalatest._

class HadoopLayerProviderSpec extends FunSpec with TestEnvironment {
  val uri = new java.net.URI("hdfs+file:/tmp/catalog")
  it("construct HadoopAttributeStore from URI"){
    val store = AttributeStore(uri)
    assert(store.isInstanceOf[HadoopAttributeStore])
  }

  it("construct HadoopLayerReader from URI") {
    val reader = LayerReader(uri)
    assert(reader.isInstanceOf[HadoopLayerReader])
  }

  it("construct HadoopLayerWriter from URI") {
    val reader = LayerWriter(uri)
    assert(reader.isInstanceOf[HadoopLayerWriter])
  }
}
