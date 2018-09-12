import kotlinx.cinterop.*
import platform.windows.*


fun main(args: Array<String>) {

	
	val fileMgmt = FileMgmt()
	
	val func = readLine()!!
	
	//if(func.toDouble().isNaN()){
	//	println("Please Specify A Number")
	//	return
	//}

	when (func.toInt()){
		1 -> fileMgmt.createFile(readLine()!!)
		2 -> copyFile()
		3 -> fileMgmt.getCompressedFileSize(readLine()!!,"KB")
		4 -> fileMgmt.deleteFile(readLine()!!)
		5 -> createLink()
		6 -> moveFile()
		7 -> fileMgmt.getFileAttr(readLine()!!)
		8 -> println(fileMgmt.isDirectory(readLine()!!))
		8 -> println(fileMgmt.isDirectory(readLine()!!))
		
	}


}

fun createLink() {
	val fileMgmt = FileMgmt()
	val linkNameTag = readLine()!!
	val srcFile		= readLine()!!
	fileMgmt.createFileShortCut(linkNameTag, srcFile)
}

fun copyFile() {
	
	println("Enter File Name to be Copied :")
	val oldFile = readLine()!!
	println("Enter New File Name of the File :")
	val newFile = readLine()!!
	
	val fileMgmt = FileMgmt()
	fileMgmt.copyFile(oldFile,newFile)


}

fun moveFile() {
	
	println("Enter File Name to be Moved :")
	val oldFile = readLine()!!
	println("Enter New File Name of the File :")
	val newFile = readLine()!!
	
	val fileMgmt = FileMgmt()
	fileMgmt.moveFile(oldFile,newFile)


}

class FileMgmt {

	/**
	*   This Class Provides File Management Api to Create File in Windows OS by providing bridge to Win32 Api
	*   
	*   Eg for fileName Input ->
	*		val fileName = "C:\\Folder\\SubFolder\\testFile.txt"
	*
	* 	Eg: Create a Simple File without any args
	*		FileMgmt fileMgmt = FileMgmt()
	*       fileMgmt.createFile("fileName")
	*
	* 	Eg: Create a File with ReadOnly Attributes
	*		FileMgmt fileMgmt = FileMgmt()
	*       fileMgmt.createReadOnlyFile("fileName")
	*
	* 	Eg: Create a File with args required by user
	*       
	*		FileMgmt fileMgmt = FileMgmt()
	*       val args = arrayOf(2, 2, 0, 1)  //To Create Hidden File
	*       fileMgmt.createFile("fileName",args)
	*   
	*
	*  	See enums at the bottom for Other args
	*
	*/
	
	private val filePrefix  = "\\\\.\\"
	private var accessType 	= -1
	private var shareMode 	= -1
	private var action  	= -1
	private var fileAttr  	= -1
	
	private val FILE_DELETED = "File Deleted"	
	private val FILE_LINK_CREATED = "File Link Created"	
	private val FILE_COPIED = "File Copied"	
	private val FILE_MOVED  = "File Moved"	
	
	
	private val ERROR_FILE_LINK_CREATION = "Error While Creating File Shortcut"
	private val ERROR_DELETE_FILE = "Error While Deleting File, File not Exists"
	private val ERROR_SIZE_FILE = "Error While Getting File Size, File not Exists"
	private val ERROR_MOVING_FILE = "Error While Moving File or File not Exists"
	private val ERROR_COPYING_FILE = "Error While Copying File or File not Exists"

	
	fun createFile(fileName : String) : Int {
	
		val result = 0
		val args = arrayOf(2, 2, 0, 0)	
		val a =  createFileC(fileName,args)				
		println(a)
	
	return result
	}

	fun createTempFile() {
		//val result = GetTempFileNameW("temp","kon",0,"tempFile")
	}
	
	fun createReadOnlyFile(fileName : String) : Int {
		
		val result = 0
		val name = filePrefix+fileName
		
		val args = arrayOf(2, 2, 0, 5)
		
		val a =  createFileC(fileName,args)

	return result
	}
	
	fun createFileC(fileName : String, args : Array<Int>) : Int {
		
		lateinit var name : String
		
		if(fileName.contains(filePrefix)){
			name = fileName
		} else {
			name = filePrefix+fileName
		}
		
		when(args[0]){
			0 	 ->	 accessType = AccessType.READ.type
			1 	 ->	 accessType = AccessType.WRITE.type
			2 	 ->	 accessType = AccessType.ROW.type
			else ->  accessType = AccessType.ROW.type
		}	
		
		when(args[1]){
			0 	 ->	 shareMode = ShareMode.PRIVATE.mode
			1 	 ->	 shareMode = ShareMode.READ.mode
			2 	 ->	 shareMode = ShareMode.WRITE.mode
			3	 ->  shareMode = ShareMode.DELETE.mode
			else ->  shareMode = ShareMode.WRITE.mode
		}
				
		when(args[2]){
			0 	 ->	 action = Action.CREATEA.task
			1 	 ->	 action = Action.CREATE.task
			2 	 ->	 action = Action.OPEN.task
			3 	 ->	 action = Action.OPENE.task
			4 	 ->	 action = Action.DELETE.task
			else ->	 action = Action.CREATEA.task
		}	
		
		when(args[3]){
			0 	 ->	 fileAttr = FileAttr.NORMAL.attr
			1 	 ->	 fileAttr = FileAttr.HIDDEN.attr
			2 	 ->	 fileAttr = FileAttr.OFFLINE.attr
			3 	 ->	 fileAttr = FileAttr.ENCRYPTED.attr
			4 	 ->	 fileAttr = FileAttr.ARCHIVE.attr
			5 	 ->	 fileAttr = FileAttr.READONLY.attr
			6 	 ->	 fileAttr = FileAttr.TEMP.attr
			7 	 ->	 fileAttr = FileAttr.SYSTEM.attr
			else ->	 fileAttr = FileAttr.NORMAL.attr
		}
		
		val result = 0

		val file =  CreateFileA(   
								name,
								accessType,
								shareMode,
								null ,
								action,
								fileAttr,
								null
								)
		
		return result
	}
	
	fun copyFile(oldFile : String, newFile : String){
	
		var result = -1
		
		//Source https://github.com/JetBrains/kotlin-native/issues/2042#issuecomment-420526537
		  // memScoped {
			// val bool = alloc<BOOLVar>()
			// bool.value = FALSE
			// result = CopyFileExA(
								  // oldFile, 
								  // newFile, 
								  // null, 
								  // null, 
								  // bool.ptr, 
								  // CopyFlags.FAIL_IF_EXISTS.flag
								// )
		  // }
		
		memScoped {
		//val bool = alloc<WINBOOLVar>()
		//bool.value = TRUE
		 result = CopyFileA(
							  oldFile,
							  newFile,
							  TRUE
							 )
		}
		when(result){
			1 -> println(FILE_COPIED)
			0 -> println(ERROR_COPYING_FILE + result)
		}
	}
	
	fun moveFile(oldFile : String, newFile : String){
		
		val result = MoveFileExA(oldFile, newFile, MoveFlags.WRITE_THROUGH.flag)
		
		when(result){
			1 -> println(FILE_MOVED)
			0 -> println(ERROR_MOVING_FILE)
		}
	}
	
	fun deleteFile(fileName : String){
	
		val result =  DeleteFileA(fileName);
		
		when(result){
			1 -> println(FILE_DELETED)
			0 -> println(ERROR_DELETE_FILE)
		}
	
	}
	
	fun getCompressedFileSize(fileName : String, unit : String){
		var resultV = GetCompressedFileSizeA(fileName,null)
		
		if(resultV == INVALID_FILE_SIZE){
			println(ERROR_SIZE_FILE)
			return
		}
		
		var result = resultV.toLong().toDouble()
		
		if(result>0){
		
			when(unit){
				"B"  -> result = result
				"KB" -> result = result/(1024).toDouble()
				"MB" -> result = result/(1024*1024)
				"GB" -> result = result/(1024*1024)
			
			}
		}
				
		println(result.decimals(2).toString()+unit)//
		
	}
	
	// Always Results in Error DoSomething and Modify also tried with CreateSymbolicLinkA
	fun createFileShortCut(linkNameTag : String, srcFile : String) {
		
		val resultV = CreateSymbolicLinkW(
							 linkNameTag,
					         srcFile,
					         0
					)
		
		val result = resultV.toInt()
		when(result) {
			1 -> println(FILE_LINK_CREATED)
			0 -> println(ERROR_FILE_LINK_CREATION)
		}
	}
	
	fun isDirectory(fileName : String) : Boolean {
		val result = GetFileAttributesW(fileName)
		if(result == FILE_ATTRIBUTE_DIRECTORY) {
			return true
		}
	return false	
	}
	
	fun getFileAttr(fileName : String){
		val result = GetFileAttributesW(fileName)
		when(result){
			FILE_ATTRIBUTE_ARCHIVE -> println("File is Archive")
			FILE_ATTRIBUTE_COMPRESSED -> println("File is Compressed")
			FILE_ATTRIBUTE_DEVICE -> println("Value is Reserved for System Use")
			FILE_ATTRIBUTE_DIRECTORY -> println("File is Directory")
			FILE_ATTRIBUTE_ENCRYPTED -> println("File is Encrypted")
			FILE_ATTRIBUTE_HIDDEN -> println("File is Hidden")
			//FILE_ATTRIBUTE_INTEGRITY_STREAM -> println("The directory or user data stream is configured with integrity")
			FILE_ATTRIBUTE_NORMAL -> println("File is Normal File")
			FILE_ATTRIBUTE_NOT_CONTENT_INDEXED -> println("The file or directory is not to be indexed")
			//FILE_ATTRIBUTE_NO_SCRUB_DATA -> println("The user data stream not to be read by the background data integrity scanner")
			FILE_ATTRIBUTE_OFFLINE -> println("The data of a file is not available immediately")
			FILE_ATTRIBUTE_READONLY -> println("File is read-only")
			//FILE_ATTRIBUTE_RECALL_ON_DATA_ACCESS -> println(" the file or directory is not fully present locally")
			//FILE_ATTRIBUTE_RECALL_ON_OPEN -> println("This attribute only appears in directory enumeration classes ")
			FILE_ATTRIBUTE_REPARSE_POINT -> println("A file or directory that has an associated reparse point, or a file that is a symbolic link")
			FILE_ATTRIBUTE_SPARSE_FILE -> println("A file that is a sparse file.")
			FILE_ATTRIBUTE_SYSTEM -> println("A file or directory that the operating system uses a part of, or uses exclusively.")
			FILE_ATTRIBUTE_TEMPORARY -> println("A file that is being used for temporary storage")
			FILE_ATTRIBUTE_VIRTUAL -> println("This value is reserved for system use.")
		}
	}
	
	fun Double.decimals(digits : Int) : Double {
		
			var repeats = 0
			var value = 1
			do {
				value = value * 10
				repeats++
			}
			while(repeats < digits)
			
			val finalValue = (this * value).toShort()
		
		return finalValue.toDouble()/(value).toDouble()
		
	}
	
	fun checkFileError(){
	
		// Since I Don't Know How to deal with C Pointers 
		// I was not able to get
		// Whether File Created or not If you How intialize CPointer get 
		// Error Message Report me to Implement Error Handling in This 
		// Section and Implement some When Statement to catch the errors
		/* Eg:
			when (a){
				INVALID_FILE_ERROR -> println("Error Creating File")
				}
		 */
		
		// val re = println(a)
		
		// when(re.toString()){
		// "CPointer(raw=0x-1)" -> println("File Exists")
		// }
	}
	
	//** dwDesiredAccess **//
	enum class AccessType(val type : Int) {
		READ(GENERIC_READ),
		WRITE(GENERIC_WRITE),
		ROW(GENERIC_READ or GENERIC_WRITE)
	}
	
	//** dwShareMode **//
	enum class ShareMode(val mode : Int) {
		PRIVATE(0),
		READ(FILE_SHARE_READ),
		WRITE(FILE_SHARE_WRITE),
		DELETE(FILE_SHARE_DELETE)
	}
	
	//** dwCreationDisposition **//
	enum class Action(val task : Int){
		CREATEA(CREATE_ALWAYS),
		CREATE(CREATE_NEW),
		OPEN(OPEN_ALWAYS),
		OPENE(OPEN_EXISTING),
		DELETE(TRUNCATE_EXISTING)
	}
	
	//** dwFlagsAndAttributes **//
	enum class FileAttr(val attr : Int ) {
		NORMAL(FILE_ATTRIBUTE_NORMAL),
		HIDDEN(FILE_ATTRIBUTE_HIDDEN),
		OFFLINE(FILE_ATTRIBUTE_OFFLINE),
		ENCRYPTED(FILE_ATTRIBUTE_ENCRYPTED),
		ARCHIVE(FILE_ATTRIBUTE_ARCHIVE),
		READONLY(FILE_ATTRIBUTE_READONLY),
		TEMP(FILE_ATTRIBUTE_TEMPORARY),
		SYSTEM(FILE_ATTRIBUTE_SYSTEM)
	}
	
	//** dwCopyFlags **//
	enum class CopyFlags(val flag : Int ) {
		DECRYPTED_DESTINATION(COPY_FILE_ALLOW_DECRYPTED_DESTINATION),
		SYMLINK(COPY_FILE_COPY_SYMLINK),
		FAIL_IF_EXISTS(COPY_FILE_FAIL_IF_EXISTS),
		NO_BUFFERING(COPY_FILE_NO_BUFFERING),
		OURCE_FOR_WRITE(COPY_FILE_OPEN_SOURCE_FOR_WRITE),
		RESTARTABLE(COPY_FILE_RESTARTABLE),
	
	}
	
	//** dwCopyFlags **//	
	enum class MoveFlags(val flag : Int) {
		COPYALLOWED(MOVEFILE_COPY_ALLOWED),
		CREATE_HARDLINK(MOVEFILE_CREATE_HARDLINK),
		REBOOT(MOVEFILE_DELAY_UNTIL_REBOOT),
		NOT_TRACKABLE(MOVEFILE_FAIL_IF_NOT_TRACKABLE),
		REPLACE(MOVEFILE_REPLACE_EXISTING),
		WRITE_THROUGH(MOVEFILE_WRITE_THROUGH),
	}
	
	enum class SymLinkFlags(val flag : Int ) {
		FILE(0),
		DIRECTORY(SYMBOLIC_LINK_FLAG_DIRECTORY),
		UNPREVILEGED(SYMBOLIC_LINK_FLAG_ALLOW_UNPRIVILEGED_CREATE)
	}
	
	
	//typealias  HANDLE?  = CPointer<out CPointed>?
	//Help me to Create this I was Not Able to figure out how to handle
	//Error Says initialse the HANDLE? before I dont know how to do that
}
