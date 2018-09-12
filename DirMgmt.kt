import platform.windows.*
import kotlinx.cinterop.*


fun main(args: Array<String>) {
	
	val dirMgmt = DirMgmt()
	val func = readLine()!!

	when (func.toInt()){
		1 -> dirMgmt.createDir(readLine()!!)
		2 -> dirMgmt.deleteDir(readLine()!!)
		//3 -> dirMgmt.openDir(readLine()!!)
		
	}

	
}

class DirMgmt {

	private val DIR_CREATED = "Dir Created"
	private val DIR_DELETED = "Dir Deleted"
	private val DIR_OPENED = "Dir Deleted"
	
	private val ERROR_DIR_EXISTS = "Dir may be Exists or Error While Creating Dir"
	private val ERROR_DELETE_DIR = "Error While Deleting Dir, Dir not Exists or must be Empty provide empty dir to delete"
	private val ERROR_OPENING_DIR = "Error While Opening Dir or Dir not Exists"
	
	
	fun createDir(dirName : String){
	
		
		val dir = CreateDirectoryA(
								   dirName,
								   null
								   )
	
		when(dir){
			1 -> println(DIR_CREATED)
			0 -> println(ERROR_DIR_EXISTS)
		}
	}
	
	fun deleteDir(dirName : String) {
	
	
		val dir = RemoveDirectoryA(dirName)
	
		when(dir){
			1 -> println(DIR_DELETED)
			0 -> println(ERROR_DELETE_DIR)
		}
	}
	
	
	
	
	// fun openDir(dirName : String) {
	
	
		// val dir = SetCurrentDirectory(dirName)
	
		// when(dir){
			// 1 -> println(DIR_OPENED)
			// 0 -> println(ERROR_OPENING_DIR)
		// }
	// }
	

	
	
	// fun getCurrentDir() {
		
		// val dir = GetCurrentDirectory(
										// 0,
										// null
									  // )
		
		// println(dir)
	// }	
	//Implement FindFirstChangeNotificationA function here
}

