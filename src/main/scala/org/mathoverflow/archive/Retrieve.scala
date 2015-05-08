package org.mathoverflow.archive

import scala.io.Source
import scala.io.Codec
import org.apache.http.impl.client.ContentEncodingHttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.util.EntityUtils

object Retrieve {
  def apply(post: Int): String = {
    val url = s"http://api.stackexchange.com/2.2/questions/$post?order=desc&sort=activity&site=mathoverflow.net&filter=!3yXvh9)gd0IKKXn31"
    val response = new ContentEncodingHttpClient().execute(new HttpGet(url))
    val entity = response.getEntity();
    val status = response.getStatusLine().getStatusCode()
    EntityUtils.toString(entity)
  }
}