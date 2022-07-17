package com.atguigu.gmall.realtime.app

import com.alibaba.fastjson.{JSON, JSONObject}
import com.atguigu.gmall.realtime.util.{MyESUtil, MyKafkaUtil, MyRedisUtil, OffsetManagerUtil}
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.{DStream, InputDStream}
import org.apache.spark.streaming.{Seconds, StreamingContext}
import java.lang
import java.text.SimpleDateFormat
import java.util.Date

import com.atguigu.gmall.realtime.bean.DauInfo
import org.apache.kafka.common.TopicPartition
import org.apache.spark.streaming.kafka010.{HasOffsetRanges, OffsetRange}
import redis.clients.jedis.Jedis

import scala.collection.mutable.ListBuffer
object MyDauApp {
  def main(args: Array[String]): Unit = {
    val sparkConf:SparkConf=new SparkConf().setMaster("local[4]").setAppName("dau_app")
    val ssc=new StreamingContext(sparkConf,Seconds(5))

    val groupId="gmall_dau_bak"
    val topic="gmall_start_0523"

    //获取该topic,groupid的所有分区offset
    val kafkaOfffsetMap:Map[TopicPartition,Long]=OffsetManagerUtil.getOffset(topic,groupId)
    var recordDStream: InputDStream[ConsumerRecord[String, String]] = null
    if(kafkaOfffsetMap!=null&&kafkaOfffsetMap.size>0)
      {
      //  redis有offset
        recordDStream=  MyKafkaUtil.getKafkaStream(topic,ssc,kafkaOfffsetMap,groupId)
      }else{
      recordDStream=  MyKafkaUtil.getKafkaStream(topic,ssc,groupId)
    }
    //获取offset
    var offsetRanges:Array[OffsetRange]=Array.empty[OffsetRange]
    val offsetDStream:DStream[ConsumerRecord[String,String]]=recordDStream.transform(
      rdd=>{
        offsetRanges=rdd.asInstanceOf[HasOffsetRanges].offsetRanges
        println(offsetRanges(0).untilOffset+"******")
        rdd
      }
    )
    //取dt,hr
    val jsonObjDStream:DStream[JSONObject]=offsetDStream.map{
      record=>
        val jsonStr:String=record.value()
        val jsonObj:JSONObject=JSON.parseObject(jsonStr)
        val ts:lang.Long=jsonObj.getLong("ts")

        val dateHourString:String=new SimpleDateFormat("yyyy-MM-dd HH").format(new Date(ts))

        val dateHour:Array[String]=dateHourString.split(" ")
        jsonObj.put("dt",dateHour(0))
        jsonObj.put("hr",dateHour(1))
        jsonObj
    }
    //redis重复过滤
    val filteredDStream:DStream[JSONObject]=jsonObjDStream.mapPartitions{
      jsonObjItr=>{
        val jedisClient:Jedis=MyRedisUtil.getJedisClient()
        val filterredList:ListBuffer[JSONObject]=new ListBuffer[JSONObject]
        for(jsonObj<-jsonObjItr){
          val dt:String=jsonObj.getString("dt")
          val mid:String=jsonObj.getJSONObject("common").getString("mid")
          val dauKey:String="dau:"+dt
          //sadd函数将mid添加到dauKey中,当集合中已存在mid元素时,返回0L,当集合中不包含此元素时,返回1L.
          val isNew:lang.Long=jedisClient.sadd(dauKey,mid)
          jedisClient.expire(dauKey,3600*24)
          if(isNew==1L){
            filterredList.append(jsonObj)
          }
        }
        jedisClient.close()
        filterredList.toIterator
      }
    }
    filteredDStream.count().print()

    //向ES中保存数据
    filteredDStream.foreachRDD{
      //    对每一个到来的rdd，即一个micro batch。调用下边的方法
      rdd=>{
        //对rdd调用下边的方法
        //对每个分区调用下边的方法
        rdd.foreachPartition{
          //    对每个分区的数据迭代器调用下边的方法
          jsonItr=>{
            val dauList: List[(String,DauInfo)]=jsonItr.map{
              //    把迭代器的jsonObject取出，封装到DauInfo对象中，返回一个元组。然后将元组组成的迭代器使用toList方法转换为元组List。
              jsonObj=>{
                val commonJsonObj:JSONObject=jsonObj.getJSONObject("common")
                val dauInfo = DauInfo(
                  commonJsonObj.getString("mid"),
                  commonJsonObj.getString("uid"),
                  commonJsonObj.getString("ar"),
                  commonJsonObj.getString("ch"),
                  commonJsonObj.getString("vc"),
                  jsonObj.getString("dt"),
                  jsonObj.getString("hr"),
                  "00",
                  jsonObj.getLong("ts")
                )
                (dauInfo.mid,dauInfo)
              }
            }.toList

            val dt:String=new SimpleDateFormat(("yyyy-MM-dd")).format(new Date())
            println(dauList)
            MyESUtil.bulkInsert(dauList,"gmall2020_dau_info_20220417")
          }
        }
        OffsetManagerUtil.saveOffset(topic,groupId,offsetRanges)
      }
    }
    ssc.start()
    ssc.awaitTermination()
  }
  //保持先数据,后提交offset
  //  保持幂等性,即数据部分幂等性,在此程序中就是 redis去重,ES插入,即涉及到数据库的部分.如果ES插入重复,不会影响,因为id覆盖了.
//redis去重的效果是,如果已经存在,则不添加.es默认去重效果是覆盖前边相同id的数据.
}
