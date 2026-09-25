package com.ifas.vms.player
import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
object RtspPlayer{fun create(c:Context,url:String)=ExoPlayer.Builder(c).build().apply{setMediaItem(MediaItem.fromUri(url));prepare();playWhenReady=true}}
