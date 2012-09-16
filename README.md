#MEM

###Reading categories from inisystem
call startINI(fcall); first
then:
	
	function fcall() {
	//to show all that is in the inisystem
	showContents() ;
	
	//tests
	alert(INIget("categories","Food"));
	alert(INIget("categories","Travel"));
	alert(INIget("categories","Entertainment"));
	alert(INIget("categories","Other"));
	alert(INIget("categories","Telecommunications"));
	
	
	}