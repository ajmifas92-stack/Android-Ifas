package com.ifas.vms.storage
import java.io.File
class RetentionEngine{fun deleteOlderThan(root:File,cutoffMs:Long):Int{if(!root.exists())return 0;var n=0;root.walkTopDown().filter{it.isFile&&it.lastModified()<cutoffMs}.forEach{if(it.delete())n++};return n}}
