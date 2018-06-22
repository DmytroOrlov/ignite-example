package example

import org.apache.ignite.cache.query.annotations.{QuerySqlField, QueryTextField}
import org.apache.ignite.cache.query.{ScanQuery, SqlQuery, TextQuery}
import org.apache.ignite.configuration.{CacheConfiguration, IgniteConfiguration}
import org.apache.ignite.{IgniteCache, Ignition}

import scala.annotation.meta.field
import scala.collection.JavaConverters._

object IgniteSql extends App {
  val ignite = Ignition.start {
    new IgniteConfiguration().setCacheConfiguration {
      new CacheConfiguration("ignite").setIndexedTypes(Seq(classOf[String], classOf[IotDevice]): _*)
    }
  }
  val cacheIot: IgniteCache[String, IotDevice] = ignite.getOrCreateCache[String, IotDevice]("ignite")

  val temp1 = IotDevice(name = "temp1", gpio = "123ASD", sensorType = "temperature 1", model = "testTemp")
  cacheIot.put(temp1.gpio, temp1)
  val temp2 = IotDevice(name = "temp2", gpio = "456ASD", sensorType = "temperature 2", model = "testTemp")
  cacheIot.put(temp2.gpio, temp2)
  val temp3 = IotDevice(name = "temp3", gpio = "555AAA", sensorType = "temperatur", model = "testTemp")
  cacheIot.put(temp3.gpio, temp3)

  val sqlText = s"sensorType LIKE 'temperatur%'"
  val sql = new SqlQuery[String, IotDevice](classOf[IotDevice], sqlText)
  val temperatureQueryResult = cacheIot.query(sql).getAll.asScala.map(_.getValue)
  println(s"SqlQuery = $temperatureQueryResult")

  val cursor = cacheIot.query(new ScanQuery((key: String, entryValue: IotDevice) => entryValue.sensorType.startsWith("temperatur")))

  val temperatureScanResult = cursor.getAll.asScala
  println(s"ScanQuery = $temperatureScanResult")

  val textQuery = new TextQuery[IotDevice, String](classOf[IotDevice], "temperatur*")
  val temperatureTextResult = cacheIot.query(textQuery).getAll.asScala
  println(s"TextQuery = $temperatureTextResult") //all devices with sensorType/model = temperatur*
}

case class IotDevice(
  @(QuerySqlField@field)(index = true) name: String,
  @(QuerySqlField@field)(index = true) gpio: String,
  @(QueryTextField@field) sensorType: String,
  @(QueryTextField@field) model: String
)
