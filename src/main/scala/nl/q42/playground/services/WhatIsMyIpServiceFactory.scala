package nl.q42.playground.services

import retrofit.RestAdapter
import retrofit.http.GET

object WhatIsMyIpServiceFactory {

   case class MyIp(origin: String)

   trait HttpBinService {
     @GET("/ip")
     def getMyIp: MyIp
   }

   def get(): HttpBinService = {
     val restAdapter = new RestAdapter.Builder()
       .setEndpoint("https://httpbin.org")
       .build()

     restAdapter.create[HttpBinService](classOf[HttpBinService])
   }
 }
