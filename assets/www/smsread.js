
var SMSReader = function () {};

SMSReader.prototype.getInbox = function(params, success, fail) {
        return PhoneGap.exec(function(args) {
        success(args);
    }, function(args) {
        fail(args);
    }, 'SMSReader', 'inbox', [params]);
};


PhoneGap.addConstructor(function() {
	PhoneGap.addPlugin("SMSReader", new SMSReader());
	//PluginManager.addService("SMSReader", "org.flying.lions.SMSReader");
});

function getSMSData(data){
	  var txt = "";
	  for(var i = 0; i < data.messages.length; i++){
	          txt += "<b>ID:</b>" +  data.messages[i].id + "<br />";
	          txt += "<b>Number:</b>" +  data.messages[i].number + "<br />";
	          txt += "<b>Message:</b>" + data.messages[i].text + "<hr><br />";
	  }
	  
	  return txt;
}