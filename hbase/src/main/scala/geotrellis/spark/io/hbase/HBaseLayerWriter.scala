/*
 * Copyright 2016 Azavea
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

package geotrellis.spark.io.hbase

import geotrellis.spark._
import geotrellis.spark.io._
import geotrellis.spark.io.avro._
import geotrellis.spark.io.avro.codecs._
import geotrellis.spark.io.index._
import geotrellis.util._

import org.apache.spark.rdd.RDD
import spray.json._

import scala.reflect._

class HBaseLayerWriter(
  val attributeStore: AttributeStore,
  instance: HBaseInstance,
  table: String
) extends LayerWriter[LayerId] {

  protected def _write[
    K: AvroRecordCodec: JsonFormat: ClassTag,
    V: AvroRecordCodec: ClassTag,
    M: JsonFormat: GetComponent[?, Bounds[K]]
  ](id: LayerId, rdd: RDD[(K, V)] with Metadata[M], keyIndex: KeyIndex[K]): Unit = {
    val codec  = KeyValueRecordCodec[K, V]
    val schema = codec.schema

    val header =
      HBaseLayerHeader(
        keyClass = classTag[K].toString(),
        valueClass = classTag[V].toString(),
        tileTable = table
      )
    val metadata = rdd.metadata
    val encodeKey = (key: K) => keyIndex.toIndex(key)

    try {
      attributeStore.writeLayerAttributes(id, header, metadata, keyIndex, schema)
      HBaseRDDWriter.write(rdd, instance, id, encodeKey, table)
    } catch {
      case e: Exception => throw new LayerWriteError(id).initCause(e)
    }
  }
}

object HBaseLayerWriter {
  def apply(
    instance: HBaseInstance,
    table: String
  ): HBaseLayerWriter =
    new HBaseLayerWriter(
      attributeStore = HBaseAttributeStore(instance),
      instance = instance,
      table = table
    )

  def apply(
    attributeStore: HBaseAttributeStore,
    table: String
  ): HBaseLayerWriter =
    new HBaseLayerWriter(
      attributeStore = attributeStore,
      instance = attributeStore.instance,
      table = table
    )
}
