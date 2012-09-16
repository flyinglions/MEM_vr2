/*
* File handling done here:
*
*/

var theini;
var FS;
var filename="settings.ini";
var file_entry ;
var file_text="empty";

// INI object:
function Ini(contents){
    //alert('ini created');
    this.sections = new Array();
        
    this.prse = function (content,inis) {
        parse(content,inis);
    }

    // The parser function - takes text and transforms it into the structure set out by the INI class

    parse = function (content,inis) {
        var i, ln, m, key, val, prop = '',
        lines = contents.split(/\r\n|\r|\n/);
        //alert("parsing");
        
        var index =-1;

        for (i=0; i<lines.length; i++) {
           
            // First we trim the line
            ln = lines[i].replace(/^\s+/,'').replace(/\s+$/,'');

            // Register empty lines
            if (ln.length === 0) {
                //sect.line();
                continue;
            }

            // Detect whole line comments
            if (ln.charAt(0) === '#' || ln.charAt(0) === ';') {
                //sect.comment(ln.substr(1).replace(/^\s+/,''));
                continue;
            }

            // Detect section header
            if (ln.charAt(0) === '[') {
                // TODO: Detect malformed sections
                ln.replace(/^\s*\[\s*([^\s\]"]+)(\s+"([^"]+))?/, function(m0,m1,m2,m3){




                        
                    inis.push(new Section(m1));
                    index++;
                //alert(inis[0].name);

                //sect = up_sect.section(m1, m3 || null);
                });
                continue;
            }

        
            // Property
            prop = prop.length > 0 ? prop + ' ' + ln : ln;

            // Match property
            // NOTE: Does not support: prop = "foo" bar "baz"
            m = /^([A-Za-z0-9._-]+)(\s*=\s*("(\\"|.)*"|[^#;]*)?)?/.exec(prop);
            if (m && m[0].length) {
                key = m[1];
                if (m[3] && m[3].length) {
                    val = m[3];

                    // Trim the value
                    val = val.replace(/^\s+|\s+$/g, '');

                    // Remove quotes
                    if (val.charAt(0) === '"' && val.charAt(val.length-1) === '"') {
                        val = val.substr(1, val.length-2);
                        val = val.replace(/\\\\/g, '\\')
                        .replace(/\"/g, '"')
                        .replace(/\\n/g, '\n')
                        .replace(/\\t/g, '\t')
                        .replace(/\\b/g, '\b');
                    // Check boolean
                    } /*else if (/^yes|on|true$/i.test(val)) {
                        val = true;
                    } else if (/^no|off|false$/i.test(val)) {
                        val = false;
                    // Check integer
                    } */ else if (/^[0-9]+$/i.test(val)) {
                        val = parseInt(val, 10);
                    }

                } else if (m[2]) {
                    val = '';
                } else {
                    val = true;
                }

                inis[index].add(new Property(key,val));
            //sect.property(key, val);
            }

            prop = '';
        }
    }
        
    parse(contents,this.sections);
        

        
}
    
function Section(name) {
         
    this.name = name;
    this.items = new Array();
           
    this.add = function (prop) {
        this.items.push(prop);
                    
    }
            
}
    
function Property(name,val) {
    this.name = name;
    this.val = val;
            
}
    
  
// Transforms INI object into string using theini variable declared at the top
function toStr() {
    var s="";
    for (var k =0; k<theini.sections.length; k++ ) {
        s+="["+theini.sections[k].name+"]\n";
        for (var p =0; p<theini.sections[k].items.length; p++ ) {
            s+=theini.sections[k].items[p].name+"="+theini.sections[k].items[p].val+"\n";
        }
    }
    return s;
}


// change the filename
function setFileName(fname) {
    filename = fname;
}


function fileSystemInit() {

    //i made the filesize 100kb....?
    window.requestFileSystem(1, 1024 * 100, gotFSSuccess, failed);
//failed - function to call when failed
}


function gotFSSuccess(filesystem) {
    // Got the filesystem 
    FS = filesystem;   
    FS.root.getFile(filename,{
        create : true
    },gotFileEntry,failed);
}

function gotFileEntry(theFile) {
    // alert("onGetFileSuccess: " + theFile.name);

    file_entry = theFile;
    readFromFile();
//thefileentry.file(gotFile,failed);
}

/*Reading the file here*/

function readFromFile() {
    file_entry.file(fileReaderSuccess,failed);
}

/*callback functions for creating and writing to two files*/
function  fnbfilewriter() {
		
		callwriteToFile("MEM/ORI/FNBRules.ini",finalcallback,"<FNB,;> * <\"R\"Cost,;> * * <TransactionType,;> <location,\"@\"|\"from\"|\".\"> * <accName,\"from\"> * * * <\"R\"balance,.> * * <Time,;*>");
}
	
function finalcallback() {
	startINIcallback();
	/*when done with ini - write back to file*/
	stopINI();
}

function absacallback() {
	callwriteToFile("MEM/ORI/ABSARules.ini",fnbfilewriter,"<Absa,;> <accName,,> <TransactionType,,> <Date,;> <Location,,> <\"R\"amount,,> * <\"R\"balance,.> * * *");
}

function fileReaderSuccess(file) {
    //alert("get reading file");
    var reader = new FileReader();
    reader.onloadend = function(e) {
    
        file_text = e.target.result;
       // alert("read:"+file_text);

        if (file_text.length==0) {
		//alert("new files");
		console.log("file empty: in process of creating new - after callback (see stopINI)"); 
            file_text="[categories]\r\nFood=SPAR,Checkers,spar,PICK,pick\r\nTravel=BP, Sasol, Engen, Shell, Gautrain\r\nFuel=BP,SASOL\r\nEntertainment=Wang, Top CD, Amazon\r\nOther=ATM\r\nTelecommunications=Vodacom, Telkom, Cell C, MTN\r\ngames=WARG,Top CD\r\nATM= OTM,otm,atm,ATM\r\nGamble=cas,CAS\r\nbanktrans=BANK\r\n[user]";
		
		
		
        } /*else {
		
		alert("NOT new files");
	}*/
	theini = new Ini(file_text);
	createthedir(absacallback);
        // alert("file_text: "+file_text);
        
		//startINIcallback();
	/*when done with ini - write back to file*/
	//stopINI();
		
	
	
	
		

    };
    reader.onloaderror = function(e) {
        failed;
    };
  
    reader.readAsText(file);
  
/*if (file_text.length===0)
ini = new Ini('[user]');
else
ini = new Ini(file_text);*/
 
}

var aftercreatedircallback;
function createthedir(call) {
	aftercreatedircallback = call;
	window.requestFileSystem(1,0, createdirgotfs, failed);
	
	
}
var createdirfs;
function createdirgotfs(fs) {
	createdirfs = fs;
	console.log("creating dir: MEM/ORI");
	fs.root.getDirectory("MEM", {create: true}, createori, finalcallback);
	
}
function oncreatesuccess(d) {
	aftercreatedircallback();
}
function createori(d) {
createdirfs.root.getDirectory("MEM/ORI", {create: true}, oncreatesuccess, finalcallback);	
}

/*Writing the file here*/


function writeToFile() {
    FS.root.getFile(filename,{
        create : true
    },gotFileEntryforWriter,failed);
    
}

function gotFileEntryforWriter(theFile) {
    theFile.createWriter(fileWriterSuccess, failed);
}

function fileWriterSuccess(writer) {
    writer.onwrite = function(e) {
    //alert("Write Completed");
    };
    writer.write(toStr());//'[author]\nname=Pieter\n[user]');
    
}



function gotFile(thefile) {
    /*var reader = new FileReader();
reader.readAsText(file);*/
    file = thefile;
}


function failed() {
    console.log(error.code);
    //alert("failed..");
}
























    
    
    
    
//var ini;
    
    

    var startINIcallback;
    
function startINI(call) {
startINIcallback = call;
    fileSystemInit();
  //  alert("init success");
//filename = "settings.ini";
   
  
//get text from phonegap-api
/* ini = new Ini([
'[user]',
'foo = ho iszit',
'baz = "tttt"',
'[other]',
'foo = "2bar"',
'baz = "ddd"',
'[fff]',
'hello = "how are you"',
].join('\n'));*/
  


    
}



function stopINI() {
    //write to file
    //file_text =
    writeToFile();
//delete ini;
}

function INIset(isection,ikey,ivalue) {
    
    /*if (ini==null)
ini = new INI('[user]');*/
    var sectionset=false;
    var keyset=false;
    for (var k =0; k<theini.sections.length; k++ ) {
        if (theini.sections[k].name==isection) {
            for (var p =0; p<theini.sections[k].items.length; p++ ) {
                if (theini.sections[k].items[p].name==ikey) {
                    theini.sections[k].items[p].val = ivalue;
                    keyset=true;
                    return true;
                }
                
            }
            if (!keyset) {
                theini.sections[k].items.push(new Property(ikey,ivalue));
            }
            
            sectionset = true;
            return true;
        }
            
        
            
    }
    if (!sectionset) {
        theini.sections.push(new Section(isection));
        theini.sections[ini.sections.length-1].items.push(new Property(ikey,ivalue));
        return true;
    }
    
}
function INIget(section,key) {
    for (var k =0; k<theini.sections.length; k++ ) {
        if (theini.sections[k].name == section)
            for (var p =0; p<theini.sections[k].items.length; p++ ) {
                if (theini.sections[k].items[p].name==key) {
                
                    return theini.sections[k].items[p].val;
                }
            }
        
    }
    
    
    return null;
    
}

function showContents() {
    alert(toStr());
}

/***********************/
/*Regular file manager: - not tested */
/***********************/

var file_content;
var general_filename = "tmp.txt";
var regular_FS;
//var regular_file_entry;

function regular_fileSystemInit() {
    window.requestFileSystem(1,0, regular_gotFSSuccess, failed);
//failed - function to call when failed
}
function regular_gotFSSuccess(filesystem) {
    // Got the filesystem 
    console.log("got filesystem");
    regular_FS = filesystem;   
  
}



/*=============================*/
/*READING FROM THE FILE THAT IS LOADED*/
/*=============================*/
var file_to_read="";
var readcallback;
function failed_read(error) {
    console.log("reading error: "+error);
}

function readFile(fname,call) {
    readcallback = call;
    file_to_read=fname;
    window.requestFileSystem(1,0, rgotFSSuccess, failed_read);	
}
function rgotFSSuccess(filesystem) {
    // Got the filesystem 
    console.log("got filesystem");
    regular_readFromFile(filesystem,file_to_read);    
}
//function to initialize the reading of a file specified by the {regular_file_entry}
function regular_readFromFile(filesystem,fname) {
    filesystem.root.getFile(fname,{
        create : false
    },read_gotFileEntry,failed_read);     
}

function read_gotFileEntry(theFile) {	//regular_file_entry = theFile;	
    theFile.file(read_fileReaderSuccess,failed_read);
    
}

//got file - now load the content:
function read_fileReaderSuccess(file) {
    var reader = new FileReader();
    reader.onloadend = function(e) {    
        var text = e.target.result;
        console.log("file_content"+text);

        if (text.length==0) {
            console.log("file empty!");
        } 
        readcallback(text);
    };
    reader.onloaderror = function(e) {
        console.log("could not load file contents");
    };
  
    reader.readAsText(file);  
}

/*===========================*/
/*WRITING TO THE FILE THAT IS LOADED*/ /*Using regular_FS (for now)
/*===========================*/
/*remember to add regular_fileSystemInit when beginning */
function write_failed(error) {
    console.log("writing error: " + error);
}

function regular_writeToFile(fname) {
    regular_FS.root.getFile(fname,{
        create : true
    },write_gotFileEntry,write_failed);    
}

function write_gotFileEntry(theFile) {
    theFile.createWriter(write_fileWriterSuccess, write_failed);
}

function write_fileWriterSuccess(writer) {
    writer.onwrite = function(e) {
        console.log("Writing done!"+file_content);
    };	
    writer.write(file_content);
}

/*Writing - with callback support*/
/*=============================CALLBACKS=======================*/
function callwrite_failed(error) {
    console.log("callback - writing error: " + error);
}

var writercallback;
var text_to_write;
var call_fname;
function callwriteToFile(fname,call,text) {
	call_fname = fname;
   text_to_write = text;
writercallback = call;
    window.requestFileSystem(1,0, successcallwriteToFile, failed_read);	
}

function successcallwriteToFile(fs) {

    fs.root.getFile(call_fname,{
        create : true
    },callwrite_gotFileEntry,write_failed);    
}

function callwrite_gotFileEntry(theFile) {
    theFile.createWriter(callwrite_fileWriterSuccess, callwrite_failed);
}

function callwrite_fileWriterSuccess(writer) {
    writer.onwrite = function(e) {
        console.log("Writing done!"+file_content);
	    writercallback();
    };	
    writer.write(text_to_write);
}



/*=========================*/
/* DELETING OF FILE */
/*=========================*/

function fail_delete(error) {
    console.log("file could not be deleted: "+error);
}
function success_delete(entry) {
    console.log("************file deleted successfully");
}

var file_to_delete="";
function deleteFile(thefile) {
    file_to_delete = thefile;
    window.requestFileSystem(1,0, dgotFSSuccess, fail_delete);
//failed - function to call when failed
}
function dgotFSSuccess(filesystem) {
    // Got the filesystem 
    console.log("got filesystem");
    performdeletion(filesystem,file_to_delete)  ;  
}

function performdeletion(filesystem,fname) {
    filesystem.root.getFile(fname,{
        create : false
    },entryfordelete,fail_delete);    
}
//removing the file
function entryfordelete(theFile) {
    theFile.remove(success_delete,fail_delete);
}

/*=========================================================*/
/* READ DIRECTORY AND SAVE CONTENT TO {dir_entries}*/
/*========================================================*/
function file_error(error) {
    console.log("error reading dir"+error);
}

var directory_callback;
function getDirectoryEntries(call) { 
     directory_callback= call ;
	// alert("in read entries");
    window.requestFileSystem(1, 0, onGetFileSystemSuccess, file_error);
}

function onGetFileSystemSuccess(fs) {  
    //theFileSystem = fs;
   // var dr = fs.root.createReader();
	fs.root.getDirectory("MEM", {create: true}, inMEM, file_error);

    /*alert(dr);*/
    // Get a list of all the entries in the directory
   /* dr.readEntries(onDirReaderSuccess, file_error);*/
}
var mementry;
function inMEM(direntry) {
var dr = direntry.createReader();
mementry = direntry;
dr.readEntries(onDirReaderSuccess, file_error);
}
/*function rootdir(direntries) {
for( i = 0; i < len; i++) {
	if(dirEntries[i].isDirectory == true) {
		if (dirEntries[i].name==='MEM') {	
		console.log("found mem");		
		dirEntries[i].readEntries(onDirReaderSuccess, file_error);
		
		}

	} 
}*/

var dentries;
var dir_entries_arr = new Array();
var text_array = new Array();
var index =0;
var dir_currentfile;
function rec_gotFEntry(theFile) {
 dir_currentfile= theFile; 
theFile.file(dir_gotFile,failed_read);
}  

//got file - now load the content:
function dir_gotFile(file) {
    var reader = new FileReader();
    reader.onloadend = function(e) {    
        var text = e.target.result;
        console.log("file_content"+text);

        if (text.length==0) {
            console.log("file empty!");
        } 
		//text read
			text_array[index]= text;
			dir_currentfile.remove(success_delete,fail_delete);
			index++;
			file_counter++;
			//console.log(file_counter+"BEFORE WHILE: dir_gotFile -> "+ dentries[file_counter].name);
			while ( file_counter<dentries.length && dentries[file_counter].isDirectory==true ) {
				console.log("dentries:"+dentries.length+"-"+file_counter+"IN WHILE: dir_gotFile -> "+ dentries[file_counter].name);
				file_counter++;
			}
			//directory_callback();
			if (file_counter>=dentries.length) {
				directory_callback();
				dir_entries_arr = new Array();
				text_array = new Array();
			} else
			mementry.getFile(dentries[file_counter].name,{create : false},rec_gotFEntry,failed_read);  
		};
    
    reader.onloaderror = function(e) {
        console.log("could not load file contents");
    };
  
    reader.readAsText(file);  
}
var file_counter=0;
function onDirReaderSuccess(dirEntries) {
    console.log("#dir entries (" + dirEntries.length + ")");
  
    //var num_file_entries = 0;
    var i,  len;
    len = dirEntries.length;
	dentries  = dirEntries;
	console.log("length: "+dentries.length);
	//boolean onefile=false;
    if(len > 0) {
		
        for( i = 0; i < len; i++) {
            if(dentries[i].isDirectory == true) {

                console.log("dir: "+dirEntries[i].name);
        
            } else {
			
                console.log("file: "+dentries[i].name);
				file_counter=i;
				//onefile = true;
				mementry.getFile(dentries[i].name,{create : false},rec_gotFEntry,failed_read);  
				break;
                //dir_entries_arr[num_file_entries] = dirEntries[i].name;
				
                //num_file_entries =num_file_entries+1;
            }
        }
    //console.log("before calling");
    //directory_callback();
    } else {
        console.log("No entries found");
    
    }
	/*if (!onefile) {
	directory_callback();
	}*/
  
}

/*=========================*/
/*TOP LEVEL READ / WRITE FUNTIONS */
/*=========================*/

function initFileSystem() {	
    console.log("initing the filesystem..");
    regular_fileSystemInit();
}

//function to read a file and store the contents in the file_text variable
/*
function readFile(fname) {
regular_readFromFile(fname);    
}*/

function writeFile(fname) {
    regular_writeToFile(fname);
}
/*
function deleteFile(fname) {
performdeletion(fname);	
}*/

//for getting a list of all files in a directory (root - for now)
//getDirectoryEntries


/*---------END----------*/